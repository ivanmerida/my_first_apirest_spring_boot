package com.medevweb.backend.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medevweb.backend.models.entity.Client;
import com.medevweb.backend.models.entity.Region;
import com.medevweb.backend.models.service.IClientService;
import com.medevweb.backend.models.service.IUploadFileService;

import jakarta.validation.Valid;

/* SE TIENE QUE USAR EL REST CONTROLLER PORQUE ES UNA API - SI FUERA WEB
 * SERIA CONTROLLER NADA MÁS*/
@CrossOrigin(origins = { "http://localhost:4200" }) // PARA LOS CORS
@RestController
@RequestMapping("/api") // PRIMER NIVEL DE LA API
public class ClientRestController {

	@Autowired
	private IClientService clientService;

	@Autowired
	private IUploadFileService uploadService;

	// para generar el endpoint
	@GetMapping("/clients") // SEGUNDO NIVEL
	public List<Client> index() {
		return clientService.findAll();
	}

	// PARA PODER HACE LA PAGINACIÓN
	// TODAS LAS LIBRERIAS DE DATA DOMAIN SIRVE PARA LA PAGINACIÓN OJOOOOOO!!!
	@GetMapping("/clients/page/{page}") // SEGUNDO NIVEL
	public Page<Client> index(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 4);// seria 4 items por página
		return clientService.findAll(pageable);
	}

	@GetMapping("/clients/{id}")
//	@ResponseStatus(HttpStatus.OK) // CÓDIGO 200 pero es redundante asi que se omite
	// ResponseEntity = ES PARA MANEJAR ERRORES, POR EJEMPLO CUANDO EL ID NO EXISTE
	// LA PARTE QUE DEVULVE PUEDE SER GENERICO ES DECIR SE ESCRIBE EL SIMBOLO DE ?
	// OJO, LA PAGINACIÓN COMIENZA EN 0,1,2,3, ETC
	public ResponseEntity<?> show(@PathVariable Long id) {
		Client client = null;
		Map<String, Object> response = new HashMap<>();
		try {
			client = clientService.findById(id);
		} catch (DataAccessException e) {
			response.put("message", "Internal Server Error");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓDIGO 500 -
																										// NO ENCONTRADO
		}

		if (client == null) {
			response.put("message", "The client ID: ".concat(id.toString().concat(" does not exist in the DB")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // CÓDIGO 404 - NO
																							// ENCONTRADO
		}
		return new ResponseEntity<Client>(client, HttpStatus.OK);
	}

	@PostMapping("/clients")
	/* @ResponseStatus(HttpStatus.CREATED) // CÓDIGO 201 */
	// @Valid : hay que indicar la validacion, para que surta efecto en la clase
	// entity
	public ResponseEntity<?> create(@Valid @RequestBody Client client, BindingResult result) {
		Client newClient = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			// 400 - CUANDO FALLA UNA VALIDACION EN UN APP WEB CON HTTP
			/*
			 * List<String> errors = new ArrayList<>(); // Para mandar los errores for
			 * (FieldError error : result.getFieldErrors()) { errors.add("The field '"
			 * +error.getField()+"' " + error.getDefaultMessage()); }
			 */
			List<String> errors = result.getFieldErrors().stream()
					.map(error -> "The field '" + error.getField() + "' " + error.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("status", "error");
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST); //
		}

		try {
			newClient = clientService.save(client);

		} catch (DataAccessException e) {
			response.put("status", "error");
			response.put("code", "Internal Server Error - INSERT");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓD
		}

		response.put("status", "success");
		response.put("client", newClient);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); // save() SIRVE TANTO PARA INSERT
																						// COMO UPDATE
	}

	@PutMapping("/clients/{id}")
	/* @ResponseStatus(HttpStatus.CREATED) // CÓDIGO 201 */
	// Es importante que el binding sea despues de client
	public ResponseEntity<?> update(@Valid @RequestBody Client client, BindingResult result, @PathVariable Long id) {
		Client currentClient = clientService.findById(id);
		Client updatedClient = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(error -> "The field '" + error.getField() + "' " + error.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("status", "error");
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST); //
		}
		if (currentClient == null) {
			response.put("status", "error");
			response.put("message", "The client ID: ".concat(id.toString().concat(" does not exist in the DB")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // CÓDIGO 404 - NO
																							// ENCONTRADO
		}
		try {
			currentClient.setName(client.getName());
			currentClient.setSurname(client.getSurname());
			currentClient.setEmail(client.getEmail());
			currentClient.setCreatedAt(client.getCreatedAt());
//			currentClient.setRegion(client.getRegion());

			updatedClient = clientService.save(currentClient);

		} catch (DataAccessException e) {
			response.put("status", "error");
			response.put("code", "Internal Server Error - UPDATE");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓD
		}

		response.put("status", "success");
		response.put("client", updatedClient);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clients/{id}")
	/* @ResponseStatus(HttpStatus.NO_CONTENT) // CÓDIGO 204 - SIN CONTENIDO */
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Client currentClient = clientService.findById(id);

		Map<String, Object> response = new HashMap<>();
		if (currentClient == null) {
			response.put("status", "error");
			response.put("message", "The client ID: ".concat(id.toString().concat(" does not exist in the DB")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); // CÓDIGO 404 - NO //
																							// ENCONTRADO
		}
		try {
			Client client = clientService.findById(id);
			String namePreviousImage = client.getImage();
			uploadService.delete(namePreviousImage);
			clientService.delete(id);
		} catch (DataAccessException e) {
			response.put("status", "error");
			response.put("code", "Internal Server Error - DELETE");
			response.put("message", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); // CÓD
		}

		response.put("status", "success");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@PostMapping("/clients/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Client client = clientService.findById(id);

		if (!file.isEmpty()) {
			String fileName = null;

			try {
				fileName = uploadService.copy(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				response.put("status", "error");
				response.put("message", "Error upload image: ");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String namePreviousImage = client.getImage();
			uploadService.delete(namePreviousImage);

			client.setImage(fileName);
			clientService.save(client);
			response.put("status", "success");
			response.put("message", "You have succesfully uploaded the image: " + fileName);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// :.+ esta expresion regular indica que el parametro va incluir un punto y la
	// extension
	@GetMapping("/uploads/img/{nameImage:.+}")
	public ResponseEntity<Resource> viewImage(@PathVariable String nameImage) {

		Resource resource = null;

		try {
			resource = uploadService.load(nameImage);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// LAS CABECERAS SON PARA FORZAR LA DESCARGA
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	
	@GetMapping("/clients/regions")
	public List<Region> listRegions() {
		return clientService.findAllRegions();
	}

}
