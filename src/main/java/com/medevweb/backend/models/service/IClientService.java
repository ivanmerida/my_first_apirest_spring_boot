package com.medevweb.backend.models.service;

import java.util.List;

// INICIO - PARA USAR LA PAGINACIÓN
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// FIN - PARA USAR LA PAGINACIÓN

import com.medevweb.backend.models.entity.Client;
import com.medevweb.backend.models.entity.Region;

public interface IClientService {
	
	/* ESTA ES UNA CLASE ABSTRACTA ENTONCES
	 * SOLO DEFINIMOS LOS METODOS QUE VAMOS A NECESITAR
	 * PARA QUE LAS CLASES QUE IMPLEMENTEN ESTA CLASE
	 * LOS TENGA QUE DECLARAR
	*/
	public List<Client> findAll();
	
	
	// PARA LA PAGINACIÓN
	public Page<Client> findAll(Pageable pageable);
	
	public Client findById(Long id);
	
	public Client save(Client client);
	
	public void delete(Long id);
	
	public List<Region> findAllRegions();
	
	

}
