package com.habitat.identity;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {

	private final StaffManagementService staffManagementService;

	public StaffController(StaffManagementService staffManagementService) {
		this.staffManagementService = staffManagementService;
	}

	@PostMapping("/invitations")
	public ResponseEntity<UserResponse> invite(@Valid @RequestBody InviteStaffRequest request) {
		UserResponse response = staffManagementService.inviteStaff(request);
		return ResponseEntity.created(URI.create("/api/v1/staff/" + response.id())).body(response);
	}
}
