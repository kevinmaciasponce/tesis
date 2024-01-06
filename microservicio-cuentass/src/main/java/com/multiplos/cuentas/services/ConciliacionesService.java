package com.multiplos.cuentas.services;


import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.ConciliacionXls;
import com.multiplos.cuentas.models.HistorialConciliacion;


public interface ConciliacionesService {

	String convertFile(String usuario,MultipartFile file)throws Exception ;
	String conciliarDatos()throws Exception ;
	String  aprobarConciliacion(String usuario) throws Exception;
	ConciliacionXls findById(Long id);
	ConciliacionXls consultarFileData()throws Exception;

}