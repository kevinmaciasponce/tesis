package com.multiplos.cuentas.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.reportes.ReporteGeneral;
import com.multiplos.cuentas.reportes.ReportePorAnio;
import com.multiplos.cuentas.reportes.ReportePorProyecto;
import com.multiplos.cuentas.reportes.fechaValue;
import com.multiplos.cuentas.services.ConciliacionesService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.ReportesServices;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.services.impl.SolicitudServiceImpl;

@RestController
@RequestMapping("${route.service.contextPath}")
public class ReportesController {
	private static final Logger LOG = LoggerFactory.getLogger(SolicitudServiceImpl.class);
	
	private SolicitudService solicitudService;
	private TransaccionService transaccionService;
	private ConciliacionesService csvService;
	private ProyectoService proyectoService;
	private ReportesServices reportesServices;
	
	public ReportesController(SolicitudService solicitudService,
			TransaccionService transaccionService,
			ConciliacionesService csvService,
			ProyectoService proyectoService,
			ReportesServices reportesServices) {
		this.solicitudService= solicitudService;
		this.transaccionService=transaccionService;
		this.csvService= csvService;
		this.proyectoService= proyectoService;
		this.reportesServices=reportesServices;}
	
	
	
	//@PostMapping("/loquesea2")
	@PostMapping("consultas/reportes/cuotas/porMeses")
    public ResponseEntity<?> reporteCuotasPorMeses(@RequestParam(required=false) Integer anio,@RequestParam(required=false) int[] meses,@RequestParam(required=false) String[] proyectos,@RequestParam String identificacion ) {
		List<fechaValue> fechas= new ArrayList<>();
		List<ReportePorProyecto> listaProyecto = new ArrayList<>();
		ReportePorProyecto proyect;
		fechaValue fechaIto;
		
		if(proyectos.length>0) {
			for(String cod:proyectos) {
				proyect= new ReportePorProyecto();
				proyect.setCodProyect(cod);
				listaProyecto.add(proyect);
			}
		}
		
		int tam;
		LOG.warn(""+anio);
		if(anio!=null && meses.length==0 ) {
			LOG.warn("entro 1"+anio);
			if(meses.length==0) { 
				 for (int i =1 ; i <= 12; i++) {
					 fechaIto= new fechaValue();
					 fechaIto.setAnio(anio);
					 fechaIto.setMes(i);
					 fechas.add(fechaIto);
				    };
				}
		}else if(anio==null && meses.length>0 ) {
			
			LocalDate fechaLocal=LocalDate.now();
			anio=fechaLocal.getYear();
			LOG.warn("entro 2"+anio);
			for(int mes: meses) { 
				 fechaIto= new fechaValue();
				 fechaIto.setAnio(anio);
				 fechaIto.setMes(mes);
				 fechas.add(fechaIto);
			}
		
		}else if(anio!=null && meses.length>0) {
			LOG.warn("entro 3"+anio);
			for(int mes: meses) { 
				 fechaIto= new fechaValue();
				 fechaIto.setAnio(anio);
				 fechaIto.setMes(mes);
				 fechas.add(fechaIto);
			}
		}else if(anio==null && meses.length==0) {
			LocalDate fechaLocal=LocalDate.now();
			anio=fechaLocal.getYear();
			LOG.warn("entro 4 "+anio);
			int mes= fechaLocal.getMonthValue();
			for(int i=0;i<12;i++) {
				 fechaIto= new fechaValue();
				 if(mes>12) {mes=1;anio++;}
				 fechaIto.setAnio(anio);
				 fechaIto.setMes(mes);
				 fechas.add(fechaIto);
				 mes++;
			}
			
		}
		
		
		ReporteGeneral report= new ReporteGeneral();
		try {
			report= this.reportesServices.reportePorfechasInvestor(fechas,listaProyecto, identificacion);
		}catch (Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage())); }
		
		
		if(report.getReportes().isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(report);
    }
	
	//@PostMapping("loquesea2")
	@PostMapping("consultas/reportes/cuotas/Mora/porMeses")
	public ResponseEntity<?> reporteCuotasEnMora(@RequestParam int[] meses, @RequestParam Integer anio ,@RequestParam String[] codProyect ) {
		if(anio==null) {return ResponseEntity.badRequest().body(new BadResponse("Debe seleccionar un año"));}
		if(meses.length==0) { 
			meses= new int[12];
			 for (int i = 0; i <= 11; i++) {
				meses[i]=i+1; 
			    };
			}
		ReportePorAnio report=null;
		try {
			report= this.reportesServices.reportePorProyectosMora(meses, anio, codProyect);
//			if(report==null) {return ResponseEntity.noContent().build();}
		}catch (Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage())); }
		return ResponseEntity.ok(report);
	}
	
	//@PostMapping("/loquesea2")
	@PostMapping("consultas/reportes/cuotas/investors")
	    public ResponseEntity<?> reporteCuotasPorInversionita(@RequestParam(required=false) int[] meses, @RequestParam(required=false) Integer anio ,@RequestParam String[] identificaciones ) {
			if(anio==null) {return ResponseEntity.internalServerError().body(new BadResponse("Debe seleccionar un año"));}
			if(meses.length==0) { 
				meses= new int[12];
				 for (int i = 0; i <= 11; i++) {
					meses[i]=i+1; 
				    };
				}
			ReportePorAnio report= new ReportePorAnio();
			try {
				report= this.reportesServices.GerenteReportePorfechasInvestor(meses, anio, identificaciones);
			}catch (Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage())); }
			
			
			if(report.getReportes().isEmpty()) {
				return ResponseEntity.noContent().build();
			}
	        return ResponseEntity.ok(report);
	    }
	
}
