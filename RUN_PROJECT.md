# OpenCare Run Guide

This guide explains how to run OpenCare on a new machine.

## Prerequisites

Install these first:

- Java JDK 21
- Docker Desktop (with Docker Compose support)
- Node.js 18+ (Node 20 LTS recommended)
- Yarn 1.22.x
- PowerShell (Windows PowerShell or pwsh)

## Required Free Ports

Make sure these ports are free before startup:

- 3000 (frontend)
- 6700 (backend)
- 8080 (Keycloak)
- 5432 (PostgreSQL app DB)
- 5444 (PostgreSQL Keycloak DB)
- 9000 and 9001 (MinIO)

## Quick Start (Recommended)

From project root:

```powershell
.\start-demo.ps1
```

This script will:

1. Validate tools
2. Start Docker services
3. Launch backend and frontend in separate PowerShell windows
4. Run demo precheck

## Manual Start (If Needed)

### 1) Start infrastructure

```powershell
Set-Location .\open-care-backend-dev
docker compose up -d postgres-app postgres-keycloak keycloak minio
```

### 2) Start backend

Open a new terminal:

```powershell
Set-Location .\open-care-backend-dev
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run
```

### 3) Start frontend

Open another new terminal:

```powershell
Set-Location .\open-care-frontend-dev
yarn install
yarn dev --port 3000
```

## Verify Services

After startup, check:

- Frontend: http://localhost:3000
- Backend readiness: http://localhost:6700/actuator/health/readiness
- Backend Swagger: http://localhost:6700/swagger-ui.html

You can also run:

```powershell
.\demo-precheck.ps1
```

## Demo Login

Default demo user:

- Email: demo.patient@opencare.in
- Password: Demo@123A

## Troubleshooting

### Backend port already in use

```powershell
Get-NetTCPConnection -LocalPort 6700 -State Listen
Stop-Process -Id <PID> -Force
```

### Frontend not opening

```powershell
Set-Location .\open-care-frontend-dev
yarn install
yarn dev --port 3000
```

### Docker services not running

```powershell
Set-Location .\open-care-backend-dev
docker compose up -d postgres-app postgres-keycloak keycloak minio
```

## Stop the Project

- Close backend and frontend terminal windows
- Optionally stop containers:

```powershell
Set-Location .\open-care-backend-dev
docker compose down
```
