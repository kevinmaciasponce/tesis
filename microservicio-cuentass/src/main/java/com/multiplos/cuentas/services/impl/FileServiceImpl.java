package com.multiplos.cuentas.services.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.services.FileService;

@Service
public class FileServiceImpl implements FileService {

	private final Path rootFolder = Paths.get("uploads");
	//private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Override
	public void save(MultipartFile file) throws Exception {
		Files.copy(file.getInputStream(), this.rootFolder.resolve(file.getOriginalFilename()));
	}

	@Override
	public Resource load(String name) throws Exception {
		Path file = rootFolder.resolve(name);
		Resource resource = new UrlResource(file.toUri());
		return resource;
	}

	@Override
	public void save(List<MultipartFile> files) throws Exception {
		for (MultipartFile file : files) {
			this.save(file);
		}
	}

	@Override
	public Stream<Path> loadAll() throws Exception {
		return Files.walk(rootFolder, 1).filter(path -> !path.equals(rootFolder)).map(rootFolder::relativize);
	}
	
	@Override
	public String saveDocumento(MultipartFile file, String nombreArchivo) throws Exception {
		Path dest = Paths.get("D:\\upload\\" + nombreArchivo);
	    file.transferTo(dest);
		return dest.toAbsolutePath().toString();
	}

}
