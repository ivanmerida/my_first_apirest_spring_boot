package com.medevweb.backend.models.service;

import java.util.List;

import com.medevweb.backend.models.entity.Client;

public interface IClientService {
	
	/* ESTA ES UNA CLASE ABSTRACTA ENTONCES
	 * SOLO DEFINIMOS LOS METODOS QUE VAMOS A NECESITAR
	 * PARA QUE LAS CLASES QUE IMPLEMENTEN ESTA CLASE
	 * LOS TENGA QUE DECLARAR
	*/
	public List<Client> findAll();
	
	public Client findById(Long id);
	
	public Client save(Client client);
	
	public void delete(Long id);
	
	
	

}
