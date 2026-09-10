package com.habitat.identity;

import java.util.UUID;

public record StaffInvitedEvent(UUID organizationId, UUID userId) {
}
