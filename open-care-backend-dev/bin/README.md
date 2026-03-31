# OpenCare Backend

Backend service for the OpenCare healthcare platform (Odisha-focused presentation).
This is a modular monolith built with Spring Boot, exposing REST APIs for doctor onboarding, patient registration, hospital management, and appointment booking.
The current active deployment and demo scope are specifically for Odisha, India.

## Honest Project Status

What is working now:
- Backend code compiles successfully (`mvnw.cmd -DskipTests clean compile`).
- Core domain flow is implemented:
  - doctor self-registration
  - admin doctor approval
  - patient registration/login
  - appointment booking with doctor-hospital consistency checks

Current limitations:
- Local runs can face environment issues (for example, `Port 6700 is already in use`) when previous backend processes are still active.
- Deployment and operational hardening are still limited compared to a production-grade setup.

## Core Evaluation Scope

For college demo, focus only on these entities:
- Doctor
- Patient
- Hospital
- Appointment

Non-core modules exist in codebase but are out of current demo scope.

## Core API Flow

1. Doctor self-registers
- `POST /api/auth/register/doctor`
- doctor created as pending (`isVerified=false`, `isActive=false`)

2. Admin approves doctor
- `PATCH /api/doctors/{id}/approve`
- requires `update-doctor` authority

3. Patient registers and logs in
- `POST /api/auth/register`
- `POST /api/auth/login`

4. Patient books appointment
- `POST /api/appointments`
- booking validates:
  - doctor must be active
  - doctor must be verified
  - if doctor has hospital, appointment hospital must match

## Quick Start (Current Best Effort)

### Prerequisites
- Java 21
- Docker and Docker Compose

### 1) Start dependencies
```powershell
Set-Location "c:\Users\Abhinav\Desktop\Major Project\OpenCare\open-care-backend-dev"
docker-compose up -d postgres-app postgres-keycloak keycloak minio
```

### 2) Verify compile
```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
.\mvnw.cmd -DskipTests clean compile
```

### 3) Try running backend
```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
.\mvnw.cmd spring-boot:run "-Dmaven.test.skip=true" "-Dspring-boot.run.jvmArguments=-Duser.timezone=Asia/Kolkata"
```

### Port conflict handling
If port `6700` is already used, startup now logs a clear message with override options.

Preferred option (dedicated env var):
```powershell
$env:SERVER_PORT="6701"
.\mvnw.cmd spring-boot:run "-Dmaven.test.skip=true" "-Dspring-boot.run.jvmArguments=-Duser.timezone=Asia/Kolkata"
```

Alternative options:
- Pass `--server.port=6701` at startup
- Stop the process currently using port `6700`

Note: `OPENCARE_SERVER_PORT` is no longer used by the backend configuration.

### 4) API docs
- Swagger: http://localhost:6700/swagger-ui.html (or your configured SERVER_PORT)
- Readiness: http://localhost:6700/actuator/health/readiness (or your configured SERVER_PORT)

## API Bootstrap

Use this guide for first-time API data setup:
- `docs/API_DATA_BOOTSTRAP.md`

Optional Postman collection:
- `docs/postman/API_DATA_BOOTSTRAP.postman_collection.json`

## Tech Stack

- Java 21
- Spring Boot 3.4.3
- Spring Security + Keycloak (OAuth2/JWT)
- PostgreSQL + Liquibase
- MinIO
- MapStruct + Lombok
- SpringDoc OpenAPI

## Known Weaknesses (Important)

- Test depth is strongest in the core flow and less complete for non-core modules.
- Production-oriented capabilities (advanced observability, CI policy gates, cloud rollout automation) are still in progress.
- Non-core modules in this repository are intentionally out of current evaluation scope.

## Documentation

- Scope and simplification: `../docs/SIMPLIFIED_SCOPE.md`
- Master project overview: `../README.md`
- Bootstrap flow: `docs/API_DATA_BOOTSTRAP.md`
- Architecture details: `docs/ARCHITECTURE.md`
- Testing guide: `docs/TESTING.md`
- Permissions: `docs/PERMISSIONS.md`

## License

MIT License
