package com.medevweb.backend.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service // INDICAR QUE ES UN SERVICIO Y UN PINS DE SPRING
public class UploadFileServiceImplements implements IUploadFileService {
	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImplements.class);
	private final static String DIRECTORY_UPLOAD = "uploads";

	@Override
	public Resource load(String nameImage) throws MalformedURLException {
		Path routeFile = getPath(nameImage);
		log.info(routeFile.toString());
		Resource resource = new UrlResource(routeFile.toUri());

		if (!resource.exists() && !resource.isReadable()) {
			throw new RuntimeException("Error does not load the image: " + nameImage);
//			routeFile = Paths.get("src/main/resources/static/images").resolve("no-usuario.png")

		}
		return resource;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		// UUID.randomUUID().toString() = para generar un string identificador random y
		// la img no se repita
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "");
		// El path se debe importar de file.io.path
		Path routeFile = getPath(fileName);
		log.info(routeFile.toString());

		Files.copy(file.getInputStream(), routeFile); // copia el archivo al directorio indicado

		return fileName;
	}

	@Override
	public boolean delete(String nameImage) {

		if (nameImage != null && nameImage.length() > 0) {
			Path routePreviusImage = Paths.get("uploads").resolve(nameImage).toAbsolutePath();
			File archivePreviusImage = routePreviusImage.toFile();
			if (archivePreviusImage.exists() && archivePreviusImage.canRead()) {
				archivePreviusImage.delete();
				return true;
			}
		}
		return false;
	}

	@Override
	public Path getPath(String nameImage) {
		// TODO Auto-generated method stub
		return Paths.get(DIRECTORY_UPLOAD).resolve(nameImage).toAbsolutePath();
	}

}
