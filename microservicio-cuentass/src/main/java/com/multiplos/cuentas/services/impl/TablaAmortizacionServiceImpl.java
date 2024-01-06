package com.multiplos.cuentas.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.BitacoraProceso;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.DatosInversion;
import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.HistorialDeSolicitud;
import com.multiplos.cuentas.models.Pais;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.models.TipoTabla;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionRequest;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionResponse;
import com.multiplos.cuentas.pojo.amortizacion.DatosAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.DetaAmortizacion;
import com.multiplos.cuentas.pojo.amortizacion.AmortizacionDetalleResponse;
import com.multiplos.cuentas.pojo.amortizacion.SimuladorPrincipalResponse;
import com.multiplos.cuentas.pojo.amortizacion.SimuladorRequest;

import com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.UpdateSolicitudRequest;
import com.multiplos.cuentas.repository.CuentaRepository;
import com.multiplos.cuentas.repository.SolicitudRepository;
import com.multiplos.cuentas.repository.TablaAmortizacionRepository;
import com.multiplos.cuentas.services.BitacoraProcesoService;
import com.multiplos.cuentas.services.DatosInversionService;
import com.multiplos.cuentas.services.DetalleTblAmortizacionService;
import com.multiplos.cuentas.services.PaisService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.PersonaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.utils.ServicesUtils;

@Service
public class TablaAmortizacionServiceImpl implements TablaAmortizacionService {

	private static final Logger LOG = LoggerFactory.getLogger(TablaAmortizacionServiceImpl.class);

	private TablaAmortizacionRepository tblRepository;
	private ParametroService paramService;
	private DetalleTblAmortizacionService detTblAmortService;
	private PaisService paisService;
	private PersonaService personaService;
	private ServicesUtils util;
	private SolicitudService solicitudService;
	private DatosInversionService datosInversion;
	private ProyectoService proyectoService;
	private BitacoraProcesoService bitacoraService;
	private SolicitudRepository solicitudRepository;
	private DecimalFormatSymbols simb;
	private DecimalFormat df;
	private CuentaRepository cuentaRepository;

	@Autowired
	public TablaAmortizacionServiceImpl(TablaAmortizacionRepository tblRepository, ParametroService paramService,
			DetalleTblAmortizacionService detTblAmortService, PaisService paisService, PersonaService personaService,
			ServicesUtils util, @Lazy SolicitudService solicitudService, DatosInversionService datosInversion,
			ProyectoService proyectoService, SolicitudRepository solicitudrepository, CuentaRepository cuentaRepository,
			BitacoraProcesoService bitacoraService, SolicitudRepository solicitudRepository) {
		this.solicitudRepository = solicitudRepository;
		this.cuentaRepository = cuentaRepository;
		this.tblRepository = tblRepository;
		this.paramService = paramService;
		this.detTblAmortService = detTblAmortService;
		this.paisService = paisService;
		this.personaService = personaService;
		this.util = util;
		this.solicitudService = solicitudService;
		this.datosInversion = datosInversion;
		this.proyectoService = proyectoService;
		this.bitacoraService = bitacoraService;

		simb = new DecimalFormatSymbols();
		simb.setDecimalSeparator('.');
		simb.setGroupingSeparator(',');
		df = new DecimalFormat("#,###.00", simb);

	}

	@Override
	@Transactional
	public TablaAmortizacion CreaTablaAmortizacion(AmortizacionRequest amortizacion) throws Exception {
		List<DetalleAmortizacion> listDetalleAmort = new ArrayList<>();
		TablaAmortizacion tbl;
		BigDecimal porcentajeRendimientoNeto;
		int lastIdx = 0;
		List<DetalleAmortizacion> listDetAmort = new ArrayList<>();
		int plazo = 0;
		porcentajeRendimientoNeto = proyectoService.consultaTasaEfectiva(amortizacion.getCodigoProyecto());
		plazo = proyectoService.consultaPlazo(amortizacion.getCodigoProyecto());
		tbl = new TablaAmortizacion();
		LOG.warn("detalles");
		listDetalleAmort = this.generaDetalleTblAmortizacionOfi(amortizacion.getPeriodoPago(),
				amortizacion.getInversion(), plazo, porcentajeRendimientoNeto.doubleValue(), null, tbl);
		if (listDetalleAmort.isEmpty()) {
			throw new Exception("Error al generar detalles");
		}
		LOG.warn("detallesCreado0");
		lastIdx = listDetalleAmort.size() - 1;

		tbl.setMontoInversion(new BigDecimal(amortizacion.getInversion()));
		tbl.setPlazo(plazo);
		tbl.setRendimientoNeto(porcentajeRendimientoNeto);
		tbl.setRendimientoTotalInversion(listDetalleAmort.get(lastIdx).getRendimientoMensual());
		tbl.setTotalRecibir(listDetalleAmort.get(lastIdx).getTotalRecibir());
		tbl.setFechaCreacion(util.getLocalDateTime());
		tbl.setUsuarioCreacion(new Cuenta(amortizacion.getIdentificacion()));
		tbl.setEstado("A");
		tbl.setTipoTabla(new TipoTabla(amortizacion.getIdTipoTabla()));
		tbl.setDetallesTblAmortizacion(listDetalleAmort);

		return tbl;
	}

	@Override
	@Transactional(readOnly = true)
	public AmortizacionResponse consultaAmortizacionPorSolicitud(Long numSol) throws Exception {
		AmortizacionResponse tablaResponse = null;
		List<AmortizacionDetalleResponse> detalle = null;
		tablaResponse = this.tblRepository.amortizacionPorSolicitud(numSol);
		if (tablaResponse == null) {
			throw new Exception("No existe tabla de amortizacion para la solicitud " + numSol);
		}
		detalle = this.tblRepository.consultaPorTabla(tablaResponse.getId());
		tablaResponse.setDetallesTblAmortizacion(detalle);

		tablaResponse.getDetallesTblAmortizacion().sort((o, o2) -> o.getCuota() - o2.getCuota());

		return tablaResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public AmortizacionResponse consultaPagarePorSolicitud(Long numSol) throws Exception {
		AmortizacionResponse tablaResponse = null;
		List<AmortizacionDetalleResponse> detalle = null;
		tablaResponse = this.tblRepository.pagarePorSolicitud(numSol);
		if (tablaResponse == null) {
			throw new Exception("No existe pagare para la solicitud " + numSol);
		}
		detalle = this.tblRepository.consultaPorTabla(tablaResponse.getId());
		tablaResponse.setDetallesTblAmortizacion(detalle);
		tablaResponse.getDetallesTblAmortizacion().sort((o, o2) -> o.getCuota() - o2.getCuota());
		return tablaResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public AmortizacionResponse consultaAmortizacionPorProyecto(String cod) throws Exception {
		AmortizacionResponse tablaResponse = null;
		List<AmortizacionDetalleResponse> detalle = null;
		tablaResponse = this.tblRepository.amortizacionPorProyecto(cod);
		if (tablaResponse == null) {
			throw new Exception("No existe amortizacion para el Proyecto " + cod);
		}
		detalle = this.tblRepository.consultaPorTabla(tablaResponse.getId());
		tablaResponse.setDetallesTblAmortizacion(detalle);
		tablaResponse.getDetallesTblAmortizacion().sort((o, o2) -> o.getCuota() - o2.getCuota());
		return tablaResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public AmortizacionResponse simuladorTblAmortizacion(SimuladorRequest simuladorRequest) throws Exception {
		PersonaResponse persona = new PersonaResponse();
		AmortizacionResponse amortResponse = null;
		Proyecto proyecto;
		double montoInversion = 0;

//		try{
		montoInversion = simuladorRequest.getInversion().doubleValue();
		persona = personaService.consultaDatosPersona(simuladorRequest.getIdentificacion());
		if (persona == null) {
			throw new Exception("No existe inversionista con identificacion: " + simuladorRequest.getIdentificacion());
		}

		amortResponse = new AmortizacionResponse();
		amortResponse = this.generaSimuladorTblAmortizacion(simuladorRequest.getIdentificacion(), montoInversion,
				simuladorRequest.getCodigoProyecto());

//		}catch(Exception e) {
//			LOG.error("simuladorTblAmortizacion: Error "+e.getMessage());
//			return amortResponse;
//		}

		return amortResponse;
	}

	public AmortizacionResponse generaSimuladorTblAmortizacion(String identificacion, double montoInversion,
			String codProyecto) throws Exception {
		List<DetaAmortizacion> listDetalleAmort = new ArrayList<>();
		int lastIdx = 0;
		Proyecto proyecto;
		AmortizacionResponse response = null;
		PersonaResponse persona = null;
		BigDecimal rendimientoNeto;
		int plazo = 0;
		List<AmortizacionDetalleResponse> listDetalleResponse = new ArrayList<>();

		proyecto = proyectoService.consultaProyecto(codProyecto);
		if (proyecto == null) {
			throw new Exception("No existe proyecto con codigo: " + codProyecto);
		}
		rendimientoNeto = proyecto.getTasaEfectivaAnual();
		plazo = proyecto.getPlazo();

		if (montoInversion > proyecto.getMontoSolicitado().doubleValue()) {
			throw new Exception("La inversion excede el monto del proyecto");
		}

		listDetalleAmort = this.generaDetalleTblAmortizacion(proyecto.getPeriodoPago(), montoInversion, plazo,
				codProyecto, rendimientoNeto.doubleValue(), null);
		if (!listDetalleAmort.isEmpty()) {

			lastIdx = listDetalleAmort.size() - 1;
			persona = new PersonaResponse();
			try {
				persona = personaService.consultaDatosPersona(identificacion);
			} catch (Exception e) {
			}
			response = new AmortizacionResponse();
			if (persona.getTipoPersona().contains("NAT")) {
				response.setCliente(persona.getNombres() + " " + persona.getApellidos());
			} else {
				response.setCliente(persona.getRazonSocial());
			}
			response.setPais(paisService.consultaPaisPorAbrebiatura(persona.getNacionalidad()));
			response.setCodigoProyecto(codProyecto);
			response.setNombreProyecto(proyectoService.consultaNombreProyecto(codProyecto));
			response.setMontoInversion(new BigDecimal(montoInversion).setScale(2, RoundingMode.HALF_UP));
			response.setPlazo(plazo * 30);
			// response.setFechaGeneracion();
			response.setRendimientoNeto(rendimientoNeto);
			response.setRendimientoTotalInversion(new BigDecimal(listDetalleAmort.get(lastIdx).getRendimientoMensual())
					.setScale(2, RoundingMode.HALF_UP));
			response.setTotalRecibir(listDetalleAmort.get(lastIdx).getTotalRecibir());

			for (DetaAmortizacion det : listDetalleAmort) {
				AmortizacionDetalleResponse detTbl = new AmortizacionDetalleResponse();
				detTbl.setDetalleCobro(det.getDetalleCobro());
				detTbl.setCobrosCapital(new BigDecimal(det.getCobrosCapital()));
				detTbl.setSaldoCapital(new BigDecimal(det.getSaldoCapital()));
				detTbl.setRendimientoMensual(
						new BigDecimal(det.getRendimientoMensual()).setScale(2, RoundingMode.HALF_UP));
				detTbl.setTotalRecibir(
						new BigDecimal(det.getTotalRecibir().doubleValue()).setScale(2, RoundingMode.HALF_UP));
				listDetalleResponse.add(detTbl);
			}

			response.setDetallesTblAmortizacion(listDetalleResponse);
		}
		return response;
	}

	@Override
	public List<DetalleAmortizacion> generaDetalleTblAmortizacionOfi(int periodo, double montoInversion, int plazo,
			double rendimientoNeto, LocalDate fecha, TablaAmortizacion ta) throws Exception {
		DetalleAmortizacion detAmort = null;
		List<DetalleAmortizacion> listDetAmort = new ArrayList<>();
		List<ParametroResponse> listParam = new ArrayList<>();
		// int meses = 0;
		double cobroCapital = 0.0;
		double saldoCapital = 0.0;
		double rendimientoMensual = 0.0;
		double porcentajeRendimientoNeto = 0.0;
		double totalRecibir = 0.0;
		double auxSaldoCapital = 0.0;
		String detalleCobro = null;
		// totales
		double totalRendimientoMensual = 0.0;
		double totalCobroCapital = 0.0;
		double totalesRecibir = 0.0;
		int i;
		DecimalFormat df = new DecimalFormat("###.00");
		// meses = plazo/30;
		LocalDate auxFechaCobro = null;
		porcentajeRendimientoNeto = rendimientoNeto / 100;
		if (fecha != null) {
			auxFechaCobro = fecha;
		}
		try {
			LOG.warn("warning");
			for (i = 1; i <= plazo; i++) {
				LOG.warn("war "+i);
				rendimientoMensual = 0.0;
				detAmort = new DetalleAmortizacion();
				detalleCobro = "Interés mes No. ";
				if (i < 5) {
					LOG.warn("5");
					detalleCobro = detalleCobro + i;
					saldoCapital = (montoInversion - cobroCapital);// ver luego valor negativo
					cobroCapital = 0.0;
					LOG.warn(""+periodo);
					if (i%periodo == 0) {
						LOG.warn("entro ");
						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
					LOG.warn(""+totalRendimientoMensual+" "+cobroCapital+" "+ totalCobroCapital+" "+totalesRecibir);
					
				} else if (i == 5 && plazo == 5) {// cuando sea hasta el mes 5
					detalleCobro = detalleCobro + i + " + Capital";
					saldoCapital = auxSaldoCapital - cobroCapital;
					cobroCapital = montoInversion;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i < 6) {
					detalleCobro = detalleCobro + i;
					saldoCapital = (montoInversion - cobroCapital);// ver luego valor negativo
					cobroCapital = 0.0;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 6 && plazo == 6) {// cuando sea hasta el mes 6
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i < 12) {// cuando es hasta 12 meses
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = (montoInversion - cobroCapital);// ver luego valor negativo
					if (i % periodo == 0) {
						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 12 && plazo == 12) {// cuando sea hasta el mes 12
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i == 12 && plazo == 18) {// cuendo es el mes 12 se saca el 50%
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.5;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if ((i > 12 && i < 18) && plazo == 18) {
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 18 && plazo == 18) {
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.5;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i == 12 && plazo == 24) {// cuando es hasta 24 meses
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.33;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if ((i > 12 && i < 18) && plazo == 24) {
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;

					}
				} else if (i == 18 && plazo == 24) {
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.33;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if ((i > 18 && i < 24) && plazo == 24) {
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 24) {
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.34;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				}
				LOG.warn(""+totalRendimientoMensual+" "+cobroCapital+" "+ totalCobroCapital+" "+totalesRecibir);
				detAmort.setDetalleCobro(detalleCobro);
				detAmort.setCobrosCapital(new BigDecimal(df.format(cobroCapital).replace(",", ".")));
				detAmort.setSaldoCapital(new BigDecimal(df.format(saldoCapital).replace(",", ".")));
				detAmort.setRendimientoMensual(new BigDecimal(df.format(rendimientoMensual).replace(",", ".")));
				detAmort.setEstadoPago("Pendiente de pago");
				totalRecibir = rendimientoMensual + cobroCapital;
				detAmort.setTotalRecibir(new BigDecimal(totalRecibir).setScale(2, RoundingMode.HALF_UP));
				detAmort.setCuota(i);//
				detAmort.setEstado("A");
				detAmort.setTablaAmortizacion(ta);
				if (fecha != null) {
					detAmort.setFechaEstimacion(this.calculafechaCobro(auxFechaCobro));//
					auxFechaCobro = detAmort.getFechaEstimacion();
				}

				totalRendimientoMensual = totalRendimientoMensual + rendimientoMensual;
				totalCobroCapital = totalCobroCapital + cobroCapital;
				totalesRecibir = totalesRecibir + totalRecibir;

				/*
				 * LOG.info(detAmort.getDetalleCobro()+"-"+detAmort.getRendimientoMensual()+"-"+
				 * detAmort.getSaldoCapital()
				 * +"-"+detAmort.getCobrosCapital()+"-"+detAmort.getTotalRecibir());
				 */
				LOG.warn(""+totalRendimientoMensual+" "+cobroCapital+" "+ totalCobroCapital+" "+totalesRecibir);
				listDetAmort.add(detAmort); 
			}
			detAmort = new DetalleAmortizacion();
			detAmort.setDetalleCobro("Totales");
			detAmort.setRendimientoMensual(new BigDecimal(df.format(totalRendimientoMensual).replace(",", ".")));
			detAmort.setCobrosCapital(new BigDecimal(df.format(totalCobroCapital).replace(",", ".")));
			detAmort.setTotalRecibir(new BigDecimal(df.format(totalesRecibir).replace(",", ".")));
			detAmort.setEstado("A");
			detAmort.setTablaAmortizacion(ta);
			detAmort.setCuota(i++);
			listDetAmort.add(detAmort);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		 LOG.info(detAmort.getDetalleCobro()+"-"+detAmort.getRendimientoMensual()+"-"+detAmort.getCobrosCapital()+"-"+detAmort.getTotalRecibir());
		return listDetAmort;
	}

	public List<DetaAmortizacion> generaDetalleTblAmortizacion(int periodo, double montoInversion, int plazo,
			String codProyecto, double rendimientoNeto, LocalDate fecha) throws Exception {
		DetaAmortizacion detAmort = null;
		List<DetaAmortizacion> listDetAmort = new ArrayList<>();
		List<ParametroResponse> listParam = new ArrayList<>();
		// int meses = 0;
		double cobroCapital = 0.0;
		double saldoCapital = 0.0;
		double rendimientoMensual = 0.0;
		double porcentajeRendimientoNeto = 0.0;
		double totalRecibir = 0.0;
		double auxSaldoCapital = 0.0;
		String detalleCobro = null;
		// totales
		double totalRendimientoMensual = 0.0;
		double totalCobroCapital = 0.0;
		double totalesRecibir = 0.0;

		DecimalFormat df = new DecimalFormat("###.00");
		// meses = plazo/30;
		LocalDate auxFechaCobro = null;

		if (codProyecto != null) {
			porcentajeRendimientoNeto = rendimientoNeto / 100;
			if (fecha != null) {
				auxFechaCobro = fecha;
			}
		} else {// Porcentaje rendimiento para el simuladro principal
				// meses = plazo;
			listParam = paramService.findByParametro("SIMULADOR_PRI");
			for (ParametroResponse emp : listParam) {
				if (emp.getCodParametro().contains("MULTIPLO")) {
					porcentajeRendimientoNeto = Double.valueOf(emp.getValor()) / 100;
					break;
				}
			}
		}
		try {
			for (int i = 1; i <= plazo; i++) {
				rendimientoMensual = 0.0;
				detAmort = new DetaAmortizacion();
				detalleCobro = "Interés mes No. ";
				if (i < 5) {
					detalleCobro = detalleCobro + i;
					saldoCapital = (montoInversion - cobroCapital);// ver luego valor negativo
					cobroCapital = 0.0;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 5 && plazo == 5) {// cuando sea hasta el mes 5
					detalleCobro = detalleCobro + i + " + Capital";
					saldoCapital = auxSaldoCapital - cobroCapital;
					cobroCapital = montoInversion;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i < 6) {
					detalleCobro = detalleCobro + i;
					saldoCapital = (montoInversion - cobroCapital);// ver luego valor negativo
					cobroCapital = 0.0;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 6 && plazo == 6) {// cuando sea hasta el mes 6
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i < 12) {// cuando es hasta 12 meses
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = (montoInversion - cobroCapital);// ver luego valor negativo
					if (i % periodo == 0) {
						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 12 && plazo == 12) {// cuando sea hasta el mes 12
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i == 12 && plazo == 18) {// cuendo es el mes 12 se saca el 50%
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.5;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if ((i > 12 && i < 18) && plazo == 18) {
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 18 && plazo == 18) {
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.5;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (cobroCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				} else if (i == 12 && plazo == 24) {// cuando es hasta 24 meses
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.33;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if ((i > 12 && i < 18) && plazo == 24) {
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (saldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;

					}
				} else if (i == 18 && plazo == 24) {
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.33;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if ((i > 18 && i < 24) && plazo == 24) {
					detalleCobro = detalleCobro + i;
					cobroCapital = 0.0;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
						auxSaldoCapital = saldoCapital;
					}
				} else if (i == 24) {
					detalleCobro = detalleCobro + i + " + Capital";
					cobroCapital = montoInversion * 0.34;
					saldoCapital = auxSaldoCapital - cobroCapital;
					if (i % periodo == 0) {

						rendimientoMensual = (auxSaldoCapital * porcentajeRendimientoNeto * 30) / 360;
						rendimientoMensual = rendimientoMensual * periodo;
					}
				}

				detAmort.setDetalleCobro(detalleCobro);
				detAmort.setCobrosCapital(cobroCapital);
				detAmort.setSaldoCapital(saldoCapital);
				detAmort.setRendimientoMensual(rendimientoMensual);
				detAmort.setEstadoPago("Pendiente de pago");
				totalRecibir = rendimientoMensual + cobroCapital;
				detAmort.setTotalRecibir(new BigDecimal(totalRecibir).setScale(2, RoundingMode.HALF_UP));
				detAmort.setCuota(i);//

				if (fecha != null) {
					detAmort.setFechaCobro(this.calculafechaCobro(auxFechaCobro));//
					auxFechaCobro = detAmort.getFechaCobro();
				}

				totalRendimientoMensual = totalRendimientoMensual + rendimientoMensual;
				totalCobroCapital = totalCobroCapital + cobroCapital;
				totalesRecibir = totalesRecibir + totalRecibir;

				/*
				 * LOG.info(detAmort.getDetalleCobro()+"-"+detAmort.getRendimientoMensual()+"-"+
				 * detAmort.getSaldoCapital()
				 * +"-"+detAmort.getCobrosCapital()+"-"+detAmort.getTotalRecibir());
				 */
				listDetAmort.add(detAmort);

			}
			detAmort = new DetaAmortizacion();
			detAmort.setDetalleCobro("Totales");
			detAmort.setRendimientoMensual(totalRendimientoMensual);
			detAmort.setCobrosCapital(totalCobroCapital);
			detAmort.setTotalRecibir(new BigDecimal(totalesRecibir).setScale(2, RoundingMode.HALF_UP));

			listDetAmort.add(detAmort);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		 LOG.info(detAmort.getDetalleCobro()+"-"+detAmort.getRendimientoMensual()+"-"+detAmort.getCobrosCapital()+"-"+detAmort.getTotalRecibir());
		return listDetAmort;
	}

	@Override
	@Transactional(readOnly = true)
	public AmortizacionResponse consultaTablaAmortizacion(String numSolOCodProyecto, Long idTipoTabla) {
		AmortizacionResponse response = null;
		this.tblRepository.consultaTablaAmortizacionPorCodProyecto(numSolOCodProyecto, idTipoTabla);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SimuladorPrincipalResponse> simuladorPrincipal(SimuladorRequest simulador) throws Exception {
		List<DetaAmortizacion> listDetalleAmort = new ArrayList<>();
		List<ParametroResponse> listParam = new ArrayList<>();
		String tipo = null;
		int lastIdx = 0;
		SimuladorPrincipalResponse response = null;
		;

		List<SimuladorPrincipalResponse> listResponse = new ArrayList<>();
		double porcentajeRendimientoNeto = 0.0;
		double totalRecibirBanco = 0.0;
		double gananciaBanco = 0.0;
		Proyecto proyecto = proyectoService.consultaProyecto(simulador.getCodigoProyecto());
		double montoInversion = simulador.getInversion().doubleValue();

		listParam = paramService.findByParametro("SIMULADOR_PRI");
		for (ParametroResponse emp : listParam) {
			if (emp.getCodParametro().contains("MULTIPLO")) {
				tipo = emp.getDescripcion();

				listDetalleAmort = this.generaDetalleTblAmortizacion(proyecto.getPeriodoPago(), montoInversion,
						simulador.getPlazo(), null, Double.parseDouble(emp.getValor()), null);
				lastIdx = listDetalleAmort.size() - 1;

				response = new SimuladorPrincipalResponse();
				response.setTipo(tipo);
				response.setInteresGanado(df.format(listDetalleAmort.get(lastIdx).getRendimientoMensual()));
				response.setTotalRecibir(df.format(listDetalleAmort.get(lastIdx).getTotalRecibir()));
				listResponse.add(response);
			}
			if (emp.getCodParametro().contains("BANCOS")) {
				tipo = emp.getDescripcion();
				porcentajeRendimientoNeto = Double.valueOf(emp.getValor()) / 100;

				gananciaBanco = ((montoInversion * porcentajeRendimientoNeto * 30) / 360) * simulador.getPlazo();
				totalRecibirBanco = gananciaBanco + montoInversion;

				response = new SimuladorPrincipalResponse();
				response.setTipo(tipo);
				response.setInteresGanado(df.format(gananciaBanco));
				response.setTotalRecibir(df.format(totalRecibirBanco));

				listResponse.add(response);
			}
		}
		return listResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public TblAmortizacionResponse consultaTblAmortizacionFilter(Long numeroSolicitud) {
		return tblRepository.consultaTblAmortizacionFilter(numeroSolicitud);
	}

	@Override
	@Transactional(readOnly = true)
	public TablaAmortizacion consultaTblAmortizacionPorNumSolicitud(String numeroSolicitud) {
		return tblRepository.consultaTablaAmortizacionPorNumSolicitud(Long.parseLong(numeroSolicitud));
	}

	@Override
	@Transactional
	public AmortizacionResponse updateTblAmortizacion(UpdateSolicitudRequest solicitudRequest) throws Exception {
		TablaAmortizacion tblAmortizacion = new TablaAmortizacion();
		AmortizacionResponse amortResponse = null;
		String respuesta = null;
		// Solicitud solicitud = new Solicitud();
		try {
			double montoInversion = Double.parseDouble(solicitudRequest.getInversion().replace(",", ""));
			tblAmortizacion = this.consultaTblAmortizacionPorNumSolicitud(solicitudRequest.getNumeroSolicitud());
			if (tblAmortizacion != null) {
				respuesta = detTblAmortService.updateDetalleAmortizacion(tblAmortizacion.getIdTblAmortizacion());
				if (respuesta.contains("ok")) {
					tblAmortizacion.setEstado("I");
//					tblAmortizacion.setFechaModificacion(util.getLocalDateTime());
//					tblAmortizacion.setUsuarioModificacion(solicitudRequest.getUsuario());
					tblRepository.save(tblAmortizacion);

					Solicitud solicitud;
					solicitud = solicitudService.findById(Long.parseLong(solicitudRequest.getNumeroSolicitud()));

					AmortizacionRequest amortRequest = new AmortizacionRequest();
					// amortRequest.setNumeroSolictud(tblAmortizacion.getNumeroSolicitud());
					amortRequest.setCodigoProyecto(solicitud.getProyecto().getIdProyecto());
					amortRequest.setInversion(montoInversion);
					amortRequest.setUsuario(solicitudRequest.getUsuario());
					// amortRequest.setIdentificacion(solicitud.getPersona().getIdentificacion());
					amortResponse = new AmortizacionResponse();
					// amortResponse = this.guardaTablaAmortizacion(amortRequest);
					this.CreaTablaAmortizacion(amortRequest);
				}
			} else {
				LOG.info("No exite tabla de amortización numeroSolicitud: " + solicitudRequest.getNumeroSolicitud());
				throw new Exception("No exite tabla de amortización para la solicitud ingresada");
			}
		} catch (Exception e) {
			LOG.error("updateTblAmortizacion: Error numeroSolicitud " + solicitudRequest.getNumeroSolicitud() + "-"
					+ e.getMessage());
			throw new Exception(e.getMessage());
			// return amortResponse = null;
		}
		return amortResponse;
	}

	@Override
	@Transactional
	public String creaSolicitudYTablaAmortizacion(SolicitudRequest solicitudRequest) throws Exception {
		Solicitud solCreada;
		TablaAmortizacion tbl;
		Solicitud solicitud;
		PersonaResponse persona = new PersonaResponse();
		AmortizacionRequest amortRequest;
		AmortizacionResponse amortResponse = null;
		boolean existe;

		existe = solicitudService.consultaSolicitudExistente(solicitudRequest.getCodigoProyecto(),
				solicitudRequest.getIdentificacion());

		if (existe) {
			throw new Exception("Ya tiene una solicitud de inversión en estado EN PROCESO");
		}
		
		persona = personaService.consultaDatosPersona(solicitudRequest.getIdentificacion());
	
		if (persona == null) {
			throw new Exception("No exite datos de la cuenta" + solicitudRequest.getIdentificacion());
		}
		int periodo = proyectoService.consultaPeriodoPago(solicitudRequest.getCodigoProyecto());
		// solicitud.setNumeroSolicitud(solicitudService.getSecuenciaNumeroSolicitud());
//					solicitud.setUsuario(persona.getUsuario());
//					solicitud.setUsuarioContacto(persona.getUsuarioContacto());
//					solicitud.setTipoPersona(persona.getTipoPersona());
//					solicitud.setTipoContacto(persona.getTipoContacto());
//					Persona  = new Persona();
//					per.setIdentificacion(persona.getIdentificacion());
//					solicitud.setPersona(per);
//					solicitud.setFechaVigencia(null);
//					solicitud.setFechaCreacion(LocalDateTime.now());
		
		solicitud = new Solicitud();
		solicitud.setUsuarioCreacion(new Cuenta(solicitudRequest.getIdentificacion()));
		solicitud.setInversionista(new Cuenta(solicitudRequest.getIdentificacion()));
		solicitud.setProyecto(new Proyecto(solicitudRequest.getCodigoProyecto()));
		solicitud.setEstadoActual(new TipoEstado("BO"));
		solicitud.setFechaGeneracion(new Date());//
		// solCreada= solicitudService.guardaSolicitud(solicitud);
		
		DatosInversion datoInversion = new DatosInversion();
		datoInversion.setTablaAmortizacion(true);
		datoInversion.setFormulario(false);
		datoInversion.setDocumentacion(false);
		datoInversion.setPago(false);
		datoInversion.setSolicitud(solicitud);

		if (persona.getTipoPersona().contains("NAT")) {
			if (persona.getDatosFormulario() != null) {
				datoInversion.setFormulario(true);
				if (persona.getDatosFormulario().getPersonaDocumento() != null) {
					datoInversion.setDocumentacion(true);
				}

			}
		} else {// JUR
			if (persona
					.getDatosFormulario() != null /*
													 * && persona.getDatosFormulario().getPersonaDomicilio() != null &&
													 * persona.getDatosFormulario().getPersonaEstadoFinanciero() != null
													 * && persona.getDatosFormulario().getPersonaRepresentanteLegal() !=
													 * null && persona.getDatosFormulario().getPersonaFirmaAutorizada()
													 * != null
													 */) {
				datoInversion.setFormulario(true);
				if (persona.getDatosFormulario().getPersonaDocumento() != null) {
					datoInversion.setDocumentacion(true);
				}
			}
		}

		// DatosInversion datoInversionCreado = new DatosInversion();
		// datoInversionCreado = datosInversion.saveDatoInversion(datoInversion);
	
		solicitud.setDatosInversion(datoInversion);
		double montoInversion = Double.parseDouble(solicitudRequest.getInversion().replace(",", ""));
		// solCreada= solicitudService.guardaSolicitud(solicitud);
		amortRequest = new AmortizacionRequest();
		// amortRequest.setNumeroSolictud(solicitud.getNumeroSolicitud());
		amortRequest.setCodigoProyecto(solicitudRequest.getCodigoProyecto());
		amortRequest.setInversion(montoInversion);
//					amortRequest.setUsuario(solCreada.getUsuario());
		amortRequest.setIdentificacion(solicitud.getInversionista().getIdCuenta());
		amortRequest.setIdTipoTabla((long) 1);// 1: tabla amortizacion inversionista
		amortRequest.setPeriodoPago(periodo);
		amortResponse = new AmortizacionResponse();
	
		tbl = this.CreaTablaAmortizacion(amortRequest);
		if (tbl == null) {
			throw new Exception("Error al crear la Tabla de amortización");
		}
	
		solicitud.setAmortizacion(tbl);
		
		HistorialDeSolicitud historialSol = new HistorialDeSolicitud();
		historialSol.setSolicitud(solicitud);
		historialSol.setTablaCambiar("Estado");
		historialSol.setValorActual("BO");
		historialSol.setValorAnterior("Vacio");
		historialSol.setObservacion("Creacion de solicitud en proceso");
		historialSol.setUsuarioModificacion(new Cuenta(solicitudRequest.getIdentificacion()));
		solicitud.setHistorial(historialSol);
		solCreada = solicitudService.guardaSolicitud(solicitud);
//					

		if (solCreada == null) {
			throw new Exception("No se pudo conectar con la Base de datos");
		}
//						

//						double montoInversion = Double.parseDouble(solicitudRequest.getInversion().replace(",", ""));
//												
//						amortRequest = new AmortizacionRequest();
//						amortRequest.setNumeroSolictud(solCreada.getNumeroSolicitud().toString());
//						amortRequest.setCodigoProyecto(solicitudRequest.getCodigoProyecto());
//						amortRequest.setInversion(montoInversion);
////						amortRequest.setUsuario(solCreada.getUsuario());
//						amortRequest.setIdentificacion(solCreada.getInversionista().getIdCuenta());
//						amortRequest.setIdTipoTabla((long) 1);//1: tabla amortizacion inversionista
//						amortResponse = new AmortizacionResponse();
//						amortResponse = this.guardaTablaAmortizacion(amortRequest);
//					}
		
		return "" + solCreada.getNumeroSolicitud();
	}

	@Override
	@Transactional(readOnly = true)
	public TablaAmortizacion consultaTblAmortizacionPorCodProyecto(String codigoPoryecto, Long idTipoTabla) {
		return tblRepository.consultaTablaAmortizacionPorCodProyecto(codigoPoryecto, idTipoTabla);
	}

//	@Override
//	@Transactional
//	// proceso de crear tabla de amortizacion automatica
//	public void generacionTablaAmortizacionMasiva(List<DatosAmortizacion> listDatoTbl) throws Exception {
//		List<TablaAmortizacion> listTblAmort = new ArrayList<>();
//		List<String> msjError = new ArrayList<>();
//		List<DetaAmortizacion> listDetalleAmort = new ArrayList<>();
//
//		if (!listDatoTbl.isEmpty()) {
//			for (DatosAmortizacion ldt : listDatoTbl) {
//				
//				
//				listDetalleAmort = this.generaDetalleTblAmortizacion( ldt.getInversion(), ldt.getPlazo(),
//						ldt.getCodigoProyecto(), ldt.getPorcentajeRendimiento().doubleValue(), ldt.getFechaEfectiva());
//
//				if (!listDetalleAmort.isEmpty()) {
//					listTblAmort.add(this.llenaTablaAmortizacion(ldt, listDetalleAmort));
//					listDetalleAmort.clear();
//				} else {
//					// guardar errores
//					msjError.add("Error no se generó la tabla de amorizacion: codigoProyecto: "
//							+ ldt.getCodigoProyecto().concat(" tipoTabla: " + ldt.getIdTipoTabla())
//									.concat(" numeroSolicitud: " + ldt.getNumeroSolictud()));
//				}
//			}
//
//			if (!listTblAmort.isEmpty()) {
//				// guardar todas las tablas generadas
//				tblRepository.saveAll(listTblAmort);
//				listTblAmort.clear();
//			}
//			// insertar errores de la tabla
//			if (!msjError.isEmpty()) {
//				List<BitacoraProceso> listBitacora = new ArrayList<>();
//				msjError.forEach(e -> {
//					BitacoraProceso bitacora = new BitacoraProceso();
//					bitacora.setTipo("ERROR");
//					bitacora.setProceso("generacionTablaAmortizacionMasiva".toUpperCase());
//					bitacora.setFecha(LocalDateTime.now());
//					bitacora.setDescripcion(e);
//					listBitacora.add(bitacora);
//				});
//
//				bitacoraService.guardaBitacoraAll(listBitacora);
//
//				msjError.clear();
//				listBitacora.clear();
//			}
//
//		} else {
//			LOG.error("Debe enviar datos para la generación de la tabla");
//		}
//	}

	public TablaAmortizacion llenaTablaAmortizacion(DatosAmortizacion amortizacion,
			List<DetaAmortizacion> listDetalleAmort) {
		TablaAmortizacion tbl = new TablaAmortizacion();
		int lastIdx = 0;
		List<DetalleAmortizacion> listDetAmort = new ArrayList<>();

		lastIdx = listDetalleAmort.size() - 1;

		// tbl.setNumeroSolicitud(amortizacion.getNumeroSolictud());
		tbl.setMontoInversion(new BigDecimal(amortizacion.getInversion()));
		tbl.setPlazo(amortizacion.getPlazo());
		tbl.setRendimientoNeto(amortizacion.getPorcentajeRendimiento());
		tbl.setRendimientoTotalInversion(new BigDecimal(listDetalleAmort.get(lastIdx).getRendimientoMensual()));
		tbl.setTotalRecibir(new BigDecimal(listDetalleAmort.get(lastIdx).getTotalRecibir().doubleValue()).setScale(2,
				RoundingMode.HALF_UP));
		tbl.setFechaCreacion(util.getLocalDateTime());
		tbl.setUsuarioCreacion(new Cuenta("ADMIN"));
		tbl.setFechaEfectiva(amortizacion.getFechaEfectiva());
		tbl.setEstado("A");

		if (amortizacion.getIdTipoTabla() == 3) {
			// solo para tabla general - promotor
//			Proyecto proyecto = new Proyecto();
//			proyecto.setIdProyecto(amortizacion.getCodigoProyecto());
			// tbl.setProyecto(proyecto);
		}

		TipoTabla tipoTabla = new TipoTabla();
		tipoTabla.setIdTipoTabla(amortizacion.getIdTipoTabla());
		tbl.setTipoTabla(tipoTabla);

		listDetalleAmort.stream().forEach(det -> {
			DetalleAmortizacion detalleTbl = new DetalleAmortizacion();

			detalleTbl.setDetalleCobro(det.getDetalleCobro());
			detalleTbl.setCobrosCapital(new BigDecimal(det.getCobrosCapital()));
			detalleTbl.setSaldoCapital(new BigDecimal(det.getSaldoCapital()));
			detalleTbl.setRendimientoMensual(new BigDecimal(det.getRendimientoMensual()));
			detalleTbl.setTotalRecibir(
					new BigDecimal(det.getTotalRecibir().doubleValue()).setScale(2, RoundingMode.HALF_UP));
			;
			detalleTbl.setTablaAmortizacion(tbl);
			detalleTbl.setEstado("A");
			detalleTbl.setCuota(det.getCuota());
			detalleTbl.setFechaEstimacion(det.getFechaCobro());
			detalleTbl.setEstadoPago("Pendiente de pago");
			listDetAmort.add(detalleTbl);
		});

		tbl.setDetallesTblAmortizacion(listDetAmort);

		return tbl;
	}

	//
	public LocalDate calculafechaCobro(LocalDate fecha) {
		fecha = fecha.plusDays(30);
		if (fecha.getDayOfWeek().toString().equals("SATURDAY")) {
			fecha = fecha.plusDays(2);
		}
		if (fecha.getDayOfWeek().toString().equals("SUNDAY")) {
			fecha = fecha.plusDays(1);
		}
		return fecha;
	}

	@Override
	@Transactional
	public void procedimientoActualizaFechasCobro(String codigoProyecto, Long idTipoTabla, LocalDate fechaEfectiva) {
		try {
			tblRepository.actualizaFechasCobro(codigoProyecto, idTipoTabla, fechaEfectiva);
		} catch (Exception e) {
			LOG.error("Error procedimientoActualizaFechasCobro " + e.getMessage());
		}

	}
}
