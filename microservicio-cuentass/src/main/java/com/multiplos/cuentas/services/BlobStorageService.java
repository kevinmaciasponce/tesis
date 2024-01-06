package com.multiplos.cuentas.services;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.pojo.documento.DocumentoSolicitudResponse;
import com.multiplos.cuentas.pojo.documento.MultiplesDocumentosRequest;

public interface BlobStorageService {

	DocumentoSolicitudResponse[] uploadMultipleFile(MultiplesDocumentosRequest[] multipartFiles, String nameContainer);
	String uploadFile(MultipartFile multipartFile, String fileName, String nameContainer)throws Exception;
	String uploadForByteArray(ByteArrayInputStream byteArrayInputStream, String contentType, String fileName, String nameContainer);
}
