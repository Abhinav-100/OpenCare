# OpenCare Frontend

Frontend application for the OpenCare healthcare platform.
Built with Next.js and TypeScript, this app consumes backend APIs for doctor discovery, patient auth, and appointment booking.
The current active deployment and demo scope are specifically for Odisha, India.

## Honest Status

What is working now:
- Frontend project structure is in place and runs with `yarn dev`.
- Core UI flow exists for evaluation:
   - patient register/login
   - browse doctors and hospitals
   - book appointment

Current limitation that affects frontend usability:
- API-dependent pages require backend services (backend app, Keycloak, database) to be running correctly.
- Evaluation focus is on core flow only; non-core UI modules are not fully validated in this scope.

## Evaluation Scope (Core Only)

Use only this flow for college demo:
1. Doctor self-registers and gets approved by admin (backend flow)
2. Patient registers and logs in
3. Patient views approved doctors/hospitals
4. Patient books appointment

Non-core screens are out of demo scope.

References:
- `../README.md`
- `../open-care-backend-dev/README.md`

## Tech Stack

- Next.js 15.4.5
- React 19.1.0
- TypeScript 5
- Tailwind CSS v4
- Radix UI
- TanStack Query v5
- React Hook Form + Zod

## API Configuration

Frontend base URL logic:
- `NEXT_PUBLIC_API_BASE_URL` or `NEXT_PUBLIC_API_URL`
- fallback: `http://localhost:6700/api`

So local dev should point backend API to port 6700.

## Run Frontend

### Prerequisites
- Node.js 18+
- Yarn 1.22+

### Install and start
```bash
cd open-care-frontend-dev
yarn install
yarn dev
```

Open: http://localhost:3000

## Build and Quality

```bash
# build
yarn build

# start production server (package script uses port 5175)
yarn start

# lint
yarn lint
```

## Important Routes (Demo)

- `/` home
- `/login` login
- `/signup` patient registration
- `/doctors` doctor list
- `/hospitals` hospital list
- appointment booking path from doctor/hospital detail flow

## Known Weaknesses

- End-to-end behavior depends on local backend and auth service availability.
- Some non-core modules remain visible in codebase and can distract during evaluation.
- Production hardening (full monitoring, deployment automation, and broad regression depth) remains future work.

## What to Highlight in Presentation

- Clean patient journey in a real API-driven flow (not fake static data).
- Doctor approval gate before bookings.
- Hospital-aware booking validation for data consistency.

## License

MIT License
