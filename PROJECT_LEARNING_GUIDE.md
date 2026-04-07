# OpenCare Project Learning Guide (Beginner Friendly)

This guide is made for learning fast, with simple steps.
Read and run in this exact order.

## 1) What this project is

OpenCare has 2 big parts:

1. Backend (Spring Boot, Java)
- Folder: `open-care-backend-dev`
- Job: Business logic, database, auth checks, APIs

2. Frontend (Next.js, TypeScript)
- Folder: `open-care-frontend-dev`
- Job: UI pages, route protection, API calls to backend

## 2) Core demo flow (most important)

Only remember this flow first:

1. Doctor registration
2. Admin doctor approval
3. Patient registration/login
4. Patient books appointment

If you understand this one flow, you understand the project backbone.

## 3) Start the project in one command

From root folder:

```powershell
./start-demo.ps1
```

What this script does:

1. Checks required folders/tools
2. Starts Docker services (Postgres, Keycloak, MinIO)
3. Starts backend in new terminal
4. Starts frontend in new terminal
5. Runs precheck

Read this file for startup logic:
- `start-demo.ps1`

## 4) Backend learning path (easy order)

Read these files in this exact order:

1. App startup
- `open-care-backend-dev/src/main/java/com/ciphertext/opencarebackend/OpenCareBackendApplication.java`

2. Security and access control
- `open-care-backend-dev/src/main/java/com/ciphertext/opencarebackend/config/SecurityConfig.java`

3. Appointment API endpoints (controller layer)
- `open-care-backend-dev/src/main/java/com/ciphertext/opencarebackend/modules/appointment/controller/AppointmentApiController.java`

4. Appointment business logic (service layer)
- `open-care-backend-dev/src/main/java/com/ciphertext/opencarebackend/modules/appointment/service/impl/AppointmentServiceImpl.java`

5. Runtime config (ports, db, keycloak, flags)
- `open-care-backend-dev/src/main/resources/application.yml`

### Backend mental model

Request flow:

1. Request enters endpoint in Controller
2. Service validates role/user/business rules
3. Service talks to repositories
4. Entity saved/fetched
5. Mapper converts entity to response DTO
6. Response returned

## 5) Frontend learning path (easy order)

Read these files in this exact order:

1. Route guard and redirects
- `open-care-frontend-dev/middleware.ts`

2. Root app layout and providers
- `open-care-frontend-dev/src/app/layout.tsx`

3. Header/footer wrapper rules
- `open-care-frontend-dev/src/modules/platform/components/common/LayoutWrapper.tsx`

4. Appointment API calls used by UI
- `open-care-frontend-dev/src/modules/clinical/api/appointments.ts`

### Frontend mental model

UI flow:

1. User opens page
2. Middleware checks route + token + role
3. Page/component calls API helper
4. API helper adds auth header
5. Backend responds
6. UI updates with data

## 6) End-to-end appointment booking trace

Use this to understand real project flow quickly.

Frontend side:

1. User chooses doctor/date/time in UI
2. Frontend calls `fetchDoctorSlots(...)`
3. User confirms booking
4. Frontend calls `createAppointment(...)`

Backend side:

1. `POST /api/appointments` hits `AppointmentApiController`
2. JWT subject becomes patient identity
3. `AppointmentServiceImpl.createAppointment(...)` runs checks:
- required fields
- doctor exists
- doctor active + verified
- hospital consistency
- slot overlap check
4. Appointment gets number like `APT-XXXXXXXX`
5. Appointment saved and returned

## 7) 5-day learning plan (simple)

### Day 1

1. Run project with `./start-demo.ps1`
2. Open backend + frontend READMEs
3. Read startup files only

### Day 2

1. Read `SecurityConfig.java`
2. Make list of public vs authenticated routes
3. Test one route from each category

### Day 3

1. Read appointment controller + service
2. Draw request flow on paper
3. Test booking and cancellation APIs

### Day 4

1. Read frontend middleware + appointments API file
2. Track one appointment request in browser Network tab
3. Match each request with backend endpoint

### Day 5

1. Explain full flow in your own words
2. Make one small change (example: better validation message)
3. Run app and verify behavior

## 8) Hands-on tasks (to become confident)

Do these in order:

1. Add one extra log line in appointment create flow
2. Add one safe validation (example: appointment date cannot be in past)
3. Show custom frontend error when booking fails
4. Add one tiny test for appointment service method

## 9) If something fails

Common checks:

1. Port busy (6700, 3000)
2. Docker services not up
3. Keycloak not ready yet
4. Frontend token missing/expired
5. Wrong API base URL

Quick health checks:

```powershell
# Backend readiness
Invoke-WebRequest http://localhost:6700/actuator/health/readiness

# Frontend
Invoke-WebRequest http://localhost:3000
```

## 10) What to focus for college/project review

Focus only on these points:

1. Clear role-based access control
2. Real booking validation rules
3. Clean frontend-backend integration
4. Core user journey works end-to-end

---

If you want next, I can create a second file: `PROJECT_REVISION_CHECKLIST.md` with viva-style questions and model answers from your exact codebase.