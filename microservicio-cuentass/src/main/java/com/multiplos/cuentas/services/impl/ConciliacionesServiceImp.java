package com.multiplos.cuentas.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.transaccion.conciliaTransaccion;
import com.multiplos.cuentas.models.ConciliacionAprobada;
import com.multiplos.cuentas.models.ConciliacionAprobadaDetalle;
import com.multiplos.cuentas.models.ConciliacionXls;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.ConciliaXlsDetalle;
import com.multiplos.cuentas.models.HistorialConciliacion;
import com.multiplos.cuentas.models.HistorialConciliacionDetalle;
import com.multiplos.cuentas.models.HistorialDeSolicitud;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.repository.ConciliacionesAprobadasRepository;
import com.multiplos.cuentas.repository.HistorialConciliacionRepository;
import com.multiplos.cuentas.repository.ProyectoRepository;
import com.multiplos.cuentas.repository.SolicitudRepository;
import com.multiplos.cuentas.repository.TransaccionRepository;
import com.multiplos.cuentas.repository.ConciliaXlsRepository;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.ConciliacionesService;


@Service
public class ConciliacionesServiceImp implements ConciliacionesService {
	
	private ConciliaXlsRepository csvRepository ;
	private HistorialConciliacionRepository  hcRepository;
	private TransaccionRepository transaccionRepository;
	private SolicitudService solService;
	private SolicitudRepository solRepository;
	private ConciliacionesAprobadasRepository repositoryAprobadas;
	private ProyectoRepository repositoryProyecto;
	
	private static final Logger LOG = LoggerFactory.getLogger(SolicitudServiceImpl.class);
	@Autowired
	public ConciliacionesServiceImp(HistorialConciliacionRepository  historyConcialiacion,
			ConciliaXlsRepository csvRepository,
			TransaccionRepository transaccionRepository,
			SolicitudService solService,
			SolicitudRepository solRepository,
			ConciliacionesAprobadasRepository repositoryAprobadas,
			ProyectoRepository repositoryProyecto) {
		this.csvRepository = csvRepository;
		this.transaccionRepository= transaccionRepository;
		this.hcRepository  =historyConcialiacion;
		this.solService = solService;
		this.solRepository= solRepository;
		this.repositoryAprobadas = repositoryAprobadas;
		this.repositoryProyecto= repositoryProyecto;	}
	
	@Override
	@Transactional
	public ConciliacionXls findById(Long id) {
		ConciliacionXls conciliacion;
		Optional<ConciliacionXls>  aux =csvRepository.findById(id) ;
		conciliacion = aux.get();
		return conciliacion;
	}
	@Override
	@Transactional
	public String convertFile(String usuario,MultipartFile file) throws Exception {
		 ConciliacionXls xls =null;
		 BigDecimal total= new BigDecimal(0);
		/*
			csv = csvRepository.findActive();
			if(csv!=null) {
			throw new Exception("Existe Archivo Activo");
			}
			*/
		xls = new ConciliacionXls();
		ConciliacionXls aux;
		xls.setUsuario(usuario);
		List<ConciliaXlsDetalle> data = new ArrayList<>();
		InputStream in = file.getInputStream();
		Workbook workbook = new XSSFWorkbook(in);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		int index =0;
			while(rows.hasNext()) {
				Row row = rows.next();
				if(index !=0) {
					ConciliaXlsDetalle jsonObject = new ConciliaXlsDetalle();
					jsonObject.setIdConciliacion(xls);
					jsonObject.setNombreBanco(row.getCell(0).getStringCellValue());
					jsonObject.setNumeroCuenta(row.getCell(1).getStringCellValue());			
					//jsonObject.setFechaEfectivo(row.getCell(2).getStringCellValue());
					jsonObject.setFechaEfectivo(LocalDate.parse(row.getCell(2).getStringCellValue()));
					jsonObject.setNumeroComprobante(row.getCell(3).getStringCellValue());
					jsonObject.setMonto(new BigDecimal(row.getCell(4).getNumericCellValue()));
					jsonObject.setLugar(row.getCell(5).getStringCellValue());
					jsonObject.setTipoTransaccion(row.getCell(6).getStringCellValue());
					jsonObject.setConcepto(row.getCell(7).getStringCellValue());
					jsonObject.setObservacion("NO CONCILIADO");		
					total= total.add(jsonObject.getMonto());
					data.add(jsonObject);
				}
				xls.setMontoTotal(total);
				xls.setDetalleConciliaciones(data);				
				index++;
			}
				aux = csvRepository.findActive();
				if(aux!=null) {
					aux.setEstado("I");
					csvRepository.save(aux);
				}
			csvRepository.save(xls);
			workbook.close();
			return "Archivo Excel cargado correctamente";
		}
	

	
	
	@Override
	@Transactional
	public String conciliarDatos() throws Exception {
		Map<String, conciliaTransaccion> datoHash = new HashMap<>();
		List<ConciliaXlsDetalle> detalles = null; 
		List<ConciliaXlsDetalle> detallesConciliados = new ArrayList<ConciliaXlsDetalle>();
		ConciliacionXls datosFile = new ConciliacionXls();		
		BigDecimal conciliado = new BigDecimal(0);
		HistorialConciliacion hc;
		HistorialConciliacionDetalle itohcd;
		List<HistorialConciliacionDetalle>hcd;
		List<Transaccion> transaccion;
		datosFile = csvRepository.findActive();
		if(datosFile == null ) {return "Archivo no subido";}
		detalles = datosFile.getDetalleConciliaciones();
	
		//llenamos el historial de conciliacion
		 hc= new HistorialConciliacion ();
		hcd = new ArrayList<HistorialConciliacionDetalle>();
	
		hc.setIdFile(datosFile);
		hc.setUsuario(datosFile.getUsuario());
		
		
		
		transaccion = new ArrayList<Transaccion>();
		transaccion = transaccionRepository.consultaTransaccionesPorConciliar();
		conciliaTransaccion ct= new conciliaTransaccion();
		
		//guardamos las transacciones en un mapa de Hash
		for (Transaccion tr : transaccion) {
			ct= new conciliaTransaccion();
			ct.setFecha(tr.getFechaTransaccion());
			ct.setMonto(tr.getMonto());
			datoHash.put(tr.getNumeroComprobante(),ct);
			}
		
		
		
		conciliaTransaccion ct2= new conciliaTransaccion();
		for(ConciliaXlsDetalle detalle : detalles ) {
			itohcd = new HistorialConciliacionDetalle();
			 ct2= new conciliaTransaccion();
			ct2 = (conciliaTransaccion) datoHash.get(detalle.getNumeroComprobante());
			if(ct2!=null) {
			if(detalle.getFechaEfectivo().equals(ct2.getFecha()) && detalle.getMonto().equals(ct2.getMonto())) {
				detalle.setObservacion("CONCILIADO CORRECTAMENTE");
				conciliado = conciliado.add(detalle.getMonto());
				datosFile.setMontoConciliado(conciliado);
			}
			else if(!detalle.getFechaEfectivo().equals(ct2.getFecha())&& !detalle.getMonto().equals(ct2.getMonto())) {
				detalle.setObservacion("FECHA Y MONTO NO COINCIDE");
			}
			else if(!detalle.getFechaEfectivo().equals(ct2.getFecha()) ) {
				detalle.setObservacion("FECHA NO COINCIDE");
			}
			else if( !detalle.getMonto().equals(ct2.getMonto())) {
				detalle.setObservacion("MONTO NO COINCIDE");
				}
			else if(!detalle.getFechaEfectivo().equals(ct2.getFecha()) && !detalle.getMonto().equals(ct2.getMonto())) {
					detalle.setObservacion("NO EXISTE COMPROBANTE");
				}
			
			//comenzamos a llenar el detalle del historial de conciliacion
			itohcd.setIdHistorial(hc);
			itohcd.setComprobante(detalle.getNumeroComprobante());
			itohcd.setFechaFile(detalle.getFechaEfectivo());
			itohcd.setFechaTransaccion(ct2.getFecha());
			itohcd.setMontoFile(detalle.getMonto());
			itohcd.setMontoTransaccion(ct2.getMonto());
			itohcd.setObservacion(detalle.getObservacion());
			hcd.add(itohcd);
			detallesConciliados.add(detalle);
				
			}
			hc.setDetalleHistorial(hcd);
			datosFile.setDetalleConciliaciones(detallesConciliados);
		}
		
	
		if(hc!=null && datosFile!= null) {
			hcRepository.save(hc);
			csvRepository.save(datosFile);
		//	response="CONCILIACION TERMINADA, SE GENERÓ HISTORIAL";
		}
	
		return "Ha finalizado el proceso de conciliación.";
	}



	@Override
	@Transactional
	public String aprobarConciliacion(String usuario) throws Exception {
		
		ConciliacionXls datosFile = new ConciliacionXls();	
		List<ConciliaXlsDetalle> detalles = null;
		
		ConciliacionAprobada 	aprobacion = new ConciliacionAprobada();
		List<ConciliacionAprobadaDetalle> detalleAprobado = new ArrayList<>();
		
		ConciliacionAprobadaDetalle aux = null;
		Transaccion transaccion = new Transaccion();
		//List<Long> solicitudes = new ArrayList<>();
		List<Solicitud> solicitudes = new ArrayList<>();
		int contador=0;
			datosFile = csvRepository.findActive();
			if(datosFile==null) {
				throw new Exception("Error no existe archivo conciliado");
			}
		aprobacion.setMontoConciliado(datosFile.getMontoConciliado());
		detalles = datosFile.getDetalleConciliaciones();
		
		
		for(ConciliaXlsDetalle ito : detalles) {
			aux =  new ConciliacionAprobadaDetalle();
			transaccion = new Transaccion();
			if(ito.getObservacion().equals("CONCILIADO CORRECTAMENTE")) {
				aux.setIdConciliacion(aprobacion);
				aux.setNumeroCuenta(ito.getNumeroCuenta());
				aux.setNombreBanco(ito.getNombreBanco());
				aux.setNumeroComprobante(ito.getNumeroComprobante());
				aux.setConcepto(ito.getConcepto());
				aux.setFechaEfectivo(ito.getFechaEfectivo());
				aux.setLugar(ito.getLugar());
				aux.setMonto(ito.getMonto());
				aux.setObservacion(ito.getObservacion());
				aux.setTipoTransaccion(ito.getTipoTransaccion());
				detalleAprobado.add(aux);
				transaccion = transaccionRepository.consultaTransaccionPorComprobante(aux.getNumeroComprobante());
				transaccion.setConciliado("S");
				transaccionRepository.save(transaccion);
//				solicitud =solRepository.consultaSolicitud(transaccion.getSolicitud().getNumeroSolicitud());
//				solicitud.setEstadoActual(new TipoEstado("PRG"));
				//solicitudes.add(solicitud.getNumeroSolicitud());
				contador++;
			}
		}
		if(contador==0) {
			throw new Exception ("Error no se encontró ninguna Transaccion");
		}
		//guardamos las conciliaciones aprobadas
		aprobacion.setDetalleConciliaciones(detalleAprobado);
		aprobacion.setUsuario(usuario);
		this.repositoryAprobadas.save(aprobacion);
		
		//actualizamos las solicitudees
		solicitudes = transaccionRepository.consultaSolicitudPorTransaccionConciliada();
		if(solicitudes.isEmpty()) {throw new Exception ("Error en la actualizacion de Solicitudes");}
		List<Solicitud> auxSol = new ArrayList<>();
		for(Solicitud sol: solicitudes ) {
			Proyecto proyecto ;
			HistorialDeSolicitud historialSol = new HistorialDeSolicitud();
			historialSol.setSolicitud(sol);
			historialSol.setTablaCambiar("Estado");
			historialSol.setValorActual("TN");
			historialSol.setObservacion("Cambio de estado a en transito");
			historialSol.setValorAnterior(sol.getEstadoActual().getIdEstado());
			historialSol.setUsuarioModificacionInterno(new CuentaInterno(usuario));
			
			proyecto = this.repositoryProyecto.consultaProyecto(sol.getProyecto().getIdProyecto());
			proyecto.setMontoRecaudado(proyecto.getMontoRecaudado().add(sol.getAmortizacion().getMontoInversion()));
			this.repositoryProyecto.save(proyecto);
			sol.setHistorial(historialSol);
//			sol.setAceptaInformacionCorrecta("S");
//			sol.setAceptaIngresarInfoVigente("S");
//			sol.setAceptaLicitudFondos("S");
			auxSol.add(sol);
		}
		solService.updateAll(auxSol,"TN" ,new Cuenta(usuario), aux.getObservacion());
		
		
		
	
		//el archivo subido cambia a estao Inactivo y las transacciones no conciliadas se deberean poner en un nuevo excel
		datosFile.setEstado("I");
		csvRepository.save(datosFile);
		
		
		return contador+" Transacciones Aprobadas con Éxito, Total conciliado "+aprobacion.getMontoConciliado();
		
	}
	
	
	@Override
	@Transactional
	public ConciliacionXls consultarFileData()throws Exception {
		 ConciliacionXls csv =new ConciliacionXls();
			 csv = csvRepository.findActive(); 
			 if (csv==null) {
				 throw new Exception("Error archivo no subido");
			 }
			
		return csv;	
		
	}
	

		
	}
	
