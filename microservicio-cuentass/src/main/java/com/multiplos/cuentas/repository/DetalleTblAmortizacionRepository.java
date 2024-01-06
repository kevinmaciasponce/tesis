package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.DetalleAmortizacion;

public interface DetalleTblAmortizacionRepository  extends JpaRepository<DetalleAmortizacion, Long>{
	
	@Query(value = "select t from DetalleAmortizacion t where t.tablaAmortizacion.idTblAmortizacion=?1 and t.estado='A'")
	List<DetalleAmortizacion> consultaDetalleTblAmortizacion(Long idTblAmortizacion, Sort sort);

	@Query(value = "select t from DetalleAmortizacion t, Solicitud dt "
			+ "where dt.numeroSolicitud=?1 and t.estado='A'"
			+" and t.tablaAmortizacion.idTblAmortizacion = dt.amortizacion.idTblAmortizacion "
			+" and t.cuota = ?2 "
			+" and dt.amortizacion.tipoTabla.idTipoTabla = 1"
			)
	DetalleAmortizacion consultaDetalleTblAmortizacionxnumSolicitud(Long numSolicitud, int cuota);
}
