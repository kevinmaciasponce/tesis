package com.multiplos.cuentas.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.multiplos.cuentas.models.File;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.services.FileService;


@RestController
@RequestMapping("/multiplo")
public class FileController {
	
	private FileService fileService;
	
	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping(value="/documentos")
	public ResponseEntity<Response> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws Exception {
		fileService.save(files);
		return ResponseEntity.status(HttpStatus.OK).body(new Response("Los archivos fueron cargados correctamente"));
	}

	@GetMapping(value="/documentos/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) throws Exception {
		Resource resource = fileService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	@GetMapping(value="/documentos")
	public ResponseEntity<List<File>> getAllFiles() throws Exception {
		List<File> files = fileService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder.fromMethodName(FileController.class, "getFile", path.getFileName().toString()).build().toString();
			
			return new File(filename, url);
		}).collect(Collectors.toList());
		
		return ResponseEntity.status(HttpStatus.OK).body(files);
	}

}
