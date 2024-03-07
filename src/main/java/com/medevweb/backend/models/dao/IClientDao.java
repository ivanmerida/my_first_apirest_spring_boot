package com.medevweb.backend.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.medevweb.backend.models.entity.Client;

public interface IClientDao extends CrudRepository<Client, Long>{

}
