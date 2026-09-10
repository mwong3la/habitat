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
@Table(name = "owners")
public class Owner {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@Column(nullable = false, length = 160)
	private String fullName;

	@Column(nullable = false, length = 180)
	private String email;

	@Column(length = 40)
	private String phoneNumber;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	protected Owner() {
	}

	public Owner(Organization organization, String fullName, String email, String phoneNumber) {
		this.organization = organization;
		this.fullName = fullName;
		this.email = email.toLowerCase();
		this.phoneNumber = phoneNumber;
	}

	public UUID getId() {
		return id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
