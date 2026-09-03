package com.habitat.habitat.identity;

import java.util.UUID;

public record StaffInvitedEvent(UUID organizationId, UUID userId) {
}
