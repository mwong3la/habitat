package com.habitat.habitat.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "features")
public class Feature {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 80)
	private String code;

	@Column(nullable = false, length = 160)
	private String name;

	protected Feature() {
	}

	public Feature(String code, String name) {
		this.code = code;
		this.name = name;
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
}
