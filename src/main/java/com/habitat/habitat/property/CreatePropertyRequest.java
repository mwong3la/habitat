package com.habitat.habitat.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreatePropertyRequest(
		UUID ownerId,
		@NotBlank @Size(max = 160) String name,
		@NotBlank @Size(max = 220) String addressLine1,
		@NotBlank @Size(max = 100) String city,
		@Size(max = 100) String region) {
}
