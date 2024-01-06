package com.multiplos.cuentas.pojo.documento;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MultiplesDocumentosRequest {
	private MultipartFile multipartFiles;
	private String fileName;
}
