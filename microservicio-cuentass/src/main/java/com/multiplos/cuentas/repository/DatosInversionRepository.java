package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.DatosInversion;

public interface DatosInversionRepository extends JpaRepository<DatosInversion, Long>{
	DatosInversion findByIdDato(Long id);
	
	@Modifying
	@Query("update DatosInversion d set d.tablaAmortizacion = ?1 where d.idDato = ?2")
	void updateDatosTablaAmortizacion(boolean tablaAmortizacion, Long idDato);
	
	@Modifying
	@Query("update DatosInversion d set d.formulario = ?1 where d.idDato = ?2")
	void updateDatosFormulario(boolean formulario, Long idDato);
	
	@Modifying
	@Query("update DatosInversion d set d.documentacion = ?1 where d.idDato = ?2")
	void updateDatosDocumentacion(boolean documentacion, Long idDato);
	
	@Modifying
	@Query("update DatosInversion d set d.pago = ?1 where d.idDato = ?2")
	void updateDatosPago(boolean pago, Long idDato);
}
