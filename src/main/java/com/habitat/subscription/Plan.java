package com.habitat.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "plans")
public class Plan {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 40)
	private String code;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false)
	private int displayOrder;

	protected Plan() {
	}

	public Plan(String code, String name, int displayOrder) {
		this.code = code.toUpperCase();
		this.name = name;
		this.displayOrder = displayOrder;
	}

	public UUID getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}
}
