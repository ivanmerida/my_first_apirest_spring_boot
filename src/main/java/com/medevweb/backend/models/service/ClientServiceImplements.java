package com.medevweb.backend.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medevweb.backend.models.dao.IClientDao;
import com.medevweb.backend.models.entity.Client;


@Service
public class ClientServiceImplements implements IClientService{

	
	// @Autowired = Inyección de dependencias
	@Autowired
	private IClientDao clientDao;
	
	// Para manejar anotaciones aunque ya viene implementado, se puede quitar
	@Transactional(readOnly = true)
	@Override
	public List<Client> findAll() {
		// TODO Auto-generated method stub
		return (List<Client>) clientDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Client findById(Long id) {
		// TODO Auto-generated method stub
		return clientDao.findById(id).orElse(null);
	}

	@Override
	@Transactional()
	public Client save(Client client) {
		// TODO Auto-generated method stub
		return clientDao.save(client);
	}

	@Override
	@Transactional()
	public void delete(Long id) {
		// TODO Auto-generated method stub
		clientDao.deleteById(id);
	}

}
