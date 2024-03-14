package com.medevweb.backend.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.medevweb.backend.models.entity.User;


// PODEMOS EXTENDER DE JPA - SOBRE TODO CUANDO VAMOS A PAGINAR 
public interface IUserDao extends CrudRepository<User, Long>{
	
//	public User findByUsernameAndEmail(String username, String email); para tenerlo como referencia
	public User findByUsername(String username);
	
	//@Query("SELECT u FROM User u WHERE u.username=?1 AND u.otro=?2") // PARA TENERLO DE REFERENCIA
	@Query("SELECT u FROM User u WHERE u.username=?1")
	public User findByUsernameQuery(String username);
}
