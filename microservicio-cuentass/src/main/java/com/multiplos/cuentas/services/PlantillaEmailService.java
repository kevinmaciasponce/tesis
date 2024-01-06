package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.PlantillaEmail;

public interface PlantillaEmailService {
	
	PlantillaEmail consultaPlantillaEmail(String id)throws Exception ;
	List<PlantillaEmail> consultaPlantillasEmails();
}
