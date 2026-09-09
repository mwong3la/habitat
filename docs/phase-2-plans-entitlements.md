# Phase 2: Plans and Entitlements

## Scope

Phase 2 moves package capabilities into persistent subscription data.

Implemented in this phase:

- Plan records
- Feature records
- Plan-to-feature mapping
- Organization feature grants
- Current subscription API
- Plan change API
- Backend entitlement check
- Immediate upgrade behavior
- Scheduled downgrade behavior

## Modules Updated

- `subscription`: plans, features, organization feature grants, plan changes
- `organization`: organization package state and pending downgrade state
- `common`: reference data seeding for plans and features

## API Endpoints

- `GET /api/v1/subscription`
- `PATCH /api/v1/subscription/plan`

## Design Notes

- `Plan` stores the package code and display order.
- `Feature` stores named capabilities used by backend services.
- `PlanFeature` stores the features included in each plan.
- `OrganizationFeature` stores the active feature grants for an organization.
- Upgrades change the active package immediately and replace the organization's feature grants.
- Downgrades set a pending package and effective date. Active features remain unchanged until the billing boundary.
- `SubscriptionService.requireFeature` rejects disabled features in backend code.

## Seeded Plans

- `STARTER`
- `GROWTH`
- `PREMIUM`

## Seeded Features

- `listing:shareable-link`
- `listing:platform-publishing`
- `listing:marketplace`
- `listing:external-syndication`
- `owner:portal`

## Tests Added

- Upgrade takes effect immediately and grants Premium features.
- Downgrade is scheduled and current features remain active.
- Disabled feature usage is rejected.

## Verification

Test execution is blocked by project tooling:

```text
./mvnw: line 117: ./.mvn/wrapper/maven-wrapper.properties: No such file or directory
cannot read distributionUrl property in ./.mvn/wrapper/maven-wrapper.properties
```

The local environment also does not have `mvn` on `PATH`.
