package com.multiplos.cuentas.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.multiplos.cuentas.pojo.documento.DocumentoSolicitudResponse;
import com.multiplos.cuentas.pojo.documento.MultiplesDocumentosRequest;
import com.multiplos.cuentas.services.BlobStorageService;

@Service
public class BlobStorageServiceImpl implements BlobStorageService {

	private static final Logger LOG = LoggerFactory.getLogger(BlobStorageServiceImpl.class);
    private AzureStorageParam azureStorageParam;
	
    @Autowired
	public BlobStorageServiceImpl(AzureStorageParam azureStorageParam) {
		this.azureStorageParam = azureStorageParam;
	}

	@Override
	public DocumentoSolicitudResponse[] uploadMultipleFile(MultiplesDocumentosRequest[] multipartFiles, String nameContainer) {
		DocumentoSolicitudResponse[] respuesta =  new DocumentoSolicitudResponse[6];
		DocumentoSolicitudResponse dc = new DocumentoSolicitudResponse();
		 int i=0;
		 try {
            CloudBlobContainer cloudBlobContainer = this.getAzureContainer(azureStorageParam.getStorageConnectionString(), nameContainer);
            if(cloudBlobContainer == null) {
            	dc.setNombre("error");
            	dc.setRuta("Error al obtener el contenedor");
            	respuesta[0]=dc;
                return respuesta;
            }
            for (MultiplesDocumentosRequest multipartFile : multipartFiles) {
            	DocumentoSolicitudResponse dci = new DocumentoSolicitudResponse();
            	CloudBlockBlob blob = cloudBlobContainer.getBlockBlobReference(multipartFile.getFileName());
	            blob.getProperties().setContentType(multipartFile.getMultipartFiles().getContentType());
	            blob.upload(multipartFile.getMultipartFiles().getInputStream(), -1);
	            dci.setNombre(multipartFile.getFileName());
            	dci.setRuta(blob.getUri().toString());
            	LOG.error(dci.getRuta());
            	respuesta[i]=dci;
            	i++;
            	
            }
            i=0;
        } catch (URISyntaxException | StorageException | IOException e) {
        	LOG.error("uploadFile - Error al cargar archivo "+e.getMessage());
        	dc.setNombre("error");
        	dc.setRuta("Error al cargar archivo");
        	respuesta[0]=dc;
            return respuesta;
        }
        return respuesta;
	}
	
	
	

	@Override
	public String uploadFile(MultipartFile multipartFile, String fileName, String nameContainer)throws Exception {
		String respuesta;
        try {
            CloudBlobContainer cloudBlobContainer = this.getAzureContainer(azureStorageParam.getStorageConnectionString(), nameContainer);
            if(cloudBlobContainer == null) {
            
                throw new Exception( "Error al obtener el contenedor");
            }
        	
            CloudBlockBlob blob = cloudBlobContainer.getBlockBlobReference(fileName);
            blob.getProperties().setContentType(multipartFile.getContentType());
            blob.upload(multipartFile.getInputStream(), -1);
            respuesta = blob.getUri().toString();
        } catch (Exception e) {
        	LOG.error("uploadFile - Error al cargar archivo "+e.getMessage());
        	throw new Exception( "Error al cargar archivo"+e.getMessage());
        }
        return respuesta;
	}
	
	@Override
	public String uploadForByteArray(ByteArrayInputStream byteArrayInputStream, String contentType, String fileName, String nameContainer) {
		String respuesta;
        try {
            CloudBlobContainer cloudBlobContainer = this.getAzureContainer(azureStorageParam.getStorageConnectionString(), nameContainer);
            if(cloudBlobContainer == null) {
                return "Error al obtener el contenedor";
            }
        	
            CloudBlockBlob blob = cloudBlobContainer.getBlockBlobReference(fileName);
            blob.getProperties().setContentType(contentType);
            blob.upload(byteArrayInputStream, -1);
            respuesta = blob.getUri().toString();
            
        } catch (URISyntaxException | StorageException | IOException e) {
        	LOG.error("uploadForByteArray - Error al cargar archivo "+e.getMessage());
        	return "Error al cargar archivo";
        }
        return respuesta;
	}
	
	public CloudBlobContainer getAzureContainer(String storageConnectionString, String containerReference) {
	    CloudStorageAccount storageAccount;
	    CloudBlobClient blobClient;
	    CloudBlobContainer container = null;
	    try {
	        storageAccount = CloudStorageAccount.parse(storageConnectionString);
	        blobClient = storageAccount.createCloudBlobClient();
	        container = blobClient.getContainerReference(containerReference);
	        container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());

	    } catch (Exception e) {
	        LOG.error("Error al obtener contendor blog de azure: ", e.getMessage());
	    }
		return container;
	}

	
}
