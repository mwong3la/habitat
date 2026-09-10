package com.habitat.property;

import com.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
		name = "units",
		uniqueConstraints = @UniqueConstraint(name = "uk_property_unit_name", columnNames = {"property_id", "name"}))
public class Unit {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "property_id", nullable = false)
	private Property property;

	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal monthlyRent;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private UnitStatus status = UnitStatus.VACANT;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	protected Unit() {
	}

	public Unit(Organization organization, Property property, String name, BigDecimal monthlyRent) {
		this.organization = organization;
		this.property = property;
		this.name = name;
		this.monthlyRent = monthlyRent;
	}

	public UUID getId() {
		return id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Property getProperty() {
		return property;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getMonthlyRent() {
		return monthlyRent;
	}

	public UnitStatus getStatus() {
		return status;
	}

	public void markListed() {
		if (status != UnitStatus.VACANT) {
			throw new BusinessRuleViolationException("Only vacant units can be listed");
		}
		status = UnitStatus.LISTED;
	}

	public void markOccupied() {
		if (status == UnitStatus.OCCUPIED) {
			throw new BusinessRuleViolationException("Unit is already occupied");
		}
		status = UnitStatus.OCCUPIED;
	}
}
