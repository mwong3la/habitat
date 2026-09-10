package com.habitat.subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(
		name = "plan_features",
		uniqueConstraints = @UniqueConstraint(name = "uk_plan_feature", columnNames = {"plan_id", "feature_id"}))
public class PlanFeature {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "feature_id", nullable = false)
	private Feature feature;

	protected PlanFeature() {
	}

	public PlanFeature(Plan plan, Feature feature) {
		this.plan = plan;
		this.feature = feature;
	}

	public UUID getId() {
		return id;
	}

	public Plan getPlan() {
		return plan;
	}

	public Feature getFeature() {
		return feature;
	}
}
