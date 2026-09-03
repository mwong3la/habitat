package com.habitat.habitat.identity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record InviteStaffRequest(
		@NotBlank @Size(max = 160) String fullName,
		@NotBlank @Email @Size(max = 180) String email,
		@NotBlank @Size(min = 12, max = 128) String temporaryPassword,
		@NotEmpty Set<RoleName> roles) {
}
