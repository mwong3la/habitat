package com.habitat.habitat.organization;

import com.habitat.habitat.identity.RoleName;
import com.habitat.habitat.identity.TenantContext;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

	private final OrganizationOnboardingService onboardingService;
	private final TenantContext tenantContext;

	public OrganizationController(OrganizationOnboardingService onboardingService, TenantContext tenantContext) {
		this.onboardingService = onboardingService;
		this.tenantContext = tenantContext;
	}

	@PostMapping
	public ResponseEntity<OrganizationResponse> register(@Valid @RequestBody RegisterOrganizationRequest request) {
		OrganizationResponse response = onboardingService.register(request);
		return ResponseEntity.created(URI.create("/api/v1/organizations/" + response.id())).body(response);
	}

	@GetMapping("/me")
	@Transactional(readOnly = true)
	public OrganizationResponse currentOrganization() {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		return OrganizationResponse.from(tenantContext.currentOrganization());
	}
}
