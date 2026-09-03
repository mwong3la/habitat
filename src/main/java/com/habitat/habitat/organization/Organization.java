package com.habitat.habitat.organization;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, length = 160)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private OrganizationType type;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "country_id", nullable = false)
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "base_currency_id", nullable = false)
	private Currency baseCurrency;

	@Column(nullable = false, length = 40)
	private String packageCode;

	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> enabledFeatures = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrganizationStatus status = OrganizationStatus.ONBOARDING;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	protected Organization() {
	}

	public Organization(String name, OrganizationType type, Country country, String packageCode, Set<String> enabledFeatures) {
		this.name = name;
		this.type = type;
		this.country = country;
		this.baseCurrency = country.getCurrency();
		this.packageCode = packageCode.toUpperCase();
		this.enabledFeatures = new HashSet<>(enabledFeatures);
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public OrganizationType getType() {
		return type;
	}

	public Country getCountry() {
		return country;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public Set<String> getEnabledFeatures() {
		return Set.copyOf(enabledFeatures);
	}

	public OrganizationStatus getStatus() {
		return status;
	}

	public void activate() {
		status = OrganizationStatus.LIVE;
	}
}
