package com.medevweb.backend.models.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;

/*
 *Toda clase entity es mejor que implemente Serializable
 *ya que así se puede convertir de java a un estructura json
 *y poder guardar o almacenar en un sesion http
 **/
@Entity
@Table(name="users")
public class User implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true, length=20)
	private String username;
	@Column(length=60)
	private String password;
	private Boolean enabled;
	
	
	// LA TABLA INTERMEDIA SE CREA SOLO, PORQUE LA RELACION ES DE MUCHOS A MUCHOS
	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL) // PARA QUE ELIMINE EN CASCADA
	@JoinTable(name="users_roles", 
	joinColumns = @JoinColumn(name="user_id"), 
	inverseJoinColumns = @JoinColumn(name="role_id"),
	uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id" })}
	)
	private List<Role> roles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	

}
