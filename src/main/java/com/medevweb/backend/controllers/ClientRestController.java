package com.medevweb.backend.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medevweb.backend.models.entity.Client;
import com.medevweb.backend.models.service.IClientService;

/* SE TIENE QUE USAR EL REST CONTROLLER PORQUE ES UNA API - SI FUERA WEB
 * SERIA CONTROLLER NADA MÁS*/
@CrossOrigin(origins= {"http://localhost:4200"}) // PARA LOS CORS
@RestController
@RequestMapping("/api") // PRIMER NIVEL DE LA API
public class ClientRestController {
	
	@Autowired
	private IClientService clientService;
	// para generar el endpoint
	@GetMapping("/clients") // SEGUNDO NIVEL
	public List<Client> index(){
		return clientService.findAll();
	}
	
	@GetMapping("/clients/{id}") 
//	@ResponseStatus(HttpStatus.OK) // CÓDIGO 200 pero es redundante asi que se omite
	//ResponseEntity = ES PARA MANEJAR ERRORES, POR EJEMPLO CUANDO EL ID NO EXISTE
	// LA PARTE QUE DEVULVE PUEDE SER GENERICO ES DECIR SE ESCRIBE EL SIMBOLO DE ?
	public ResponseEntity<?> show(@PathVariable Long id) {
		Client client = null;
		Map<String, Object> response = new HashMap<>();
		try {
			client = clientService.findById(id);
		}catch(DataAccessException e) {
			response.put("message", "Internal Server Error");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓDIGO 500 - NO ENCONTRADO
		}

		
		
		if(client == null) {
			 response.put("message", "The client ID: ".concat(id.toString().concat(" does not exist in the DB")));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // CÓDIGO 404 - NO ENCONTRADO
		}
		return new ResponseEntity<Client>(client, HttpStatus.OK);
	}
	
	@PostMapping("/clients")
	/*@ResponseStatus(HttpStatus.CREATED) // CÓDIGO 201*/
	public ResponseEntity<?> create(@RequestBody Client client) {
		Client newClient = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			newClient = clientService.save(client);
	
		}catch(DataAccessException e) {
			response.put("status", "error");
			response.put("code", "Internal Server Error - INSERT");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓD
		}
		
		response.put("status", "success");
		response.put("client", newClient);
		
		return new ResponseEntity<Map<String, Object>>(response , HttpStatus.CREATED); // save() SIRVE TANTO PARA INSERT COMO UPDATE
	}
	
	
	
	
	@PutMapping("/clients/{id}")
	/*@ResponseStatus(HttpStatus.CREATED) // CÓDIGO 201*/
	public ResponseEntity<?> update(@RequestBody Client client, @PathVariable Long id) {
		Client currentClient = clientService.findById(id);
		Client updatedClient = null;
		Map<String, Object> response = new HashMap<>();
		
		if(currentClient == null) {
			response.put("status", "error");
			 response.put("message", "The client ID: ".concat(id.toString().concat(" does not exist in the DB")));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // CÓDIGO 404 - NO ENCONTRADO
		}
		try {
			currentClient.setName(client.getName());
			currentClient.setSurname(client.getSurname());
			currentClient.setEmail(client.getEmail());
			currentClient.setCreatedAt(client.getCreatedAt());
			
			updatedClient = clientService.save(currentClient);
			
		}catch (DataAccessException e) {
			response.put("status", "error");
			response.put("code", "Internal Server Error - UPDATE");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓD
		}
		
		
		response.put("status", "success");
		response.put("client", updatedClient);
		
		return new ResponseEntity<Map<String, Object>>(response , HttpStatus.CREATED); 
	}
	@DeleteMapping("/clients/{id}")
	/*@ResponseStatus(HttpStatus.NO_CONTENT) // CÓDIGO 204 - SIN CONTENIDO*/
	public ResponseEntity<?>  delete(@PathVariable Long id) {
		Client currentClient = clientService.findById(id);
		
		
		Map<String, Object> response = new HashMap<>();
		if(currentClient == null) {
			response.put("status", "error");
			 response.put("message", "The client ID: ".concat(id.toString().concat(" does not exist in the DB")));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // CÓDIGO 404 - NO ENCONTRADO
		}
		try {
			clientService.delete(id);
		}catch (DataAccessException e) {
			response.put("status", "error");
			response.put("code", "Internal Server Error - DELETE");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓD
		}

		response.put("status", "success");
		return new ResponseEntity<Map<String, Object>>(response , HttpStatus.OK); 
	}

}
