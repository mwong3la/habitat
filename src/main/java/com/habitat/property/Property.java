package com.habitat.property;

import com.habitat.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "properties")
public class Property {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(nullable = false, length = 220)
	private String addressLine1;

	@Column(nullable = false, length = 100)
	private String city;

	@Column(length = 100)
	private String region;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	protected Property() {
	}

	public Property(Organization organization, Owner owner, String name, String addressLine1, String city, String region) {
		this.organization = organization;
		this.owner = owner;
		this.name = name;
		this.addressLine1 = addressLine1;
		this.city = city;
		this.region = region;
	}

	public UUID getId() {
		return id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Owner getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public String getCity() {
		return city;
	}

	public String getRegion() {
		return region;
	}
}
