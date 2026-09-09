# Phase 1: Foundation

## Baseline

- Spring Boot application package: `com.habitat.habitat`
- Existing dependencies: Spring Data JPA, Spring Security, Bean Validation, Spring WebMVC, PostgreSQL runtime, Lombok, and Spring Boot test starters
- Existing configuration: application name only
- Existing database setup: PostgreSQL dependency only; no datasource settings, migrations, or schema
- Existing security setup: dependency only; no configured users, roles, or security filter chain
- Existing domain model: none
- Existing APIs: none
- Existing tests: generated context-load test

`Prism.pdf` was not present in the repository or attachment directory. This phase was implemented from the pasted Habitat brief.

## Scope

Phase 1 establishes the organization and identity boundary for Habitat. The goal is to make every later workflow organization-scoped from the start.

Implemented in this phase:

- Organization registration
- Country and base currency selection
- Package selection
- Default feature resolution
- Admin user creation
- Role and permission seed data
- Staff invitation
- Staff role assignment
- Current organization resolution from the authenticated user
- DTO-based API responses
- Basic validation and API error responses

## Modules Added

- `common`: API errors, exception handling, shared domain exceptions, reference data seeding
- `identity`: users, roles, permissions, staff invitation, authentication support, tenant context
- `organization`: organizations, countries, currencies, onboarding API
- `subscription`: initial package-to-feature catalog

## API Endpoints

- `POST /api/v1/organizations`
- `GET /api/v1/organizations/me`
- `POST /api/v1/staff/invitations`

## Design Notes

- Organization is the tenant boundary.
- Organization-scoped operations resolve the organization from the authenticated user through `TenantContext`.
- Client-supplied organization ids are not used as trusted tenant context.
- `ORG_ADMIN` is required for staff invitation.
- Tenant and owner portal roles are blocked from the staff invitation flow.
- Package features are resolved through subscription plan and feature records.
- HTTP Basic is used for the first security pass because no JWT or session design existed in the project.
- Hibernate schema update is enabled for local development. Flyway migrations belong before shared database use.

## Tests Added

- Organization registration creates a live organization and admin user.
- Duplicate admin email is rejected.
- Tenant context resolves the organization from the authenticated principal.
- Organization admin can invite staff.
- Non-admin staff cannot invite staff.
- Staff invitation rejects tenant portal roles.

## Verification

Test execution is blocked by project tooling:

```text
./mvnw: line 117: ./.mvn/wrapper/maven-wrapper.properties: No such file or directory
cannot read distributionUrl property in ./.mvn/wrapper/maven-wrapper.properties
```

The local environment also does not have `mvn` on `PATH`.

## Next Phase

Phase 2 replaces the initial entitlement catalog with persistent subscription data:

- `Plan`
- `Feature`
- `PlanFeature`
- `OrganizationFeature`
- upgrade and downgrade rules
- entitlement checks used by application services
