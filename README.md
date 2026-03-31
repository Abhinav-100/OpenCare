# OpenCare

## One-Line Description
OpenCare is a focused healthcare coordination platform for Odisha, India.

## Problem Statement
Patients in Odisha often face fragmented healthcare access:
- difficulty finding verified doctors
- weak linkage between doctor and hospital context
- inconsistent appointment booking paths

For evaluation, this project focuses on solving one clear and reliable care journey instead of covering every healthcare sub-domain.

## Solution Overview
OpenCare provides a single end-to-end flow:
1. doctor onboarding and approval
2. patient authentication
3. doctor and hospital discovery
4. appointment booking with validation rules

This keeps the system understandable for viva/demo while still demonstrating real backend constraints and role-aware behavior.

## Core Features (Evaluation Scope)
Only these four flows are in active evaluation scope:
1. Doctor onboarding + admin approval
2. Patient registration/login
3. Doctor/Hospital discovery
4. Appointment booking

## Tech Stack (Minimal)
- Frontend: Next.js 15, React 19, TypeScript
- Backend: Spring Boot 3.4, Java 21
- Database: PostgreSQL 16
- Auth: Keycloak (OAuth2/JWT)
- Infra: Docker Compose

## Architecture Overview
Simple request path:

```text
Frontend (Next.js)
	-> calls Backend REST APIs (Spring Boot)
	-> Backend validates JWT with Keycloak
	-> Backend reads/writes PostgreSQL
```

Component roles:
- Frontend: user interface for admin and patient actions.
- Backend: business rules, role checks, booking constraints.
- PostgreSQL: source of truth for doctors, hospitals, profiles, appointments.
- Keycloak: authentication and token issuance.

Why this matters for evaluation:
- the frontend handles user interactions
- the backend enforces business and access rules
- PostgreSQL stores healthcare data consistently
- Keycloak manages secure identity and token validation

## How To Run (Step-by-Step)

### Prerequisites
- Java 21 installed
- Docker Desktop running
- Node.js 18+ and Yarn

### 1) Start backend dependencies
```powershell
Set-Location "c:\Users\Abhinav\Desktop\Major Project\OpenCare\open-care-backend-dev"
docker-compose up -d postgres-app postgres-keycloak keycloak minio
```

### 2) Start backend
```powershell
Set-Location "c:\Users\Abhinav\Desktop\Major Project\OpenCare\open-care-backend-dev"
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
.\mvnw.cmd "-Dmaven.test.skip=true" spring-boot:run
```

If port 6700 is busy, run on another port:
```powershell
$env:SERVER_PORT="6701"
.\mvnw.cmd "-Dmaven.test.skip=true" spring-boot:run
```

### 3) Start frontend
```powershell
Set-Location "c:\Users\Abhinav\Desktop\Major Project\OpenCare\open-care-frontend-dev"
yarn install
yarn dev
```

### 4) Verify services
- Frontend: http://localhost:3000
- Backend Swagger: http://localhost:6700/swagger-ui.html
- Backend readiness: http://localhost:6700/actuator/health/readiness

If you started backend on another port (for example `6701`), replace `6700` in the URLs above.

## Pre-Demo Verification

Run this from project root:

```powershell
Set-Location "c:\Users\Abhinav\Desktop\Major Project\OpenCare"
.\demo-precheck.ps1
```

What it checks:
- Backend readiness at `/actuator/health/readiness` (expects `status=UP`).
- Frontend availability on `http://localhost:3000` or `http://localhost:3001`.
- Port sanity for `6700` and `3000/3001` (detects stale or conflicting listener state).
- Required Docker services running: `postgres-app`, `postgres-keycloak`, `keycloak`, `minio`.
- Demo data checks:
	- at least one approved doctor exists (via `/api/doctors?page=0&size=1`)
	- at least one patient exists (uses default demo account, auto-creates when supported)

Default demo account used by the script:

```text
Email: demo.patient@opencare.in
Password: Demo@123A
```

The script prints a final summary:
- Backend: PASS/FAIL
- Frontend: PASS/FAIL
- Data: PASS/FAIL
- Dependencies: PASS/FAIL

And final verdict:
- `✅ SYSTEM READY FOR DEMO`
- `❌ DEMO NOT READY`

If script fails:
- Read the `[FAIL]` line and apply the exact `fix:` command shown beneath it.
- Re-run `.\demo-precheck.ps1` until all checks show `[PASS]`.

### 5) Bootstrap demo data (optional)
- Use existing seeded/local data from your running database.
- If needed, create one admin user, one approved doctor, and one patient via auth and doctor APIs before the demo flow.

## Demo Flow (Exact Evaluation Sequence)
1. Admin approves doctor.
2. Patient logs in.
3. Patient searches doctor.
4. Patient books appointment.
5. System validates constraints (doctor active/verified, doctor-hospital consistency, slot availability).

Suggested walkthrough:
- Admin approves doctor.
- Patient signs in.
- Patient searches a doctor.
- Patient books an appointment and verifies booking in appointments list.

## Demo Highlights (Auth + Booking)
1. Authentication flow
- Patient registration and login via `/api/auth/register` and `/api/auth/login`.
- Doctor self-registration via `/api/auth/register/doctor` with pending approval state.

2. Appointment booking flow
- Patient books with `POST /api/appointments` from doctor/hospital discovery.
- Booking guards are enforced: doctor must be active, verified, and hospital-compatible.
- Slot availability check prevents overlapping bookings.

## Honest Limitations
- The project is not fully production hardened yet.
- Coverage is strongest in core flow; non-core modules are not fully validated for final deployment.
- Deployment setup is simplified for local evaluation and demonstration.
- Operational hardening (full observability, robust CI gates, cloud rollout automation) is still limited.

## Future Improvements
1. Expand security regression tests beyond core routes.
2. Add end-to-end automated tests with real containerized dependencies.
3. Strengthen deployment automation and environment configuration profiles.
4. Improve monitoring dashboards and incident diagnostics.
5. Re-introduce broader healthcare modules only after core reliability gates are met.

## Documentation Map
- Root overview: this file.
- Backend setup and API notes: [open-care-backend-dev/README.md](open-care-backend-dev/README.md)
- Frontend setup and routes: [open-care-frontend-dev/README.md](open-care-frontend-dev/README.md)
