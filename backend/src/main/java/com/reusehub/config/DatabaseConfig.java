package com.reusehub.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final Pattern POSTGRES_URI_PATTERN =
            Pattern.compile("^postgres(?:ql)?://(?:([^:]+)(?::([^@]+))?@)?([^:/]+)(?::(\\d+))?/(.+)$");

    @Value("${spring.datasource.url:${DB_URL:${DATABASE_URL:}}}")
    private String rawUrl;

    @Value("${spring.datasource.username:${DB_USER:sa}}")
    private String rawUsername;

    @Value("${spring.datasource.password:${DB_PASSWORD:}}")
    private String rawPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Check if database URL is in Render/Heroku standard format (postgresql://... or postgres://...)
        if (rawUrl != null && !rawUrl.isBlank() && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
            Matcher matcher = POSTGRES_URI_PATTERN.matcher(rawUrl);
            if (matcher.matches()) {
                String user = matcher.group(1);
                String pass = matcher.group(2);
                String host = matcher.group(3);
                String portStr = matcher.group(4);
                String database = matcher.group(5);

                int port = (portStr != null && !portStr.isBlank()) ? Integer.parseInt(portStr) : 5432;
                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;

                config.setJdbcUrl(jdbcUrl);
                if (user != null) {
                    config.setUsername(user);
                } else {
                    config.setUsername(rawUsername);
                }
                if (pass != null) {
                    config.setPassword(pass);
                } else {
                    config.setPassword(rawPassword);
                }
                config.setDriverClassName("org.postgresql.Driver");
                log.info("Successfully adapted cloud PostgreSQL URL for Spring JDBC -> {}:{}", host, port);
                return new HikariDataSource(config);
            } else {
                log.warn("PostgreSQL URI did not match expected regex, attempting raw URL fallback...");
            }
        }

        // Standard JDBC URL (e.g. jdbc:postgresql://... or jdbc:h2:mem:...)
        String jdbcUrl = (rawUrl != null && !rawUrl.isBlank())
                ? rawUrl
                : "jdbc:h2:mem:reusehub_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL";

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(rawUsername);
        config.setPassword(rawPassword);

        if (jdbcUrl.contains(":h2:")) {
            config.setDriverClassName("org.h2.Driver");
            log.info("Configured embedded H2 database (in-memory dev mode).");
        } else if (jdbcUrl.contains(":postgresql:")) {
            config.setDriverClassName("org.postgresql.Driver");
            log.info("Configured PostgreSQL database via standard JDBC URL.");
        }

        return new HikariDataSource(config);
    }
}
