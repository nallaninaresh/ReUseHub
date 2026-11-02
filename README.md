# ♻️ ReUseHub

> **Community Circular Sharing, Donation & Reuse Marketplace**  
> An open full-stack platform empowering communities and neighborhoods to declutter responsibly, find quality second-hand goods, and donate items to keep them out of landfills.

[![Frontend](https://img.shields.io/badge/Frontend-React%2018%20%2B%20Vite%20%2B%20Tailwind-61dafb)](frontend)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203%20%2B%20Java%2021-6db33f)](backend)
[![Security](https://img.shields.io/badge/Security-Spring%20Security%206%20%2B%20JWT-red)](backend)

---

## 🏛️ Project Architecture

This repository is structured as a unified full-stack monorepo:

```text
ReUseHub/
├── frontend/                   # React 18, Vite 5, Tailwind CSS, Lucide Icons
│   ├── src/
│   │   ├── app/                # Root layout & React Router v6 configuration
│   │   ├── components/         # UI primitives, feedback, and layout wrappers
│   │   ├── context/            # AuthContext (JWT rehydration & session state)
│   │   └── features/           # Items catalog, Auth, Claims coordination, Dashboard
│   └── package.json
└── backend/                    # Java 21, Spring Boot 3.3.2, Spring Security 6, JPA
    ├── src/main/java/com/reusehub/
    │   ├── config/             # Security filter chain, CORS, DataInitializer
    │   ├── common/             # Standard ApiResponse envelope & GlobalExceptionHandler
    │   ├── security/           # JWT token provider & OncePerRequest filter
    │   └── modules/            # Auth, Item lifecycle, Claims state machine, Reviews
    ├── src/main/resources/     # application.yml (H2 zero-config / PostgreSQL ready)
    └── pom.xml
```

---

## ✨ Key Features

1. **Universal Community Discovery**: Filter across 7 categories (Home & Furniture, Electronics, Tools & DIY, Baby & Kids, Kitchen, Books, Sports).
2. **Flexible Listing Models**: Donate (100% Free), Subsidized Sale, or Direct Item Swap.
3. **Atomic Claim & Reservation Workflow**: Propose pickup notes; seller acceptance automatically transitions the item to `RESERVED` and rejects competing inquiries.
4. **Stateless Security**: Spring Security 6 with JJWT (0.12.5) Bearer token authentication and BCrypt password encryption.
5. **Zero-Setup Database**: Runs on embedded H2 mode by default with auto-seeding; easily configures to PostgreSQL in production via environment variables.

---

## 🚀 Quick Start Guide

### 1. Run the Backend (Spring Boot)
Requires Java 21:
```bash
cd backend
mvn spring-boot:run
```
*The REST API will be live on `http://localhost:8080/api/v1`.*

### 2. Run the Frontend (React + Vite)
Requires Node.js 18+:
```bash
cd frontend
npm install
npm run dev
```
*The web interface will be live on `http://localhost:3000`.*

---

## 📡 REST API Reference

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Public | Register a new user |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user & return JWT |
| `GET` | `/api/v1/auth/me` | User | Get current profile |
| `GET` | `/api/v1/items` | Public | List paginated & filtered items |
| `GET` | `/api/v1/items/{id}` | Public | Get single item details |
| `POST` | `/api/v1/items` | User | Publish a new item listing |
| `PATCH`| `/api/v1/items/{id}/status`| Owner | Update listing status |
| `POST` | `/api/v1/items/{id}/requests` | User | Submit a claim / pickup inquiry |
| `GET` | `/api/v1/requests/incoming` | Owner | View claims on your items |
| `GET` | `/api/v1/requests/outgoing` | User | View your claims on others' items |
| `PATCH`| `/api/v1/requests/{id}/status`| Owner | Accept or decline claim |
| `POST` | `/api/v1/reviews` | User | Submit feedback for user handoff |

---

## 🌐 Deploy to Render

ReUseHub is fully configured for deployment on [Render](https://render.com).

### Option A: Render Blueprint (Recommended - 1-Click Deployment)
1. Push your repository to GitHub.
2. In the Render Dashboard, click **New +** > **Blueprint**.
3. Select your repository. Render reads [`render.yaml`](file:///c:/Users/nalla/OneDrive/Desktop/ReUseHub/render.yaml) and automatically configures:
   - **Backend**: Docker Web Service running Spring Boot 3 on Java 21.
   - **Frontend**: Global CDN Static Site running React 18 + Vite with SPA routing rewrites.
4. Click **Apply**.
5. Once deployed, copy your backend URL (e.g. `https://reusehub-backend.onrender.com`) and update `VITE_API_URL` in the frontend static site settings to `https://<backend-url>/api/v1`. Also set `ALLOWED_ORIGINS` on the backend to include your frontend URL.

### Option B: Manual Setup on Render
1. **Backend (Web Service)**:
   - **Name**: `reusehub-backend`
   - **Root Directory**: `backend`
   - **Environment / Runtime**: `Docker`
   - **Health Check Path**: `/api/v1/health`
   - **Environment Variables**:
     - `PORT`: `10000`
     - `ALLOWED_ORIGINS`: `https://your-frontend.onrender.com`
     - `DATABASE_URL`: *(Optional)* Render PostgreSQL connection string or leave blank for embedded H2.
2. **Frontend (Static Site)**:
   - **Name**: `reusehub-frontend`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Environment Variables**:
     - `VITE_API_URL`: `https://your-backend.onrender.com/api/v1`
   - **Redirects / Rewrites**:
     - Add rewrite rule: `/*` -> `/index.html` (Rewrite)

---

## 👤 Author

Developed by **[Naresh Nallani](https://github.com/Naresh1817)**

