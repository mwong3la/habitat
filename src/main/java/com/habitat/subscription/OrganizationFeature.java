package com.habitat.subscription;

import com.habitat.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
		name = "organization_features",
		uniqueConstraints = @UniqueConstraint(name = "uk_organization_feature", columnNames = {"organization_id", "feature_id"}))
public class OrganizationFeature {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "feature_id", nullable = false)
	private Feature feature;

	@Column(nullable = false)
	private Instant enabledAt = Instant.now();

	protected OrganizationFeature() {
	}

	public OrganizationFeature(Organization organization, Feature feature) {
		this.organization = organization;
		this.feature = feature;
	}

	public UUID getId() {
		return id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Feature getFeature() {
		return feature;
	}
}
