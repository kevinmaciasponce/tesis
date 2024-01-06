package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.PlantillaDocumento;

public interface PlantillaDocumentoService {
	PlantillaDocumento consultaPlantillaDocumento(String id);
	List<PlantillaDocumento> consultaPlantillasDocumentos();
}
