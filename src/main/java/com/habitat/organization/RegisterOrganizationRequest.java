package com.habitat.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterOrganizationRequest(
		@NotBlank @Size(max = 160) String name,
		@NotNull OrganizationType organizationType,
		@NotBlank @Pattern(regexp = "[A-Za-z]{2}") String countryCode,
		@NotBlank @Pattern(regexp = "[A-Za-z_]{3,40}") String packageCode,
		@NotBlank @Size(max = 160) String adminFullName,
		@NotBlank @Email @Size(max = 180) String adminEmail,
		@NotBlank @Size(min = 12, max = 128) String adminPassword) {
}
