package com.multiplos.cuentas.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.BancariaResponse;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.PersonaDocumento;
import com.multiplos.cuentas.models.TipoDocumento;
import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;
import com.multiplos.cuentas.pojo.formulario.FormDomicilioResponse;
import com.multiplos.cuentas.pojo.formulario.FormRepresentanteLegalResponse;
import com.multiplos.cuentas.pojo.formulario.PaisResponse;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersInfoAdicionalResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.repository.PersonaRepository;
import com.multiplos.cuentas.services.BlobStorageService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.PersonaService;

@Service
public class PersonaServiceImpl implements PersonaService {

	private static final Logger LOG = LoggerFactory.getLogger(PersonaServiceImpl.class);
	
	private PersonaRepository repository;
	private ParametroService parametroService;
	private BlobStorageService blobStorageService;;
	
	@Autowired
    public PersonaServiceImpl(PersonaRepository repository
    		,ParametroService parametroService,
    		BlobStorageService blobStorageService) {
        this.repository = repository;
        this.parametroService= parametroService;
        this.blobStorageService= blobStorageService;
    }
	
	@Override
	@Transactional(readOnly = true)
	public Persona consultaPersonas(String idCuenta) {
		return repository.consultaPersonas( idCuenta) ;
	}
	@Override
	@Transactional(readOnly = true)
	public Persona findById(String identificacion) {
		
		Persona persona=null;
		try {
		persona=repository.findById( identificacion) ;
		}catch(EntityNotFoundException e) {
			persona=null;
		}
		return persona;
	}

	@Override
	@Transactional
	public String guardaPersona(Persona persona) {
		String response = null;
		try {
			repository.save(persona);
			response = "ok";
		}catch(Exception e) {
			LOG.error("save: Problema al crear la cuenta "+e.getMessage());
			response = "Problema al guardar datos Persona";
		}
		return response;
	}
	
//	public String guardaIdentificacion(String identificacion,String idRepre, MultipartFile file, MultipartFile fileposterior)throws Exception {
//		String nombreArchivofront;
//		String nombreArchivopost;
//		String rutaArchivo;
//		String rutaArchivopost;
//		String contentType = file.getContentType().toLowerCase();// application/pdf
//		String contentType2 = fileposterior.getContentType().toLowerCase();// application/pdf
//		LOG.error("Error archino no valido, debe cargar un jpg o png2");
//		if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
//				&& !contentType2.equals("image/jpeg") && !contentType2.equals("image/png")) {
//			throw new Exception("Error archino no valido, debe cargar un jpg o png");
//		}
//		ParametroResponse paramResponse = new ParametroResponse();
//		paramResponse = parametroService.findByParametroCodParametro("CONTIDENT");
//		String type = contentType2.split("/")[1];// saca extension del archivo
//		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//		String type2 = contentType2.split("/")[1];// saca extension del archivo
//		nombreArchivofront = identificacion + "frontal_" + timeStamp + "." + type;
//		// numeroSolicitud = solDocIdentificacion.getNumeroSolicitud();
//		nombreArchivopost = identificacion + "posterior_" + timeStamp + "." + type;
//		rutaArchivo = blobStorageService.uploadFile(file, nombreArchivofront, paramResponse.getValor());
//		rutaArchivopost = blobStorageService.uploadFile(fileposterior, nombreArchivopost,paramResponse.getValor());
//		PersonaDocumento documento = new PersonaDocumento();
//		documento.setNombre(nombreArchivofront);
//		documento.setRuta(rutaArchivo);
//		documento.setFechaCreacion(LocalDateTime.now());
//		documento.setUsuarioCreacion(idRepre);
//		documento.setRutapost(rutaArchivopost);
//		documento.setNombrepost(nombreArchivopost);
//		documento.setPersonaInfoAdicional(idRepre);
//		documento.setDocumento(TipoDocumento.builder().idTipoDocumento((long)1).build());// 1=tipo de documento cedula
//		return "";
//	}
	
	@Override
	@Transactional
	public DocIdentificacionResponse consultaDocIdentificacion(String identificacion) {
		DocIdentificacionResponse dc = null;
		dc=	repository.consultaDocIdentificacion(identificacion);
		return dc;
	}
	
	@Override
	@Transactional
	public List<?> consultaFilterPersona(){
		List<?> filterPersona= new ArrayList<>();
		filterPersona= repository.consultaFilterPersona();
		return filterPersona;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public PersonaResponse consultaDatosPersona(String identificacion)throws Exception {
		Persona persona = new Persona();
		PersonaResponse persResponse = null;
		persona = repository.findById(identificacion);
		if(persona == null) {throw new Exception("No existe persona con ced "+identificacion); }
			persResponse = new PersonaResponse();
			persResponse.setIdentificacion(persona.getIdentificacion());
			persResponse.setTipoCliente(persona.getTipoCliente());
			persResponse.setTipoPersona(persona.getTipoPersona());
			persResponse.setTipoIdentificacion(persona.getTipoIdentificacion());
			persResponse.setNacionalidad(persona.getNacionalidad());
			persResponse.setNombres(persona.getNombres());
			persResponse.setApellidos(persona.getApellidos());
			persResponse.setFechaNacimiento(persona.getFechaNacimiento());
			persResponse.setNumeroCelular(persona.getNumeroCelular());
			persResponse.setRazonSocial(persona.getRazonSocial());
			persResponse.setNombreRepresentante(persona.getNombreContacto());
			persResponse.setCargoRepresentante(persona.getCargoContacto());
			persResponse.setEmailRepresentante(persona.getEmailContacto());
			persResponse.setAnioInicioActividad(persona.getAnioInicioActividad());
			persResponse.setEmail(persona.getCuenta().getEmail());
			persResponse.setUsuario(persona.getCuenta().getUsuario());
			persResponse.setTipoContacto(persona.getCuenta().getTipoContacto());
			persResponse.setUsuarioContacto(persona.getCuenta().getUsuarioContacto());
			
			
			if(persona.getPersInfoAdicional() != null && persona.getPersInfoAdicional().getEstado().contains("A")) {
				PersInfoAdicionalResponse infoAdicional = new PersInfoAdicionalResponse();
				infoAdicional.setEstadoCivil(persona.getPersInfoAdicional().getEstadoCivil());
				infoAdicional.setSexo(persona.getPersInfoAdicional().getSexo());
				infoAdicional.setNumeroTelefono(persona.getPersInfoAdicional().getNumeroTelefono());
				infoAdicional.setFuenteIngresos(persona.getPersInfoAdicional().getFuenteIngresos());
				infoAdicional.setCargoPersona(persona.getPersInfoAdicional().getCargoPersona());
				infoAdicional.setResidenteDomicilioFiscal(persona.getPersInfoAdicional().getResidenteDomicilioFiscal());
				infoAdicional.setPaisDomicilioFiscal(persona.getPersInfoAdicional().getPaisDomicilioFiscal());
				FormDomicilioResponse domicilioResponse = new FormDomicilioResponse();
				PaisResponse pais = new PaisResponse();
				pais.setId(persona.getPersInfoAdicional().getPersDomicilio().getPais().getIdNacionalidad());
				pais.setPais(persona.getPersInfoAdicional().getPersDomicilio().getPais().getPais());
				domicilioResponse.setPais(pais);
				CiudadResponse ciudadRes=new CiudadResponse();
				ciudadRes.setId(persona.getPersInfoAdicional().getPersDomicilio().getCiudad().getIdCiudad());
				ciudadRes.setCiudad(persona.getPersInfoAdicional().getPersDomicilio().getCiudad().getCiudad());
				domicilioResponse.setCiudad(ciudadRes);
				domicilioResponse.setDireccion(persona.getPersInfoAdicional().getPersDomicilio().getDireccion());
				domicilioResponse.setNumeroDomicilio(persona.getPersInfoAdicional().getPersDomicilio().getNumeroDomicilio());
				domicilioResponse.setSector(persona.getPersInfoAdicional().getPersDomicilio().getSector());
				infoAdicional.setPersonaDomicilio(domicilioResponse);
				BancariaResponse bancaria = BancariaResponse.builder()
						.banco(persona.getPersInfoAdicional().getPersTipoCuenta().getBanco().getNombre())
						.numeroCuenta(persona.getPersInfoAdicional().getPersTipoCuenta().getNumeroCuenta())
						.tipoCuenta(persona.getPersInfoAdicional().getPersTipoCuenta().getTipoCuenta())
						.titular(persona.getPersInfoAdicional().getPersTipoCuenta().getTitular())
						.build();
						
				
				infoAdicional.setBancaria(bancaria);
				
				if(persona.getPersInfoAdicional().getPersonaDocumentos()!=null) {
					DocIdentificacionResponse docResponse = new DocIdentificacionResponse();
					PersonaDocumento d = new PersonaDocumento();
					 d = persona.getPersInfoAdicional().getPersonaDocumentos();
					 if(d!=null) {
						docResponse.setNombre(d.getDocumento().getDocumento());
						docResponse.setRuta(d.getRuta());
						docResponse.setNombre_post(d.getNombrepost());
						docResponse.setRuta_post(d.getRutapost());
					 }
					infoAdicional.setPersonaDocumento(docResponse);
				}
				
				if(persona.getTipoPersona().contains("JUR")) {
					infoAdicional.setPersonaEstadoFinanciero(persona.getPersInfoAdicional().getPersEstadoFinanciero());
					
					FormRepresentanteLegalResponse repreLegal = new FormRepresentanteLegalResponse();
					repreLegal.setNombres(persona.getPersInfoAdicional().getPersRepreLegal().getNombres());
					repreLegal.setApellidos(persona.getPersInfoAdicional().getPersRepreLegal().getApellidos());
					repreLegal.setIdentificacion(persona.getPersInfoAdicional().getPersRepreLegal().getIdentificacion());
					repreLegal.setEmail(persona.getPersInfoAdicional().getPersRepreLegal().getEmail());
					repreLegal.setNumeroCelular(persona.getPersInfoAdicional().getPersRepreLegal().getNumeroCelular());
					repreLegal.setCargo(persona.getPersInfoAdicional().getPersRepreLegal().getCargo());
					pais.setId(persona.getPersInfoAdicional().getPersRepreLegal().getPais().getIdNacionalidad());
					pais.setPais(persona.getPersInfoAdicional().getPersRepreLegal().getPais().getPais());
					repreLegal.setPais(pais);
					repreLegal.setTelefono(persona.getPersInfoAdicional().getPersRepreLegal().getTelefono());
					repreLegal.setDireccionDomicilio(persona.getPersInfoAdicional().getPersRepreLegal().getDireccionDomicilio());
					repreLegal.setFechaInicioActividad(persona.getPersInfoAdicional().getPersRepreLegal().getFechaInicioActividad());
					repreLegal.setNumeroDomicilio(persona.getPersInfoAdicional().getPersRepreLegal().getNumeroDomicilio());
					infoAdicional.setPersonaRepresentanteLegal(repreLegal);
					
					infoAdicional.setPersonaFirmaAutorizada(persona.getPersInfoAdicional().getPersFirma());
				}
				persResponse.setDatosFormulario(infoAdicional);	
			}		
		return persResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existePersona(String identificacion) {
		if(repository.existePersona(identificacion) > 0) {
			return true;
		}
		return false;
	}
	
}
