package com.multiplos.cuentas.services;

import java.io.ByteArrayOutputStream;

public interface GeneraPdfService {

	ByteArrayOutputStream getLicitudFondoPDF(String identificacion) throws Exception;
	
}
