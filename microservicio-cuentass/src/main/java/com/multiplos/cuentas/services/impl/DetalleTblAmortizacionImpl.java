package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.repository.DetalleTblAmortizacionRepository;
import com.multiplos.cuentas.services.DetalleTblAmortizacionService;

@Service
public class DetalleTblAmortizacionImpl implements DetalleTblAmortizacionService{

	private static final Logger LOG = LoggerFactory.getLogger(DetalleTblAmortizacionImpl.class);
	private DetalleTblAmortizacionRepository detTblRepository;
	
	@Autowired
	public DetalleTblAmortizacionImpl(DetalleTblAmortizacionRepository detTblRepository) {
		this.detTblRepository = detTblRepository;
	}
	
	@Override
	@Transactional
	public DetalleAmortizacion save(DetalleAmortizacion detTblAmortizacion) {
		return detTblRepository.save(detTblAmortizacion);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DetalleAmortizacion> consultaDetalleTblAmortizacion(Long idTblAmortizacion) {
		return detTblRepository.consultaDetalleTblAmortizacion(idTblAmortizacion, Sort.by("idDetAmortizacion").ascending());
	}
	
	@Override
	@Transactional
	public String updateDetalleAmortizacion(Long idTblAmortizacion) {
		List<DetalleAmortizacion> listDetalle = null;
		String respuesta = null;
		try{
			listDetalle = new ArrayList<>();
			listDetalle = this.consultaDetalleTblAmortizacion(idTblAmortizacion);
			if(!listDetalle.isEmpty()) {
				for(DetalleAmortizacion dt: listDetalle) {
					dt.setEstado("I");
					detTblRepository.save(dt);
				}
				respuesta = "ok";
			}else {
				respuesta = "No exite detalle tabla de amortización";
				LOG.info("No exite detalle tabla de amortización idTblAmortizacion: "+idTblAmortizacion);
			}
		}catch(Exception e) {
			LOG.error("update: Error idTblAmortizacion: "+idTblAmortizacion+"-"+e.getMessage());
			return "Error en actualización del detalle tabla de amortización";
		}
		return respuesta;
	}

}
