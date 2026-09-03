# Habitat Implementation Phases

This document tracks the implementation order for the Habitat backend. Each phase ends with a compile check, test run, and a usable system state.

## Phase 1: Foundation

Status: implemented.

Scope:

- Organization
- Country
- Currency
- User
- Role
- Permission
- Tenant context
- Organization onboarding
- Staff invitation

Notes:

- Organization is the tenant boundary.
- Backend services resolve tenant context from the authenticated user.
- Package features are temporarily catalog-backed until Phase 2.

## Phase 2: Plans and Entitlements

Scope:

- Plan model
- Feature model
- Plan-to-feature mapping
- Organization feature grants
- Entitlement checks in service code
- Package upgrade rules
- Package downgrade rules

Business rules:

- Upgrades take effect immediately.
- Downgrades take effect at the next billing boundary.
- Downgrades do not delete historical data.
- Disabled features are rejected in the backend, not only hidden in the UI.

## Phase 3: Properties, Units, and Owners

Scope:

- Property
- Unit
- Owner
- Organization-scoped repositories
- Unit lifecycle state

Business rules:

- Unit state starts with `VACANT`, `LISTED`, and `OCCUPIED`.
- State transitions are service operations, not arbitrary field updates.
- Users cannot access property data outside their organization.

## Phase 4: Listings, Inquiries, Viewings, and Leasing

Scope:

- Listing
- Listing photo metadata
- Inquiry
- Viewing
- Tenant
- Lease
- Inquiry-to-lease conversion

Business rules:

- Listings follow `DRAFT`, `PUBLISHED`, `LEASED`, and `DEACTIVATED`.
- Inquiry conversion creates the tenant and lease from captured inquiry data.
- Successful lease conversion marks the unit occupied and deactivates the listing.
- The database must prevent two active leases for one unit.
- Concurrent lease attempts must be handled transactionally.

## Phase 5: Billing and Invoices

Scope:

- Billing schedule
- Invoice
- Invoice item
- Scheduled invoice generation

Business rules:

- Billing is based on active leases.
- Invoices include period, line items, amount, currency, due date, status, and lease reference.
- Invoice generation must be repeat-safe for scheduled execution.

## Phase 6: Payments and Webhooks

Scope:

- Payment service
- Payment adapter interface
- Provider-specific adapters
- Webhook intake
- Payment reconciliation
- Idempotency records

Business rules:

- Core billing must not depend directly on a payment provider.
- Country configuration selects the payment adapter.
- Webhook requests validate, persist, acknowledge quickly, and process asynchronously.
- The same provider transaction must not be processed twice.

## Phase 7: Ledger and Multi-Currency

Scope:

- Ledger account
- Journal entry
- Journal line
- Exchange rate capture
- Base currency conversion

Business rules:

- Journal entries use double-entry accounting.
- Every posted journal entry must balance.
- Posted ledger entries are immutable.
- Corrections use reversals or adjustments.
- Historical transactions retain their original exchange rate and timestamp.

## Phase 8: Maintenance and Vendors

Scope:

- Maintenance request
- Work order
- Vendor
- Work order assignment
- Maintenance expense

Business rules:

- Maintenance follows `SUBMITTED`, `TRIAGED`, `ASSIGNED`, `IN_PROGRESS`, and `RESOLVED`.
- External work creates a work order and vendor assignment.
- Vendor access is limited to assigned jobs only.
- Maintenance expenses post into the ledger.

## Phase 9: Owner Reporting

Scope:

- Reporting period
- Owner statement
- Statement item
- Accountant review
- Statement delivery

Business rules:

- Owner reporting is based on ledger data.
- Maintenance costs are included.
- Currency conversion uses captured transaction rates.
- Owner portal access follows package entitlements.

## Phase 10: Notifications, Integrations, and Hardening

Scope:

- Notification delivery
- Audit log coverage
- External listing integrations
- Production security review
- Migration hardening
- Operational monitoring

Business rules:

- Sensitive financial and administrative actions are auditable.
- External integrations run through module-specific abstractions.
- Production deployments use explicit migrations instead of generated schema updates.
