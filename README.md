# OpenCare

OpenCare is a focused healthcare coordination platform for Odisha, India.

## Quick Start

Run these commands from project root.

```powershell
# 1) Start backend dependencies
cd open-care-backend-dev
docker compose up -d postgres-app postgres-keycloak keycloak minio

# 2) Start backend
.\mvnw.cmd "-Dmaven.test.skip=true" spring-boot:run

# 3) Start frontend (new terminal)
cd ..\open-care-frontend-dev
yarn install
yarn dev

# 4) Run pre-demo check (new terminal)
cd ..
.\demo-precheck.ps1
```

## Problem Statement

Patients in Odisha often face fragmented healthcare access:
- difficulty finding verified doctors
- weak linkage between doctor and hospital context
- inconsistent appointment booking paths

For evaluation, this project focuses on one reliable care journey instead of covering every healthcare sub-domain.

## Solution Overview

OpenCare provides one end-to-end flow:
1. doctor onboarding and approval
2. patient authentication
3. doctor and hospital discovery
4. appointment booking with validation rules

This keeps the system easy to demo while still showing real backend rules and role-based behavior.

## Core Features (Current Scope)

1. Doctor onboarding + admin approval
2. Patient registration/login
3. Doctor/Hospital discovery
4. Appointment booking

## Architecture

Request path:

```text
Frontend (Next.js)
	-> Backend REST APIs (Spring Boot)
	-> JWT validation via Keycloak
	-> Data read/write in PostgreSQL
```

Component roles:
- Frontend: user interface for admin and patient actions
- Backend: business rules, role checks, booking constraints
- PostgreSQL: source of truth for doctors, hospitals, profiles, appointments
- Keycloak: authentication and token issuance

## Project Structure

```text
.
├── open-care-backend-dev/    # Spring Boot backend
├── open-care-frontend-dev/   # Next.js frontend
├── .github/workflows/        # CI workflow
├── demo-precheck.ps1         # Root pre-demo verification script
└── README.md
```

Notes:
- Backend scripts and deployment helpers are under `open-care-backend-dev/scripts`.
- A backend wrapper exists: `open-care-backend-dev/demo-precheck.ps1`.

## Team Workflow

Use a simple, safe team process.

### Branching rules
- `main` should stay stable and demo-ready.
- Create feature branches from `main`.
- Suggested naming: `feature/<topic>`, `fix/<topic>`, `docs/<topic>`.

### Pull request flow
1. Pull latest `main`.
2. Create your branch.
3. Make small focused changes.
4. Run local checks:
	 - backend compile/tests as needed
	 - frontend tests
	 - pre-demo check script
5. Open PR to `main`.
6. Merge only after CI is green and reviewer approval.

### Precheck usage before merge/demo
- Run from root: `.\demo-precheck.ps1`
- Or from backend folder: `.\demo-precheck.ps1` (wrapper)
- If result is `❌ DEMO NOT READY`, fix listed issues first.

## Setup (Step-by-Step)

### Prerequisites
- Java 21
- Docker Desktop
- Node.js 18+ and Yarn

### Start backend dependencies

```powershell
cd open-care-backend-dev
docker compose up -d postgres-app postgres-keycloak keycloak minio
```

### Start backend

```powershell
cd open-care-backend-dev
.\mvnw.cmd "-Dmaven.test.skip=true" spring-boot:run
```

If port 6700 is busy:

```powershell
$env:SERVER_PORT="6701"
.\mvnw.cmd "-Dmaven.test.skip=true" spring-boot:run
```

### Start frontend

```powershell
cd open-care-frontend-dev
yarn install
yarn dev
```

### Verify services
- Frontend: http://localhost:3000
- Backend Swagger: http://localhost:6700/swagger-ui.html
- Backend readiness: http://localhost:6700/actuator/health/readiness

If backend started on another port (for example 6701), use that port in URLs.

## Pre-Demo Verification

Run from project root:

```powershell
cd .
.\demo-precheck.ps1
```

What it checks:
- Backend readiness at `/actuator/health/readiness` (expects `status=UP`)
- Frontend availability on `http://localhost:3000` or `http://localhost:3001`
- Port sanity for 6700 and 3000/3001
- Required Docker services: `postgres-app`, `postgres-keycloak`, `keycloak`, `minio`
- Demo data checks:
	- approved doctor exists
	- patient exists (default demo account, auto-create where supported)

Default demo account used by precheck:

```text
Email: demo.patient@opencare.in
Password: Demo@123A
```

Script output summary:
- Backend: PASS/FAIL
- Frontend: PASS/FAIL
- Data: PASS/FAIL
- Dependencies: PASS/FAIL

Final verdict:
- `✅ SYSTEM READY FOR DEMO`
- `❌ DEMO NOT READY`

If it fails, apply the exact `fix:` line shown and rerun.

## Demo Flow

Exact evaluation sequence:
1. Admin approves doctor
2. Patient logs in
3. Patient searches doctor
4. Patient books appointment
5. System validates:
	 - doctor active/verified
	 - doctor-hospital consistency
	 - slot availability

Suggested live walkthrough:
- Approve doctor
- Sign in as patient
- Search doctor
- Book appointment
- Show booked appointment in list

## Common Issues & Fixes

### 1) Port already in use
Symptoms:
- Backend fails to start on 6700
- Frontend shifts from 3000 to 3001 or becomes stale

Fix:
```powershell
# Inspect listeners
Get-NetTCPConnection -LocalPort 6700,3000,3001 -State Listen

# Stop conflicting process by PID
Stop-Process -Id <PID> -Force
```

### 2) Backend not starting
Symptoms:
- `spring-boot:run` exits early

Fix:
```powershell
cd open-care-backend-dev
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd "-Dmaven.test.skip=true" spring-boot:run
```

### 3) Frontend not loading
Symptoms:
- browser cannot open localhost:3000 or localhost:3001

Fix:
```powershell
cd open-care-frontend-dev
yarn install
yarn dev
```

### 4) Precheck failing
Symptoms:
- `❌ DEMO NOT READY`

Fix:
- Read the first `[FAIL]` and run the exact `fix:` line shown.
- Rerun precheck until all categories are PASS.

## Demo Highlights (Auth + Booking)

Authentication:
- Patient registration/login via `/api/auth/register` and `/api/auth/login`
- Doctor self-registration via `/api/auth/register/doctor` (pending approval)

Booking:
- Patient books via `POST /api/appointments`
- Guards enforced:
	- doctor active
	- doctor verified
	- hospital-compatible booking
	- no overlapping slot

## Tech Stack

- Frontend: Next.js 15, React 19, TypeScript
- Backend: Spring Boot 3.4, Java 21
- Database: PostgreSQL 16
- Auth: Keycloak (OAuth2/JWT)
- Infra: Docker Compose

## Honest Limitations

- Not fully production-hardened yet
- Coverage is strongest in core flow; non-core modules are less validated
- Local/demo setup is stronger than cloud operational hardening

## Future Improvements

1. Expand security regression tests beyond core routes
2. Add end-to-end tests with containerized dependencies
3. Strengthen deployment automation and environment profiles
4. Improve monitoring and incident diagnostics
5. Re-introduce broader modules only after core reliability gates are stable

## Documentation Map

- Root overview: this file
- Backend details: [open-care-backend-dev/README.md](open-care-backend-dev/README.md)
- Frontend details: [open-care-frontend-dev/README.md](open-care-frontend-dev/README.md)
