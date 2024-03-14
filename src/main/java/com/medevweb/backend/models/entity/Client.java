package com.medevweb.backend.models.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name="clients")
public class Client implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message = "No puede estár vacio")  // NO VACIO   message= para customizar los mensajes que devuelve la api
	@Size(min=4, max=12, message="El tamaño tiene que estar entre 4 y 12") // TAMAÑO DEL STRING
	@Column(nullable=false) // NO NULO
	private String name;

	@NotEmpty // NO VACIO
	private String surname;
	@NotEmpty // NO VACIO
	@Email // TIPO EMAIL
	@Column(nullable=false, unique=true)
	private String email;
	/* EL PARAMETRO NAME SE USA CUANDO EL CAMPO DE LA DB Y EL ATRIBUTO DE ESTA CLASE
	 * SON DISTINTOS O SE ESCRIBEN DISTINTO*/
	
	@NotNull()
	@Column(name = "created_at")
	@Temporal(TemporalType.DATE) 
	private Date createdAt;	
	
	
	@PrePersist // Asigna la fecha actual lo puedes usar cuando quieres agregar por form tu fecha
	public void prePersist() {
		createdAt = new Date();
	}
	
	private String image;
	/*
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY) // MUCHOS CLIENTES TENDRA UNA MISMA REGION  LAZY = PARA LA CARGA PERESOZA
	@JoinColumn(name="region_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // PARA IGNORAR ESTAS PROPIEDADES Y QUE NO LANCE UN ERROR +	// PORQUE ANDAMOS USANDO LAZY
	private Region region;*/
	
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
		
//	public Region getRegion() {
//		return region;
//	}
//	public void setRegion(Region region) {
//		this.region = region;
//	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

}
