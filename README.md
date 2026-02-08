# Secure Auth App (Spring Boot + React + TypeScript)

This repository contains a full login/logout flow with JWT authentication, refresh-token rotation, idle-session handling, and Dockerized local setup.

## Stack

- Backend: Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, JWT
- Frontend: React, TypeScript, React Router, Vite
- E2E: Playwright
- Containerization: Docker + Docker Compose

## Project Structure

- `/auth-backend` Spring Boot API
- `/auth-frontend` React + TypeScript UI
- `/e2e` Playwright end-to-end tests
- `/docker-compose.yml` local multi-service runtime

## Implemented Requirements

### Backend

- User model + DB:
  - `UserEntity` with `id`, `email`, `password`, `role` in `app_users`
  - `RefreshTokenEntity` in `refresh_token` table
- Auth controller:
  - `POST /api/auth/login`
  - `POST /api/auth/refresh`
  - `POST /api/auth/logout`
  - `POST /api/auth/ping`
  - `GET /api/auth/me`
- JWT service:
  - Access token signed with secret, 15-minute expiry
  - Refresh token lifetime 7 days
  - Refresh token persisted in DB and rotated on refresh
- Security config:
  - Stateless security filter chain with JWT auth filter
  - Logout endpoint revokes refresh token
- Health:
  - `GET /api/health`
  - `GET /actuator/health`

### Frontend

- Login form with email/password and API integration
- Protected route for dashboard
- Dashboard displays authenticated user role
- Logout button calls backend logout and clears local auth state
- Idle timeout behavior
- Semantic labels, keyboard-usable form controls, alert messaging
- Error handling:
  - Invalid credentials, expired/invalid token, and network/API failures are shown with user-friendly messages

## Seed Users (Local)

On startup, backend seeds two users (configurable via env vars):

- Admin:
  - Email: `admin@inpulse.dev`
  - Password: `ChangeMe123!`
- User:
  - Email: `user@inpulse.dev`
  - Password: `ChangeMe123!`

## Run With Docker

From repository root:

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Postgres: `localhost:5432` (`authdb` / `postgres` / `postgres`)

## Tests

### Frontend unit tests

```bash
cd auth-frontend
npm ci
npm test
```

Includes:

- API client tests
- Auth API wrapper tests

### Backend unit tests (only)

```bash
cd auth-backend
./mvnw -Dtest='*Test,!*IntegrationTest' test
```

Includes:

- Controller unit tests
- Service unit tests
- JWT service unit tests

### Backend integration tests (only)

```bash
cd auth-backend
./mvnw -Dtest='*IntegrationTest' test
```

Includes:

- Auth flow integration (login, refresh rotation, logout invalidation)
- Health endpoint integration

### E2E tests

```bash
cd e2e
npm ci
npx playwright install chromium 
# The command above takes some time for the first run to download the browser, 
# but only needs to be done once
npm run test:ui
```

### Demo Screenshots

<img width="1429" height="762" alt="Screenshot 2026-02-07 at 11 26 20 PM" src="https://github.com/user-attachments/assets/562c6d78-a38f-46d7-8c6f-b72670534703" />
<img width="1433" height="775" alt="Screenshot 2026-02-07 at 11 22 35 PM" src="https://github.com/user-attachments/assets/e02d74b4-5079-4128-88c3-c26ded1e01fc" />
<img width="1437" height="768" alt="Screenshot 2026-02-07 at 11 22 25 PM" src="https://github.com/user-attachments/assets/7f3aa6dc-3b06-4f32-abe9-c2258b83beb1" />


