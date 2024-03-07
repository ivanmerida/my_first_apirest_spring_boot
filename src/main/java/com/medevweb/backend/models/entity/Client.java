package com.medevweb.backend.models.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;


@Entity
@Table(name="clients")
public class Client implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String name;

	private String surname;
	@Column(nullable=false, unique=true)
	private String email;
	/* EL PARAMETRO NAME SE USA CUANDO EL CAMPO DE LA DB Y EL ATRIBUTO DE ESTA CLASE
	 * SON DISTINTOS O SE ESCRIBEN DISTINTO*/
	@Column(name = "created_at")
	@Temporal(TemporalType.DATE)
	private Date createdAt;	
	
	@PrePersist
	public void prePersist() {
		createdAt = new Date();
	}
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

}
