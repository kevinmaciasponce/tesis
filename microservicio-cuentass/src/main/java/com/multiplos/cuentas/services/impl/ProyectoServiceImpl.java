package com.multiplos.cuentas.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiplos.cuentas.models.Calificacion;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.FormaPago;
import com.multiplos.cuentas.models.HistorialDeProyecto;
import com.multiplos.cuentas.models.HistorialDeSolicitud;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.ProyectoRuta;
import com.multiplos.cuentas.models.RangoPago;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;
import com.multiplos.cuentas.pojo.promotor.ProyectoFormulario;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.promotor.CodigoYFecha;
import com.multiplos.cuentas.pojo.promotor.EmpresaRequest;
import com.multiplos.cuentas.pojo.proyecto.AvanceInversionResponse;
import com.multiplos.cuentas.pojo.proyecto.IndicadorResponse;
import com.multiplos.cuentas.pojo.proyecto.ProyectoRequest;
import com.multiplos.cuentas.pojo.proyecto.ProyectoResponse;
import com.multiplos.cuentas.pojo.proyecto.PruebaDestino;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocPagoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.repository.FacturaRepository;
import com.multiplos.cuentas.repository.HistorialDeProyectoRepository;
import com.multiplos.cuentas.repository.ProyectoRepository;
import com.multiplos.cuentas.repository.RangPagoRepository;
import com.multiplos.cuentas.repository.SolicitudRepository;
import com.multiplos.cuentas.repository.TablaAmortizacionRepository;
import com.multiplos.cuentas.services.BlobStorageService;
import com.multiplos.cuentas.services.EmpresaService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.TipoDocumentoService;

@Service
public class ProyectoServiceImpl implements ProyectoService {

	private static final Logger LOG = LoggerFactory.getLogger(CuentaServiceImpl.class);
	private ProyectoRepository proyectoRepository;
	private ParametroService parametroService;
	private HistorialDeProyectoRepository historyRepository;
	private DecimalFormatSymbols simb;
	private TablaAmortizacionRepository tablaAmortizacionRepository;
	private DecimalFormat df;
	private SolicitudRepository solicitudRepository;
	private EmpresaService empresaService;
	private FacturaRepository facturaRepository;
	private TipoDocumentoService tipoDocService;
	private BlobStorageService blobStorageService;
	private RangPagoRepository rangoPagoRepository;
	
	@Autowired
	public ProyectoServiceImpl(ProyectoRepository proyectoRepository
			, ParametroService parametroService,
			HistorialDeProyectoRepository historyRepository, 
			SolicitudRepository solicitudRepository,
			TablaAmortizacionRepository tablaAmortizacionRepository,
			@Lazy EmpresaService empresaService,
			FacturaRepository facturaRepository,
			TipoDocumentoService tipoDocService,
			BlobStorageService blobStorageService,
			RangPagoRepository rangoRepository) {
		
		
		this.rangoPagoRepository=rangoRepository;
		this.proyectoRepository = proyectoRepository;
		this.parametroService = parametroService;
		this.solicitudRepository = solicitudRepository;
		this.tablaAmortizacionRepository = tablaAmortizacionRepository;
		this.historyRepository = historyRepository;
		this.empresaService = empresaService;
		this.facturaRepository=facturaRepository;
		this.tipoDocService=tipoDocService;
		this.blobStorageService=blobStorageService;
		simb = new DecimalFormatSymbols();
		simb.setDecimalSeparator(',');
		simb.setGroupingSeparator('.');
		df = new DecimalFormat("#,###.00", simb);
	}

	public Proyecto findById(String idProyecto, Boolean nulleable) throws Exception {
		Optional<Proyecto> proyecto;
		proyecto = proyectoRepository.findById(idProyecto);
		if (proyecto.isEmpty()) {
			if (nulleable) {
				return null;
			} else {
				throw new CuentaException("No existe proyecto con código: " + idProyecto);
			}
		}
		return proyecto.get();
	}

	public long counProyect(Long idEmpresa) throws Exception {
		Long count=null;
		// ExampleMatcher caseInsensitiveExampleMatcher =
		// ExampleMatcher.matchingAll().withIgnoreCase();
		 ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matchingAny()
			    //  .withMatcher("empresa", ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
			      ;
		Proyecto proyectoExample = new Proyecto();
		proyectoExample.setEmpresa(new Empresa(idEmpresa));
		//proyectoExample.setRonda(2);
		Example<Proyecto> example = Example.of(proyectoExample, caseInsensitiveExampleMatcher);
		LOG.warn(""+count);
		count = this.proyectoRepository.count(example);
		LOG.warn(""+count);
		return count;
	}
	
	public Boolean existProyecto(Proyecto Pexample) {
		 ExampleMatcher matcher = ExampleMatcher.matchingAll();
//				 .withMatcher("estadoActual", ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
//				 .withMatcher("empresa", ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase());
		 Example<Proyecto> example = Example.of(Pexample,matcher);
		 
		 return this.proyectoRepository.exists(example);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Proyecto> findAll() {
		List<Proyecto> proyectos = new ArrayList<>();
		
		proyectos = proyectoRepository.findAll(Sort.by("fechaLimiteInversion").descending());
		
		return proyectos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProyectoResponse> consultaProyectos(int partitions, int group) throws Exception {
	
		List<ProyectoResponse> proyectos = new ArrayList<>();
		try {
		proyectos= this.proyectoRepository.findAllProyectoResponse(PageRequest.of(partitions, group));
		}catch(IllegalArgumentException i) {
			throw new IllegalArgumentException(i.getMessage());
		}
		proyectos.stream().forEach(x->{
			try {
				if(x.getEstado().equals("AV")||x.getEstado().equals("PATF")) {
					x.setEstado("AVANCE");
				}
				if(x.getEstado().equals("FC")||x.getEstado().equals("TF")||x.getEstado().equals("EXI")||x.getEstado().equals("PLQ")) {
					x.setEstado("EXITOSO");
				}
				x.setEmpresa(this.empresaService.getByfilter(EmpresaRequest.builder().id(x.getIdEmpresa()).build()));
				x.setProyectoRutas(this.proyectoRepository.getRutasResponseByProyecto(x.getCodigoOperacion()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
//		for(ProyectoResponse proyecto: proyectos) {
//			proyecto.setEmpresa(this.empresaService.getByfilter());
//			
//		}
//		if (proyectos.isEmpty()) {
//			proyectos = null;
//		} else {
//			proyectos.clear();
//			LOG.info("consultaProyectosFormat: No se encontro proyectos.");
//		}
		return proyectos;
	}

	// nuevo
	@Override
	@Transactional(readOnly = true)
	public List<proyectosResponse> consultasPorEstado(String estado, String codProyecto) {
		Solicitud sol = new Solicitud();
		List<proyectosResponse> proyectosResponse = new ArrayList<>();
		proyectosResponse = proyectoRepository.consultaPorProyectoYEstado(estado, codProyecto);

		return proyectosResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<proyectosResponse> consultasPorEstado2(String estado, FilterProyectoRequest filter) {
		Solicitud sol = new Solicitud();
		List<proyectosResponse> proyectosResponse = new ArrayList<>();
		proyectosResponse = proyectoRepository.consultaPorProyectoYEstado2(estado, filter.getCodProyecto(),
				filter.getIdTipoCalificacion(), filter.getIdActividad());
		return proyectosResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public Proyecto consultaProyecto(String idProyecto) {

		return proyectoRepository.consultaProyecto(idProyecto);
	}

	@Override
	@Transactional(readOnly = true)
	public int consultaPeriodoPago(String idProyecto) throws CuentaException {
		int periodo = proyectoRepository.consultaPeriodoPago(idProyecto);
		return periodo;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean validarProyectoAntesDeFechaEfectiva(String cod) throws Exception {
		int i;
		i = proyectoRepository.validarProyectoAntesDeFechaEfectiva(cod);
		if (i > 0) {
			throw new Exception("Existen Solicitudes Pendientes");
		}
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public String guardar(Proyecto proyecto) {
		proyectoRepository.save(proyecto);
		return "Guardado";
	}

	public Object generatedCode() {
		CodigoYFecha ult;
		ult = this.proyectoRepository.getCodeByFecha(PageRequest.of(0, 1));
		LOG.warn(ult.getCodigo());

		String[] split = ult.getCodigo().split("-");
		char c;
		LocalDateTime tim = ult.getFecha();
		Instant instant = tim.atZone(ZoneId.systemDefault()).toInstant();
		Date date = Date.from(instant);
		Calendar calendario = Calendar.getInstance();

		calendario.setTime(date);
		Integer wr = calendario.get(Calendar.WEEK_OF_YEAR);
		calendario.setTime(new Date());
		Integer wh = calendario.get(Calendar.WEEK_OF_YEAR);
		int val = Integer.parseInt(split[1]);
		Formatter fmt = new Formatter();
		val++;
		fmt.format("%04d", val);
		if (wr.equals(wh)) {
			c = split[2].charAt(0);
			c++;
		} else {
			c = 'A';
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YY");
		System.out.println("R-" + fmt + "-" + c + "-" + formatter.format(LocalDate.now()).toString().replace("-", "")
				+ "-" + "EC");
		String code = "R-" + fmt + "-" + c + "-" + formatter.format(LocalDate.now()).toString().replace("-", "") + "-"
				+ "EC";
		return code;
	}

	@Override
	public Object putProyecto(ProyectoFormulario form) throws Exception {
		String response = null;
		Proyecto proyect = null;
		proyect = new Proyecto();
		
		if(this.existProyecto(Proyecto.builder().estadoActual(new TipoEstado("BO")).empresa(new Empresa(form.getIdEmpresa())).build())) {
			throw new CuentaException("Ya posse un proyecto en estado BO");
		}
		if (form.getIdProyecto() != null) {
			proyect = this.findById(form.getIdProyecto(), false);
			response = "Proyecto actualizado correctamente";
		} else {
			response = "Proyecto guardado correctamente";
			proyect.setIdProyecto(this.generatedCode().toString());
		}
		if(form.getIdCalificacion()!=null) {
			proyect.setCalificacion(new Calificacion(form.getIdCalificacion()));
		}
		proyect.setDestinoFinanciamiento(form.getDestinoFinanciamiento());
		EmpresaResponse empre = this.empresaService
				.getByfilter(new EmpresaRequest(form.getIdEmpresa(), form.getUserCompose()));
		if (empre == null) {
			throw new CuentaException("No posee ninguna empresa");
		}
		proyect.setRonda((int) counProyect(form.getIdEmpresa())+1);
		proyect.setEmpresa(new Empresa(empre.getId()));
		proyect.setPeriodoPago(form.getPeriodoPago());
		proyect.setPagoInteres(form.getPagoInteres());
		proyect.setPagoCapital(form.getPagoCapital());
		proyect.setMontoSolicitado(form.getMontoSolicitado());
		proyect.setUsuarioCreacion(form.getUserCompose());
		proyect.setPlazo(form.getPlazo());
		proyect.setTasaEfectivaAnual(form.getTasaEfectivaAnual());
		proyect.setTipoInversion(form.getTipoInversion());
		this.guardar(proyect);
		return proyect.getIdProyecto();
	}
	@Override
	public Object getProyectoResponse(ProyectoRequest filter)throws Exception{
		
		List<proyectosResponse> response=null;
		response= this.proyectoRepository.consultaProyectosResponse(filter.getIdEmpresa(), filter.getEstadoActual());
		return response;
	}
	
	public Object putHistorialDeProyecto (HistorialDeProyecto hist) {
		return null;
	}
	

	@Override
	@Transactional(readOnly = true)
	public BigDecimal consultaTasaEfectiva(String idProyecto) {
		return proyectoRepository.consultaTasaEfectiva(idProyecto);
	}

//	public List<ProyectoResponse> seteaProyectosResponse(List<Proyecto> proyectos) throws Exception {
//		List<ProyectoResponse> proyectosResponse = new ArrayList<>();
//		AvanceInversionResponse avanceInversion;
//
//		for (Proyecto p : proyectos) {
//			ProyectoResponse pro = new ProyectoResponse();
//
//			pro.setCodigoOperacion(p.getIdProyecto());
//			pro.setFechaInicioInversion(p.getFechaInicioInversion());
//			pro.setFechaLimiteInversion(p.getFechaLimiteInversion());
//			pro.setTipoInversion(p.getTipoInversion());
//			pro.setDestinoFinanciamiento(p.getDestinoFinanciamiento());
//			pro.setPlazo(p.getPlazo());
//			pro.setPagoInteres(p.getPagoInteres());
//			pro.setPagoCapital(p.getPagoCapital());
//			
//			if(p.getIndicadores()!=null) {
//				IndicadorResponse indicador = new IndicadorResponse();
//				indicador.setAnio(p.getIndicadores().getAnio());
//				indicador.setIdIndicador(p.getIndicadores().getIdIndicador());
//				indicador.setSolvencia(p.getIndicadores().getSolvencia());
//				indicador.setPorcentajeSolvencia(df.format(p.getIndicadores().getPorcentajeSolvencia()));
//				indicador.setLiquidez(p.getIndicadores().getLiquidez());
//				indicador.setPorcentajeLiquidez(df.format(p.getIndicadores().getPorcentajeLiquidez()));
//				indicador.setRetornoCapital(p.getIndicadores().getRetornoCapital());
//				indicador.setPorcentajeRetornoCapital(df.format(p.getIndicadores().getPorcentajeRetornoCapital()));
//				indicador.setGarantia(p.getIndicadores().getGarantia());
//				indicador.setPorcentajeGarantia(df.format(p.getIndicadores().getPorcentajeGarantia()));
//				pro.setIndicadores(indicador);
//			}
//			
//
//			EmpresaResponse empresa = this.empresaService
//					.getByfilter(EmpresaRequest.builder().id(p.getEmpresa().getIdEmpresa()).build());
//			pro.setEmpresa(empresa);
//			pro.setCalificacion(p.getCalificacion());
//			pro.setProyectoRutas(p.getProyectoRutas());
//			pro.setTasaEfectivaAnual(df.format(p.getTasaEfectivaAnual()));
//			pro.setMontoSolicitado(df.format(p.getMontoSolicitado()));
//			avanceInversion = new AvanceInversionResponse();
//		avanceInversion = this.consultaAvanceInversion(p.getIdProyecto(), p.getMontoSolicitado());
//			pro.setAvanceInversion(avanceInversion);
//
//			if (LocalDate.now().isBefore(p.getFechaInicioInversion())) {
//				pro.setEstado("PRÓXIMAMENTE");
//			} else if (p.getEstadoActual().getIdEstado().contains("AV")
//					|| p.getEstadoActual().getIdEstado().contains("PATF")) {
//				pro.setEstado("AVANCE");
//			} else if (p.getEstadoActual().getIdEstado().contains("FC")
//					|| p.getEstadoActual().getIdEstado().contains("TF")
//					|| p.getEstadoActual().getIdEstado().contains("EXI")
//					|| p.getEstadoActual().getIdEstado().contains("PLQ")) {
//				pro.setEstado("EXITOSO");
//			}
//
//			proyectosResponse.add(pro);
//		}
//
//		if (!proyectosResponse.isEmpty()) {
//			// ordena por rutas img
//			for (ProyectoResponse p : proyectosResponse) {
//				Comparator<ProyectoRuta> porcAvanceComparator = Comparator.comparing(ProyectoRuta::getIdProyectoRuta);
//				Collections.sort(p.getProyectoRutas(), porcAvanceComparator); // ascendente
//			}
//		}
//
//		return proyectosResponse;
//	}

	public AvanceInversionResponse consultaAvanceInversion(String codigoOperacion, BigDecimal montoSolicitado) {
		AvanceInversionResponse avanceInversion;
		double avanceMonto = 0;
		double porcAvance;
		String paramDescripcion;
		String porcentaje;
		String monto;
		double montosProyecto;
		paramDescripcion = parametroService.consultaParametroDescripcion(codigoOperacion);
		montosProyecto = Double.valueOf(paramDescripcion);
		avanceMonto = proyectoRepository.consultaMontoTotalInversionPorProyecto(codigoOperacion).doubleValue();
		avanceMonto = avanceMonto + montosProyecto;

		porcAvance = (avanceMonto * 100) / montoSolicitado.doubleValue();

		if (avanceMonto > 0) {
			porcentaje = df.format(porcAvance);
			monto = df.format(avanceMonto);
		} else {
			monto = String.valueOf(avanceMonto);
			porcentaje = String.valueOf(porcAvance);
		}
		avanceInversion = new AvanceInversionResponse();
		avanceInversion.setCodigoOperacion(codigoOperacion);
		avanceInversion.setValor(monto);
		avanceInversion.setPorcentaje(porcentaje);
		return avanceInversion;
	}

	@Override
	@Transactional(readOnly = true)
	public String consultaNombreProyecto(String idProyecto) {
		return proyectoRepository.consultaNombreEmpresa(idProyecto);
	}

	@Override
	@Transactional(readOnly = true)
	public int consultaPlazo(String idProyecto) {
		return proyectoRepository.consultaPlazo(idProyecto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FilterEmpresa> filterEmpresas() {
		return proyectoRepository.consultaEmpresasActivas();
	}

	@Override
	@Transactional(readOnly = true)
	public List<FilterEmpresa> filterEmpresasxEstado(TipoEstado estadoActual) {
		return proyectoRepository.consultaEmpresasActivasxEstado(estadoActual);
	}

	@Override
	@Transactional(readOnly = true)
	public FilterEmpresa consultaEmpresa(String idProyecto) {
		return proyectoRepository.consultaEmpresa(idProyecto);
	}

	@Override
	@Transactional
	public void updateEstadoProyecto(String estadoActual, String estadoAnterior, String usuario,
			LocalDateTime fechaModificacion, String idProyecto) {
		proyectoRepository.updateEstadosProyecto(estadoActual, estadoAnterior, usuario, fechaModificacion, idProyecto);
	}

	@Override
	public List<FilterEmpresa> consultaProyectosPorEstado(String estadoActual) {
		return proyectoRepository.consultaProyectosPorEstado(estadoActual);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FilterEmpresa> consultaProyectosEnAvance() {
		return proyectoRepository.consultaProyectosEnAvance();
	}

	@Override
	@Transactional
	public String updateProyecto(HistorialDeProyecto history) {
		try {
			history.setFechaHistorial(LocalDateTime.now());
			history.setTablaCambiar("estado_actual");
			historyRepository.save(history);
			proyectoRepository.updateEstadosProyecto(history.getValorActual(), history.getValorAnterior(),
					history.getUsuarioCreacion(), history.getFechaHistorial(), history.getProyecto().getIdProyecto());
		} catch (Exception e) {
			LOG.error("save: problema al crear la cuenta " + e.getMessage());
			return "NO SE PUDO ACTUALIZAR EL PROYECTO";
		}

		return " PROYECTO ACTUALIZADO, CONSULTAR EL HISTORIAL";
	}
	
	@Override
	public proyectosResponse consultarProyectoResponse(String idProyecto) {
		proyectosResponse response;
		response= this.proyectoRepository.consultaProyectoResponse(idProyecto);
		return response;
	}
	
	public SolicitudDocPagoRequest getObjSolComprobantePago(String solicitud) {
		SolicitudDocPagoRequest comprobantePagoObj = new SolicitudDocPagoRequest();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			comprobantePagoObj = objectMapper.readValue(solicitud, SolicitudDocPagoRequest.class);
		} catch (IOException e) {
			LOG.error("Error " + e.toString());
		}
		return comprobantePagoObj;
	}
	
	@Override
	@Transactional
	public String guardaDocumentoComprobantePago(String datoPago,String codFact ,MultipartFile file, String tipoDocumento)throws Exception {
		// VALIDAMOS SI TIENE DATOS EL PARAMETRO SOLICITUD
		String respuestaEmail;
		if (datoPago.isEmpty()) {
			throw new Exception("Informacion incompleta");
		}
		// DECLARAMOS VARIABLES

		SolicitudDocPagoRequest solDocPago = new SolicitudDocPagoRequest();
		solDocPago = this.getObjSolComprobantePago(datoPago);

		String rutaArchivo;
		String contentType = file.getContentType().toLowerCase();// application/pdf
		String type = contentType.split("/")[1];// saca extension del archivo
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
		String nombreArchivo = "Proyecto:" + solDocPago.getCodProyecto() + "_" + timeStamp + "." + type;
		Proyecto proyecto;
		ParametroResponse paramResponse = new ParametroResponse();

		// CONSULTAMOS LA SOLICITUD
		proyecto = this.proyectoRepository.consultaProyecto(solDocPago.getCodProyecto());
		LOG.warn("consulto");
		double montoT = Double.parseDouble(solDocPago.getMonto().toString().replace(",", ""));
		BigDecimal monto = new BigDecimal(montoT);
		if (proyecto == null) {
			throw new Exception("No existe Proyecto: " + solDocPago.getCodProyecto());
		}
		BigDecimal valorFact = this.facturaRepository.totalFacturaByCod(codFact);
		if(valorFact ==null) {
			throw new CuentaException("No existe datos de factura para el proyecto");
		}
		if (valorFact.doubleValue() != monto.doubleValue()) {
			throw new Exception("El monto de la factura no coincide: " + "factura: "+valorFact.doubleValue());
		}

		// GUARDAMOS EL ARCHIVO EN UN CONTENEDOR

		try {
			// docResp = this.guardaDocumentos(null, solDocPago, file, tipoDocumento);
			paramResponse = parametroService.findByParametroCodParametro("CONTDEPOS");
			rutaArchivo = blobStorageService.uploadFile(file, nombreArchivo, paramResponse.getValor());
			LOG.warn("cargado");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception("Error al guardar documento" + e.getMessage());
		}
		// CREAMOS LA NUEVA TRANSACCION Y LA GUARDAMOS EN SOLICITUD

		Transaccion transaccion = new Transaccion();
		transaccion.setProyecto(proyecto);
		transaccion.setNumeroComprobante(solDocPago.getNumeroComprobante());
		transaccion.setFechaTransaccion(LocalDate.parse(solDocPago.getFechaTransaccion()));
		double montoInversion = Double.parseDouble(solDocPago.getMonto().toString().replace(",", ""));
		transaccion.setMonto(new BigDecimal(montoInversion));
		transaccion.setFormaPago(new FormaPago(solDocPago.getFormaPago()));
		transaccion.setNombreDocumento(nombreArchivo);
		transaccion.setRutaComprobante(rutaArchivo);
		transaccion.setUsuarioCreacion(proyecto.getEmpresa().getCuenta());
		transaccion.setFechaCreacion(LocalDateTime.now());
		transaccion.setDocumento(tipoDocService.findByIdTipoDocumento((long) 2));
		transaccion.setDepositante(solDocPago.getDepositante());
		proyecto.setEstadoActual(new TipoEstado("PC"));
		proyecto.getTransacciones().add(transaccion);
		proyecto.setAceptaInformacionCorrecta("S");
		proyecto.setAceptaIngresarInfoVigente("S");
		proyecto.setAceptaLicitudFondos("S");

		
		LOG.warn("guardado");

		return "Transaccion guardada correctamente";
	}

	@Override
	public PruebaDestino findPrueba(String idProyecto)throws Exception {
		PruebaDestino prueba;
		
		prueba =	proyectoRepository.findByProyect(idProyecto);
		
		return prueba;
	}

	@Override
	public Object GuardarRangoPago(RangoPago pago) throws Exception {
		this.rangoPagoRepository.save(pago);
		return "Rango Guardado Exitosamente";
	}
	

}
