package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.PlantillaDocumento;

public interface PlantillaDocumentoRepository extends JpaRepository<PlantillaDocumento, String> {
	
	PlantillaDocumento findByIdPlantilla(String id);

}
