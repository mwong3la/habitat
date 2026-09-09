package com.habitat.habitat.property;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOwnerRequest(
		@NotBlank @Size(max = 160) String fullName,
		@NotBlank @Email @Size(max = 180) String email,
		@Size(max = 40) String phoneNumber) {
}
