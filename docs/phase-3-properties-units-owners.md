# Phase 3: Properties, Units, and Owners

## Scope

Phase 3 adds the property management foundation used by listing, leasing, billing, maintenance, and owner reporting.

Implemented in this phase:

- Owner records
- Property records
- Unit records
- Organization-scoped property lookups
- Organization-scoped owner lookups
- Explicit unit status
- Property creation API
- Unit creation API
- Owner creation API

## Modules Added

- `property`: owners, properties, units, API requests, API responses, repositories, and property management service

## API Endpoints

- `POST /api/v1/owners`
- `POST /api/v1/properties`
- `GET /api/v1/properties`
- `POST /api/v1/properties/{propertyId}/units`
- `GET /api/v1/properties/{propertyId}/units`

## Design Notes

- Owner, property, and unit records are tied to an organization.
- Service methods resolve the organization through `TenantContext`.
- Owner ids are validated inside the current organization before property creation.
- Property ids are validated inside the current organization before unit creation and unit listing.
- `ORG_ADMIN` is required for owner, property, and unit creation.
- Unit status starts at `VACANT`.
- Unit lifecycle is represented by `UnitStatus` instead of boolean vacancy flags.

## Unit States

- `VACANT`
- `LISTED`
- `OCCUPIED`

## Tests Added

- Owner, property, and unit creation under the authenticated organization.
- Property creation rejects an owner from another organization.
- Newly created units start as `VACANT`.

## Verification

Test execution is blocked by project tooling:

```text
./mvnw: line 117: ./.mvn/wrapper/maven-wrapper.properties: No such file or directory
cannot read distributionUrl property in ./.mvn/wrapper/maven-wrapper.properties
```

The local environment also does not have `mvn` on `PATH`.
