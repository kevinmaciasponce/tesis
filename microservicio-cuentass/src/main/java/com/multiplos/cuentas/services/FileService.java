package com.multiplos.cuentas.services;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	
	void save(MultipartFile file) throws Exception;
	String saveDocumento(MultipartFile file, String nombreArchivo) throws Exception;

	Resource load(String name) throws Exception;
	
	void save(List<MultipartFile> files) throws Exception;
	
	Stream<Path> loadAll() throws Exception;
	
}
