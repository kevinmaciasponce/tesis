package com.multiplos.cuentas.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiplos.cuentas.controllers.PromotorController;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.DocumentosFinancierosPromotor;
import com.multiplos.cuentas.models.DocumentosJuridicosPromotor;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.EmpresaDatosAnuales;
import com.multiplos.cuentas.models.Factura;
import com.multiplos.cuentas.models.Pais;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.TipoActividad;
import com.multiplos.cuentas.pojo.documento.MultiplesDocumentosRequest;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.promotor.DocumentosFinancierosResponse;
import com.multiplos.cuentas.pojo.promotor.DocumentosJuridicosResponse;
import com.multiplos.cuentas.pojo.promotor.EmpresaFormulario;
import com.multiplos.cuentas.pojo.promotor.EmpresaRequest;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;
import com.multiplos.cuentas.pojo.promotor.PromotorResponse;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.repository.EmpresaRepository;
import com.multiplos.cuentas.repository.FacturaRepository;
import com.multiplos.cuentas.services.BlobStorageService;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.EmpresaService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.ProyectoService;

@Service
public class EmpresaServiceImpl implements EmpresaService {

	public static final Logger LOG = LoggerFactory.getLogger(EmpresaServiceImpl.class);
	private EmpresaRepository empresaRepository;
	private CuentaService cuentaService;
	private BlobStorageService blobStorageService;
	private ParametroService parametroService;
	private EnvioEmailService enviaEmailService;
	private ProyectoService proyectoservice;
	private FacturaRepository factRepository;
	@Autowired
	public EmpresaServiceImpl(EmpresaRepository empresaRepository,
			CuentaService cuentaService,
			BlobStorageService blobStorageService,
			ParametroService parametroService,
			EnvioEmailService enviaEmailService,
			@Lazy ProyectoService proyectoservice,
			FacturaRepository factRepository) {
		this.empresaRepository = empresaRepository;
		this.cuentaService = cuentaService;
		this.blobStorageService=blobStorageService;
		this.parametroService=parametroService;
		this.enviaEmailService=enviaEmailService;
		this.proyectoservice=proyectoservice;
		this.factRepository=factRepository;
	}
	@Override
	public Empresa findById(Long id) throws Exception {
		Optional<Empresa> empresa = this.empresaRepository.findById(id);
		if (empresa.isEmpty()) {
			throw new CuentaException("No existe empresa con id: " + id);
		}
		return empresa.get();
	}

	public DocumentosJuridicosPromotor getDocJur(Long idEmpresa, String cuenta) {
		DocumentosJuridicosPromotor doc;
		doc = this.empresaRepository.getDocJur(idEmpresa, cuenta);
		return doc;
	}
	public DocumentosFinancierosPromotor getDocFin(Long idEmpresa, String cuenta) {
		DocumentosFinancierosPromotor doc;
		doc = this.empresaRepository.getDocFin(idEmpresa, cuenta);
		return doc;
	}

	@Override
	@Transactional()
	public List<Empresa> consultaEmpresas() {
		List<Empresa> empresas = new ArrayList<>();
		empresas = empresaRepository.findAll().stream().filter(es -> es.getEstado().contains("A"))
				.collect(Collectors.toList());
		return empresas;
	}

	@Override
	public EmpresaResponse getByfilter(EmpresaRequest filter) throws Exception {
		EmpresaResponse response = this.empresaRepository.getByFilter(filter.getId(), filter.getCuenta());

		return response;
	}

	@Override
	public Object putEmpresa(EmpresaFormulario form) throws Exception {

		Empresa empresa;
		if (form.getId() == null) {
			empresa = new Empresa();
			Cuenta cuenta = this.cuentaService.findByCuentaIdCuenta(form.getCuenta());
			empresa.setCuenta(cuenta);
			empresa.setRuc(cuenta.getPersona().getIdentificacion());
		} else {
			empresa = this.findById(form.getId());
		}
		empresa.getCuenta().getPersona().setAnioInicioActividad(form.getAnioInicioActividad());
		empresa.setDireccion(form.getDireccion());
		empresa.setActividad(TipoActividad.builder().idActividad(form.getActividad()).build());
		empresa.setPais(Pais.builder().idNacionalidad(form.getPais()).build());
		empresa.setNombre(form.getNombre());
		empresa.setAntecedente(form.getAntecedente());
		empresa.setVentajaCompetitiva(form.getVentajaCompetitiva());
//		empresa.setEstado(form.getEstado());
//		empresa.setMargenContribucion(form.getMargenContribucion());
		empresa.setUsuarioCreacion(form.getUserCompose());
		empresa.setDescripcionProducto(form.getDescripcionProducto());
		empresa.setCiudad(form.getCiudad());
		
		if(form.getMargenContribucion()!=null && form.getVentasTotales()!=null && form.getAnio()!=null ) {
			EmpresaDatosAnuales datoAnual=empresa.getDatosAnualActual();
		if(datoAnual!=null) {
			empresa.getDatosAnualActual().setActivo(false);
		}
		empresa.setDatosAnualActual(EmpresaDatosAnuales.builder().empresa(empresa).anio(form.getAnio()).margenContribucion(form.getMargenContribucion()).ventasTotales(form.getVentasTotales()).usuarioCreacion(form.getUserCompose()).build());
		}
		this.empresaRepository.save(empresa);

		return "Empresa guardada correctamente";
	}

	@Override
	public PromotorResponse getPromotor(String id) throws Exception {
		PromotorResponse promotor;
		promotor = this.empresaRepository.getPromotor(id);

		return promotor;
	}
	@Override
	public PromotorResponse findPromotor(String id) throws Exception {
		PromotorResponse promotor;
		promotor = this.empresaRepository.findPromotor(id);

		return promotor;
	}
	@Override
	public Object CargarDocumentosJuridicos(Long idEmpresa, List<MultipartFile> file, String userCompose)throws Exception {
		Empresa empresa = this.findById(idEmpresa);
		DocumentosJuridicosPromotor documentosJuridicos= new DocumentosJuridicosPromotor();
		DocumentosJuridicosPromotor viejos;
		viejos = this.getDocJur(idEmpresa, userCompose);
		if (viejos != null) {
			
			viejos.setActivo(false);
			documentosJuridicos = viejos.clone();
			documentosJuridicos.setId(null) ;
		}else {
			documentosJuridicos.setEmpresa(empresa);
		
			for (MultipartFile dat : file) {
				if (dat==null) {
					throw new CuentaException("Error archivo no valido, debe cargar todos los archivos");
				}
			}
		}
		documentosJuridicos.setUserCreacion(userCompose);
		
		ParametroResponse paramResponse = new ParametroResponse();
		paramResponse =  parametroService.findByParametroCodParametro("CONTENEDORPROMOTOR");
	
		String nombreArchivo = null;
		int i = 0;
		for (MultipartFile dat : file) {

			try {
				String contentType=null;
				String timeStamp=null;
				String type=null;
				if(dat!=null) {
					LOG.warn("PESO "+dat.getSize());
					LOG.warn("ITERAMOS FILES");
					
					LOG.warn(" files lleno");
					 contentType = dat.getContentType().toLowerCase();// application/pdf
					  timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss"));
						
						if (!contentType.equals("application/pdf")) {
							throw new CuentaException("Error archivo no valido, debe cargar un pdf");
						}
						 type = contentType.split("/")[1];
				}
				switch (i) {
			
				case 0:
					if(dat!=null) {
					
//						if( dat.getSize()> 3000000  ) {
//							throw new CuentaException("El archivo de escrituras pesa mas de 3MB");
//						}
						LOG.info("subimos escritura");
						nombreArchivo = userCompose + "@escritura"
								+ timeStamp + "." + type;
						documentosJuridicos.setEscritura(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				case 1:
					if(dat!=null) {
//						if( dat.getSize()> 3000000) {
//							throw new CuentaException("El archivo estatutosVigentes  pesa mas de 3MB");
//						}
						LOG.warn("subimos estatutos");
						nombreArchivo = userCompose + "@estatutosVigentes"
								+ timeStamp + "." + type;
						documentosJuridicos.setEstatutosVigentes(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				case 2:
					if(dat!=null) {
//						if( dat.getSize()> 3000000) {
//							throw new CuentaException("El archivo ruc Vigente  pesa mas de 3MB");
//						}
						LOG.warn("subimos ruc");
						nombreArchivo = userCompose + "@rucVigente"
								+ timeStamp + "." + type;
						documentosJuridicos.setRucVigente(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				case 3:
					if(dat!=null) {
//						if( dat.getSize()> 3000000) {
//							throw new CuentaException("El archivo nombramiento representante  pesa mas de 3MB");
//						}
						LOG.warn("subimos nombramiento");
						nombreArchivo = userCompose + "@nombramientoRl"
								+ timeStamp + "." + type;
						documentosJuridicos.setNombramientoRl(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				case 4:
					if(dat!=null) {
//						if( dat.getSize()> 3000000) {
//							throw new CuentaException("El archivo de cedula representante pesa mas de 3MB");
//						}
						LOG.warn("subimos cedula");
						nombreArchivo = userCompose + "@cedulaRl"
								+ timeStamp + "." + type;
						documentosJuridicos.setCedulaRl(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				case 5:
					if(dat!=null) {
//						if( dat.getSize()> 3000000) {
//							throw new CuentaException("El archivo de nomina accionista pesa mas de 3MB");
//						}
						LOG.warn("subimos nomina");
						nombreArchivo = userCompose + "@nominaAccionista"
								+ timeStamp + "." + type;
						documentosJuridicos.setNominaAccionista(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				case 6:
					if(dat!=null) {
//						if( dat.getSize()> 3000000) {
//							throw new CuentaException("El archivo de identificaciones accionista pesa mas de 3MB");
//						}
						LOG.warn("subimos identificaciones");
						nombreArchivo = userCompose + "@identificacionesAccionista"
								+ timeStamp + "." + type;
						documentosJuridicos.setIdentificacionesAccionista(this.blobStorageService.uploadFile(dat, nombreArchivo, paramResponse.getValor()));
					}
					break;
				}
				i++;
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		}
		empresa.getDocumentosJuridicos().add(documentosJuridicos);
		this.empresaRepository.save(empresa);
	return"Documentos subidos correctamente";
}
	@Override
	public Object CargarDocumentosFinancieros(Long idEmpresa, List<MultipartFile> file, String userCompose)throws Exception {
		Empresa empresa = this.findById(idEmpresa);
		DocumentosFinancierosPromotor documentosFinancieros= new DocumentosFinancierosPromotor();
		DocumentosFinancierosPromotor viejos;
		viejos = this.getDocFin(idEmpresa, userCompose);
		if (viejos != null) {
			viejos.setActivo(false);
			documentosFinancieros = viejos.clone();
			documentosFinancieros.setId(null) ;
		}else {
			documentosFinancieros.setEmpresa(empresa);
		
			for (MultipartFile dat : file) {
				if (dat==null) {
					throw new CuentaException("Error archivo no valido, debe cargar todos los archivos");
				}
			}
			//enviar mail
		}
		documentosFinancieros.setUserCreacion(userCompose);
		ParametroResponse paramResponse = new ParametroResponse();
		paramResponse =  parametroService.findByParametroCodParametro("CONTENEDORPROMOTOR");
		LOG.info(userCompose);
		String container=paramResponse.getValor();
		String nombreArchivo = "";
		int i = 0;
		for (MultipartFile dat : file) {
			LOG.warn("iteramos files");
			try {
				String contentType=null;
				String timeStamp=null;
				String type=null;
				if(dat!=null) {
					LOG.warn(" files lleno");
					 contentType = dat.getContentType().toLowerCase();// application/pdf
					  timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss"));
						
						if (!contentType.equals("application/pdf")) {
							throw new CuentaException("Error archivo no valido, debe cargar un pdf");
						}
						 type = contentType.split("/")[1];
				}
				switch (i) {
				case 0:
					if(dat!=null) {
						LOG.warn("subimos impuestoRentaAnioAnterior");
						nombreArchivo = userCompose + "@impuestoRentaAnioAnterior"
								+ timeStamp + "." + type;
						documentosFinancieros.setImpuestoRentaAnioAnterior(this.blobStorageService.uploadFile(dat, nombreArchivo, container));
					}
					break;
				case 1:
					if(dat!=null) {
						LOG.warn("subimos estadoFinancieroAnioAnterior");
						nombreArchivo = userCompose + "@estadoFinancieroAnioAnterior"
								+ timeStamp + "." + type;
						documentosFinancieros.setEstadoFinancieroAnioAnterior(this.blobStorageService.uploadFile(dat, nombreArchivo, container));
					}
					break;
				case 2:
					if(dat!=null) {
						LOG.warn("subimos estadoFinancieroActuales");
						nombreArchivo = userCompose + "@estadoFinancieroActuales"
								+ timeStamp + "." + type;
						documentosFinancieros.setEstadoFinancieroActuales(this.blobStorageService.uploadFile(dat, nombreArchivo, container));
					}
					break;
				case 3:
					if(dat!=null) {
						LOG.warn("subimos anexoCtsCobrar");
						nombreArchivo = userCompose + "@anexoCtsCobrar"
								+ timeStamp + "." + type;
						documentosFinancieros.setAnexoCtsCobrar(this.blobStorageService.uploadFile(dat, nombreArchivo, container));
					}
					break;
				
				}
				i++;
			} catch (Exception e) {
				throw new CuentaException(e.getMessage());
			}
		}
		empresa.getDocumentosFinancieros().add(documentosFinancieros);
		this.empresaRepository.save(empresa);
	return"Documentos subidos correctamente";
}
	@Override
	public Object enviarMailPagoFactura1(Proyecto proyecto,String codFact,String numDoc,String idPlantilla) throws Exception {
		this.enviaEmailService.enviaEmailPromotor_registroProyecto(proyecto,idPlantilla);
		return "Factura generada, se a enviado un email a su cuenta, con los datos del proyecto";
	}
	
	
	@Override
	public Object consultarFacturaPorCliente(String cliente)throws Exception {
		List<Factura> factura;
		factura = this.factRepository.findByIdCliente(cliente);
		return factura;
	}
	@Override
	@Transactional
	public Object getDocumentosJurResponse(Long idEmpresa, String cuenta)throws Exception {
		DocumentosJuridicosResponse document;
		document = this.empresaRepository.getDocJurRes(idEmpresa, cuenta);
		return document;
	}
	
	@Override
	@Transactional
	public Object getDocumentosFinResponse(Long idEmpresa, String cuenta)throws Exception {
		DocumentosFinancierosResponse document;
		document = this.empresaRepository.getDocFinRes(idEmpresa, cuenta);
		return document;
	}


	
	
	
	
	
	
}
