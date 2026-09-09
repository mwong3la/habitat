package com.habitat.habitat.property;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController {

	private final PropertyManagementService propertyManagementService;

	public OwnerController(PropertyManagementService propertyManagementService) {
		this.propertyManagementService = propertyManagementService;
	}

	@PostMapping
	public ResponseEntity<OwnerResponse> create(@Valid @RequestBody CreateOwnerRequest request) {
		OwnerResponse response = propertyManagementService.createOwner(request);
		return ResponseEntity.created(URI.create("/api/v1/owners/" + response.id())).body(response);
	}
}
