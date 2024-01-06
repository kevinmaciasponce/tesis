package com.multiplos.cuentas.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiplos.cuentas.models.Banco;
import com.multiplos.cuentas.models.Ciudad;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.DatosInversion;
import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.DocumentoAceptado;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.FormaPago;
import com.multiplos.cuentas.models.HistorialDeProyecto;
import com.multiplos.cuentas.models.HistorialDeSolicitud;
import com.multiplos.cuentas.models.Pais;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.PersonaDocumento;
import com.multiplos.cuentas.models.PersonaDomicilio;
import com.multiplos.cuentas.models.PersonaEstadoFinanciero;
import com.multiplos.cuentas.models.PersonaFirma;
import com.multiplos.cuentas.models.PersonaInfoAdicional;
import com.multiplos.cuentas.models.PersonaRepresentanteLegal;
import com.multiplos.cuentas.models.PersonaTipoCuenta;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.models.SolicitudDocumentos;
import com.multiplos.cuentas.models.TablaAmortizacion;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.models.TipoSolicitud;

import com.multiplos.cuentas.models.Transaccion;
import com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse;
import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;
import com.multiplos.cuentas.pojo.documento.DocumentoSolicitudResponse;
import com.multiplos.cuentas.pojo.documento.MultiplesDocumentosRequest;
import com.multiplos.cuentas.pojo.empleado.PersonalDetalleResponse;
import com.multiplos.cuentas.pojo.formulario.FormDatosIngresadoResponse;
import com.multiplos.cuentas.pojo.formulario.FormDomicilioResponse;
import com.multiplos.cuentas.pojo.formulario.FormJurRequest;
import com.multiplos.cuentas.pojo.formulario.FormNatRequest;
import com.multiplos.cuentas.pojo.formulario.FormRepresentanteLegalResponse;
import com.multiplos.cuentas.pojo.formulario.PaisResponse;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersInfoAdicionalResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoProyecto;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoSolicitud;
import com.multiplos.cuentas.pojo.proyecto.ProyectoPorEstadoResponse;
import com.multiplos.cuentas.pojo.proyecto.UpdatePYSRequest;
import com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa;
import com.multiplos.cuentas.pojo.solicitud.NumeroSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocPagoRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocumentoRequest;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudResponse.SolicitudResponseBuilder;
import com.multiplos.cuentas.pojo.solicitud.SolicitudVigenteResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.CantidadSolicitudResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterProyectoRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterSolicitudRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionAprobTransFondoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnProcesoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerencia;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoGerenciaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoInversionista;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionEnTransitoResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionHistorialEstado;
import com.multiplos.cuentas.pojo.solicitud.filter.TransaccionesPorConciliarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionPorConfirmarResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersona;
import com.multiplos.cuentas.pojo.solicitud.filter.InversionSolPersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.SolicitudPersonaResponse;
import com.multiplos.cuentas.pojo.solicitud.filter.inversionResponse;
import com.multiplos.cuentas.pojo.solicitud.responseManagerOper.SolicitudPapResponse;
import com.multiplos.cuentas.pojo.transaccion.conciliaTransaccion;
import com.multiplos.cuentas.repository.CuentaRepository;
import com.multiplos.cuentas.repository.DetalleTblAmortizacionRepository;
import com.multiplos.cuentas.repository.EmpresaRepository;
import com.multiplos.cuentas.repository.PersonaInfoAdicionalRepository;
import com.multiplos.cuentas.repository.SolicitudDocumentosRepository;
import com.multiplos.cuentas.repository.SolicitudRepository;
import com.multiplos.cuentas.services.BlobStorageService;
import com.multiplos.cuentas.services.DatosInversionService;
import com.multiplos.cuentas.services.DocumentoAceptadoService;
import com.multiplos.cuentas.services.PersonaInternaService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.GeneraPdfService;
import com.multiplos.cuentas.services.PaisService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.PersonaDocumentoService;
import com.multiplos.cuentas.services.PersonaDomicilioService;
import com.multiplos.cuentas.services.PersonaEstadoFinancieroService;
import com.multiplos.cuentas.services.PersonaFirmaService;
import com.multiplos.cuentas.services.PersonaInfoAdicionalService;
import com.multiplos.cuentas.services.PersonaRepresentanteLegalService;
import com.multiplos.cuentas.services.PersonaService;
import com.multiplos.cuentas.services.PersonaTipoCuentaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TablaAmortizacionService;
import com.multiplos.cuentas.services.TipoDocumentoService;
import com.multiplos.cuentas.services.TipoEstadoService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.utils.ServicesUtils;

@Service
public class SolicitudServiceImpl implements SolicitudService {

	private static final Logger LOG = LoggerFactory.getLogger(SolicitudServiceImpl.class);
	private SolicitudRepository solicitudRepository;
	private SolicitudDocumentosRepository solicitudDocRepository;
	private PersonaInfoAdicionalService infoAdicionalService;
	private PersonaDomicilioService persDomicilioService;
	private PersonaTipoCuentaService persTipoCtaService;
	private PersonaEstadoFinancieroService persEstadoFinanService;
	private PersonaRepresentanteLegalService persRepreLegalService;
	private PersonaFirmaService persFirmaService;
	private DatosInversionService datoInversionService;
	private PaisService paisService;
	private PersonaService personaService;
	private ServicesUtils utils;
	private PersonaDocumentoService persDocService;
	private ProyectoService proyectoService;
	private BlobStorageService blobStorageService;
	private TransaccionService transaccionService;
	private ParametroService parametroService;
	private TipoDocumentoService tipoDocService;
	private GeneraPdfService generaPdfService;
	private DocumentoAceptadoService documentoAceptadoService;
	private PersonaInternaService empleadoService;
	private TablaAmortizacionService tblAmortizacionService;
	private TipoEstadoService tipoEstadoService;
	private EnvioEmailService enviaEmailService;
	private DetalleTblAmortizacionRepository detalleAmortizacionRepository;
	private EmpresaRepository empresaRepository;
	private CuentaRepository cuentaRepository;
	private PersonaInfoAdicionalRepository personaInfoAdicionalRepository;

	private DecimalFormatSymbols simb;
	private DecimalFormat df;
	private EntityManager em;

	@Autowired
	public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
			PersonaInfoAdicionalService infoAdicionalService, PersonaDomicilioService persDomicilioService,
			PersonaTipoCuentaService persTipoCtaService, PersonaEstadoFinancieroService persEstadoFinanService,
			PersonaRepresentanteLegalService persRepreLegalService, PersonaFirmaService persFirmaService,
			DatosInversionService datoInversionService, PaisService paisService, PersonaService personaService,
			ServicesUtils utils, PersonaDocumentoService persDocService, ProyectoService proyectoService,
			BlobStorageService blobStorageService, TransaccionService transaccionService,
			ParametroService parametroService, TipoDocumentoService tipoDocService, GeneraPdfService generaPdfService,
			DocumentoAceptadoService documentoAceptadoService, PersonaInternaService empleadoService,
			TablaAmortizacionService tblAmortizacionService, TipoEstadoService tipoEstadoService,
			EnvioEmailService enviaEmailService, SolicitudDocumentosRepository solicitudDocRepository,
			DetalleTblAmortizacionRepository detalleAmortizacionRepository, EmpresaRepository empresaRepository,
			CuentaRepository cuentaRepository, PersonaInfoAdicionalRepository personaInfoAdicionalRepository,
			EntityManager em) {
		this.solicitudRepository = solicitudRepository;
		this.solicitudDocRepository = solicitudDocRepository;
		this.infoAdicionalService = infoAdicionalService;
		this.persDomicilioService = persDomicilioService;
		this.persTipoCtaService = persTipoCtaService;
		this.persEstadoFinanService = persEstadoFinanService;
		this.persRepreLegalService = persRepreLegalService;
		this.persFirmaService = persFirmaService;
		this.datoInversionService = datoInversionService;
		this.paisService = paisService;
		this.personaService = personaService;
		this.utils = utils;
		this.persDocService = persDocService;
		this.proyectoService = proyectoService;
		this.blobStorageService = blobStorageService;
		this.transaccionService = transaccionService;
		this.parametroService = parametroService;
		this.tipoDocService = tipoDocService;
		this.generaPdfService = generaPdfService;
		this.documentoAceptadoService = documentoAceptadoService;
		this.empleadoService = empleadoService;
		this.tblAmortizacionService = tblAmortizacionService;
		this.tipoEstadoService = tipoEstadoService;
		this.enviaEmailService = enviaEmailService;
		this.detalleAmortizacionRepository = detalleAmortizacionRepository;
		this.empresaRepository = empresaRepository;
		this.cuentaRepository = cuentaRepository;
		this.personaInfoAdicionalRepository = personaInfoAdicionalRepository;
		this.em = em;
		simb = new DecimalFormatSymbols();
		simb.setDecimalSeparator('.');
		simb.setGroupingSeparator(',');
		df = new DecimalFormat("#,###.00", simb);
	}

	@Override
	@Transactional
	public Solicitud guardaSolicitud(Solicitud solicitud) {
		Solicitud solCreada = new Solicitud();
		try {
			solCreada = solicitudRepository.save(solicitud);
		} catch (Exception e) {
			LOG.error("guardaSolicitud: Error " + e.getMessage());
		}
		return solCreada;
	}

	@Override
	@Transactional(readOnly = true)
	public Solicitud findById(Long id) throws Exception {
		Solicitud sol = null;
		sol = solicitudRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		if (sol == null) {
			throw new Exception("No existe Solicitud: " + id);
		}
		if (sol.getEstadoActual().getIdEstado() == "ANU") {
			throw new Exception("La Solicitud: " + id + ", se encuentra anulada");
		}
		return sol;
	}

	@Override
	@Transactional
	public FormDatosIngresadoResponse continuarProceso(Long numSol) {
		FormDatosIngresadoResponse datos = null;
		datos = this.solicitudRepository.ContinuarProceso(numSol);
		return datos;
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<SolicitudResponse> consultaSolicitudesPorPersonaYEstado(String identificacion, String estado) {
//		List<SolicitudResponse> listSolicitudResponse = new ArrayList<>();
//		List<Solicitud> listSolicitud = new ArrayList<Solicitud>();
//		listSolicitud = solicitudRepository.consultaSolicitudesPorPersonaYEstado(identificacion, estado);
//		if (!listSolicitud.isEmpty()) {
//			listSolicitudResponse = this.consultaListaSolicitudes(listSolicitud);
//		} else {
//			listSolicitudResponse.clear();
//		}
//		return listSolicitudResponse;
//	}

	@Override
	@Transactional(readOnly = true)
	public List<?> consultaSolcitudesByFilter(FilterSolicitudRequest filterInvRequest, String campo, String valor) {

		return solicitudRepository.consultaSolicitudByFilter(filterInvRequest.getCodProyecto(),
				filterInvRequest.getNumeroSolicitud(), filterInvRequest.getIdTipoCalificacion(),
				filterInvRequest.getIdActividad(), filterInvRequest.getIdentificacion(), valor);
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<?> consultaSolcitudesByFilter(FilterSolicitudRequest filterInvRequest, String campo, String valor) {
//
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<SolicitudResponse> query = cb.createQuery(SolicitudResponse.class);
//		Root<Solicitud> root = query.from(Solicitud.class);
//		query.multiselect(root);
//		//query.select(cb.tuple(tagsJoin.alias("tag"), cb.count(tagsJoin).alias("count_tags")));
//		
//		 List<?> result = em.createQuery(query).setMaxResults(5).getResultList();
//		
//		return result;
//	}

	@Override
	@Transactional(readOnly = true)
	public List<?> consultaSolcitudesPapFilter(FilterSolicitudRequest filterInvRequest, String campo, String valor) {
		List<?> solResponse = new ArrayList<>();

		solResponse = solicitudRepository.consultaSolicitudPapFilter(filterInvRequest.getCodProyecto(),
				filterInvRequest.getNumeroSolicitud(), filterInvRequest.getIdTipoCalificacion(),
				filterInvRequest.getIdActividad(), filterInvRequest.getIdentificacion(), valor);

		return solResponse;
	}

	@Override
	@Transactional
	public String aprobarSolicitudPap(Long numSol, String usuario, String campo, String valor) throws Exception {
		Solicitud sol = null;
		sol = this.findById(numSol);
		if (sol.getEstadoActual().getIdEstado().contains("PC")) {
			throw new Exception(
					"No se puede aprobar la solicitud: " + numSol + ", se encuentra en estado por confirmar");
		}
		HistorialDeSolicitud solHisto = new HistorialDeSolicitud();
		solHisto.setTablaCambiar(campo);
		solHisto.setValorAnterior(sol.getEstadoActual().getIdEstado());
		solHisto.setValorActual(valor);
		solHisto.setObservacion("Se aprueba solicitud por cambio de monto");
		solHisto.setUsuarioModificacionInterno(new CuentaInterno(usuario));
		sol.setHistorial(solHisto);
		sol.setObservacion(solHisto.getObservacion());
		sol.setEstadoActual(new TipoEstado(valor));
		this.solicitudRepository.save(sol);
		return "Solicitud Aprobada";
	}

	@Override
	@Transactional
	public String anularSolicitudPap(Long numSol, String usuario) throws Exception {
		Solicitud sol = null;
		sol = this.findById(numSol);
		// if (sol.getEstadoActual().getIdEstado().contains("PC")) {throw new
		// Exception("No se puede aprobar la solicitud: "+numSol+", se encuentra en
		// estado por confirmar");}
		HistorialDeSolicitud solHisto = new HistorialDeSolicitud();
		solHisto.setTablaCambiar("Estado");
		solHisto.setValorAnterior(sol.getEstadoActual().getIdEstado());
		solHisto.setValorActual("ANU");
		solHisto.setObservacion("Se anula solicitud " + numSol + " por cambio de monto");
		try {
			solHisto.setUsuarioModificacionInterno(new CuentaInterno(usuario));
			sol.setHistorial(solHisto);
			this.solicitudRepository.save(sol);
		} catch (Exception e) {
			throw new Exception("No existe usuario modificacion: " + usuario + " " + e.getMessage());
		}

		sol.setObservacion(solHisto.getObservacion());
		// sol.setActivo(valor);
		sol.setEstadoActual(new TipoEstado("ANU"));
		this.solicitudRepository.save(sol);
		return "Solicitud Anulada";
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<SolicitudResponse> consultaSolicitudPorProyecto(String codigoPoryecto) {
//		List<Solicitud> solicitudes = new ArrayList<>();
//		List<SolicitudResponse> solResponse = new ArrayList<>();
//		solicitudes = solicitudRepository.consultaSolicitudPorProyecto(codigoPoryecto);
//		if (!solicitudes.isEmpty()) {
//			solResponse = this.consultaListaSolicitudes(solicitudes);
//		} else {
//			solResponse.clear();
//		}
//		return solResponse;
//	}

	@Override
	@Transactional(readOnly = true)
	public boolean consultaSolicitudExistente(String codigoProyecto, String identificacion) {
		boolean existe = false;
		if (solicitudRepository.consultaSolicitudExistente(codigoProyecto, identificacion) >= 1) {
			existe = true;
		}
		return existe;
	}

//	public List<SolicitudResponse> consultaListaSolicitudes(List<Solicitud> solicitudes) {
//		List<SolicitudResponse> listSolicitud = new ArrayList<>();
//		String tipoTransacion;
//		for (Solicitud sol : solicitudes) {
//			SolicitudResponse solicitud = new SolicitudResponse();
//
//			solicitud.setNumeroSolicitud(sol.getNumeroSolicitud().toString());
////			solicitud.setTipoPersona(sol.getTipoPersona());
////			solicitud.setUsuario(sol.getUsuario());
//			solicitud.setCodigoProyecto(sol.getProyecto().getIdProyecto());
//			solicitud.setNombreProyecto(sol.getProyecto().getEmpresa().getNombre());
//
//			if (sol.getTipoSolicitud() != null) {
//				tipoTransacion = sol.getTipoSolicitud().getDescripcion();
//			} else {
//				tipoTransacion = null;
//			}
//			solicitud.setTipoTransaccion(tipoTransacion);
////			solicitud.setTipoContacto(sol.getTipoContacto());
////			solicitud.setUsuarioContacto(sol.getUsuarioContacto());
//			solicitud.setObservacion(sol.getObservacion());
//			solicitud.setEstadoActual(sol.getEstadoActual());
////			solicitud.setEstadoAnterior(sol.getEstadoAnterior());
//			solicitud.setFechaCreacion(sol.getFechaGeneracion());
//			solicitud.setUsuarioCreacion(sol.getUsuarioCreacion().getPersona().getNombres());
//
//			PersonaResponse persResponse = new PersonaResponse();
//			persResponse.setIdentificacion(sol.getInversionista().getPersona().getIdentificacion());
//			persResponse.setTipoCliente(sol.getInversionista().getPersona().getTipoCliente());
//			persResponse.setTipoPersona(sol.getInversionista().getPersona().getTipoPersona());
//			persResponse.setTipoIdentificacion(sol.getInversionista().getPersona().getTipoIdentificacion());
//			persResponse.setNacionalidad(sol.getInversionista().getPersona().getNacionalidad());
//			persResponse.setNombres(sol.getInversionista().getPersona().getNombres());
//			persResponse.setApellidos(sol.getInversionista().getPersona().getApellidos());
//			persResponse.setFechaNacimiento(sol.getInversionista().getPersona().getFechaNacimiento());
//			persResponse.setNumeroCelular(sol.getInversionista().getPersona().getNumeroCelular());
//			persResponse.setRazonSocial(sol.getInversionista().getPersona().getRazonSocial());
//			persResponse.setNombreRepresentante(sol.getInversionista().getPersona().getNombreContacto());
//			persResponse.setCargoRepresentante(sol.getInversionista().getPersona().getCargoContacto());
//			persResponse.setEmailRepresentante(sol.getInversionista().getPersona().getEmailContacto());
//			persResponse.setAnioInicioActividad(sol.getInversionista().getPersona().getAnioInicioActividad());
//			persResponse.setEmail(sol.getInversionista().getEmail());
//			persResponse.setUsuario(sol.getInversionista().getUsuario());
//			persResponse.setTipoContacto(sol.getInversionista().getTipoContacto());
//			persResponse.setUsuarioContacto(sol.getInversionista().getUsuarioContacto());
//
//			if (sol.getDatosInversion().isFormulario()) {
//				PersInfoAdicionalResponse infoAdicional = new PersInfoAdicionalResponse();
//				infoAdicional
//						.setEstadoCivil(sol.getInversionista().getPersona().getPersInfoAdicional().getEstadoCivil());
//				infoAdicional.setSexo(sol.getInversionista().getPersona().getPersInfoAdicional().getSexo());
//				infoAdicional.setNumeroTelefono(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getNumeroTelefono());
//				infoAdicional.setFuenteIngresos(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getFuenteIngresos());
//				infoAdicional
//						.setCargoPersona(sol.getInversionista().getPersona().getPersInfoAdicional().getCargoPersona());
//				infoAdicional.setResidenteDomicilioFiscal(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getResidenteDomicilioFiscal());
//				infoAdicional.setPaisDomicilioFiscal(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getPaisDomicilioFiscal());
//
//				FormDomicilioResponse domicilioResponse = new FormDomicilioResponse();
//				PaisResponse pais = new PaisResponse();
//				pais.setId(sol.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getPais()
//						.getIdNacionalidad());
//				pais.setPais(sol.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getPais()
//						.getPais());
//				domicilioResponse.setPais(pais);
//				CiudadResponse ciudadRes = new CiudadResponse();
//				ciudadRes.setId(sol.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio()
//						.getCiudad().getIdCiudad());
//				ciudadRes.setCiudad(sol.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio()
//						.getCiudad().getCiudad());
//				domicilioResponse.setCiudad(ciudadRes);
//				domicilioResponse.setDireccion(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getDireccion());
//				domicilioResponse.setNumeroDomicilio(sol.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersDomicilio().getNumeroDomicilio());
//				domicilioResponse.setSector(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getSector());
//				infoAdicional.setPersonaDomicilio(domicilioResponse);
//				infoAdicional.setPersonaCuenta(
//						sol.getInversionista().getPersona().getPersInfoAdicional().getPersTipoCuenta());
//
//				if (sol.getInversionista().getPersona().getTipoPersona().contains("JUR")) {
//					infoAdicional.setPersonaEstadoFinanciero(
//							sol.getInversionista().getPersona().getPersInfoAdicional().getPersEstadoFinanciero());
//
//					FormRepresentanteLegalResponse repreLegal = new FormRepresentanteLegalResponse();
//					repreLegal.setNombres(sol.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal()
//							.getNombres());
//					repreLegal.setApellidos(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getApellidos());
//					repreLegal.setIdentificacion(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getIdentificacion());
//					repreLegal.setEmail(
//							sol.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal().getEmail());
//					repreLegal.setNumeroCelular(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getNumeroCelular());
//					repreLegal.setCargo(
//							sol.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal().getCargo());
//					pais.setId(sol.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal().getPais()
//							.getIdNacionalidad());
//					pais.setPais(sol.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal()
//							.getPais().getPais());
//					repreLegal.setPais(pais);
//					repreLegal.setTelefono(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getTelefono());
//					repreLegal.setDireccionDomicilio(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getDireccionDomicilio());
//					repreLegal.setFechaInicioActividad(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getFechaInicioActividad());
//					repreLegal.setNumeroDomicilio(sol.getInversionista().getPersona().getPersInfoAdicional()
//							.getPersRepreLegal().getNumeroDomicilio());
//					infoAdicional.setPersonaRepresentanteLegal(repreLegal);
//
//					infoAdicional.setPersonaFirmaAutorizada(
//							sol.getInversionista().getPersona().getPersInfoAdicional().getPersFirma());
//				}
//
//				if (sol.getInversionista().getPersona().getPersInfoAdicional().getPersonaDocumentos() == null) {
//					PersonaDocumento ps = new PersonaDocumento();
//					ps = sol.getInversionista().getPersona().getPersInfoAdicional().getPersonaDocumentos();
//					if (ps.getEstado().contains("A")) {
//						DocIdentificacionResponse formDoc = new DocIdentificacionResponse();
//						formDoc.setNombre(ps.getDocumento().getDocumento());
//						formDoc.setRuta(ps.getRuta());
//						infoAdicional.setPersonaDocumento(formDoc);
//					}
//
//				}
//
//				persResponse.setDatosFormulario(infoAdicional);
//
//				List<Transaccion> trx = new ArrayList<>();
//				trx = transaccionService.findBySolicitud(sol.getNumeroSolicitud());
//				if (!trx.isEmpty()) {
//					List<DocIdentificacionResponse> comprobantesPagos = new ArrayList<>();
//					trx.forEach(t -> {
//						DocIdentificacionResponse trxDoc = new DocIdentificacionResponse();
//						trxDoc.setNombre(t.getDocumento().getDocumento());
//						trxDoc.setRuta(t.getRutaComprobante());
//						comprobantesPagos.add(trxDoc);
//
//					});
//					solicitud.setComprobantesPagos(comprobantesPagos);
//				}
//			}
//			// solicitud.setPersona(persResponse);
//
//			FormDatosIngresadoResponse datosInversion = new FormDatosIngresadoResponse();
//			datosInversion.setTablaAmortizacion(sol.getDatosInversion().isTablaAmortizacion());
//			datosInversion.setFormulario(sol.getDatosInversion().isFormulario());
//			datosInversion.setDocumentacion(sol.getDatosInversion().isDocumentacion());
//			datosInversion.setPago(sol.getDatosInversion().isPago());
//			solicitud.setDatosInversionIngresados(datosInversion);
//			listSolicitud.add(solicitud);
//		}
//		return listSolicitud;
//	}

//	public SolicitudResponse consultaSolicitud(Solicitud solicitud) {
//		SolicitudResponse sol = new SolicitudResponse();
//		String tipoTransacion;
//
//		sol.setNumeroSolicitud(solicitud.getNumeroSolicitud().toString());
////		sol.setTipoPersona(solicitud.getTipoPersona());
////		sol.setUsuario(solicitud.getUsuario());
//		sol.setCodigoProyecto(solicitud.getProyecto().getIdProyecto());
//		sol.setNombreProyecto(proyectoService.consultaNombreProyecto(sol.getCodigoProyecto()));
//		if (solicitud.getTipoSolicitud() != null) {
//			tipoTransacion = solicitud.getTipoSolicitud().getDescripcion();
//		} else {
//			tipoTransacion = null;
//		}
//		sol.setTipoTransaccion(tipoTransacion);
////		sol.setTipoContacto(solicitud.getTipoContacto());
////		sol.setUsuarioContacto(solicitud.getUsuarioContacto());
//		sol.setObservacion(solicitud.getObservacion());
//		sol.setEstadoActual(solicitud.getEstadoActual());
////		sol.setEstadoAnterior(solicitud.getEstadoAnterior());
//		sol.setFechaCreacion(solicitud.getFechaGeneracion());
//		sol.setUsuarioCreacion(solicitud.getUsuarioCreacion().getPersona().getNombres());
//
//		PersonaResponse persResponse = new PersonaResponse();
//		persResponse.setIdentificacion(solicitud.getInversionista().getPersona().getIdentificacion());
//		persResponse.setTipoCliente(solicitud.getInversionista().getPersona().getTipoCliente());
//		persResponse.setTipoPersona(solicitud.getInversionista().getPersona().getTipoPersona());
//		persResponse.setTipoIdentificacion(solicitud.getInversionista().getPersona().getTipoIdentificacion());
//		persResponse.setNacionalidad(solicitud.getInversionista().getPersona().getNacionalidad());
//		persResponse.setNombres(solicitud.getInversionista().getPersona().getNombres());
//		persResponse.setApellidos(solicitud.getInversionista().getPersona().getApellidos());
//		persResponse.setFechaNacimiento(solicitud.getInversionista().getPersona().getFechaNacimiento());
//		persResponse.setNumeroCelular(solicitud.getInversionista().getPersona().getNumeroCelular());
//		persResponse.setRazonSocial(solicitud.getInversionista().getPersona().getRazonSocial());
//		persResponse.setNombreRepresentante(solicitud.getInversionista().getPersona().getNombreContacto());
//		persResponse.setCargoRepresentante(solicitud.getInversionista().getPersona().getCargoContacto());
//		persResponse.setEmailRepresentante(solicitud.getInversionista().getPersona().getEmailContacto());
//		persResponse.setAnioInicioActividad(solicitud.getInversionista().getPersona().getAnioInicioActividad());
//		persResponse.setEmail(solicitud.getInversionista().getEmail());
//		persResponse.setUsuario(solicitud.getInversionista().getUsuario());
//		persResponse.setTipoContacto(solicitud.getInversionista().getTipoContacto());
//		persResponse.setUsuarioContacto(solicitud.getInversionista().getUsuarioContacto());
//
//		if (solicitud.getDatosInversion().isFormulario()) {
//			PersInfoAdicionalResponse infoAdicional = new PersInfoAdicionalResponse();
//			infoAdicional
//					.setEstadoCivil(solicitud.getInversionista().getPersona().getPersInfoAdicional().getEstadoCivil());
//			infoAdicional.setSexo(solicitud.getInversionista().getPersona().getPersInfoAdicional().getSexo());
//			infoAdicional.setNumeroTelefono(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getNumeroTelefono());
//			infoAdicional.setFuenteIngresos(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getFuenteIngresos());
//			infoAdicional.setCargoPersona(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getCargoPersona());
//			infoAdicional.setResidenteDomicilioFiscal(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getResidenteDomicilioFiscal());
//			infoAdicional.setPaisDomicilioFiscal(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getPaisDomicilioFiscal());
//
//			FormDomicilioResponse domicilioResponse = new FormDomicilioResponse();
//			PaisResponse pais = new PaisResponse();
//			pais.setId(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getPais()
//					.getIdNacionalidad());
//			pais.setPais(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getPais()
//					.getPais());
//			domicilioResponse.setPais(pais);
//			CiudadResponse ciudadRes = new CiudadResponse();
//			ciudadRes.setId(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio()
//					.getCiudad().getIdCiudad());
//			ciudadRes.setCiudad(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio()
//					.getCiudad().getCiudad());
//			domicilioResponse.setCiudad(ciudadRes);
//			domicilioResponse.setDireccion(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getDireccion());
//			domicilioResponse.setNumeroDomicilio(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//					.getPersDomicilio().getNumeroDomicilio());
//			domicilioResponse.setSector(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersDomicilio().getSector());
//			infoAdicional.setPersonaDomicilio(domicilioResponse);
//			infoAdicional.setPersonaCuenta(
//					solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersTipoCuenta());
//
//			if (solicitud.getInversionista().getPersona().getTipoPersona().contains("JUR")) {
//				infoAdicional.setPersonaEstadoFinanciero(
//						solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersEstadoFinanciero());
//
//				FormRepresentanteLegalResponse repreLegal = new FormRepresentanteLegalResponse();
//				repreLegal.setNombres(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getNombres());
//				repreLegal.setApellidos(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getApellidos());
//				repreLegal.setIdentificacion(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getIdentificacion());
//				repreLegal.setEmail(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal()
//						.getEmail());
//				repreLegal.setNumeroCelular(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getNumeroCelular());
//				repreLegal.setCargo(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal()
//						.getCargo());
//				pais.setId(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal()
//						.getPais().getIdNacionalidad());
//				pais.setPais(solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersRepreLegal()
//						.getPais().getPais());
//				repreLegal.setPais(pais);
//				repreLegal.setTelefono(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getTelefono());
//				repreLegal.setDireccionDomicilio(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getDireccionDomicilio());
//				repreLegal.setFechaInicioActividad(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getFechaInicioActividad());
//				repreLegal.setNumeroDomicilio(solicitud.getInversionista().getPersona().getPersInfoAdicional()
//						.getPersRepreLegal().getNumeroDomicilio());
//				infoAdicional.setPersonaRepresentanteLegal(repreLegal);
//
//				infoAdicional.setPersonaFirmaAutorizada(
//						solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersFirma());
//			}
//			PersonaDocumento ps = new PersonaDocumento();
//			if (solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersonaDocumentos() == null) {
//				ps = solicitud.getInversionista().getPersona().getPersInfoAdicional().getPersonaDocumentos();
//				if (ps.getEstado().contains("A")) {
//					DocIdentificacionResponse formDoc = new DocIdentificacionResponse();
//					formDoc.setNombre(ps.getDocumento().getDocumento());
//					formDoc.setRuta(ps.getRuta());
//					formDoc.setNombre_post(ps.getNombrepost());
//					formDoc.setRuta_post(ps.getRutapost());
//					infoAdicional.setPersonaDocumento(formDoc);
//				}
//
//			}
//
//			persResponse.setDatosFormulario(infoAdicional);
//
//			List<Transaccion> trx = new ArrayList<>();
//			trx = transaccionService.findBySolicitud(solicitud.getNumeroSolicitud());
//			if (!trx.isEmpty()) {
//				List<DocIdentificacionResponse> comprobantesPagos = new ArrayList<>();
//				trx.forEach(t -> {
//					DocIdentificacionResponse trxDoc = new DocIdentificacionResponse();
//					trxDoc.setNombre(t.getDocumento().getDocumento());
//					trxDoc.setRuta(t.getRutaComprobante());
//					comprobantesPagos.add(trxDoc);
//
//				});
//				sol.setComprobantesPagos(comprobantesPagos);
//			}
//		}
//
//		// sol.setPersona(persResponse);
//
//		FormDatosIngresadoResponse datosInversion = new FormDatosIngresadoResponse();
//		datosInversion.setTablaAmortizacion(solicitud.getDatosInversion().isTablaAmortizacion());
//		datosInversion.setFormulario(solicitud.getDatosInversion().isFormulario());
//		datosInversion.setDocumentacion(solicitud.getDatosInversion().isDocumentacion());
//		datosInversion.setPago(solicitud.getDatosInversion().isPago());
//		sol.setDatosInversionIngresados(datosInversion);
//
//		TblAmortizacionResponse tblResponse = new TblAmortizacionResponse();
////		tblResponse = tblAmortizacionService.consultaTblAmortizacionFilter(solicitud.getNumeroSolicitud(),(long)1);
////		sol.setDatosAmortizacion(tblResponse);
//
//		return sol;
//	}

	@Override
	public String guardaPersonaInfoAdicional(FormNatRequest requestNat, FormJurRequest requestJur, String tipo) {
		String response = null;
		try {
			if (requestNat != null) {// persona natural
				if (tipo != null) {// update
					response = this.updatePersonaNat(requestNat);
				} else {
					response = this.guardaPersonaNat(requestNat);
				}
			} else if (requestJur != null) {// persona juridica
				if (tipo != null) {// update
					response = this.updatePersonaJur(requestJur);
				} else {
					response = this.guardaPersonaJur(requestJur);
				}
			}
		} catch (DataAccessException e) {
			LOG.error("guardaPersonaInfoAdicional: Error al guardar datos del formulario " + e.getMessage());
			return "Error al guardar datos del formulario.";
		}
		return response;
	}

	public String guardaPersonaNat(FormNatRequest requestNat) {
		String response = null;
		Persona pers = new Persona();
		pers= this.cuentaRepository.findByCuentaIdCuenta(requestNat.getIdentificacion()).getPersona();
		Pais pais = new Pais();
		Ciudad ciudad = new Ciudad();
		Banco banco = new Banco();
		Long idDatoInversion;
		LOG.error("entrooooooooooooooooooooo");

		try {
			LOG.error("entroooo2");

//			idDatoInversion = solicitudRepository.consultaDatoInversion(requestNat.getNumeroSolicitud());
//			DatosInversion datoInversion = new DatosInversion();
//			datoInversion = datoInversionService.findByIdDato(idDatoInversion);
//			if(datoInversion.isFormulario()) {
//				response = "Ya existen datos de la persona con identificacion: "+requestNat.getIdentificacion();
//			}else {
			PersonaInfoAdicional infoAdicional = new PersonaInfoAdicional();
//			pers.setIdentificacion(requestNat.getIdentificacion());
			infoAdicional.setPersona(pers);
			infoAdicional.setEstadoCivil(requestNat.getEstadoCivil());
			infoAdicional.setSexo(requestNat.getSexo());
			infoAdicional.setNumeroTelefono(requestNat.getTelefonoAdicional());
			infoAdicional.setFuenteIngresos(requestNat.getFuenteIngresos());
			infoAdicional.setCargoPersona(requestNat.getCargo());
			infoAdicional.setResidenteDomicilioFiscal(requestNat.getResidenteDomicilioFiscal());
			if (requestNat.getPaisDomicilioFiscal() != null)
				infoAdicional.setPaisDomicilioFiscal(requestNat.getPaisDomicilioFiscal());
			infoAdicional.setFechaRegistro(utils.getLocalDate());
			infoAdicional.setFechaCreacion(utils.getLocalDateTime());
			infoAdicional.setUsuarioCreacion(new Cuenta(requestNat.getIdentificacion()));

			PersonaDomicilio formDomicilio = new PersonaDomicilio();
			pais.setIdNacionalidad(requestNat.getDomicilio().getPais());
			formDomicilio.setPais(pais);
			ciudad.setIdCiudad(requestNat.getDomicilio().getCiudad());
			formDomicilio.setCiudad(ciudad);
			formDomicilio.setDireccion(requestNat.getDomicilio().getDireccion());
			formDomicilio.setNumeroDomicilio(requestNat.getDomicilio().getNumeroDomicilio());

			PersonaDomicilio persDomicilio = new PersonaDomicilio();
			persDomicilio = persDomicilioService.saveDomicilio(formDomicilio);
			infoAdicional.setPersDomicilio(persDomicilio);

			PersonaTipoCuenta formTipoCuenta = new PersonaTipoCuenta();
			formTipoCuenta.setTitular(requestNat.getTipoCuenta().getTitularCuenta().toUpperCase());
			banco.setIdBanco(requestNat.getTipoCuenta().getBancoCuenta());
			formTipoCuenta.setBanco(banco);
			formTipoCuenta.setPersona(pers);
			formTipoCuenta.setTipoCuenta(requestNat.getTipoCuenta().getTipoCuenta());
			formTipoCuenta.setNumeroCuenta(requestNat.getTipoCuenta().getNumeroCuenta());

			PersonaTipoCuenta persTipoCuenta = new PersonaTipoCuenta();
			persTipoCuenta = persTipoCtaService.saveTipoCuenta(formTipoCuenta);
			infoAdicional.setPersTipoCuenta(persTipoCuenta);

			infoAdicionalService.guardaInforAdicional(infoAdicional);

//				idDatoInversion = solicitudRepository.consultaDatoInversion(requestNat.getNumeroSolicitud());
//				datoInversionService.updateDatosFormulario(true, idDatoInversion);

			// Actualiza solicitud, campos AceptaLicitudFondos y AceptaInformacionCorrecta
			// solicitudRepository.updateLicitudYInfoCorrecta(requestNat.getAceptaLicitudFondos(),
			// requestNat.getAceptaInformacionCorrecta(), requestNat.getNumeroSolicitud());

			response = "ok";
//			}
		} catch (DataAccessException e) {
			LOG.error("guardaPersonaNat: Error al guardar datos del formulario " + e.getMessage());
			return "Error al guardar datos del formulario.";
		}
		return response;
	}

	public String guardaPersonaJur(FormJurRequest requestJur) {
		String response = null;
		Persona pers = new Persona();

		Ciudad ciudad = new Ciudad();
		Banco banco = new Banco();
		Long idDatoInversion;
		try {
			idDatoInversion = solicitudRepository.consultaDatoInversion(requestJur.getNumeroSolicitud());
			DatosInversion datoInversion = new DatosInversion();
			datoInversion = datoInversionService.findByIdDato(idDatoInversion);
			if (datoInversion.isFormulario()) {
				response = "Ya existen datos de la persona con RUC: " + requestJur.getRuc();
			} else {
				PersonaInfoAdicional infoAdicional = new PersonaInfoAdicional();
				pers.setIdentificacion(requestJur.getRuc());
				infoAdicional.setPersona(pers);

				infoAdicional.setActividadEconomica(requestJur.getActividadEconomica().toUpperCase());
				infoAdicional.setNumeroTelefono(requestJur.getNumeroTelefono());
				infoAdicional.setFuenteIngresos(requestJur.getFuenteIngresos());
				infoAdicional.setFechaRegistro(utils.getLocalDate());
				infoAdicional.setResidenteDomicilioFiscal(requestJur.getResidenteDomicilioFiscal());
				infoAdicional.setPaisDomicilioFiscal(requestJur.getPaisDomicilioFiscal());
				infoAdicional.setFechaCreacion(utils.getLocalDateTime());
				infoAdicional.setUsuarioCreacion(new Cuenta(requestJur.getRuc()));

				PersonaDomicilio formDomicilio = new PersonaDomicilio();
				Pais pais = new Pais();
				pais = paisService.findById(requestJur.getDomicilio().getPais());
				formDomicilio.setPais(pais);
				ciudad.setIdCiudad(requestJur.getDomicilio().getCiudad());
				formDomicilio.setCiudad(ciudad);
				formDomicilio.setDireccion(requestJur.getDomicilio().getDireccion());
				formDomicilio.setNumeroDomicilio(requestJur.getDomicilio().getNumeroDomicilio());
				formDomicilio.setSector(requestJur.getDomicilio().getSector());

				PersonaDomicilio persDomicilio = new PersonaDomicilio();
				persDomicilio = persDomicilioService.saveDomicilio(formDomicilio);
				infoAdicional.setPersDomicilio(persDomicilio);

				PersonaTipoCuenta formTipoCuenta = new PersonaTipoCuenta();
				formTipoCuenta.setTitular(requestJur.getTipoCuenta().getTitularCuenta());
				banco.setIdBanco(requestJur.getTipoCuenta().getBancoCuenta());
				formTipoCuenta.setBanco(banco);
				formTipoCuenta.setPersona(pers);
				formTipoCuenta.setTipoCuenta(requestJur.getTipoCuenta().getTipoCuenta());
				formTipoCuenta.setNumeroCuenta(requestJur.getTipoCuenta().getNumeroCuenta());

				PersonaTipoCuenta persTipoCuenta = new PersonaTipoCuenta();
				persTipoCuenta = persTipoCtaService.saveTipoCuenta(formTipoCuenta);
				infoAdicional.setPersTipoCuenta(persTipoCuenta);

				PersonaEstadoFinanciero formEstFinan = new PersonaEstadoFinanciero();
				formEstFinan.setIngresoAnual(requestJur.getEstadoFinanciero().getIngresoAnual());
				formEstFinan.setEgresoAnual(requestJur.getEstadoFinanciero().getEgresoAnual());
				formEstFinan.setTotalActivo(requestJur.getEstadoFinanciero().getTotalActivo());
				formEstFinan.setTotalPasivo(requestJur.getEstadoFinanciero().getTotalPasivo());
				formEstFinan.setTotalPatrimonio(requestJur.getEstadoFinanciero().getTotalPatrimonio());

				PersonaEstadoFinanciero persEstFinan = new PersonaEstadoFinanciero();
				persEstFinan = persEstadoFinanService.saveEstadoFinanciero(formEstFinan);
				infoAdicional.setPersEstadoFinanciero(persEstFinan);

				PersonaRepresentanteLegal formRepreLegal = new PersonaRepresentanteLegal();
				formRepreLegal.setNombres(requestJur.getRepresentanteLegal().getNombres().toUpperCase());
				formRepreLegal.setApellidos(requestJur.getRepresentanteLegal().getApellidos().toUpperCase());
				formRepreLegal.setIdentificacion(requestJur.getRepresentanteLegal().getIdentificacion());
				formRepreLegal.setEmail(requestJur.getRepresentanteLegal().getEmail());
				formRepreLegal.setNumeroCelular(requestJur.getRepresentanteLegal().getCelular());
				formRepreLegal.setCargo(requestJur.getRepresentanteLegal().getCargo());
				Pais paisRepreLegal = new Pais();
				paisRepreLegal = paisService.findById(requestJur.getRepresentanteLegal().getPais());
				formRepreLegal.setPais(paisRepreLegal);
				formRepreLegal.setTelefono(requestJur.getRepresentanteLegal().getTelefono());
				formRepreLegal.setDireccionDomicilio(requestJur.getRepresentanteLegal().getDireccionDomicilio());
				formRepreLegal.setFechaInicioActividad(requestJur.getRepresentanteLegal().getFechaInicioActividad());
				formRepreLegal.setNumeroDomicilio(requestJur.getRepresentanteLegal().getNumeroDomicilio());

				PersonaRepresentanteLegal persRepreLegal = new PersonaRepresentanteLegal();
				persRepreLegal = persRepreLegalService.saveRepresentanteLegal(formRepreLegal);
				infoAdicional.setPersRepreLegal(persRepreLegal);

				PersonaFirma formFirma = new PersonaFirma();
				formFirma.setNombresCompletos(requestJur.getFirmaAutorizada().getNombresCompletos().toUpperCase());
				formFirma.setIdentificacion(requestJur.getFirmaAutorizada().getIdentificacion());
				formFirma.setEmail(requestJur.getFirmaAutorizada().getEmail());

				PersonaFirma persFirma = new PersonaFirma();
				persFirma = persFirmaService.savePersonaFirma(formFirma);
				infoAdicional.setPersFirma(persFirma);

				infoAdicionalService.guardaInforAdicional(infoAdicional);

				idDatoInversion = solicitudRepository.consultaDatoInversion(requestJur.getNumeroSolicitud());
				datoInversionService.updateDatosFormulario(true, idDatoInversion);

				// Actualiza solicitud, campos AceptaLicitudFondos y AceptaInformacionCorrecta
				// solicitudRepository.updateLicitudYInfoCorrecta(requestJur.getAceptaLicitudFondos(),
				// requestJur.getAceptaInformacionCorrecta(), requestJur.getNumeroSolicitud());

				response = "ok";
			}
		} catch (Exception e) {
			LOG.error("guardaPersonaJur: Error al guardar datos del formulario " + e.getMessage());
			return "Error al guardar datos del formulario.";
		}
		return response;
	}

	public String updatePersonaNat(FormNatRequest formRequest) {
		PersonaInfoAdicional persInfoAdicional = new PersonaInfoAdicional();

		String response = null;
		try {
			persInfoAdicional = infoAdicionalService.consultaPersonaInfoAdicional(formRequest.getIdentificacion());
			if (persInfoAdicional != null) {

				if (!persInfoAdicional.getEstadoCivil().equals(formRequest.getEstadoCivil())
						|| !persInfoAdicional.getSexo().equals(formRequest.getSexo())
						|| !persInfoAdicional.getResidenteDomicilioFiscal()
								.equals(formRequest.getResidenteDomicilioFiscal())
						|| !persInfoAdicional.getCargoPersona().equals(formRequest.getCargo())
						|| !persInfoAdicional.getFuenteIngresos().equals(formRequest.getFuenteIngresos())
						|| !persInfoAdicional.getNumeroTelefono().equals(formRequest.getTelefonoAdicional())) {

					persInfoAdicional.setEstadoCivil(formRequest.getEstadoCivil());
					persInfoAdicional.setSexo(formRequest.getSexo());
					persInfoAdicional.setResidenteDomicilioFiscal(formRequest.getResidenteDomicilioFiscal());
					persInfoAdicional.setPaisDomicilioFiscal(formRequest.getPaisDomicilioFiscal());
					persInfoAdicional.setCargoPersona(formRequest.getCargo());
					persInfoAdicional.setFuenteIngresos(formRequest.getFuenteIngresos());
					persInfoAdicional.setFechaModificacion(utils.getLocalDateTime());
					persInfoAdicional.setUsuarioModificacion(new Cuenta(formRequest.getIdentificacion()));
					persInfoAdicional.setNumeroTelefono(formRequest.getTelefonoAdicional());
					infoAdicionalService.guardaInforAdicional(persInfoAdicional);
				}

//				if(!persInfoAdicional.getPersona().getNumeroCelular().equals(formRequest.getNumeroCelular())) {
				persInfoAdicional.getPersona().setNumeroCelular(formRequest.getNumeroCelular());
				;
//					persona.setNumeroCelular(formRequest.getNumeroCelular());
//					persona.setFechaModificacion(new Date());
//					persona.setUsuarioModificacion( "Admin");
//					personaService.guardaPersona(persona);
//				}

				if (!persInfoAdicional.getPersDomicilio().getPais().getIdNacionalidad()
						.equals(formRequest.getDomicilio().getPais())
						|| !persInfoAdicional.getPersDomicilio().getCiudad().getIdCiudad()
								.equals(formRequest.getDomicilio().getCiudad())
						|| !persInfoAdicional.getPersDomicilio().getDireccion()
								.equals(formRequest.getDomicilio().getDireccion())
						|| !persInfoAdicional.getPersDomicilio().getNumeroDomicilio()
								.equals(formRequest.getDomicilio().getNumeroDomicilio())) {

					PersonaDomicilio persDomicilio = new PersonaDomicilio();
					Pais pais = new Pais();
					Ciudad ciudad = new Ciudad();
					persDomicilio.setIdDomicilio(persInfoAdicional.getPersDomicilio().getIdDomicilio());
					pais.setIdNacionalidad(formRequest.getDomicilio().getPais());
					persDomicilio.setPais(pais);
					ciudad.setIdCiudad(formRequest.getDomicilio().getCiudad());
					persDomicilio.setCiudad(ciudad);
					persDomicilio.setDireccion(formRequest.getDomicilio().getDireccion());
					persDomicilio.setNumeroDomicilio(formRequest.getDomicilio().getNumeroDomicilio());
					persDomicilio.setEstado(persInfoAdicional.getPersDomicilio().getEstado());
					persDomicilioService.saveDomicilio(persDomicilio);
				}

				if (!persInfoAdicional.getPersTipoCuenta().getTitular()
						.equals(formRequest.getTipoCuenta().getTitularCuenta().toUpperCase())
						|| !persInfoAdicional.getPersTipoCuenta().getBanco().getIdBanco()
								.equals(formRequest.getTipoCuenta().getBancoCuenta())
						|| !persInfoAdicional.getPersTipoCuenta().getTipoCuenta()
								.equals(formRequest.getTipoCuenta().getTipoCuenta())
						|| !persInfoAdicional.getPersTipoCuenta().getNumeroCuenta()
								.equals(formRequest.getTipoCuenta().getNumeroCuenta())) {

					Banco banco = new Banco();
					PersonaTipoCuenta formTipoCuenta = new PersonaTipoCuenta();
					formTipoCuenta.setIdPersCuenta(persInfoAdicional.getPersTipoCuenta().getIdPersCuenta());
					formTipoCuenta.setTitular(formRequest.getTipoCuenta().getTitularCuenta());
					banco.setIdBanco(formRequest.getTipoCuenta().getBancoCuenta());
					formTipoCuenta.setBanco(banco);
					formTipoCuenta.setPersona(persInfoAdicional.getPersona());
					formTipoCuenta.setTipoCuenta(formRequest.getTipoCuenta().getTipoCuenta());
					formTipoCuenta.setNumeroCuenta(formRequest.getTipoCuenta().getNumeroCuenta());
					formTipoCuenta.setEstado(persInfoAdicional.getPersTipoCuenta().getEstado());
					persTipoCtaService.saveTipoCuenta(formTipoCuenta);

				}
				response = "ok";

			} else {
//				LOG.info("updatePersonaNat: no exite datos del formulario de inversion para la solicitud "+formRequest.getNumeroSolicitud());
				response = "No existe datos del formulario de inversión";
			}
		} catch (Exception e) {
			LOG.error("updatePersonaNat: Error al actualizar formulario " + e.getMessage());
			return "Error al actualizar formulario";
		}
		return response;
	}

	public String updatePersonaJur(FormJurRequest formRequest) {
		PersonaInfoAdicional persInfoAdicional = new PersonaInfoAdicional();

		String response = null;
		try {
			persInfoAdicional = infoAdicionalService.consultaPersonaInfoAdicional(formRequest.getRuc());
			if (persInfoAdicional != null) {

				if (!persInfoAdicional.getActividadEconomica().equals(formRequest.getActividadEconomica().toUpperCase())
						|| !persInfoAdicional.getResidenteDomicilioFiscal()
								.equals(formRequest.getResidenteDomicilioFiscal())
						|| !persInfoAdicional.getNumeroTelefono().equals(formRequest.getNumeroTelefono())) {
					persInfoAdicional.setActividadEconomica(formRequest.getActividadEconomica().toUpperCase());
					persInfoAdicional.setResidenteDomicilioFiscal(formRequest.getResidenteDomicilioFiscal());
					persInfoAdicional.setPaisDomicilioFiscal(formRequest.getPaisDomicilioFiscal());
					persInfoAdicional.setFechaModificacion(utils.getLocalDateTime());
					persInfoAdicional.setUsuarioModificacion(new Cuenta("Admin"));
					persInfoAdicional.setNumeroTelefono(formRequest.getNumeroTelefono());
					infoAdicionalService.guardaInforAdicional(persInfoAdicional);
				}

				if (!persInfoAdicional.getPersona().getNombreContacto()
						.equals(formRequest.getNombreContacto().toUpperCase())
						|| !persInfoAdicional.getPersona().getNumeroCelular().equals(formRequest.getNumeroCelular())) {
					Persona persona = new Persona();
					persona = persInfoAdicional.getPersona();
					persona.setNombreContacto(formRequest.getNombreContacto().toUpperCase());
					persona.setNumeroCelular(formRequest.getNumeroCelular());
					persona.setFechaModificacion(new Date());
					persona.setUsuarioModificacion(formRequest.getUsuario());
					personaService.guardaPersona(persona);
				}

				if (!persInfoAdicional.getPersDomicilio().getPais().getIdNacionalidad()
						.equals(formRequest.getDomicilio().getPais())
						|| !persInfoAdicional.getPersDomicilio().getCiudad().getIdCiudad()
								.equals(formRequest.getDomicilio().getCiudad())
						|| !persInfoAdicional.getPersDomicilio().getDireccion()
								.equals(formRequest.getDomicilio().getDireccion())
						|| !persInfoAdicional.getPersDomicilio().getNumeroDomicilio()
								.equals(formRequest.getDomicilio().getNumeroDomicilio())
						|| !persInfoAdicional.getPersDomicilio().getSector()
								.equals(formRequest.getDomicilio().getSector())) {

					PersonaDomicilio persDomicilio = new PersonaDomicilio();
					Pais pais = new Pais();
					Ciudad ciudad = new Ciudad();
					persDomicilio.setIdDomicilio(persInfoAdicional.getPersDomicilio().getIdDomicilio());
					pais = paisService.findById(formRequest.getDomicilio().getPais());
					persDomicilio.setPais(pais);
					ciudad.setIdCiudad(formRequest.getDomicilio().getCiudad());
					persDomicilio.setCiudad(ciudad);
					persDomicilio.setDireccion(formRequest.getDomicilio().getDireccion());
					persDomicilio.setNumeroDomicilio(formRequest.getDomicilio().getNumeroDomicilio());
					persDomicilio.setSector(formRequest.getDomicilio().getSector());
					persDomicilio.setEstado(persInfoAdicional.getPersDomicilio().getEstado());
					persDomicilioService.saveDomicilio(persDomicilio);
				}

				if (!persInfoAdicional.getPersTipoCuenta().getTitular()
						.equals(formRequest.getTipoCuenta().getTitularCuenta().toUpperCase())
						|| !persInfoAdicional.getPersTipoCuenta().getBanco().getIdBanco()
								.equals(formRequest.getTipoCuenta().getBancoCuenta())
						|| !persInfoAdicional.getPersTipoCuenta().getTipoCuenta()
								.equals(formRequest.getTipoCuenta().getTipoCuenta())
						|| !persInfoAdicional.getPersTipoCuenta().getNumeroCuenta()
								.equals(formRequest.getTipoCuenta().getNumeroCuenta())) {

					Banco banco = new Banco();
					PersonaTipoCuenta formTipoCuenta = new PersonaTipoCuenta();
					formTipoCuenta.setIdPersCuenta(persInfoAdicional.getPersTipoCuenta().getIdPersCuenta());
					formTipoCuenta.setTitular(formRequest.getTipoCuenta().getTitularCuenta());
					banco.setIdBanco(formRequest.getTipoCuenta().getBancoCuenta());
					formTipoCuenta.setBanco(banco);
					formTipoCuenta.setPersona(persInfoAdicional.getPersona());
					formTipoCuenta.setTipoCuenta(formRequest.getTipoCuenta().getTipoCuenta());
					formTipoCuenta.setNumeroCuenta(formRequest.getTipoCuenta().getNumeroCuenta());
					formTipoCuenta.setEstado(persInfoAdicional.getPersTipoCuenta().getEstado());
					persTipoCtaService.saveTipoCuenta(formTipoCuenta);
				}

				if (persInfoAdicional.getPersEstadoFinanciero().getIngresoAnual()
						.compareTo(formRequest.getEstadoFinanciero().getIngresoAnual()) != 0
						|| persInfoAdicional.getPersEstadoFinanciero().getEgresoAnual()
								.compareTo(formRequest.getEstadoFinanciero().getEgresoAnual()) != 0
						|| persInfoAdicional.getPersEstadoFinanciero().getTotalActivo()
								.compareTo(formRequest.getEstadoFinanciero().getTotalActivo()) != 0
						|| persInfoAdicional.getPersEstadoFinanciero().getTotalPasivo()
								.compareTo(formRequest.getEstadoFinanciero().getTotalPasivo()) != 0
						|| persInfoAdicional.getPersEstadoFinanciero().getTotalPatrimonio()
								.compareTo(formRequest.getEstadoFinanciero().getTotalPatrimonio()) != 0) {

					PersonaEstadoFinanciero formEstFinan = new PersonaEstadoFinanciero();
					formEstFinan.setIdEstFinan(persInfoAdicional.getPersEstadoFinanciero().getIdEstFinan());
					formEstFinan.setIngresoAnual(formRequest.getEstadoFinanciero().getIngresoAnual());
					formEstFinan.setEgresoAnual(formRequest.getEstadoFinanciero().getEgresoAnual());
					formEstFinan.setTotalActivo(formRequest.getEstadoFinanciero().getTotalActivo());
					formEstFinan.setTotalPasivo(formRequest.getEstadoFinanciero().getTotalPasivo());
					formEstFinan.setTotalPatrimonio(formRequest.getEstadoFinanciero().getTotalPatrimonio());
					formEstFinan.setEstado(persInfoAdicional.getPersEstadoFinanciero().getEstado());
					persEstadoFinanService.saveEstadoFinanciero(formEstFinan);
				}

				if (!persInfoAdicional.getPersRepreLegal().getNombres()
						.equals(formRequest.getRepresentanteLegal().getNombres().toUpperCase())
						|| !persInfoAdicional.getPersRepreLegal().getApellidos()
								.equals(formRequest.getRepresentanteLegal().getApellidos().toUpperCase())
						|| !persInfoAdicional.getPersRepreLegal().getIdentificacion()
								.equals(formRequest.getRepresentanteLegal().getIdentificacion())
						|| !persInfoAdicional.getPersRepreLegal().getEmail()
								.equals(formRequest.getRepresentanteLegal().getEmail())
						|| !persInfoAdicional.getPersRepreLegal().getNumeroCelular()
								.equals(formRequest.getRepresentanteLegal().getCelular())
						|| !persInfoAdicional.getPersRepreLegal().getCargo()
								.equals(formRequest.getRepresentanteLegal().getCargo())
						|| !persInfoAdicional.getPersRepreLegal().getPais().getIdNacionalidad()
								.equals(formRequest.getRepresentanteLegal().getPais())
						|| !persInfoAdicional.getPersRepreLegal().getTelefono()
								.equals(formRequest.getRepresentanteLegal().getTelefono())
						|| !persInfoAdicional.getPersRepreLegal().getDireccionDomicilio().toUpperCase()
								.equals(formRequest.getRepresentanteLegal().getDireccionDomicilio().toUpperCase())
						|| persInfoAdicional.getPersRepreLegal().getFechaInicioActividad()
								.compareTo(formRequest.getRepresentanteLegal().getFechaInicioActividad()) != 0
						|| !persInfoAdicional.getPersRepreLegal().getNumeroDomicilio().toUpperCase()
								.equals(formRequest.getRepresentanteLegal().getNumeroDomicilio().toUpperCase())) {

					PersonaRepresentanteLegal formRepreLegal = new PersonaRepresentanteLegal();
					formRepreLegal.setIdRepreLegal(persInfoAdicional.getPersRepreLegal().getIdRepreLegal());
					formRepreLegal.setNombres(formRequest.getRepresentanteLegal().getNombres());
					formRepreLegal.setApellidos(formRequest.getRepresentanteLegal().getApellidos());
					formRepreLegal.setIdentificacion(formRequest.getRepresentanteLegal().getIdentificacion());
					formRepreLegal.setEmail(formRequest.getRepresentanteLegal().getEmail());
					formRepreLegal.setNumeroCelular(formRequest.getRepresentanteLegal().getCelular());
					formRepreLegal.setCargo(formRequest.getRepresentanteLegal().getCargo());
					Pais paisRepreLegal = new Pais();
					paisRepreLegal = paisService.findById(formRequest.getRepresentanteLegal().getPais());
					formRepreLegal.setPais(paisRepreLegal);
					formRepreLegal.setTelefono(formRequest.getRepresentanteLegal().getTelefono());
					formRepreLegal.setDireccionDomicilio(formRequest.getRepresentanteLegal().getDireccionDomicilio());
					formRepreLegal
							.setFechaInicioActividad(formRequest.getRepresentanteLegal().getFechaInicioActividad());
					formRepreLegal.setNumeroDomicilio(formRequest.getRepresentanteLegal().getNumeroDomicilio());
					formRepreLegal.setEstado(persInfoAdicional.getPersRepreLegal().getEstado());

					persRepreLegalService.saveRepresentanteLegal(formRepreLegal);
				}

				if (!persInfoAdicional.getPersFirma().getNombresCompletos()
						.equals(formRequest.getFirmaAutorizada().getNombresCompletos().toUpperCase())
						|| !persInfoAdicional.getPersFirma().getIdentificacion()
								.equals(formRequest.getFirmaAutorizada().getIdentificacion())
						|| !persInfoAdicional.getPersFirma().getEmail()
								.equals(formRequest.getFirmaAutorizada().getEmail())) {

					PersonaFirma formFirma = new PersonaFirma();
					formFirma.setIdFirma(persInfoAdicional.getPersFirma().getIdFirma());
					formFirma.setNombresCompletos(formRequest.getFirmaAutorizada().getNombresCompletos().toUpperCase());
					formFirma.setIdentificacion(formRequest.getFirmaAutorizada().getIdentificacion());
					formFirma.setEmail(formRequest.getFirmaAutorizada().getEmail());
					formFirma.setEstado(persInfoAdicional.getPersFirma().getEstado());

					persFirmaService.savePersonaFirma(formFirma);
				}

				response = "ok";

			} else {
				LOG.info("updatePersonaJur: no exite datos del formulario de inversion para la solicitud "
						+ formRequest.getNumeroSolicitud());
				response = "No existe datos del formulario de inversión";
			}
		} catch (Exception e) {
			LOG.error("updatePersonaJur: Error al actualizar formulario " + e.getMessage());
			return "Error al actualizar formulario";
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean tieneSolicitudes(String identificacion) {
		boolean existe = false;
		if (solicitudRepository.tieneSolicitudes(identificacion) > 0) {
			existe = true;
		}
		return existe;
	}

//	@Override
//	@Transactional(readOnly = true)
//	public SolicitudResponse consultaSolicitud(Long numeroSolicitud) {
//		SolicitudResponse solResponse = new SolicitudResponse();
//		Solicitud sol = new Solicitud();
//		sol = solicitudRepository.consultaSolicitud(numeroSolicitud);
//		if (sol != null) {
//			solResponse = this.consultaSolicitud(sol);
//		} else {
//			solResponse = null;
//		}
//		return solResponse;
//	}

	@Override
	@Transactional
	public String guardaDocumentoIdentificacion(String solicitud, MultipartFile file, MultipartFile filepost)
			throws Exception {
		SolicitudDocumentoRequest solDocumento = new SolicitudDocumentoRequest();
		String docResp = "";
		solDocumento = this.getJsonSolicitud(solicitud);
		Cuenta cuenta;
		cuenta = cuentaRepository.findByCuentaIdCuenta(solDocumento.getIdentificacion());
		if (cuenta == null) {
			throw new Exception("No existe Cuenta con la identificacion " + solDocumento.getIdentificacion());
		}
		if (cuenta.getPersona().getPersInfoAdicional().getPersonaDocumentos() != null) {
			throw new Exception("Ya existe documentos para la identificacion " + solDocumento.getIdentificacion());
		}
		if (file == null && filepost == null) {
			throw new Exception("Falta Archivo por cargar " + solDocumento.getIdentificacion());
		}
		try {
			docResp = this.guardacedula(solDocumento, file, filepost);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception(e.getMessage());
		}
		return docResp;
	}

	@Override
	@Transactional
	public String actualizaDocumentoFormulario(String solicitud, MultipartFile file, MultipartFile filepost)
			throws Exception {
		// Solicitud = new Solicitud();
		Persona pers;
		SolicitudDocumentoRequest solDocumento = new SolicitudDocumentoRequest();
		String docResp = "";
		String usuario;
		if (!solicitud.isEmpty()) {
			solDocumento = this.getJsonSolicitud(solicitud);
			pers = this.personaService.findById(solDocumento.getIdentificacion());
			if (pers == null) {
				throw new Exception("No existe persona con " + solDocumento.getIdentificacion());
			}

			if (pers.getPersInfoAdicional().getPersonaDocumentos() != null) {
				LOG.error("Error debe adjuntar las fotos de la cedula");
				// caduca el documento que tiene cargado actualmente
				usuario = solDocumento.getUsuario();
				PersonaDocumento ps = new PersonaDocumento();
				ps = pers.getPersInfoAdicional().getPersonaDocumentos();

				if (ps.getEstado().contains("A")) {
					ps.setEstado("I");
					ps.setFechaModificacion(LocalDateTime.now());
					ps.setUsuarioModificacion(usuario);
					// persDocService.saveDocumento(d);
				}

				// carga nuevo documento
				try {
					docResp = this.guardacedula(solDocumento, file, filepost);
				} catch (Exception e) {
					LOG.error(e.getMessage());
					throw new Exception(e.getMessage());
				}
			} else {
				LOG.error("El inversionista no tiene un documento existente");
				throw new Exception("El inversionista no tiene un documento existente");
			}
		} else {
			LOG.error("Los campos numeroSolicitud, identificacion y usuario no deben ser nulos");
			throw new Exception("Los campos numeroSolicitud, identificacion y usuario no deben ser nulos");
		}

		return docResp;
	}

	public SolicitudDocumentoRequest getJsonSolicitud(String solicitud) {
		SolicitudDocumentoRequest solicitudJson = new SolicitudDocumentoRequest();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			solicitudJson = objectMapper.readValue(solicitud, SolicitudDocumentoRequest.class);
		} catch (IOException e) {
			LOG.error("Error " + e.toString());
		}
		return solicitudJson;
	}

	// Cambiar nombre de contenedor
	@Override
	@Transactional
	public String guardarDocumentosxSolicitud(Long numeroSolicitud, MultipartFile[] file, String observacion,
			String usuarioInterno) throws Exception {
		String formDoc = null;
		LOG.error("entro");
		Solicitud s = this.solicitudRepository.consultaSolicitud(numeroSolicitud);
		if (!s.getEstadoActual().getIdEstado().equals("SATF")) {
			throw new Exception("La solicitud no se encuentra en el estado Aprobada por transferir fondos");
		}
		LOG.error("entro pro");
		SolicitudDocumentos sdd = this.solicitudRepository.consultaDocumentos(numeroSolicitud);

		if (sdd != null) {
			throw new Exception("Error el numero de solicitud ya tiene documentos ingresados");
		}
		LOG.error("entro");
		ParametroResponse paramResponse = new ParametroResponse();
		paramResponse = parametroService.findByParametroCodParametro("CONTCONTRATO");
		LOG.error("entro");
		MultiplesDocumentosRequest[] files = new MultiplesDocumentosRequest[6];
		DocumentoSolicitudResponse[] doc = new DocumentoSolicitudResponse[6];
		int i = 0;
		String nombreArchivo = "";
		LOG.error("" + file[0].getName());
		for (MultipartFile dat : file) {
			MultiplesDocumentosRequest aux = new MultiplesDocumentosRequest();
			if (dat == null) {
				formDoc = "Error archivo no valido, debe cargar todos los archivos";
				throw new Exception("Error archivo no valido, debe cargar todos los archivos");
			}
			try {
				String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss"));
				String contentType = dat.getContentType().toLowerCase();// application/pdf
				if (!contentType.equals("application/pdf")) {
					formDoc = "Error archivo no valido, debe cargar un pdf";
					throw new Exception("Error archivo no valido, debe cargar un pdf");
				}
				aux.setMultipartFiles(dat);
				String type = contentType.split("/")[1];
				switch (i) {
				case 0:
					nombreArchivo = s.getUsuarioCreacion().getPersona().getIdentificacion() + "@datosInversionista_"
							+ timeStamp + "." + type;
					break;
				case 1:
					nombreArchivo = s.getUsuarioCreacion().getPersona().getIdentificacion() + "@contratoPrenda_"
							+ timeStamp + "." + type;
					break;
				case 2:
					nombreArchivo = s.getUsuarioCreacion().getPersona().getIdentificacion() + "@modeloContrato_"
							+ timeStamp + "." + type;
					break;
				case 3:
					nombreArchivo = s.getUsuarioCreacion().getPersona().getIdentificacion() + "@pagare_" + timeStamp
							+ "." + type;
					break;
				case 4:
					nombreArchivo = s.getUsuarioCreacion().getPersona().getIdentificacion() + "@tablaAmortizacion_"
							+ timeStamp + "." + type;
					break;
				case 5:
					nombreArchivo = s.getUsuarioCreacion().getPersona().getIdentificacion() + "@acuerdoUso_" + timeStamp
							+ "." + type;
					break;
				}
				aux.setFileName(nombreArchivo);
				files[i] = aux;
				i++;
			} catch (Exception e) {
				formDoc = "Error al cargar documento: " + e.getMessage();
				LOG.error("Error al cargar documento: " + e.getMessage());
				throw new Exception("Error al cargar documento:");
			}
		}
		doc = blobStorageService.uploadMultipleFile(files, paramResponse.getValor());
		SolicitudDocumentos sd = new SolicitudDocumentos();
		sd.setSolicitud(new Solicitud(s.getNumeroSolicitud()));
		sd.setDatosInversionista(doc[0].getRuta());
		sd.setContratoPrendaRi(doc[1].getRuta());
		sd.setModeloContrato(doc[2].getRuta());
		sd.setPagare(doc[3].getRuta());
		sd.setTablaAmortizacion(doc[4].getRuta());
		sd.setAcuerdoUso(doc[5].getRuta());
		sd.setFechaCreacion(LocalDateTime.now());
		sd.setUsuarioCreacion(usuarioInterno);
		sd.setEstado("A");
		sd.setObservacion(observacion);
		s.setDocumentos(sd);
		solicitudRepository.save(s);
		formDoc = "ok";
		return formDoc;
	}

	@Override
	@Transactional
	public String aprobarProyectoaFirmaContrato(String codProyecto, String usuario) throws Exception {
		String respuesta = "";
		Map<Long, SolicitudDocumentos> datoHash = new HashMap<>();
		List<Solicitud> lista = new LinkedList<Solicitud>();
		List<SolicitudDocumentos> listaDocs = new LinkedList<SolicitudDocumentos>();
		listaDocs = this.solicitudDocRepository.consultarTodos();
		SolicitudDocumentos aux = new SolicitudDocumentos();
		boolean estado = false;
		String mensaje = "";
		for (SolicitudDocumentos s : listaDocs) {
			datoHash.put(s.getSolicitud().getNumeroSolicitud(), s);
		}
		lista = this.solicitudRepository.consultaSolicitudPorProyecto(codProyecto);
		if (lista.isEmpty()) {
			return ("Codigo de proyecto no existe o no tiene solicitudes vigentes");
		}
		Iterator<Solicitud> i = lista.iterator();
		while (i.hasNext()) {
			Solicitud e = i.next();
			if (!e.getEstadoActual().getIdEstado().equals("SATF")) {
				i.remove();
			} else {
				aux = (SolicitudDocumentos) datoHash.get(e.getNumeroSolicitud());
				if (aux == null) {
					estado = true;
					mensaje = mensaje + " - " + e.getNumeroSolicitud();
				}
			}
		}
		if (estado) {
			respuesta = "La(s) solicitud(es): " + mensaje + " faltan de subir documentos";
			return respuesta;
		}
		List<Long> listans = new ArrayList<>();
		for (Solicitud s : lista) {
			// NumeroSolicitudRequest ns = new NumeroSolicitudRequest();
			// ns.setNumeroSolicitud(s.getNumeroSolicitud());
			listans.add(s.getNumeroSolicitud());
		}
		UpdatePYSRequest up = new UpdatePYSRequest();
		up.setCodigoProyecto(codProyecto);
		up.setSolicitudes(listans);
		up.setObservacionProyecto("Proyectos cambiados por subida de documentos");
		up.setRutaComprobante(null);
		up.setStatusSol("SFC");
		up.setStatusProyect("FC");
		up.setUsuario(usuario);
		mensaje = mensaje + this.updatePYS(up);
		LOG.warn(mensaje);
		respuesta = "ok";
		return respuesta;
	}

	public String guardacedula(SolicitudDocumentoRequest solDocIdentificacion, MultipartFile file,
			MultipartFile fileposterior) throws Exception {
		String formDoc = "";
		String nombreArchivofront;
		String nombreArchivopost;

		PersonaInfoAdicional infoForm = new PersonaInfoAdicional();

		String nombreDocCargado = null;
		String rutaDocCargado = null;

		String rutaArchivo;
		String rutaArchivopost;
		LOG.error("Error archino no valido, debe cargar un jpg o png");
		if (!file.isEmpty() && !fileposterior.isEmpty()) {
			try {
				String contentType = file.getContentType().toLowerCase();// application/pdf
				String contentType2 = fileposterior.getContentType().toLowerCase();// application/pdf
				LOG.error("Error archino no valido, debe cargar un jpg o png2");
				if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
						&& !contentType2.equals("image/jpeg") && !contentType2.equals("image/png")) {
					LOG.error("Error archino no valido, debe cargar un jpg o png");
					throw new Exception("Error archino no valido, debe cargar un jpg o png");
				}
				ParametroResponse paramResponse = new ParametroResponse();
				paramResponse = parametroService.findByParametroCodParametro("CONTIDENT");
				String type = contentType2.split("/")[1];// saca extension del archivo
				String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				String type2 = contentType2.split("/")[1];// saca extension del archivo
				nombreArchivofront = solDocIdentificacion.getIdentificacion() + "frontal_" + timeStamp + "." + type;
				// numeroSolicitud = solDocIdentificacion.getNumeroSolicitud();
				nombreArchivopost = solDocIdentificacion.getIdentificacion() + "posterior_" + timeStamp + "." + type;
				rutaArchivo = blobStorageService.uploadFile(file, nombreArchivofront, paramResponse.getValor());
				rutaArchivopost = blobStorageService.uploadFile(fileposterior, nombreArchivopost,
						paramResponse.getValor());
				if (rutaArchivo.contains("Error") || rutaArchivopost.contains("Error")) {
					LOG.error(rutaArchivo);
					LOG.error(rutaArchivopost);
					throw new Exception("Error al subir archivos");
				}
				// idDatoInversion = solicitudRepository.consultaDatoInversion(numeroSolicitud);
				infoForm = infoAdicionalService.consultaPersonaInfoAdicional(solDocIdentificacion.getIdentificacion());
				PersonaDocumento documento = new PersonaDocumento();
				documento.setNombre(nombreArchivofront);
				documento.setRuta(rutaArchivo);
				documento.setFechaCreacion(LocalDateTime.now());
				documento.setUsuarioCreacion(solDocIdentificacion.getUsuario());
				documento.setRutapost(rutaArchivopost);
				documento.setNombrepost(nombreArchivopost);
				documento.setPersonaInfoAdicional(infoForm);
				documento.setDocumento(tipoDocService.findByIdTipoDocumento((long) 1));// 1=tipo de documento cedula
				infoForm.setPersonaDocumentos(documento);
				this.personaInfoAdicionalRepository.save(infoForm);
				// datoInversionService.updateDatosDocumentacion(true, idDatoInversion);
				formDoc = "ok";
			} catch (Exception e) {
				LOG.error("Error al cargar documento: " + e.getMessage());
				throw new Exception(e.getMessage());
			}
		} else {
			LOG.error("Error debe adjuntar las fotos de la cedula");
			throw new Exception("Error debe adjuntar las fotos de la cedula");
		}
		return formDoc;
	}

	public DocIdentificacionResponse guardaDocumentos(SolicitudDocumentoRequest solDocIdentificacion,
			SolicitudDocPagoRequest solDocPago, MultipartFile file, String tipoDocumento) throws Exception {
		Long idInfoAdicional;
		Long idDatoInversion;
		String rutaArchivo;
		DocIdentificacionResponse formDoc;
		String nombreArchivo;
		String nombreDocCargado = null;
		String rutaDocCargado = null;
		Long numeroSolicitud;
		if (!file.isEmpty()) {
			try {
				String contentType = file.getContentType().toLowerCase();// application/pdf
				String type = contentType.split("/")[1];// saca extension del archivo
				String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
					LOG.error("Error archino no valido, debe cargar un jpg o png");
					throw new Exception("Error archino no valido, debe cargar un pdf");
				}

				ParametroResponse paramResponse = new ParametroResponse();
				if (tipoDocumento.contains("CED")) {
					nombreArchivo = solDocIdentificacion.getIdentificacion() + "_" + timeStamp + "." + type;
					paramResponse = parametroService.findByParametroCodParametro("CONTIDENT");// contenedor para
																								// identificaciones
					// numeroSolicitud = solDocIdentificacion.getNumeroSolicitud();
				} else {
					nombreArchivo = "SOL" + solDocPago.getNumeroSolicitud() + "_" + timeStamp + "." + type;
					paramResponse = parametroService.findByParametroCodParametro("CONTDEPOS");// contenedor para
																								// depositos
					numeroSolicitud = solDocPago.getNumeroSolicitud();
				}

				rutaArchivo = blobStorageService.uploadFile(file, nombreArchivo, paramResponse.getValor());

				if (rutaArchivo.contains("Error")) {
					LOG.error(rutaArchivo);
					throw new Exception(rutaArchivo);
				}

				// idDatoInversion = solicitudRepository.consultaDatoInversion(numeroSolicitud);
				if (tipoDocumento.contains("CED")) {
					idInfoAdicional = infoAdicionalService
							.consultaIdInfoAdicional(solDocIdentificacion.getIdentificacion());
					PersonaDocumento documento = new PersonaDocumento();
					documento.setNombre(nombreArchivo);
					documento.setRuta(rutaArchivo);
					documento.setFechaCreacion(LocalDateTime.now());
					documento.setUsuarioCreacion(solDocIdentificacion.getUsuario());

					PersonaInfoAdicional infoForm = new PersonaInfoAdicional();
					infoForm.setIdInfoAdicional(idInfoAdicional);
					documento.setPersonaInfoAdicional(infoForm);
					documento.setDocumento(tipoDocService.findByIdTipoDocumento((long) 1));// 1=tipo de documento cedula
					persDocService.saveDocumento(documento);

					// datoInversionService.updateDatosDocumentacion(true, idDatoInversion);

					nombreDocCargado = documento.getDocumento().getDocumento();
					rutaDocCargado = documento.getRuta();
				} else {// DEPOSITO
					Transaccion transaccion = new Transaccion();
					transaccion.setSolicitud(new Solicitud(solDocPago.getNumeroSolicitud()));
					transaccion.setNumeroComprobante(solDocPago.getNumeroComprobante());
					transaccion.setFechaTransaccion(LocalDate.parse(solDocPago.getFechaTransaccion()));
					double montoInversion = Double.parseDouble(solDocPago.getMonto().toString().replace(",", ""));
					transaccion.setMonto(new BigDecimal(montoInversion));
					transaccion.setFormaPago(new FormaPago(solDocPago.getFormaPago()));
					transaccion.setNombreDocumento(nombreArchivo);
					transaccion.setRutaComprobante(rutaArchivo);
					// transaccion.setUsuarioCreacion(new Cuenta(sol.getIdentificacion()));
					transaccion.setFechaCreacion(LocalDateTime.now());
					transaccion.setDocumento(tipoDocService.findByIdTipoDocumento((long) 2));// 2=tipo de documento
																								// deposito

					// consulta solicitud para verificar el tipo de transaccion
					Solicitud sol = new Solicitud();
					sol = solicitudRepository.consultaSolicitud(solDocPago.getNumeroSolicitud());

					sol.getTransaccion().add(transaccion);
					solicitudRepository.save(sol);

//		        	transaccionService.guardaTransaccion(transaccion);

					// datoInversionService.updateDatosPago(true, idDatoInversion);

					// sol.setFechaModificacion(LocalDateTime.now());
					// sol.setUsuarioModificacion(solDocPago.getUsuario());
//		        	sol.setFechaVigencia(LocalDateTime.now());
					sol.setTipoSolicitud(new TipoSolicitud((long) 1));

					// Generar y guardar el documento de licitud de fondos en documentos aceptados
					String rutaDocLicitudFondo;
					rutaDocLicitudFondo = this.generayGuardaDocLicitudFondoAzure(sol);
					if (rutaDocLicitudFondo.contains("Error")) {
						LOG.warn(rutaDocLicitudFondo);
					}

					// guardar nuevo registro de la solicitud
					this.actualizaSolicitudPorConfirmar(sol, solDocPago);
					sol = null;

					// enviar correo al cliente
					String respuestaEmail;
					respuestaEmail = this.enviaEmailInversionFinalizada(solDocPago.getNumeroSolicitud());
					if (respuestaEmail.contains("Error")) {
						LOG.warn(respuestaEmail);
					}

					nombreDocCargado = transaccion.getDocumento().getDocumento();
					rutaDocCargado = transaccion.getRutaComprobante();
				}
				formDoc = new DocIdentificacionResponse();
				formDoc.setNombre(nombreDocCargado);
				formDoc.setRuta(rutaDocCargado);

			} catch (Exception e) {
				LOG.error("Error al cargar documento: " + e.getMessage());
				throw new Exception(e.getMessage());
			}

		} else {
			LOG.error("Error debe adjuntar las fotos de la cedula");
			throw new Exception("Error debe adjuntar las fotos de la cedula");
		}

		return formDoc;
	}

	@Override
	@Transactional
	public String guardaDocumentoComprobantePago(String solicitud, MultipartFile file, String tipoDocumento)
			throws Exception {
		// VALIDAMOS SI TIENE DATOS EL PARAMETRO SOLICITUD
		String respuestaEmail;
		if (solicitud.isEmpty()) {
			throw new Exception("Informacion incompleta");
		}
		// DECLARAMOS VARIABLES

		SolicitudDocPagoRequest solDocPago = new SolicitudDocPagoRequest();
		solDocPago = this.getObjSolComprobantePago(solicitud);

		String rutaArchivo;
		String contentType = file.getContentType().toLowerCase();// application/pdf
		String type = contentType.split("/")[1];// saca extension del archivo
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String nombreArchivo = "SOL" + solDocPago.getNumeroSolicitud() + "_" + timeStamp + "." + type;
		Solicitud sol;
		ParametroResponse paramResponse = new ParametroResponse();

		// CONSULTAMOS LA SOLICITUD
		sol = this.solicitudRepository.consultaSolicitud(solDocPago.getNumeroSolicitud());
		LOG.warn("consulto");
		double montoT = Double.parseDouble(solDocPago.getMonto().toString().replace(",", ""));
		BigDecimal monto = new BigDecimal(montoT);
		if (sol == null) {
			throw new Exception("No existe Solicitud: " + solDocPago.getNumeroSolicitud());
		}
		if (sol.getAmortizacion().getMontoInversion().doubleValue() != monto.doubleValue()) {
			throw new Exception("El monto de la inversión no coincide: " + sol.getAmortizacion().getMontoInversion());
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
		transaccion.setSolicitud(new Solicitud(solDocPago.getNumeroSolicitud()));
		transaccion.setNumeroComprobante(solDocPago.getNumeroComprobante());
		transaccion.setFechaTransaccion(LocalDate.parse(solDocPago.getFechaTransaccion()));
		double montoInversion = Double.parseDouble(solDocPago.getMonto().toString().replace(",", ""));
		transaccion.setMonto(new BigDecimal(montoInversion));
		transaccion.setFormaPago(new FormaPago(solDocPago.getFormaPago()));
		transaccion.setNombreDocumento(nombreArchivo);
		transaccion.setRutaComprobante(rutaArchivo);
		transaccion.setUsuarioCreacion(sol.getInversionista());
		transaccion.setFechaCreacion(LocalDateTime.now());
		transaccion.setDocumento(tipoDocService.findByIdTipoDocumento((long) 2));
		transaccion.setDepositante(solDocPago.getDepositante());
		sol.setEstadoActual(new TipoEstado("PC"));
		sol.getTransaccion().add(transaccion);
		sol.setAceptaInformacionCorrecta("S");
		sol.setAceptaIngresarInfoVigente("S");
		sol.setAceptaLicitudFondos("S");

		HistorialDeSolicitud historialSol = new HistorialDeSolicitud();
		historialSol.setSolicitud(sol);
		historialSol.setTablaCambiar("Estado");
		historialSol.setValorActual("PC");
		historialSol.setObservacion("Cambio de estado a por confirmar");
		historialSol.setValorAnterior(sol.getEstadoActual().getIdEstado());
		historialSol.setUsuarioModificacion(sol.getInversionista());
		sol.setHistorial(historialSol);
		this.solicitudRepository.save(sol);
		LOG.warn("guardado");
//    	String respuestaEmail;
		respuestaEmail = this.enviaEmailInversionFinalizada(solDocPago.getNumeroSolicitud());
		if (respuestaEmail.contains("Error")) {
			LOG.warn(respuestaEmail);
		}

//		int count=0;
//		if(!solicitud.isEmpty()) {
//			solDocPago = this.getObjSolComprobantePago(solicitud);
//			count = solicitudRepository.tieneSolicitud(Long.parseLong(solDocPago.getNumeroSolicitud()),null);
//			if(count > 0) {
//				
////				Transaccion solTrx = new Transaccion();
////				solTrx = transaccionService.consultaTransaccion(Long.parseLong(solDocPago.getNumeroSolicitud()));
////				if(solTrx != null) {
////					LOG.info("Ya existe un comprobante de pago: numeroSolicitud {"+solDocPago.getNumeroSolicitud()+"}");
////					throw new Exception("Ya existe un comprobante de pago");
////				}
//				
//				TablaAmortizacion tbl = new TablaAmortizacion();
//				tbl = tblAmortizacionService.consultaTblAmortizacionPorNumSolicitud(solDocPago.getNumeroSolicitud(), (long) 1);
//				double montoComprobante = Double.parseDouble(solDocPago.getMonto().replace(",", ""));
//				
//				if(montoComprobante < tbl.getMontoInversion().doubleValue() || montoComprobante > tbl.getMontoInversion().doubleValue()) {
//					LOG.error("El monto del comprobante es diferente al monto de la solicitud de inversion. Comprobante[$"+montoComprobante+"], Solicitud[$"+tbl.getMontoInversion().doubleValue()+"]");
//					throw new Exception("El monto del comprobante es diferente al monto de la solicitud de inversión. Comprobante[$"+montoComprobante+"], Solicitud[$"+tbl.getMontoInversion().doubleValue()+"]");
//				}
//			
//			}else {
//				LOG.error("No existe información del inversionista con los datos ingresados: numeroSolicitud {"+solDocPago.getNumeroSolicitud()+"}");
//				throw new Exception("No existe información del inversionista con la solicitud ingresada");
//			}
//			
//		}else {
//			LOG.error("Los campos numeroSolicitud, usuario, nombreCliente, formaPago, numeroComprobante, fechaTransaccion y monto no deben ser nulos");
//			throw new Exception("Los campos numeroSolicitud, usuario, nombreCliente, formaPago, numeroComprobante, fechaTransaccion y monto no deben ser nulos");
//		}
		return "Transaccion guardada correctamente";
	}

	@Override
	@Transactional
	public DocIdentificacionResponse actualizaDocumentoComprobantePago(String solicitud, MultipartFile file,
			String tipoDocumento) throws Exception {
		Transaccion solTrx = new Transaccion();
		SolicitudDocPagoRequest solDocPago = new SolicitudDocPagoRequest();
		DocIdentificacionResponse docResp = new DocIdentificacionResponse();
		if (!solicitud.isEmpty()) {
			solDocPago = this.getObjSolComprobantePago(solicitud);
			solTrx = transaccionService.consultaTransaccion(solDocPago.getNumeroSolicitud());
			if (solTrx != null) {
				// caduca el documento que tiene cargado actualmente
				solTrx.setEstado("I");
//				solTrx.setFechaModificacion(LocalDateTime.now());
//				solTrx.setUsuarioModificacion(solDocPago.getUsuario());

				// carga nuevo documento
				try {
					docResp = this.guardaDocumentos(null, solDocPago, file, tipoDocumento);
				} catch (Exception e) {
					LOG.error(e.getMessage());
					throw new Exception(e.getMessage());
				}
			} else {
				LOG.error("El inversionista no tiene un comprobante de pago existente");
				throw new Exception("El inversionista no tiene un comprobante de pago existente");
			}
		} else {
			LOG.error(
					"Los campos numeroSolicitud, usuario, nombreCliente, formaPago, numeroComprobante, fechaTransaccion y monto no deben ser nulos");
			throw new Exception(
					"Los campos numeroSolicitud, usuario, nombreCliente, formaPago, numeroComprobante, fechaTransaccion y monto no deben ser nulos");
		}
		return docResp;
	}

	@Override
	@Transactional
	public List<Transaccion> consultarDocumentoComprobantePago(Long numSolicitud) {
		List<Transaccion> solTrx = new ArrayList<>();
		solTrx = transaccionService.findBySolicitud(numSolicitud);
		return solTrx;
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

	public void actualizaSolicitudPorConfirmar(Solicitud solicitud, SolicitudDocPagoRequest solDocPago) {
		Solicitud solNueva = new Solicitud();
		solNueva.setNumeroSolicitud(solicitud.getNumeroSolicitud());
//		solNueva.setTipoPersona(solicitud.getTipoPersona());
//		solNueva.setUsuario(solicitud.getUsuario());
		solNueva.setProyecto(solicitud.getProyecto());
		solNueva.setTipoSolicitud(solicitud.getTipoSolicitud());
//		solNueva.setTipoContacto(solicitud.getTipoContacto());
//		solNueva.setUsuarioContacto(solicitud.getUsuarioContacto());
		solNueva.setObservacion(solicitud.getObservacion());
//		solNueva.setEstadoAnterior(solicitud.getEstadoActual());
		TipoEstado tipoEstado = new TipoEstado();
		tipoEstado.setIdEstado("PC");
		solNueva.setEstadoActual(tipoEstado);
		solNueva.setDatosInversion(solicitud.getDatosInversion());
		solNueva.getInversionista().setPersona(solicitud.getInversionista().getPersona());
		solNueva.setAceptaInformacionCorrecta(solDocPago.getAceptaInformacionCorrecta());
		solNueva.setAceptaIngresarInfoVigente(solDocPago.getAceptaIngresarInfoVigente());
		solNueva.setAceptaLicitudFondos(solDocPago.getAceptaLicitudFondos());
		solNueva.setFechaGeneracion(new Date());

		solNueva.setUsuarioCreacion(solicitud.getUsuarioCreacion());

//		solNueva.setUsuarioModificacion(solDocPago.getUsuario());//
//		solNueva.setFechaModificacion(LocalDateTime.now());//

		solicitudRepository.save(solNueva);
	}

	public String generayGuardaDocLicitudFondoAzure(Solicitud solicitud) {
		String nombreArchivo;
		String contentType;
		String type;
		String respuestaRuta;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ParametroResponse paramResponse = new ParametroResponse();

		// genera documento pdf
		try {
			baos = generaPdfService.getLicitudFondoPDF(solicitud.getInversionista().getPersona().getIdentificacion());
		} catch (Exception e) {
		}
		if (baos != null) {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
			contentType = "application/pdf";
			type = contentType.split("/")[1];
			String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			nombreArchivo = "LF" + solicitud.getInversionista().getPersona().getIdentificacion() + "_SOL"
					+ solicitud.getNumeroSolicitud() + "_" + timeStamp + "." + type;
			// contenedor para documentos aprobados en las solicitudes de inversion
			paramResponse = parametroService.findByParametroCodParametro("CONTSOLIC");

			// guara documento en azure
			respuestaRuta = blobStorageService.uploadForByteArray(inputStream, contentType, nombreArchivo,
					paramResponse.getValor());
			if (respuestaRuta.contains("Error")) {
				return respuestaRuta;
			}

			DocumentoAceptado docAceptado = new DocumentoAceptado();
			docAceptado.setNombre(nombreArchivo);
			docAceptado.setDocumento(tipoDocService.findByIdTipoDocumento((long) 5));
			docAceptado.setRuta(respuestaRuta);
			docAceptado.setVersion(1);
//	    	docAceptado.setUsuarioCreacion(solicitud.getUsuario());
			docAceptado.setFechaCreacion(LocalDateTime.now());
			docAceptado.setNumeroSolicitud(solicitud.getNumeroSolicitud().toString());
			docAceptado.setHabilitado(true);
			docAceptado.setPersona(solicitud.getInversionista().getPersona());
			// guarda documento en la tabla de documentos aceptados
			respuestaRuta = documentoAceptadoService.guardaDocumentoAceptado(docAceptado);
			if (respuestaRuta.contains("Error")) {
				return respuestaRuta;
			}
		} else {
			return "Error en generacion de Documento de Licitud de fondo";
		}
		return respuestaRuta;
	}

	/////////////////////////////// CONSULTAS PARA FLUJO DE
	/////////////////////////////// INVERSIONISTA/////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	@Transactional(readOnly = true)
	public List<InversionEnProcesoResponse> consultaSolcitudesEnProceso(FilterSolicitudRequest filter) {
		return solicitudRepository.consultaSolEnProceso(filter.getIdentificacion(), filter.getCodProyecto(),
				filter.getNumeroSolicitud(), filter.getIdTipoCalificacion(), filter.getIdActividad());
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionPorConfirmarResponse> consultaSolcitudesPorConfirmar(FilterSolicitudRequest filter) {
		return solicitudRepository.consultaSolPorConfirmar(filter.getIdentificacion(), filter.getCodProyecto(),
				filter.getNumeroSolicitud(), filter.getIdTipoCalificacion(), filter.getIdActividad());
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionEnTransitoResponse> consultaSolEnTransitoInversionista(
			FilterSolicitudRequest filterEntransitoInv) {
		List<InversionEnTransitoInversionista> listEnTransito = new ArrayList<>();
		List<InversionEnTransitoResponse> listEnTransitoResponse = new ArrayList<>();
		BigDecimal totalMontoInversion;
		double porcAvance = 0;
		InversionEnTransitoResponse response;

		listEnTransito = solicitudRepository.consultaSolEnTransitoInversionista(filterEntransitoInv.getCodProyecto(),
				filterEntransitoInv.getNumeroSolicitud(), filterEntransitoInv.getIdTipoCalificacion(),
				filterEntransitoInv.getIdActividad(), filterEntransitoInv.getIdentificacion());

		if (!listEnTransito.isEmpty()) {
			for (InversionEnTransitoInversionista s : listEnTransito) {
				response = new InversionEnTransitoResponse();

				// totalMontoInversion =
				// solicitudRepository.consultaMontoInvertidoPorProyectoInversionistas(s.getCodProyecto());
				// porcAvance = (totalMontoInversion.doubleValue() *
				// 100)/s.getMontoSolicitado().doubleValue();
				response.setCodProyecto(s.getCodProyecto());
				response.setMontoInversion(df.format(s.getInversion()));
				response.setMontoPago(df.format(s.getMontoPago()));
				response.setNombreEmpresa(s.getNombreEmpresa());
				response.setNumeroSolicitud(s.getNumeroSolicitud().toString());
				response.setRecaudado(new BigDecimal(porcAvance).setScale(2, RoundingMode.HALF_UP));
				response.setPlazo(s.getPlazo());
				response.setEstado("EN TRÁNSITO");
				listEnTransitoResponse.add(response);
			}
		} else {
			listEnTransitoResponse.clear();
		}

		return listEnTransitoResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionHistorialEstado> consultaHistorialEstadosSolicitud(String numeroSolicitud) {
		List<InversionHistorialEstado> list = new ArrayList<>();
//		List<Solicitud> listSol = new ArrayList<>();
//		PersonalDetalleResponse emp = new PersonalDetalleResponse();
//		listSol = solicitudRepository.consultaHistorialEstadosSolicitud(numeroSolicitud, Sort.by("idSolicitud").ascending());
//		if(!listSol.isEmpty()) {
//			for(Solicitud s : listSol) {
//				InversionHistorialEstado e = new InversionHistorialEstado();
//				e.setEstadoActual(s.getEstadoActual().getDescripcion());
//				if(s.getEstadoAnterior() != null) {
//					if(s.getUsuarioCreacion().contains(s.getUsuarioModificacion())) {
//						e.setUsuario(s.getPersona().getNombres()+" "+s.getPersona().getApellidos());
//					}else {
//						emp = empleadoService.consultaEmpleado(s.getUsuarioModificacion());
//						if(emp != null) {
//							e.setUsuario(emp.getNombres()+" "+emp.getApellidos());
//						}
//					}
//					e.setEstadoAnterior(s.getEstadoAnterior().getDescripcion());
//					e.setFecha(s.getFechaModificacion());
//				}else {
//					e.setUsuario(s.getPersona().getNombres()+" "+s.getPersona().getApellidos());
//					e.setFecha(s.getFechaCreacion());
//				}
//				
//				list.add(e);
//			}
//			listSol.clear();
//		}else {
//			list.clear();
//		}
		return list;
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<CantidadSolicitudResponse> consultaCantidadSolicitudes(String identificacion, String tipoCliente) {
//		List<CantidadSolicitudResponse> listCantSol = new ArrayList<>();
//		List<TipoEstado> listTipoEstado = new ArrayList<>();
//		boolean consultar;
//
//		LOG.info("Servicio dashboard: Tipo Cliente=" + tipoCliente + " identificacion=" + identificacion);
//		if (tipoCliente.equals("INVERSIONISTA")) {// consulta por inversionista
//			consultar = personaService.existePersona(identificacion);
//		} else {// consulta las solicitudes de ANALISTAOPER y GERENTEGENERAL
//			consultar = true;
//		}
//		if (consultar) {
//			listTipoEstado = tipoEstadoService.findAll().stream().filter(te -> te.getEstado().contains("A"))
//					.collect(Collectors.toList());
//			if (!listTipoEstado.isEmpty()) {
//				listTipoEstado.forEach(e -> {
//
//					if (tipoCliente.equals("INVERSIONISTA") && (e.getIdEstado().equals("BO")
//							|| e.getIdEstado().equals("PC") || e.getIdEstado().equals("TN")
//							|| e.getIdEstado().equals("VG") || e.getIdEstado().equals("LQ"))) {
//
//						CantidadSolicitudResponse cantSol = new CantidadSolicitudResponse();
//						cantSol.setEstado(e.getDescripcion());
//						cantSol.setCantidad(
//								solicitudRepository.consultaCantidadSolicitudes(identificacion, e.getIdEstado()));
//						listCantSol.add(cantSol);
//
//					} else if (tipoCliente.equals("ANALISTAOPER")
//							&& (e.getIdEstado().equals("PC") || e.getIdEstado().equals("SATF")
//									|| e.getIdEstado().equals("SFC") || e.getIdEstado().equals("VG")
//									|| e.getIdEstado().equals("LQ") || e.getIdEstado().equals("CON")
//									|| e.getIdEstado().equals("PAP") || e.getIdEstado().equals("ANU"))) {
//
//						CantidadSolicitudResponse cantSol = new CantidadSolicitudResponse();
//						cantSol.setEstado(e.getDescripcion());
//						cantSol.setCantidad(
//								solicitudRepository.consultaCantidadSolicitudes(identificacion, e.getIdEstado()));
//						listCantSol.add(cantSol);
//
//					} else if (tipoCliente.equals("GERENTEGENERAL") && (e.getIdEstado().equals("TN")
//							|| e.getIdEstado().equals("VG") || e.getIdEstado().equals("LQ"))) {
//
//						CantidadSolicitudResponse cantSol = new CantidadSolicitudResponse();
//						cantSol.setEstado(e.getDescripcion());
//						cantSol.setCantidad(
//								solicitudRepository.consultaCantidadSolicitudes(identificacion, e.getIdEstado()));
//						listCantSol.add(cantSol);
//					}
//				});
//			} else {
//				LOG.info("Servicio dashboard: No existen tipos de estados activos");
//				listCantSol.clear();
//			}
//		} else {
//			LOG.info("Servicio dashboard: Consultar=" + consultar);
//			listCantSol.clear();
//		}
//		return listCantSol;
//	}
	@Override
	@Transactional(readOnly = true)
	public List<CantidadSolicitudResponse> consultaCantidadSolicitudes(String identificacion, String tipoCliente) {
		List<CantidadSolicitudResponse> listCantSol = new ArrayList<>();
		List<TipoEstado> listTipoEstado = new ArrayList<>();
		boolean consultar;

		LOG.info("Servicio dashboard: Tipo Cliente=" + tipoCliente + " identificacion=" + identificacion);
		if (tipoCliente.equals("INVERSIONISTA")) {// consulta por inversionista
			consultar = personaService.existePersona(identificacion);
		} else {// consulta las solicitudes de ANALISTAOPER y GERENTEGENERAL
			consultar = true;
		}
		if (consultar) {
			listTipoEstado = tipoEstadoService.findAll().stream().filter(te -> te.getEstado().contains("A"))
					.collect(Collectors.toList());
			if (!listTipoEstado.isEmpty()) {
				listTipoEstado.forEach(e -> {

					if (tipoCliente.equals("INVERSIONISTA") && (e.getIdEstado().equals("BO")
							|| e.getIdEstado().equals("PC") || e.getIdEstado().equals("TN")
							|| e.getIdEstado().equals("VG") || e.getIdEstado().equals("LQ"))) {

						CantidadSolicitudResponse cantSol = new CantidadSolicitudResponse();
						cantSol.setEstado(e.getDescripcion());
						cantSol.setCantidad(
								solicitudRepository.consultaCantidadSolicitudes(identificacion, e.getIdEstado()));
						listCantSol.add(cantSol);

					} else if (tipoCliente.equals("ANALISTAOPER")
							&& (e.getIdEstado().equals("PC") || e.getIdEstado().equals("SATF")
									|| e.getIdEstado().equals("SFC") || e.getIdEstado().equals("VG")
									|| e.getIdEstado().equals("LQ") || e.getIdEstado().equals("CON")
									|| e.getIdEstado().equals("PAP") || e.getIdEstado().equals("ANU"))) {

						CantidadSolicitudResponse cantSol = new CantidadSolicitudResponse();
						cantSol.setEstado(e.getDescripcion());
						cantSol.setCantidad(
								solicitudRepository.consultaCantidadSolicitudes(identificacion, e.getIdEstado()));
						listCantSol.add(cantSol);

					} else if (tipoCliente.equals("GERENTEGENERAL") && (e.getIdEstado().equals("TN")
							|| e.getIdEstado().equals("VG") || e.getIdEstado().equals("LQ"))) {

						CantidadSolicitudResponse cantSol = new CantidadSolicitudResponse();
						cantSol.setEstado(e.getDescripcion());
						cantSol.setCantidad(
								solicitudRepository.consultaCantidadSolicitudes(identificacion, e.getIdEstado()));
						listCantSol.add(cantSol);
					}
				});
			} else {
				LOG.info("Servicio dashboard: No existen tipos de estados activos");
				listCantSol.clear();
			}
		} else {
			LOG.info("Servicio dashboard: Consultar=" + consultar);
			listCantSol.clear();
		}
		return listCantSol;
	}

	public String enviaEmailInversionFinalizada(Long numeroSolicitud) throws Exception {
		String respuesta;
//		SolicitudResponse solicitud = this.consultaSolicitud(  numeroSolicitud);
//		FilterEmpresa empresa = proyectoService.consultaEmpresa(solicitud.getCodigoProyecto());
		DatoSolicitud solPorConfirmar = new DatoSolicitud();
		solPorConfirmar = this.solicitudRepository.consultaDatoSolicitud(numeroSolicitud);

		respuesta = enviaEmailService.enviaEmailSolPorConfirmar("CN_INVERSIONISTA", solPorConfirmar,
				solPorConfirmar.getEmail());
		if (respuesta.contains("ok")) {
			// Se envian email a los analistas de operaciones
			List<PersonalDetalleResponse> listEmp = empleadoService.consultaEmpleadosPorRol((long) 6);
			if (!listEmp.isEmpty()) {
				for (PersonalDetalleResponse p : listEmp) {
					enviaEmailService.enviaEmailSolPorConfirmar("CN_ANALISTA", solPorConfirmar, p.getEmail());
				}
			}
		}
		// solicitud = null;
		return respuesta;
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<TransaccionesPorConciliar> consultaSolcitudesPorConfirmarAnalista(FilterIntSolicitudRequest filterAnalistaOpRequest) {
//		LocalDate fInicio = null;
//		LocalDate fFin = null;
//		/*
//		if(filterAnalistaOpRequest.getFecha() != null) {
//			fInicio = filterAnalistaOpRequest.getFecha();
//			fFin = filterAnalistaOpRequest.getFecha();
//		}else {
//			fInicio = LocalDate.now().minusMonths(1);//ajustar para produccion
//			fFin = LocalDate.now().plusDays(1);
//		}
//		*/
//		return solicitudRepository.consultaSolPorConfirmarAnalista(filterAnalistaOpRequest.getCodProyecto(),filterAnalistaOpRequest.getNumeroSolicitud(),filterAnalistaOpRequest.getIdTipoCalificacion(),filterAnalistaOpRequest.getIdActividad());
//	}

	@Override
	@Transactional
	public String updateAll(List<Solicitud> solicitudes, String nuevoEstado, Cuenta usuarioModificacion,
			String Observacion) throws Exception {
		List<Solicitud> solicitudesActualizadas = new ArrayList<>();
		if (solicitudes.isEmpty()) {
			throw new Exception("Error en la actualizacion de Solicitudes");
		}
		for (Solicitud ito : solicitudes) {
			ito.setEstadoActual(new TipoEstado(nuevoEstado));
			ito.setObservacion(Observacion);
			solicitudesActualizadas.add(ito);
		}
		this.solicitudRepository.saveAll(solicitudes);
		return "Solicitudes Actualizadas";
	}

	@Override
	@Transactional
	public String actualizaEstadoSolicitudes(List<Long> solicitud, TipoEstado estado, String usuario,
			String observacion) throws Exception {
		String response = "";
		String mensaje = "";
		if (solicitud.isEmpty()) {
			throw new Exception("La(s) solicitud(es) no Existe(n)");
		}
		for (Long s : solicitud) {
			Solicitud sol = new Solicitud();
			LOG.info("actualizaEstadoSolicitud");
			sol = solicitudRepository.consultaSolicitud(s);
			LOG.info("fin");
			if (sol != null) {
				HistorialDeSolicitud historialSol = new HistorialDeSolicitud();
				historialSol.setSolicitud(sol);
				historialSol.setTablaCambiar("Estado");
				historialSol.setValorActual("SATF");
				historialSol.setObservacion("Cambio de estado a aprobada por transferir fondos");
				historialSol.setValorAnterior(sol.getEstadoActual().getIdEstado());
				historialSol.setUsuarioModificacionInterno(new CuentaInterno(usuario));
				sol.setHistorial(historialSol);
				this.actualizaEstadoSolicitud(sol, estado, observacion);
			} else {
				response = response + ", " + s;
				LOG.error("No existe solicitud, numero solicitud: " + s);
			}
		}
		if (!response.equals("")) {
			throw new Exception("La(s) solicitud(es) pendientes son: " + response);
		}

		mensaje = "las solicitudes pendientes son: " + response;
		return "Solicitudes actualizadas correctamente";
	}

	public void actualizaEstadoSolicitud(Solicitud solicitud, TipoEstado nuevoEstado, String observacion) {

		// actualiza solicitud - caduca registro
		// solicitud.setFechaModificacion(LocalDateTime.now());
		// solicitud.setUsuarioModificacion(usuario);
//		solicitud.setFechaVigencia(LocalDateTime.now());

		solicitud.setEstadoActual(nuevoEstado);// nuevo estado
		if (observacion != null) {
			solicitud.setObservacion(observacion);
		} else {
			solicitud.setObservacion(solicitud.getObservacion());
		}
		solicitudRepository.save(solicitud);

		// HISTORIAL
//		Solicitud solNueva = new Solicitud();
//		
//		//actualiza solicitud - nuevo registro
//		solNueva.setNumeroSolicitud(solicitud.getNumeroSolicitud());
//		solNueva.setTipoPersona(solicitud.getTipoPersona());
//		solNueva.setUsuario(solicitud.getUsuario());
//		solNueva.setProyecto(solicitud.getProyecto());
//		solNueva.setTipoSolicitud(solicitud.getTipoSolicitud());
//		solNueva.setTipoContacto(solicitud.getTipoContacto());
//		solNueva.setUsuarioContacto(solicitud.getUsuarioContacto());
//		solNueva.setEstadoAnterior(solicitud.getEstadoActual());
//		solNueva.setEstadoActual(nuevoEstado);//nuevo estado
//		solNueva.setDatosInversion(solicitud.getDatosInversion());
//		solNueva.getInversionista().setPersona(solicitud.getInversionista().getPersona());
//		solNueva.setAceptaInformacionCorrecta(solicitud.getAceptaInformacionCorrecta());
//		solNueva.setAceptaIngresarInfoVigente(solicitud.getAceptaIngresarInfoVigente());
//		solNueva.setAceptaLicitudFondos(solicitud.getAceptaLicitudFondos());
//		solNueva.setFechaGeneracion( new Date());
//		solNueva.setUsuarioCreacion(solicitud.getUsuarioCreacion());
//		
//		solNueva.setUsuarioModificacion(usuario);//
//		solNueva.setFechaModificacion(LocalDateTime.now());//
//		
//		
//		solicitudRepository.save(solNueva);
//		LOG.info("Estado de Solicitud actualizada, numero solicitud: "+solicitud.getNumeroSolicitud());
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionEnTransitoGerenciaResponse> consultaSolicitudesEnTransitoGerencia(
			FilterProyectoRequest filterGerenciaRequest) {
		List<InversionEnTransitoGerenciaResponse> listResponse = new ArrayList<>();
		BigDecimal totalMontoProyecto;
		double porcAvance;
		List<InversionEnTransitoGerencia> listEnTransito = new ArrayList<>();
		listEnTransito = solicitudRepository.consultaSolicitudesEnTransitoPorProyecto(
				filterGerenciaRequest.getCodProyecto(), filterGerenciaRequest.getIdTipoCalificacion(),
				filterGerenciaRequest.getIdActividad(), "TN");
		InversionEnTransitoGerenciaResponse inversionResponse;
		if (!listEnTransito.isEmpty()) {
			for (InversionEnTransitoGerencia p : listEnTransito) {
				inversionResponse = new InversionEnTransitoGerenciaResponse();
				inversionResponse.setCodProyecto(p.getCodProyecto());
				inversionResponse.setNombreEmpresa(p.getNombreEmpresa());
				inversionResponse.setInversionSolicitada(df.format(p.getInversionSolicitada()));
				inversionResponse.setPlazo(p.getPlazo());
				inversionResponse.setEstado(p.getEstado());
				totalMontoProyecto = this.consultaMontoRecaudadoPorProyectoyEstado(p.getCodProyecto(), "TN");
				porcAvance = (totalMontoProyecto.doubleValue() * 100) / p.getInversionSolicitada().doubleValue();
				inversionResponse.setInversionRealizada(df.format(totalMontoProyecto));
				inversionResponse.setPorcentajeRecuadado(new BigDecimal(porcAvance).setScale(2, RoundingMode.HALF_UP));
				listResponse.add(inversionResponse);
			}

			Comparator<InversionEnTransitoGerenciaResponse> porcAvanceComparator = Comparator
					.comparing(InversionEnTransitoGerenciaResponse::getPorcentajeRecuadado);
			Collections.sort(listResponse, porcAvanceComparator.reversed());// descendente
			// Collections.sort(listResponse, porcAvanceComparator); //ascendente

		} else {
			listResponse.clear();
		}

		return listResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProyectoPorEstadoResponse> consultaSolicitudesAgrupadas(FilterProyectoRequest filterProyectoRequest,
			String campo, String valor) {
		List<ProyectoPorEstadoResponse> listResponse = new ArrayList<>();
		BigDecimal totalMontoProyecto;
		double porcAvance;
		List<InversionEnTransitoGerencia> listEnTransito = new ArrayList<>();
		listEnTransito = solicitudRepository.consultaSolicitudesEnTransitoPorProyecto(
				filterProyectoRequest.getCodProyecto(), filterProyectoRequest.getIdTipoCalificacion(),
				filterProyectoRequest.getIdActividad(), valor);
		ProyectoPorEstadoResponse proyectoResponse;
		if (!listEnTransito.isEmpty()) {
			for (InversionEnTransitoGerencia p : listEnTransito) {
				proyectoResponse = new ProyectoPorEstadoResponse();
				proyectoResponse.setCodProyecto(p.getCodProyecto());
				proyectoResponse.setNombreEmpresa(p.getNombreEmpresa());
				proyectoResponse.setInversionSolicitada(df.format(p.getInversionSolicitada()));
				proyectoResponse.setPlazo(p.getPlazo());
				proyectoResponse.setEstado(p.getEstado());
				totalMontoProyecto = this.consultaMontoRecaudadoPorProyectoyEstado(p.getCodProyecto(), valor);
				porcAvance = (totalMontoProyecto.doubleValue() * 100) / p.getInversionSolicitada().doubleValue();
				proyectoResponse.setInversionRealizada(df.format(totalMontoProyecto));
				proyectoResponse.setPorcentajeRecuadado(new BigDecimal(porcAvance).setScale(2, RoundingMode.HALF_UP));
				listResponse.add(proyectoResponse);
			}
		} else {
			listResponse.clear();
		}
		return listResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public InversionSolPersonaResponse consultaDatoSolPersonaPorProyectoYEstado(String codigoProyecto,
			String estadoActual) {
		List<SolicitudPersonaResponse> listSolPersResponse = new ArrayList<>();
		List<InversionSolPersona> listSolPersona = new ArrayList<>();
		SolicitudPersonaResponse solPers;
		InversionSolPersonaResponse solPersResponse = new InversionSolPersonaResponse();
		double totalInversion = 0;

		listSolPersona = this.consultaSolPersonaPorProyectoYEstado(codigoProyecto, estadoActual);
		if (!listSolPersona.isEmpty()) {
			for (InversionSolPersona s : listSolPersona) {
				solPers = new SolicitudPersonaResponse();
				solPers.setNumeroSolicitud(s.getNumeroSolicitud().toString());
				solPers.setMontoInversion(df.format(s.getMontoInversion()));
				solPers.setIdentificacion(s.getIdentificacion());
				totalInversion = totalInversion + s.getMontoInversion().doubleValue();

				if (s.getTipoPersona().contains("NAT")) {
					solPers.setInversionista(s.getNombres() + " " + s.getApellidos());
				} else {
					solPers.setInversionista(s.getRazonSocial());
				}
				listSolPersResponse.add(solPers);
			}
			solPersResponse.setTotalInversion(df.format(totalInversion));
			solPersResponse.setSolicitudes(listSolPersResponse);
		} else {
			solPersResponse = null;
			listSolPersResponse.clear();
		}
		return solPersResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionAprobTransFondoResponse> consultaSolicitudesAprobTransFondos(
			FilterProyectoRequest filterGerenciaRequest) {

		List<InversionAprobTransFondoResponse> listResponse = new ArrayList<>();
		BigDecimal totalMontoProyecto;

		List<InversionEnTransitoGerencia> listEnTransito = new ArrayList<>();
		listEnTransito = solicitudRepository.consultaSolPorEstadoGerencia(filterGerenciaRequest.getCodProyecto(),
				filterGerenciaRequest.getIdTipoCalificacion(), filterGerenciaRequest.getIdActividad(), "SATF");

		InversionAprobTransFondoResponse inversionResponse;

		if (!listEnTransito.isEmpty()) {
			for (InversionEnTransitoGerencia p : listEnTransito) {
				inversionResponse = new InversionAprobTransFondoResponse();
				inversionResponse.setCodProyecto(p.getCodProyecto());
				inversionResponse.setNombreEmpresa(p.getNombreEmpresa());
				inversionResponse.setMontoSolicitado(df.format(p.getInversionSolicitada()));
				inversionResponse.setPlazo(p.getPlazo());

				inversionResponse.setEstado(p.getEstado());
				totalMontoProyecto = this.consultaMontoRecaudadoPorProyectoyEstado(p.getCodProyecto(), "SATF");
				inversionResponse.setMontoRecuadado(df.format(totalMontoProyecto));

				listResponse.add(inversionResponse);
			}
		} else {
			listResponse.clear();
		}
		return listResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DatoProyecto> consultaProyectosEnTransito() {
		List<FilterEmpresa> listProyectos = new ArrayList<>();
		List<DatoProyecto> listAvanceProyectos = new ArrayList<>();
		DatoProyecto avanceProyecto;
		BigDecimal totalMontoInversion;
		double porcAvance = 0;

		try {
			listProyectos = proyectoService.consultaProyectosEnAvance();
			if (!listProyectos.isEmpty()) {
				for (FilterEmpresa p : listProyectos) {
					avanceProyecto = new DatoProyecto();

					// totalMontoInversion =
					// solicitudRepository.consultaMontoInvertidoPorProyectoInversionistas(p.getCodProyecto());
					// porcAvance = (totalMontoInversion.doubleValue() * 100)/
					// p.getMontoSolicitado().doubleValue();

					avanceProyecto.setNombreEmpresa(p.getNombreEmpresa());
					// avanceProyecto.setMontoRecaudado(df.format(totalMontoInversion));
					// avanceProyecto.setPorcentaje(new BigDecimal(porcAvance).setScale(2,
					// RoundingMode.HALF_UP));

					listAvanceProyectos.add(avanceProyecto);
				}
			} else {
				LOG.info("No hay proyectos en Transito");
				listAvanceProyectos.clear();
			}
		} catch (Exception e) {
			LOG.error("Error en consultaProyectosEnTransito " + e.getMessage());
			listAvanceProyectos.clear();
			return listAvanceProyectos;
		}
		return listAvanceProyectos;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existenSolicitudesPorProyectoyEstado(String codigoProyecto, String estadoActual) {
		if (solicitudRepository.exiteSolicitudesPorProyectoyEstado(codigoProyecto, estadoActual) > 0) {
			return true;
		}
		return false;
	}

	// Cambiar nombre de contenedor

	@Override
	@Transactional
	public String updatePYS(UpdatePYSRequest cambiar) throws Exception {
		String resCambioEstado = null;

		HistorialDeProyecto historyP = new HistorialDeProyecto();
		historyP.setProyecto(new Proyecto(cambiar.getCodigoProyecto()));
		historyP.setUsuarioCreacion(cambiar.getUsuario());
		historyP.setValorActual(cambiar.getStatusProyect());
		historyP.setObservacion(cambiar.getObservacionProyecto());
		historyP.setComprobanteRuta("sin ruta");
		Proyecto proyecto = new Proyecto();
		List<Long> listNumSolicitudes = new ArrayList<>();
		try {

			proyecto = proyectoService.consultaProyecto(cambiar.getCodigoProyecto());
			historyP.setValorAnterior(proyecto.getEstadoActual().getIdEstado());

			if (!cambiar.getSolicitudes().isEmpty()) {
				cambiar.getSolicitudes().forEach(s -> {
					listNumSolicitudes.add(s);
				});
				// proyectoService.updateEstadoProyecto(tipoEstadoP.getIdEstado(),
				// proyecto.getEstadoActual().getIdEstado(), cambiar.getUsuario(),
				// LocalDateTime.now(), proyecto.getIdProyecto());
				proyectoService.updateProyecto(historyP);
				resCambioEstado = this.actualizaEstadoSolicitudes(listNumSolicitudes,
						new TipoEstado(cambiar.getStatusSol()), cambiar.getUsuario(), null);
				if (resCambioEstado.contains("Error")) {
					LOG.error("Error al actualizar estado de las solicitudes Codigo proyecto: "
							+ cambiar.getCodigoProyecto());
				}

			}
		} catch (Exception e) {
			LOG.error("Error al cambiar estados " + e.getMessage());
			throw new Exception(e.getMessage());
		}
		return resCambioEstado;
	}

	@Override
	@Transactional
	public String updatePYS2(UpdatePYSRequest cambiar, MultipartFile file) throws Exception {
		String resCambioEstado = null;
		String ruta = null;
		TipoEstado tipoEstadoP = new TipoEstado();
		TipoEstado tipoEstadoS = new TipoEstado();
		HistorialDeProyecto historyP = new HistorialDeProyecto();
		historyP.setProyecto(new Proyecto(cambiar.getCodigoProyecto()));
		historyP.setUsuarioCreacion(cambiar.getUsuario());
		historyP.setValorActual(cambiar.getStatusProyect());
		historyP.setObservacion(cambiar.getObservacionProyecto());
		historyP.setComprobanteRuta(cambiar.getRutaComprobante());
		Proyecto proyecto = new Proyecto();
		List<Long> listNumSolicitudes = new ArrayList<>();
		try {
			if (!file.isEmpty()) {
				ParametroResponse paramResponse = new ParametroResponse();
				paramResponse = parametroService.findByParametroCodParametro("CONTDOCSOL");
				String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				String contentType = file.getContentType().toLowerCase();// application/pdf
				if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
					resCambioEstado = "Error archivo no valido, debe cargar un jpg o png";
					throw new Exception("Error archivo no valido, debe cargar un jpg o png");
				}
				String type = contentType.split("/")[1];
				String nombreArchivo = "datos_Pago" + timeStamp + "." + type;
				ruta = this.blobStorageService.uploadFile(file, nombreArchivo, paramResponse.getValor());
				historyP.setComprobanteRuta(ruta);
			}
		} catch (NullPointerException e) {
		}
		try {
			tipoEstadoP = tipoEstadoService.findById(cambiar.getStatusProyect());
			tipoEstadoS = tipoEstadoService.findById(cambiar.getStatusSol());

			proyecto = proyectoService.consultaProyecto(cambiar.getCodigoProyecto());
			historyP.setValorAnterior(proyecto.getEstadoActual().getIdEstado());
			if (historyP.getComprobanteRuta() == null) {
				historyP.setComprobanteRuta("sin ruta");
			}
			try {
				if (cambiar.getSolicitudes() == null) {
					List<Solicitud> ls = new ArrayList<Solicitud>();
					TipoEstado t = new TipoEstado();
					t.setIdEstado(cambiar.getSearchSol());
					ls = this.solicitudRepository.consultaSolicitudPorProyectoyEstado(cambiar.getCodigoProyecto(), t);
					ls.forEach(s -> {
						listNumSolicitudes.add(s.getNumeroSolicitud());
					});
					proyectoService.updateProyecto(historyP);
					resCambioEstado = this.actualizaEstadoSolicitudes(listNumSolicitudes, tipoEstadoS,
							cambiar.getUsuario(), null);
				}
			} catch (Exception e) {
				LOG.error("Null pointer Exception");
			}
			if (!cambiar.getSolicitudes().isEmpty()) {
				cambiar.getSolicitudes().forEach(s -> {
					listNumSolicitudes.add(s);
				});
				// proyectoService.updateEstadoProyecto(tipoEstadoP.getIdEstado(),
				// proyecto.getEstadoActual().getIdEstado(), cambiar.getUsuario(),
				// LocalDateTime.now(), proyecto.getIdProyecto());
				proyectoService.updateProyecto(historyP);
				resCambioEstado = this.actualizaEstadoSolicitudes(listNumSolicitudes, tipoEstadoS, cambiar.getUsuario(),
						null);

				if (resCambioEstado.contains("Error")) {
					LOG.error("Error al actualizar estado de las solicitudes Codigo proyecto: "
							+ cambiar.getCodigoProyecto());
				}
			}
		} catch (Exception e) {
			LOG.error("Error al cambiar estados " + e.getMessage());
			resCambioEstado = "Error al cambiar estados ";
		}
		return "actualizado";
	}

	@Override
	@Transactional
	public String registraPagoVigente(Long numSolicitud, int cuota, LocalDate fechaRealizada,
			String usuarioModificacion, MultipartFile file) throws Exception {
		String respuesta = "", ruta = "";
		DetalleAmortizacion dt = new DetalleAmortizacion();
		LOG.warn("entro restistra pago");
		dt = this.detalleAmortizacionRepository.consultaDetalleTblAmortizacionxnumSolicitud(numSolicitud, cuota);
		LOG.warn(" La cuota " + dt.getEstadoPago());
		if (dt.getEstadoPago().equals("Al dia")) {
			throw new Exception("La cuota ya tiene documentos ingresados");
		}
		LOG.warn(" La cuota " + dt.getRutaPago() + numSolicitud);
		Solicitud sol = this.solicitudRepository.consultaSolicitud(numSolicitud);
		if (sol == null) {
			throw new Exception("No existe solicitud " + numSolicitud);
		}
		LOG.warn(" " + sol.getProyecto().getEmpresa().getNombre() + " mail: " + sol.getInversionista().getEmail());
		String email = sol.getInversionista().getEmail();
		String empresa = sol.getProyecto().getEmpresa().getNombre();

		if (dt != null) {
			LOG.warn("dt");

			if (file.isEmpty()) {
				throw new Exception("Debe subir un archivo valido");
			}
			ParametroResponse paramResponse = new ParametroResponse();
			paramResponse = parametroService.findByParametroCodParametro("CONTPAGCUO");
			String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			String contentType = file.getContentType().toLowerCase();// application/pdf
			if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
				throw new Exception("Error archivo no valido, debe cargar un jpg");
			}
			String type = contentType.split("/")[1];
			String nombreArchivo = "Pago_inversionista" + " - cuota: " + dt.getCuota() + " - " + timeStamp + "." + type;
			ruta = this.blobStorageService.uploadFile(file, nombreArchivo, paramResponse.getValor());
			dt.setRutaPago(ruta);
			dt.setEstadoPago("Al dia");
			dt.setFechaRealizada(fechaRealizada);
			dt.setFechaRegistro(LocalDate.now());

			if (sol.getAmortizacion().getPlazo() == cuota) {
				HistorialDeSolicitud historial = new HistorialDeSolicitud();
				historial.setSolicitud(sol);
				historial.setTablaCambiar("Estados");
				historial.setValorAnterior(sol.getEstadoActual().getIdEstado());
				historial.setValorActual("LQ");
				historial.setObservacion("Solicitud Liquidada");
				historial.setUsuarioModificacionInterno(new CuentaInterno(usuarioModificacion));
				// mandar correo
				sol.setHistorial(historial);
				sol.setEstadoActual(new TipoEstado("LQ"));
				this.solicitudRepository.save(sol);
			}
			detalleAmortizacionRepository.save(dt);
			this.enviaEmailService.enviaEmailPagoCuotas("PAGO_INVERSIONISTAS", empresa,
					sol.getProyecto().getIdProyecto(), numSolicitud.toString(), dt, email);
			respuesta = "ok";
		}

		return respuesta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionSolPersona> consultaSolPersonaPorProyectoYEstado(String codigoProyecto, String estadoActual) {
		return solicitudRepository.consultaSolPersonaPorProyectoYEstado(codigoProyecto, estadoActual);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal consultaMontoRecaudadoPorProyectoyEstado(String codigoProyecto, String estadoActual) {
		return solicitudRepository.consultaMontosInvertidosPorProyecto(codigoProyecto, estadoActual);

	}

	@Override
	@Transactional(readOnly = true)
	public List<SolicitudVigenteResponse> consultaVigente(FilterProyectoRequest filterRequest) {
		List<SolicitudVigenteResponse> ls = solicitudRepository
				.consultaSolicitudVigente(filterRequest.getCodProyecto());
		List<SolicitudVigenteResponse> ls2 = solicitudRepository
				.consultaSolicitudVigenteVencidosyMora(filterRequest.getCodProyecto());
		try {
			for (SolicitudVigenteResponse s : ls2) {
				ls.add(s);
			}
			ls.sort((elemento1, elemento2) -> elemento1.getCuota() - elemento2.getCuota());
			;
		} catch (Exception e) {

		}

		return ls;
	}

	/*
	 * @Override
	 * 
	 * @Transactional public void enviaEmailSolicitudesPorEstado(String
	 * estadoActual) { List<String> solicitudes = new ArrayList<>(); solicitudes =
	 * solicitudRepository.consultaSolicitudesVigentesPorEstado(estadoActual);
	 * if(!solicitudes.isEmpty()) { SolicitudResponse solicitud = new
	 * SolicitudResponse(); String identificacionAux = null; for(String numeroSol :
	 * solicitudes) { solicitud = this.consultaSolicitud(numeroSol); FilterEmpresa
	 * empresa = proyectoService.consultaEmpresa(solicitud.getCodigoProyecto());
	 * 
	 * if(identificacionAux == null) { DatoSolicitud datoSol = new DatoSolicitud();
	 * datoSol.setIdentificacion(solicitud.getPersona().getIdentificacion());
	 * datoSol.setInversionista(solicitud.getPersona().getNombres()+" "+solicitud.
	 * getPersona().getApellidos());
	 * datoSol.setNumeroSolicitud(solicitud.getNumeroSolicitud());
	 * datoSol.setEmpresa(empresa.getNombreEmpresa());
	 * datoSol.setCodigoProyecto(empresa.getCodProyecto());
	 * datoSol.setMontoInversion(solicitud.getDatosAmortizacion().getMontoInversion(
	 * )); datoSol.setPlazo(solicitud.getDatosAmortizacion().getPlazo());
	 * datoSol.setRentabilidad(solicitud.getDatosAmortizacion().getRendimientoNeto()
	 * ); //&&
	 * identificacionAux.contains(solicitud.getPersona().getIdentificacion())) { //}
	 * 
	 * }else {
	 * 
	 * } identificacionAux = solicitud.getPersona().getIdentificacion();
	 * 
	 * DatoSolicitud datoSol = new DatoSolicitud();
	 * 
	 * "<td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'>"
	 * .concat(solicitud.getNumeroSolicitud())
	 * .concat("</td><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'>"
	 * ) .concat(empresa.getNombreEmpresa())
	 * .concat("</td><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'>"
	 * ) .concat(empresa.getCodProyecto())
	 * .concat("</td><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'>"
	 * ) .concat(solicitud.getDatosAmortizacion().getMontoInversion().toString())
	 * .concat("</td><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'>"
	 * ) .concat(String.valueOf(solicitud.getDatosAmortizacion().getPlazo()))
	 * .concat("</td><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'><td style='text-align: center; border: 1px solid #004889; font-size:12px;font-family: Georgia'>"
	 * ) .concat(solicitud.getDatosAmortizacion().getRendimientoNeto().toString()).
	 * concat("</td>");
	 * 
	 * 
	 * datoSol.setIdentificacion(solicitud.getPersona().getIdentificacion());
	 * datoSol.setInversionista(solicitud.getPersona().getNombres()+" "+solicitud.
	 * getPersona().getApellidos());
	 * datoSol.setNumeroSolicitud(solicitud.getNumeroSolicitud());
	 * datoSol.setEmpresa(empresa.getNombreEmpresa());
	 * datoSol.setCodigoProyecto(empresa.getCodProyecto());
	 * datoSol.setMontoInversion(solicitud.getDatosAmortizacion().getMontoInversion(
	 * )); datoSol.setPlazo(solicitud.getDatosAmortizacion().getPlazo());
	 * datoSol.setRentabilidad(solicitud.getDatosAmortizacion().getRendimientoNeto()
	 * ); //datoSol.setEmail();
	 * 
	 * LOG.info(solicitud.getNumeroSolicitud().concat("-"+empresa.getNombreEmpresa()
	 * ).concat("-"+empresa.getCodProyecto()).concat("-"+solicitud.
	 * getDatosAmortizacion().getMontoInversion().toString())
	 * .concat("-"+String.valueOf(solicitud.getDatosAmortizacion().getPlazo()))
	 * .concat("-"+solicitud.getDatosAmortizacion().getRendimientoNeto().toString())
	 * );
	 * 
	 * };
	 * 
	 * } solicitudes.clear(); }
	 */

}
