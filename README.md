# OpenCare

OpenCare is a healthcare coordination platform designed to streamline core outpatient workflows for Odisha, India. It demonstrates a complete, role-based journey across authentication, provider discovery, and appointment management.

## One-Command Demo Start

Run from project root:

```powershell
.\start-demo.ps1
```

The launcher handles startup end-to-end:

1. Validates required commands (`docker`, `powershell`, `yarn`).
2. Cleans stale demo listeners on ports `6700`, `3000`, and `3001`.
3. Starts required Docker services.
4. Opens backend and frontend in separate PowerShell windows.
5. Waits for backend readiness and frontend HTTP reachability.
6. Executes precheck (`demo-precheck.ps1`) unless skipped.

## Script Options

```powershell
.\start-demo.ps1 `
  -WarmupSeconds 12 `
  -BackendTimeoutSeconds 180 `
  -FrontendTimeoutSeconds 180 `
  -FailOnPrecheck
```

Skip precheck if you only want to boot services:

```powershell
.\start-demo.ps1 -SkipPrecheck
```

Use longer waits on slower machines:

```powershell
.\start-demo.ps1 -BackendTimeoutSeconds 300 -FrontendTimeoutSeconds 300
```

Run in strict mode when you want the launcher to fail on precheck issues:

```powershell
.\start-demo.ps1 -FailOnPrecheck
```

## What start-demo.ps1 Starts

### Docker services

The script starts:

- `postgres-app`
- `postgres-keycloak`
- `keycloak`
- `minio`

using:

```powershell
docker compose -f open-care-backend-dev/docker-compose.yml up -d postgres-app postgres-keycloak keycloak minio
```

### Backend window

Runs in `open-care-backend-dev`:

```powershell
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

### Frontend window

Runs in `open-care-frontend-dev`:

```powershell
if (-not (Test-Path node_modules)) { yarn install }
yarn dev --port 3000
```

## Readiness and Health Gates

Before precheck, the launcher waits for:

- Backend: `http://localhost:6700/actuator/health/readiness` returns `UP`
- Frontend: HTTP response on `http://localhost:3000` or `http://localhost:3001`

If a service requires additional startup time, the launcher reports a timeout warning and continues so precheck can provide targeted diagnostics.

## Pre-Demo Verification

You can run precheck independently too:

```powershell
.\demo-precheck.ps1
```

Checks performed:

- Backend readiness
- Frontend reachability
- Port sanity (6700/3000/3001)
- Required Docker services
- Demo data readiness

Summary categories:

- Backend
- Frontend
- Data
- Dependencies

Final verdict:

- `SYSTEM READY FOR DEMO`
- `DEMO NOT READY`

If any check fails, apply the exact `fix:` command shown by the script and run precheck again.

By default, launcher startup completes even if precheck reports issues. Use `-FailOnPrecheck` to enforce strict failure behavior.

## Quick Troubleshooting

### Backend not coming up

```powershell
cd open-care-backend-dev
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

### Frontend not reachable

```powershell
cd open-care-frontend-dev
yarn install
yarn dev --port 3000
```

### Port conflicts

```powershell
Get-NetTCPConnection -LocalPort 6700,3000,3001 -State Listen
Stop-Process -Id <PID> -Force
```

### Docker services missing

```powershell
cd open-care-backend-dev
docker compose up -d postgres-app postgres-keycloak keycloak minio
```

## Manual Start (Fallback)

If you do not want to use the launcher:

1. Start Docker services from `open-care-backend-dev`.
2. Start backend with the Maven wrapper.
3. Start frontend in another terminal.
4. Run `.\demo-precheck.ps1` from root.

## Demo Credentials (Precheck Defaults)

```text
Email: demo.patient@opencare.in
Password: Demo@123A
```

## Architecture

```text
Frontend (Next.js)
  -> Backend REST APIs (Spring Boot)
  -> JWT validation via Keycloak
  -> Data in PostgreSQL
```

This architecture separates presentation, business logic, identity, and persistence concerns, which makes the demo flow clear and operationally predictable.

## Project Structure

```text
.
├── start-demo.ps1
├── demo-precheck.ps1
├── open-care-backend-dev/
├── open-care-frontend-dev/
└── README.md
```

## Tech Stack

- Frontend: Next.js, React, TypeScript
- Backend: Spring Boot, Java 21
- Database: PostgreSQL
- Auth: Keycloak
- Infra: Docker Compose

The stack is selected to provide a production-aligned development experience with strong ecosystem support and clear separation of responsibilities.

## Documentation Map

- Backend: [open-care-backend-dev/README.md](open-care-backend-dev/README.md)
- Frontend: [open-care-frontend-dev/README.md](open-care-frontend-dev/README.md)
