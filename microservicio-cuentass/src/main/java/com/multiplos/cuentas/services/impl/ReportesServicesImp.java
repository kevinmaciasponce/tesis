package com.multiplos.cuentas.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.pojo.transaccion.conciliaTransaccion;
import com.multiplos.cuentas.reportes.ReporteCuotas;
import com.multiplos.cuentas.reportes.ReporteGeneral;
import com.multiplos.cuentas.reportes.ReportePorAnio;
import com.multiplos.cuentas.reportes.ReportePorMes;
import com.multiplos.cuentas.reportes.ReportePorProyecto;
import com.multiplos.cuentas.reportes.fechaValue;
import com.multiplos.cuentas.reportes.ReportePorInvestors;
import com.multiplos.cuentas.reportes.reportesMora.ReporteCuotasPorPagos;
import com.multiplos.cuentas.repository.ProyectoRepository;
import com.multiplos.cuentas.repository.ReportesRepository;
import com.multiplos.cuentas.repository.SolicitudRepository;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.ReportesServices;
import com.multiplos.cuentas.services.SolicitudService;

@Service
public class ReportesServicesImp implements ReportesServices {
	private static final Logger LOG = LoggerFactory.getLogger(SolicitudServiceImpl.class);

	private ReportesRepository reporteRepository;
	private SolicitudRepository solr;
	private CuentaService cuentaService;
	private ProyectoRepository proyectoRepository;
	 String[] meses = {"ENE", "FEB","MAR", "ABR", "MAY", "JUN",
             "JUL", "AGO","SEP", "OCT", "NOV", "DIC"};

// lo configuras así, después de inicializar symbols:
	 
	
	@Autowired
	public ReportesServicesImp(ReportesRepository reporteRepository, SolicitudRepository solr,
			CuentaService cuentaService, ProyectoRepository proyectoRepository) {
		this.reporteRepository = reporteRepository;
		this.solr = solr;
		this.cuentaService = cuentaService;
		this.proyectoRepository = proyectoRepository;
	}

	@Override
	public ReporteGeneral reportePorfechasInvestor(List<fechaValue> fechas,List<ReportePorProyecto> proyectos , String ide) throws Exception {
		ReporteGeneral report = null;
		Cuenta cuenta = null;
		int anio=0;
		List<ReportePorInvestors> reportInvestor = new ArrayList<>();
		List<ReportePorMes>  listaMes = new ArrayList<>();
		ReportePorInvestors reportInvestorIto;
		
			reportInvestorIto = new ReportePorInvestors();
			cuenta = this.cuentaService.findByCuentaIdCuenta(ide);
			if (cuenta == null) {
				throw new Exception("NO EXISTE INVERSIONISTA CON CEDULA: "+ide);
			}
				
				List<ReportePorProyecto> listaProyecto = new ArrayList<>();
				ReportePorProyecto reportePorProyecto=null;
				List<ReporteCuotas> listaCuota;
				List<ReporteCuotas> listaCuotaAux;
				ReportePorMes aux;
				for (fechaValue fecha : fechas) {
					if (fecha.getMes() <= 0 || fecha.getMes() > 12) {
						LOG.warn(""+fecha.getMes());
						throw new Exception("Valor del mes no válido " + fecha.getMes());
					}
					anio= fecha.getAnio();
					
			
					if(proyectos.isEmpty()) {
						listaProyecto= this.reporteRepository.reporteProyectoPorMes(fecha.getMes(),fecha.getAnio(), ide);
					}else {
						listaProyecto= proyectos;
					}
					
					if(!listaProyecto.isEmpty()) {
					
					
					for(int i =0 ; i<listaProyecto.size();i++) {
						listaCuota = this.reporteRepository.generearReportePorFechaYProyecto2(fecha.getMes(), fecha.getAnio(), ide,listaProyecto.get(i).getCodProyect());
		
							if (!listaCuota.isEmpty()) {
								listaProyecto.get(i).setNombreEmpresa(listaCuota.get(0).getNombreProyecto());
										//reportePorProyecto.setMes(mes);
								listaProyecto.get(i).setTotalProyecto(listaCuota.stream().map((x)-> x.getTotalCuota()).reduce((x, y)->x.add(y)).get());
								listaProyecto.get(i).setListaCuotas(listaCuota);
										//LOG.warn("2222 "+listaProyecto.get(0).getCodProyect());
							}
					}
					}
							if(!listaProyecto.isEmpty()) {
								//listaProyecto = (List<ReportePorProyecto>) datoHash.values().stream().collect(Collectors.toCollection(ArrayList::new));
								aux = new ReportePorMes();
								aux.setExiste(true);
								aux.setInfo("ok");
							
								aux.setTotalMes(listaProyecto.stream().map((x) -> x.getTotalProyecto()).reduce((x, y) -> x.add(y)).get());
								Locale locale = new Locale("es", "ES");
								
								Month mMonth = Month.of(fecha.getMes());
								String monthName = mMonth.getDisplayName(TextStyle.FULL, locale);
								String abreviatura= monthName.substring(0, 3);
								aux.setReporteFechaProyecto(listaProyecto);
								aux.setMes(String.valueOf(abreviatura).toUpperCase()+"-"+anio);
								listaMes.add(aux);
								LOG.warn(listaMes.size()+" "+aux.getMes());
							}							
				}
			//	LOG.warn("2222 aux" + listaMes.size());
				
				if(!listaMes.isEmpty()) {
					reportInvestorIto.setTotalInvestor(listaMes.stream().map((x) -> x.getTotalMes()).reduce((x, y) -> x.add(y)).get());
					reportInvestorIto.setReportePorFechas(listaMes);
				}else {
					reportInvestorIto.setTotalInvestor(new BigDecimal(0));
				}
				reportInvestorIto.setNombre(cuenta.getPersona().getNombres() + ' ' + cuenta.getPersona().getApellidos());
			
			reportInvestorIto.setIdentificacion(ide);
			reportInvestor.add(reportInvestorIto);
		
		if (reportInvestor.size() == 0) {
			throw new Exception("No existe datos para la consulta ");
		}
		report = new ReporteGeneral();
		//report.setAnio(anio);
		report.setTotalReporte(reportInvestor.stream().map((x) -> x.getTotalInvestor()).reduce((x, y) -> x.add(y)).get());
		report.setReportes(reportInvestor);
		return report;
	}

	@Override
	public ReportePorAnio GerenteReportePorfechasInvestor(int[] meses, int anio, String[] identificaciones)throws Exception {
		ReportePorAnio report = null;
		Cuenta cuenta = null;
		List<ReportePorInvestors> reportInvestor = new ArrayList<>();
		ReportePorInvestors reportInvestorIto;
		for (String ide : identificaciones) {
			reportInvestorIto = new ReportePorInvestors();
			cuenta = this.cuentaService.findByCuentaIdCuenta(ide);
			if (cuenta != null) {
				List<ReportePorMes> listaMes = new ArrayList<>();
				List<ReportePorProyecto> listaProyecto = new ArrayList<>();
				ReportePorProyecto reportePorProyecto=null;
				List<ReporteCuotas> listaCuota;
				List<ReporteCuotas> listaCuotaAux;
				ReportePorMes aux;
				for (int mes : meses) {
					if (mes <= 0 || mes > 12) {
						throw new Exception("Valor del mes no válido " + mes);
					}
					aux = new ReportePorMes();
					Map<String, ReportePorProyecto> datoHash = new HashMap<>();
							listaCuota = this.reporteRepository.generearReportePorFechaYProyecto(mes, anio, ide);
							if (!listaCuota.isEmpty()) {
								for(ReporteCuotas cuota: listaCuota) {									
									reportePorProyecto= (ReportePorProyecto)datoHash.get(cuota.getCodProyecto());
									if(reportePorProyecto==null) {
										reportePorProyecto = new ReportePorProyecto();
										reportePorProyecto.setCodProyect(cuota.getCodProyecto());
										reportePorProyecto.setNombreEmpresa(cuota.getNombreProyecto());
										//reportePorProyecto.setMes(mes);
										reportePorProyecto.setTotalProyecto(cuota.getTotalCuota());
										listaCuotaAux= new ArrayList<>();
									}else {
										listaCuotaAux= (List<ReporteCuotas>) reportePorProyecto.getListaCuotas();
										reportePorProyecto.setTotalProyecto(reportePorProyecto.getTotalProyecto().add(cuota.getTotalCuota()));
									}
									listaCuotaAux.add(cuota);
									reportePorProyecto.setListaCuotas(listaCuotaAux);
									datoHash.put(reportePorProyecto.getCodProyect(), reportePorProyecto);
								}
							}	
							if(!datoHash.isEmpty()) {
								listaProyecto = (List<ReportePorProyecto>) datoHash.values().stream().collect(Collectors.toCollection(ArrayList::new));
								aux.setExiste(true);
								aux.setInfo("ok");
								aux.setTotalMes(listaProyecto.stream().map((x) -> x.getTotalProyecto()).reduce((x, y) -> x.add(y)).get());
								Locale locale = new Locale("es", "ES");
								Month mMonth = Month.of(mes);
								String monthName = mMonth.getDisplayName(TextStyle.FULL, locale);
								aux.setReporteFechaProyecto(listaProyecto);
								aux.setMes(String.valueOf(monthName).toUpperCase());
								listaMes.add(aux);
							}
				}
				if(!listaMes.isEmpty()) {
					reportInvestorIto.setTotalInvestor(listaMes.stream().map((i) -> i.getTotalMes()).reduce((i, y) -> i.add(y)).get());
					reportInvestorIto.setReportePorFechas(listaMes);
				}else {
					reportInvestorIto.setTotalInvestor(new BigDecimal(0));
				}
				reportInvestorIto.setNombre(cuenta.getPersona().getNombres() + ' ' + cuenta.getPersona().getApellidos());
			}else {
				reportInvestorIto.setNombre("No existe inversionista con ced");
				reportInvestorIto.setTotalInvestor(new BigDecimal(0));	
			}
			reportInvestorIto.setIdentificacion(ide);
			reportInvestor.add(reportInvestorIto);
		}
		if (reportInvestor.size() == 0) {
			throw new Exception("No existe datos para la consulta ");
		}
		report = new ReportePorAnio();
		report.setAnio(anio);
		report.setTotalReporte(reportInvestor.stream().map((x) -> x.getTotalInvestor()).reduce((x, y) -> x.add(y)).get());
		report.setReportes(reportInvestor);
		return report;
	}

	@Override
	public ReportePorAnio reportePorProyectosMora(int[] meses, int anio, String[] codProyect) throws Exception {
		ReportePorAnio report = null;
		List<ReportePorProyecto> listaProyecto = new ArrayList<>();
		ReportePorProyecto proyectoAux;
		List<ReportePorMes> listafecha = null;
		 List<ReporteCuotasPorPagos> listaCuotasMora = new ArrayList<>();
		// ReportePorProyecto proyectoAux;
		ReporteCuotasPorPagos reportMora;
		ReportePorMes aux;
		for (String cod : codProyect) {
			proyectoAux = new ReportePorProyecto();
			listafecha = new ArrayList<>();
			for (int mes : meses) {
				if (mes <= 0 || mes > 12) {
					throw new Exception("Valor del mes no valido " + mes);
				}
				listaCuotasMora = new ArrayList<>();
				reportMora = null;
				reportMora = this.reporteRepository.generarReporteCuotasPorPagos(mes, anio, cod);
				
				if (reportMora!=null) {
					aux = new ReportePorMes();
					int sumaDiasAtraso;
					sumaDiasAtraso = this.reporteRepository.diasAtrasoPorMes(mes, anio, cod);
					if (sumaDiasAtraso <= 0) {
						sumaDiasAtraso = 0;
					}
					reportMora.setDiasAtraso(sumaDiasAtraso);
					LocalDate fechaPago= reportMora.getFechaCobro();
					fechaPago= fechaPago.plusDays(sumaDiasAtraso) ;
					reportMora.setFechaPromedioPago(fechaPago);
					listaCuotasMora.add(reportMora);
					Locale locale = new Locale("es", "ES");
					Month mMonth = Month.of(mes);
					String monthName = mMonth.getDisplayName(TextStyle.FULL, locale);
					aux.setMes(String.valueOf(monthName.toUpperCase()));
					aux.setReporteFechaProyecto(listaCuotasMora);
					aux.setTotalMes(new BigDecimal(sumaDiasAtraso / listaCuotasMora.size()));
					listafecha.add(aux);
				}
			}
			if (listafecha.size() != 0) {
				BigDecimal sumaDiasAtraso;
				sumaDiasAtraso = listafecha.stream().map((x) -> x.getTotalMes()).reduce((x, y) -> x.add(y)).get();
				if (sumaDiasAtraso.doubleValue() <= 0) {
					sumaDiasAtraso = new BigDecimal(0);
				}
				proyectoAux.setTotalProyecto(new BigDecimal(sumaDiasAtraso.doubleValue() / listafecha.size())
						.setScale(2, RoundingMode.HALF_UP));
				proyectoAux.setListaCuotas(listafecha);
			} else {
				proyectoAux.setNombreEmpresa("No existe datos para el proyecto");
				proyectoAux.setTotalProyecto(new BigDecimal(0));
			}
			proyectoAux.setCodProyect(cod);
			listaProyecto.add(proyectoAux);

		}
		if (listaProyecto.isEmpty()) {
			throw new Exception("No se encontraron resultados ");
		}
		BigDecimal sumaDiasAtraso;
		sumaDiasAtraso = listaProyecto.stream().map((x) -> x.getTotalProyecto()).reduce((x, y) -> x.add(y)).get();
		if (sumaDiasAtraso.doubleValue() <= 0) {
			sumaDiasAtraso = new BigDecimal(0);
		}
		report = new ReportePorAnio();
		report.setTotalReporte(new BigDecimal(sumaDiasAtraso.doubleValue() / listaProyecto.size()).setScale(2, RoundingMode.HALF_UP));
		report.setAnio(anio);
		report.setReportes(listaProyecto);

		return report;

	}
}
