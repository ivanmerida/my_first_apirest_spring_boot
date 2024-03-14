package com.medevweb.backend.models.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="regions")
public class Region implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // AUTOINCREMENTAL
	@Column(name = "region_id")
	private Long regionId;
	
	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}



	private String name;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	private static final long serialVersionUID = 1L;

}
