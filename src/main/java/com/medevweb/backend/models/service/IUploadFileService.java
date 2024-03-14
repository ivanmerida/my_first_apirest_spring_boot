package com.medevweb.backend.models.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
	
	public Resource load(String nameImage) throws MalformedURLException;
	public String copy(MultipartFile file) throws IOException;
	public boolean delete(String nameImage);
	public Path getPath(String nameImage);

}
