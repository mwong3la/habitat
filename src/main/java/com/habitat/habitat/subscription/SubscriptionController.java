package com.habitat.habitat.subscription;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

	private final SubscriptionService subscriptionService;

	public SubscriptionController(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	@GetMapping
	public OrganizationSubscriptionResponse current() {
		return subscriptionService.currentSubscription();
	}

	@PatchMapping("/plan")
	public OrganizationSubscriptionResponse changePlan(@Valid @RequestBody ChangePlanRequest request) {
		return subscriptionService.changePlan(request);
	}
}
