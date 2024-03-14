package com.medevweb.backend.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import com.medevweb.backend.models.entity.Client;
import com.medevweb.backend.models.entity.Region;


// SE EXTIENDE JpaRepository para poder usar la paginación
public interface IClientDao extends JpaRepository<Client, Long>{
	
	@Query("from Region")
	public List<Region> findAllRegions();

}
