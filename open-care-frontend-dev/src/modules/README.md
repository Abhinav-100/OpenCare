# Frontend Module Architecture

The frontend is organized by responsibility (domain-first), not by technical type.

## Rules

- Keep domain API clients inside `src/modules/<domain>/api`.
- Import domain APIs using the `@/modules/...` alias.
- Do not create new files under `src/api`.
- Shared cross-domain helpers belong in `src/lib`.
- Shared UI primitives belong in `src/components/ui`.

## Current Domains

- `access`: authentication, profile, permission modules
- `catalog`: countries, domain values, genders, locations
- `content`: home, dashboards
- `clinical`: appointments, health records, health vitals, medical metadata
- `provider`: doctors, hospitals, nurses, ambulances, institutions, associations, workplaces
- `blood`: blood banks and blood groups
- `payments`: payments and contribution features
- `storage`: file upload and related APIs

## Why

This structure makes ownership clear, reduces import sprawl, and keeps each domain cohesive as features grow.
