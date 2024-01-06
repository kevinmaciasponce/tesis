

package com.multiplos.cuentas.auspicios.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
//import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multiplos.cuentas.auspicios.controllers.AuspiciosController;
import com.multiplos.cuentas.auspicios.models.Auspicio;
import com.multiplos.cuentas.auspicios.models.AuspicioEstados;
import com.multiplos.cuentas.auspicios.models.AuspicioRecompensa;
import com.multiplos.cuentas.auspicios.models.AuspicioRequest;
import com.multiplos.cuentas.auspicios.models.AuspicioResponse;
import com.multiplos.cuentas.auspicios.models.AuspicioTorneo;
import com.multiplos.cuentas.auspicios.models.AuspicioValoracion;
import com.multiplos.cuentas.auspicios.models.Beneficiario;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFilter;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFormRequest;
import com.multiplos.cuentas.auspicios.models.BeneficiarioResponse;
import com.multiplos.cuentas.auspicios.models.Categorias;
import com.multiplos.cuentas.auspicios.models.Deportista;
import com.multiplos.cuentas.auspicios.models.Disciplina;
import com.multiplos.cuentas.auspicios.models.AuspicioException;
import com.multiplos.cuentas.auspicios.models.AuspicioFilter;
import com.multiplos.cuentas.auspicios.models.Modalidad;
import com.multiplos.cuentas.auspicios.models.BeneficiarioPersonaResponse;
import com.multiplos.cuentas.auspicios.models.RecompensaResponse;
import com.multiplos.cuentas.auspicios.models.RecompensasAllow;
import com.multiplos.cuentas.auspicios.models.TitulosDeportivos;
import com.multiplos.cuentas.auspicios.models.TitulosDepostivosResponse;
import com.multiplos.cuentas.auspicios.models.TitulosDetalleRequest;
import com.multiplos.cuentas.auspicios.models.TitulosRequest;
import com.multiplos.cuentas.auspicios.models.TorneosAllow;
import com.multiplos.cuentas.auspicios.models.ValoracionRequest;
import com.multiplos.cuentas.auspicios.models.ValoracionResponse;
import com.multiplos.cuentas.auspicios.repository.CategoriaRepository;
import com.multiplos.cuentas.auspicios.repository.DisciplinaRepository;
import com.multiplos.cuentas.auspicios.repository.ModalidadRepository;
import com.multiplos.cuentas.auspicios.repository.RecompensasRepository;
import com.multiplos.cuentas.auspicios.repository.TorneosRepository;
import com.multiplos.cuentas.auspicios.repository.auspiciosRepository;
import com.multiplos.cuentas.auspicios.services.AuspiciosService;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.PersonaTipoCuenta;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.solicitud.SolicitudDocumentoRequest;
import com.multiplos.cuentas.services.BancoService;
import com.multiplos.cuentas.services.BlobStorageService;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.PersonaService;
import com.multiplos.cuentas.utils.ServicesUtils;

@Service
public class AuspiciosServiceImpl implements AuspiciosService{
	private static final Logger LOG = LoggerFactory.getLogger(AuspiciosServiceImpl.class);
	private EntityManager em;
	private CuentaService cuentaService;
	private PersonaService personaService;
	private auspiciosRepository ausRepo;
	private DisciplinaRepository disciplinaRepository;
	private CategoriaRepository  cateogoriaRepository;
	private ModalidadRepository  modalidadRepository;
	private TorneosRepository torneoRepo;
	private RecompensasRepository recomRepo;
	private ParametroService parametroService;
	private BlobStorageService blobStorageService;
	private EnvioEmailService enviaEmailService;
	private BancoService bancoService;
	private ServicesUtils utils;
	CriteriaBuilder cb ;
	@Autowired
	public AuspiciosServiceImpl(EntityManager em,CuentaService cuentaService
			,auspiciosRepository ausRepo,
			TorneosRepository torneoRepo,
			RecompensasRepository recomRepo,
			PersonaService personaService,
			ParametroService parametroService,
			BlobStorageService blobStorageService,
			EnvioEmailService enviaEmailService,
			BancoService bancoService,
			ServicesUtils utils,
			DisciplinaRepository disciplinaRepository,
			CategoriaRepository  cateogoriaRepository,
			 ModalidadRepository  modalidadRepository) {
		this.cuentaService= cuentaService;
		this.em=em;
		this.ausRepo=ausRepo;
		this.torneoRepo= torneoRepo;
		this.recomRepo=recomRepo;
		this.personaService=personaService;
		this.parametroService= parametroService;
		this.blobStorageService=blobStorageService;
		this.enviaEmailService=enviaEmailService;
		this.bancoService=bancoService;
		this.utils=utils;
		this.disciplinaRepository =disciplinaRepository;
		this.cateogoriaRepository= cateogoriaRepository;
		this.modalidadRepository= modalidadRepository;
		cb = em.getCriteriaBuilder();
	}
	
	public Object  save(Beneficiario beneficiario)throws Exception {
		Map<String,String>errors = new HashMap<String, String>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator =  factory.getValidator();
		//ifNameIsNull_nameValidationFails
		Set<ConstraintViolation<Beneficiario>> constraintViolations = validator.validate(beneficiario);
		for (ConstraintViolation<Beneficiario> v : constraintViolations) {
		   errors.put(v.getMessageTemplate(), v.getMessage());
		}
		if(constraintViolations.size()>0) {
			return  errors;
		}
		try {
			this.ausRepo.save(beneficiario);
			}catch(ConstraintViolationException e) {
		    throw new ConstraintViolationException("campo "+e.getConstraintName()+" no valido", e.getSQLException(), e.getConstraintName());
			}
		return "";
	}
	
	public AuspicioValoracion findValoracionById(Long id) {
		AuspicioValoracion valoracion = ausRepo.findValidationById(id); 
		if(valoracion==null) {
			throw new NoResultException("No existe Valoracion con numero: "+id);
		}
		return valoracion;
	}

	public List<AuspicioValoracion> findValoracionList(String bene) {
		List<AuspicioValoracion> valoracion = ausRepo.findValidationList(bene,true); 
		if(valoracion.isEmpty()) {
			valoracion= new ArrayList<>();
		}
		return valoracion;
	}
	
	
	public Auspicio findAuspicioById(Long id)throws Exception {
		Auspicio auspicio = this.ausRepo.findAuspicioById(id);
		if(auspicio==null) {
			throw new NullPointerException("No existe auspicio con numero: "+id );
		}
		if(auspicio.getActivo()==false) {
			throw new NullPointerException("Auspicio caducado");
		}
		return auspicio;
	}
	public Beneficiario consultarBeneById(String idBene)throws Exception {
		Optional<Beneficiario>opt=ausRepo.findById(idBene);
		Beneficiario beneficiario=null;
		if(!opt.isEmpty()) { beneficiario= opt.get();}
		return beneficiario;
		
	}
	
	
	public Beneficiario findById(String idBene)throws Exception {
		Optional<Beneficiario> beneficiario= null;
		beneficiario= ausRepo.findById(idBene);
		if(beneficiario.isEmpty() ) {throw new NoResultException("Beneficiario no existe con identificacion: " +idBene);}
		Beneficiario bene= beneficiario.get();
		if(bene.getActivo()==false) {throw new NoResultException("Beneficiario no esta activo con identificacion: " +idBene);}
		return bene;
	}
	
	public Boolean isBeneficiario(String id)throws Exception {
		Boolean isBeneficiario=null;
		isBeneficiario= ausRepo.isBeneficiario(id);
		if(isBeneficiario==null  ) {throw new Exception("Beneficiario no existe con identificacion:" +id);}
		if(isBeneficiario==false) {throw new Exception("Beneficiario no esta activo con identificacion:" +id);}
		return isBeneficiario;
	}
	
	@Override
	@Transactional
	public List<Categorias> listarCategorias(Boolean status) {
		CriteriaQuery<Categorias> query = cb.createQuery(Categorias.class);
		Root<Categorias> root = query.from(Categorias.class);
		List<Predicate> predicates= new ArrayList<>();
		  if(status!=null) {
				predicates.add(cb.equal(root.get("activo"),status));
			}
		  query.select(root).where(predicates.toArray(new Predicate[] {}));
		List<Categorias> result = em.createQuery(query).getResultList();
		return result;
	}
	@Override
	@Transactional
	public String putCategoria(Categorias cat) {
		
		cateogoriaRepository.save(cat);
		return "Ingresado";
	}
	@Override
	@Transactional
	public List<Disciplina> listarDisciplina(Boolean status) {
		CriteriaQuery<Disciplina> query = cb.createQuery(Disciplina.class);
		Root<Disciplina> root = query.from(Disciplina.class);
		List<Predicate> predicates= new ArrayList<>();
		  if(status!=null) {
				predicates.add(cb.equal(root.get("activo"),status));
			}
		  query.select(root).where(predicates.toArray(new Predicate[] {}));
		List<Disciplina> result = em.createQuery(query).getResultList();
		return result;
	}
	@Override
	@Transactional
	public String putDisciplina(Disciplina dis) {
		disciplinaRepository.save(dis);
		return "Ingresado";
	}
	@Override
	@Transactional
	public List<Modalidad> listarModalidad(Boolean status) {
		CriteriaQuery<Modalidad> query = cb.createQuery(Modalidad.class);
		Root<Modalidad> root = query.from(Modalidad.class);
		List<Predicate> predicates= new ArrayList<>();
		  if(status!=null) {
				predicates.add(cb.equal(root.get("activo"),status));
			}
		  query.select(root).where(predicates.toArray(new Predicate[] {}));
		List<Modalidad> result = em.createQuery(query).getResultList();
		return result;
	}
	@Override
	@Transactional
	public String putModalidad(Modalidad mod) {
		
		modalidadRepository.save(mod);
		return "Ingresado";
	}
	public Disciplina findDisiplinaById(Long id)throws Exception {
		Disciplina dis=null;
		try {
		CriteriaQuery<Disciplina> query = cb.createQuery(Disciplina.class);
		Root<Disciplina> root = query.from(Disciplina.class);
		query.select(root).where(cb.equal(root.get("id"),id));
		dis = em.createQuery(query).getSingleResult();
		}catch(NoResultException e) {
			throw new NoResultException("No existe disciplina con el valor: "+id);
		}
		return dis;
	}
	
	
	
	@Override
	@Transactional
	public String fomularioBeneficiario(BeneficiarioFormRequest form) throws Exception{
		Persona repre=null;
		Persona persona=null;
		PersonaTipoCuenta cuentaBancaria= null;
		persona= this.personaService.findById(form.getIdentificacion());
		repre= this.personaService.findById(form.getIdRepresentante());
		if(repre==null) {throw new AuspicioException("Cuenta del representante no existe: "+form.getIdRepresentante());}
		if(persona==null) {
			persona= new Persona();
			persona.setIdentificacion(form.getIdentificacion());
		}
		Beneficiario beneficiario =null;
		beneficiario = this.consultarBeneById(form.getIdentificacion());
		if(beneficiario==null) {
			beneficiario = new Beneficiario();
			beneficiario.setId(form.getIdentificacion());
			cuentaBancaria=new PersonaTipoCuenta();
		}else {
			cuentaBancaria=beneficiario.getCuentaBancaria();
		}
		
		
		cuentaBancaria.setTipoCuenta(form.getTipoCuenta());	
		cuentaBancaria.setNumeroCuenta(form.getNumeroCuenta());
		cuentaBancaria.setBanco(this.bancoService.findById(form.getIdBanco()));
		if(form.getTipoCliente().equals("NAT")) {
			persona.setTipoIdentificacion("CED");
			cuentaBancaria.setTitular(form.getNombres()+" "+form.getApellidos());
			persona.setNombres(form.getNombres());
			persona.setApellidos(form.getApellidos());
			persona.setFechaNacimiento(utils.configuraSumaDia(form.getFechaNacimiento()));
		}else {
			if(form.getRazonSocial()==""||form.getAnnioInicioAct()==null) {
				throw new AuspicioException("Falta informacion: Razon social o año inicio de actividad") ;
			}
			cuentaBancaria.setTitular(form.getRazonSocial());
			persona.setRazonSocial(form.getRazonSocial());
			persona.setAnioInicioActividad(form.getAnnioInicioAct());
			persona.setTipoIdentificacion("RUC");
		}
		
		persona.setNacionalidad(form.getNacionalidad());
		persona.setNumeroCelular(form.getCelular());
		
		persona.setTipoPersona(form.getTipoCliente());
		persona.setUsuarioCreacion(form.getIdRepresentante());
		
		
		beneficiario.setTituloActual(form.getTituloActual());
		beneficiario.setCorreo(form.getCorreo());
		beneficiario.setPersona(persona);
		cuentaBancaria.setPersona(persona);
		beneficiario.setPerfil(form.getPerfil());
		beneficiario.setRepresentanteLegal(repre);
		beneficiario.setCategoria(Categorias.builder().id(form.getCategoria()).build());
		beneficiario.setDisciplina(this.findDisiplinaById(form.getDisciplina()));
		beneficiario.setModalidad(Modalidad.builder().id(form.getModalidad()).build());
		beneficiario.setCuentaBancaria(cuentaBancaria);		
		
		this.save(beneficiario);
		
		return "Ingresado correctamente";
	}
	
	@Override
	@Transactional
	public String agregarTitulosBeneficiario(TitulosRequest requestList) throws Exception{
		
		Beneficiario beneficiario=this.findById(requestList.getIdentificacion());
		List<TitulosDeportivos> listi= new ArrayList<>();
	//	requestList.getTitulos().stream().forEach((x)->{});		
		for(TitulosDetalleRequest request : requestList.getTitulos()) {
			
			TitulosDeportivos titulo=null;
			if(request.getIdTitulo()!=null) {
	
				titulo= beneficiario.getTitulos().stream().filter(t ->request.getIdTitulo().equals(t.getId())).findAny().orElseThrow();
			}
			 if(titulo==null) {
				titulo= new TitulosDeportivos();;
			}
			titulo.setAnioTitulo(request.getAnioTitulo());
			titulo.setBeneficiario(beneficiario);
			titulo.setDisciplina(Disciplina.builder().id(request.getIdDisciplina()).build());
			titulo.setNombreCompetencia(request.getNombreCompetencia());
			titulo.setOtros(request.getOtros());
			titulo.setRankingInternacional(request.getRankingInternacional());
			titulo.setRankingNacional(request.getRankingNacional());
			listi.add(titulo);
			
		}
		beneficiario.setTitulos(listi);
		this.save(beneficiario);
		return "Titulos agregados correctamente";
	}
	
	@Override
	@Transactional
	public String agregarFotosBeneficiario(String identificacion, String idRepre, MultipartFile file, MultipartFile file2) throws Exception{
		Beneficiario beneficiario = this.ausRepo.findById(identificacion).get();
		String nombreArchivofront;
		String nombreArchivopost;
		String rutaArchivo;
		String rutaArchivopost;
		String contentType = file.getContentType().toLowerCase();// application/pdf
		String contentType2 = file2.getContentType().toLowerCase();// application/pdf
	
		if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
				&& !contentType2.equals("image/jpeg") && !contentType2.equals("image/png")) {
			throw new Exception("Error archino no valido, debe cargar un jpg o png");
		}
		ParametroResponse paramResponse = new ParametroResponse();
		paramResponse = parametroService.findByParametroCodParametro("CONTBENEFICIARIO");
		String type = contentType2.split("/")[1];// saca extension del archivo
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String type2 = contentType2.split("/")[1];// saca extension del archivo
		nombreArchivofront = identificacion + "frontal_" + timeStamp + "." + type;
		// numeroSolicitud = solDocIdentificacion.getNumeroSolicitud();
		nombreArchivopost = identificacion + "posterior_" + timeStamp + "." + type2;
		try {
		rutaArchivo = blobStorageService.uploadFile(file, nombreArchivofront, paramResponse.getValor());
		rutaArchivopost = blobStorageService.uploadFile(file2, nombreArchivopost,paramResponse.getValor());
		}catch(Exception e) {throw new Exception ("Error al subir fotos");}
		beneficiario.setRuta1(rutaArchivo);
		beneficiario.setRuta2(rutaArchivopost);
		return "Fotos subidas correctamente";
	}
	
	
	
	
	
	@Override
	@Transactional
	public Object setearPersonaAcuentaBancaria() {
		CriteriaQuery<Persona> query = cb.createQuery(Persona.class);
		Root<Persona> root = query.from(Persona.class);
		query.select(root);
		 List<Persona>pers = em.createQuery(query).getResultList();
		pers.stream().forEach((x)->{
			if(x.getPersInfoAdicional()!=null) {
				x.getPersInfoAdicional().getPersTipoCuenta().setPersona(x);			}
		});
		CriteriaQuery<Beneficiario> query2 = cb.createQuery(Beneficiario.class);
		Root<Beneficiario> root2 = query2.from(Beneficiario.class);
		query2.select(root2);
		List<Beneficiario>benes = em.createQuery(query2).getResultList();
		benes.stream().forEach((x)->{
			if(x.getCuentaBancaria()!=null) {
				x.getCuentaBancaria().setPersona(x.getPersona())	;
				}
		});
		
		return "";
	}
	
	@Override
	@Transactional
	public Object aggValoracion( ValoracionRequest  rq, MultipartFile file)throws Exception {
		Beneficiario beneficiario=  this.findById(rq.getIdBene());
		AuspicioValoracion validacion=null;
		if(rq.getId()!=null) {
			validacion = this.findValoracionById(rq.getId());
		}else {
			validacion = this.ausRepo.findValidationByActive(rq.getIdBene(), true);
			
		if(validacion!=null) {
			LOG.warn("entro");
			if(validacion.getFechaCaducidad().isAfter(LocalDate.now()) && 
					rq.getFechaCaducidad().getYear()<= validacion.getFechaCaducidad().getYear()) {
				LOG.warn("valido");
				throw new CuentaException("Ya posee una valoracion activa, caduca el: "+validacion.getFechaCaducidad());
			}
			if(validacion.getFechaCaducidad().isBefore(LocalDate.now())){
				validacion.setActivo(false);			}
		}
			validacion= new AuspicioValoracion();
			if(rq.getFechaCaducidad().getYear()>LocalDate.now().getYear()) {
				 validacion.setActivo(false);		 
				 }
		}
		try {
			rq.validar();
		}catch(Exception e) {
			throw new CuentaException(e.getMessage());
		}
		if(rq.getPresupuestoAprobado().doubleValue()<0) {throw new CuentaException("El presupuesto aprobado no puede ser nulo");}
		
		ParametroResponse paramResponse = new ParametroResponse();
		paramResponse = parametroService.findByParametroCodParametro("CONTBENEFICIARIO");
		String rutaArchivo = blobStorageService.uploadFile(file,rq.getIdBene()+file.getContentType().toLowerCase().split("/")+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), paramResponse.getValor());
		
		
		
		validacion.setAnio(rq.getAnio());
		validacion.setBeneficiario(beneficiario);
		validacion.setCalificacion(rq.getCalificacion());
		validacion.setFechaCalificacion(rq.getFechaCalificacion());
		validacion.setFechaCaducidad(rq.getFechaCaducidad());
		validacion.setBianual(rq.getBianual());
		validacion.setPresupuestoAprobado(rq.getPresupuestoAprobado());
		validacion.setPresupuestoRecaudado(rq.getPresupuestoRecaudado());
		validacion.setRuta(rutaArchivo);
	
		beneficiario.getValoraciones().add(validacion);
		this.save(beneficiario);
		return "Guardado correctamente";
	}
	
	
	
	@Override
	@Transactional
	public boolean validateValoracion(String id)throws Exception {
		AuspicioValoracion validacion = ausRepo.findValidationByActive(id, true);
		Beneficiario beneficiario= this.consultarBeneById(id);
		
		boolean activa=false;
		if(validacion!=null) {
			activa=true;
		}else {
		activa= false;
		}
	return activa;
	}
	
	
	@Override
	@Transactional
	public String aggAuspicio( AuspicioRequest rq,String estado)throws Exception {
		Beneficiario beneficiario= this.findById(rq.getIdBene());
		AuspicioValoracion validacion = ausRepo.findValidationByActive(rq.getIdBene(), true);
		Auspicio isAuspicio=null;
		if(validacion==null) {return"Ingrese su valoracion anual";}
		if(validacion.getPresupuestoRestante().doubleValue() < rq.getPresupuestoSolicitudo().doubleValue()) {
		throw new Exception("El presupuesto solicitado exede a su presupuesto aprobado: "+validacion.getPresupuestoAprobado());
		}
		if(LocalDate.now().isAfter(validacion.getFechaCaducidad())) {
			validacion.setActivo(false);
			return "Su valoracion a caducado, ingrese su nueva valoracion.";
			}
		if(rq.getNumeroAuspicio()==null) {
			isAuspicio = this.ausRepo.findAuspicioByEstado(rq.getIdBene(), "BO");
			if(isAuspicio!=null) {
				throw new Exception("El beneficiario ya tiene un auspicio en proceso");
			}
		}
		isAuspicio = this.ausRepo.findAuspicioById(rq.getNumeroAuspicio());
		if(isAuspicio!=null&&isAuspicio.getActivo()==false) {throw new Exception("El Auspicio se encuentra caducado");}
		if(isAuspicio==null) {
			isAuspicio= new Auspicio();	
		}
		isAuspicio.setBeneficiario(beneficiario);
		isAuspicio.setValoracion(validacion);
		isAuspicio.setPresupuestoSolicitado(rq.getPresupuestoSolicitudo());
		isAuspicio.setEstado(AuspicioEstados.builder().idEstado(estado).build());

		beneficiario.getAuspicios().add(isAuspicio);
		this.save(beneficiario);
		
		return "Su asuspicio esta en proceso :) ";
	}
	
	@Override
	@Transactional
		public	String aggTorneos(TorneosAllow torneos)throws Exception{
	try {
			Auspicio auspicio= this.findAuspicioById(torneos.getNumeroAuspicio());
			if(!torneos.getIdentificacion().equals(auspicio.getBeneficiario().getId())) {
				throw new AuspicioException("El auspicio no pertenece al beneficiario con ci: "+torneos.getIdentificacion());
			}

			torneos.getTorneos().stream().forEach((x)->{x.setAuspicio(auspicio);});
			
			if(!auspicio.getTorneosParticipar().isEmpty()) {
//			for(int i=0; i<torneos.getTorneos().size();i++ ) {
//				torneos.getTorneos().get(i).setId(auspicio.getTorneosParticipar().get(i).getId());
//			}
				this.torneoRepo.deleteAll( auspicio.getTorneosParticipar());	
			}
			auspicio.setTorneosParticipar(torneos.getTorneos());
	}catch(Exception c) {
		throw new Exception(c.getMessage());
	}	
			return"Torneos guardados correctamente";
		}
	
	@Override
	@Transactional
		public 	String aggRecompensas(RecompensasAllow recompensas)throws Exception{
			Auspicio auspicio= this.findAuspicioById(recompensas.getNumeroAuspicio());
			
			
			
			recompensas.getRecompensas().stream().forEach((x)->{x.setAuspicio(auspicio);});
			
			if(!auspicio.getRecompesas().isEmpty()) {
				this.recomRepo.deleteAll( auspicio.getRecompesas());	
			}
			auspicio.setRecompesas(recompensas.getRecompensas());
			return"Recompenas guardados correctamente";
		}
	
	@Override
	@Transactional
	public Object EnviarAuspicio( Long id)throws Exception{
		
		Auspicio auspicio = this.findAuspicioById(id);
		auspicio.setEstado( AuspicioEstados.builder().idEstado("PC").build());
		Map<Object,Object> mensajes= new HashMap<Object, Object>();;
		try {
		this.enviaEmailService.enviaEmailAus_pc_beneficiario(auspicio.getBeneficiario().getCorreo());
		}catch(NullPointerException n) {mensajes.put("error1", "Beneficiario no tiene correo");}
		try {
		this.enviaEmailService.enviaEmailAus_pc_representante(auspicio.getBeneficiario().getRepresentanteLegal().getCuenta().getEmail()); 
		}catch(NullPointerException n) {mensajes.put("error2", "representante no tiene correo");}
		try {
		this.enviaEmailService.enviaEmailAus_pc_analista("analistaprueba@multiplolenders"); 
		}catch(NullPointerException n) {mensajes.put("error3", "analista no tiene correo");} 
		
		mensajes.put("mensaje", "Su auspicio se envió para la revisión :)");
		return mensajes;
	}
	
	
	
	/////////////////////////////////////
	////////////////////////////////
	/////////////////////////CONSULTAS MAESTRAS/////////////////////
	////////////////////////////
	/////////////////////////////////////////////////
	////////////////////////////////////	
	
	public BeneficiarioPersonaResponse consultaPersonaBene(String identificacion) {
		BeneficiarioPersonaResponse	persona=null;
		CriteriaQuery<BeneficiarioPersonaResponse> queryPerson = cb.createQuery(BeneficiarioPersonaResponse.class);
		Root<Persona> rooter = queryPerson.from(Persona.class);
		queryPerson.multiselect(
				rooter.get("identificacion"),
				rooter.get("tipoPersona"),
				rooter.get("razonSocial"),
				rooter.get("anioInicioActividad"),
				rooter.get("nombres"),
				rooter.get("apellidos"),
				rooter.get("nacionalidad"),
				rooter.get("fechaNacimiento"),
				rooter.get("numeroCelular")
				)
		.where(cb.equal(rooter.get("identificacion"),identificacion));	
		try {
			persona = em.createQuery(queryPerson).getSingleResult();
			}catch(NoResultException n) {
		return persona= null;
		}
	
		return persona;
	}
	
	
	public List<BeneficiarioResponse> consultarBeneByFilter(BeneficiarioFilter filter)throws Exception {
	
		CriteriaQuery<BeneficiarioResponse> query = cb.createQuery(BeneficiarioResponse.class);
		Root<Beneficiario> root = query.from(Beneficiario.class);
		List<Predicate> predicates= new ArrayList<>();
		  if(!filter.getIdentificacion().isEmpty()) {
			predicates.add(cb.equal(root.get("id"),filter.getIdentificacion()));
		}
		  if(!filter.getRepresentante().isEmpty()) {
				predicates.add(cb.equal(root.get("representanteLegal").get("identificacion"),filter.getRepresentante()));
		  }
//		  if(filter.getDisciplina()!=null) {
//				predicates.add(cb.equal(root.get("disciplina").get("id"),filter.getDisciplina()));
//		  }
		query.multiselect(
				root.get("cuentaBancaria").get("banco").get("nombre"),		
				root.get("cuentaBancaria").get("tipoCuenta"),
				root.get("cuentaBancaria").get("numeroCuenta"),
				root.get("id"),
				root.get("representanteLegal").get("identificacion"),
				root.get("disciplina").get("nombre"),
				root.get("categoria").get("nombre"),
				root.get("modalidad").get("nombre"),
				root.get("correo"),
				root.get("perfil"),
				root.get("tituloActual"),
				root.get("ruta2"),
				root.get("ruta1")
				)
			.where(predicates.toArray(new Predicate[]{}));
		
		List<BeneficiarioResponse>	benef = em.createQuery(query).getResultList(); 
			
		
		
		return benef;
	}
	
	@Override
	@Transactional
	public List<AuspicioResponse> consultarAuspicioFilter(AuspicioFilter filter)throws Exception {
		
		List<AuspicioResponse> auspicios;
		CriteriaQuery<AuspicioResponse> query = cb.createQuery(AuspicioResponse.class);
		Root<Auspicio> root = query.from(Auspicio.class);
		List<Predicate> predicates= new ArrayList<>();
		  if(filter.getId()!=null) {
				predicates.add(cb.equal(root.get("id"),filter.getId()));
			}
		  if(!filter.getNomApe().isEmpty()) {
			  Expression<String> parentExpression = root.get( "beneficiario").get("persona").get("nombres");
				predicates.add(parentExpression.in(filter.getNomApe()));
			}
		  if(!filter.getIdentificacion().isEmpty()) {
			predicates.add(cb.equal(root.get("beneficiario").get("id"),filter.getIdentificacion()));
		}
		  if(!filter.getEstado().isEmpty()) {
				predicates.add(cb.equal(root.get("estado").get("idEstado"),filter.getEstado()));
			}
		  if(filter.getDisciplina()!=null) {
			predicates.add(cb.equal(root.get("beneficiario").get("disciplina").get("id"),filter.getDisciplina()));
		  }
		  if(!filter.getEstado().isEmpty()) {
				 predicates.add(cb.equal(root.get("estado").get("idEstado"),filter.getEstado()));
			  }
		query.multiselect(
				root.get("id"),
				root.get("beneficiario").get("id"),
				root.get("presupuestoSolicitado"),
				root.get("presupuestoRecaudado"),
				root.get("estado").get("detalle")	
				)
		.where(predicates.toArray(new Predicate[] {}));
		
		 auspicios = em.createQuery(query).getResultList();
		
		return auspicios;
	}
	
	
	
	@Override
	@Transactional
	public AuspicioResponse Consultar(String id) {
		AuspicioResponse auspicio= null;
		return auspicio;
	}
	

	
	@Override
	@Transactional
	public void eliminarBene(String id) {
		this.ausRepo.deleteById(id);
		
	}
	@Override
	@Transactional
	public Object beneByPredicates(String identificacion,String representante) {
//	BeneficiarioResponse  bene=null;
//		
//		List<Predicate> predicates = new ArrayList<>();
//		CriteriaQuery<BeneficiarioResponse> query = cb.createQuery(BeneficiarioResponse.class);
//		Root<Beneficiario> root = query.from(Beneficiario.class);
//		
//		Predicate finalQuery = cb.greaterThan(root.get("id"),identificacion);
//		predicates.add(finalQuery);
//		Predicate represen=cb.greaterThan(root.get("representanteLegal").get("identificacion"),representante);
//		predicates.add(represen);
		//bene= (BeneficiarioResponse) this.BeneficiarioByPredicartes(predicates);
		return null;
	}
	
	@Override
	@Transactional
	public Object consultaBeneForAgg(BeneficiarioFilter filter)throws Exception {
		 if(filter.getIdentificacion().isEmpty()) {
				throw new AuspicioException("Identificacion no puede estar vacio");
		  }
		 if(filter.getRepresentante().isEmpty()) {
				throw new AuspicioException("Representante no puede estar vacio");
		  }
			String guardaRepre= filter.getRepresentante();
			filter.setRepresentante("");
			Map<String,Object>res= new HashMap<>();
		BeneficiarioPersonaResponse persona= this.consultaPersonaBene(filter.getIdentificacion());
		res.put("persona", persona);
		List<BeneficiarioResponse> listaBene= this.consultarBeneByFilter(filter);
		for(BeneficiarioResponse beneficiario:listaBene) {
			if(!beneficiario.getRepresentante().equals(guardaRepre)) {
				throw new AuspicioException("El beneficiario con cedula: "+filter.getIdentificacion()+" ya pertenece a un representante");
			}
			if(beneficiario!=null) {
				res.put("beneficiario", beneficiario);
				}
		}
	

		return res;
	}
	

	
	
	@Override
	@Transactional
	public Object consultarBeneByRepre(BeneficiarioFilter filter)throws Exception {
		List<BeneficiarioResponse> benes;
		List<Map<String,Object>> response= new ArrayList<>();
		benes=(List<BeneficiarioResponse>) this.consultarBeneByFilter(filter);		
		if(benes.isEmpty()) {
			return benes;
		}
		for(BeneficiarioResponse bene:benes) {
			BeneficiarioPersonaResponse person= new BeneficiarioPersonaResponse();	
			Map<String,Object>res= new HashMap<>();
			person= (BeneficiarioPersonaResponse) this.consultaPersonaBene(bene.getIdentificacion());
			res.put("beneficiario", bene);
			res.put("persona", person)	;
			response.add(res);
			}
		return response;
	}
	
	
	
	@Override
	@Transactional
	public List<?> consultarTitulos(String identificacion)throws Exception {
			
		CriteriaQuery<TitulosDepostivosResponse> query = cb.createQuery(TitulosDepostivosResponse.class);
		Root<TitulosDeportivos> root = query.from(TitulosDeportivos.class);
		query.multiselect(
				root.get("id"),
				root.get("anioTitulo"),
				root.get("nombreCompetencia"),
				root.get("rankingNacional"),
				root.get("rankingInternacional"),
				root.get("otros"))
		.where(cb.equal(root.get("beneficiario").get("id"),identificacion));
		List<TitulosDepostivosResponse>titulos = em.createQuery(query).getResultList();
	
		return titulos;
	}
	
	@Override
	@Transactional
	public Object consultarAuspicioByBeneficiario(AuspicioFilter filter)throws Exception {
		Object res;
		if(filter.getIdentificacion().isBlank()) {
			throw new AuspicioException("La identificacion no puede estar vacia");
		}
		res= this.consultarAuspicioFilter(filter);
	
		return res;
	}
	
	@Override
	@Transactional
	public AuspicioResponse consultarAuspicioById(Long id)throws Exception {
		AuspicioResponse auspicio =null;
		
		CriteriaQuery<AuspicioResponse> query = cb.createQuery(AuspicioResponse.class);
		Root<Auspicio> root = query.from(Auspicio.class);
		query.multiselect(
				root.get("id"),
				root.get("beneficiario").get("id"),
				root.get("presupuestoSolicitado"),
				root.get("presupuestoRecaudado"),
				root.get("estado").get("detalle")	
				//root.get("beneficiario").get("titulos")
				)
		.where(cb.equal(root.get("id"),id),cb.equal( root.get("activo"),true));
		
		 auspicio = em.createQuery(query).getSingleResult();
		 
		 
		
		return auspicio;
	}
	
	@Override
	@Transactional
	public List<?> consultarAuspiciosPorEstado(AuspicioFilter filter)throws Exception {
	
		List<Map<String,Object>>response = new ArrayList<>();
		List<AuspicioResponse>listaAus;
		listaAus= (List<AuspicioResponse>) this.consultarAuspicioFilter(filter);
		
		for(AuspicioResponse auspicio:listaAus) {
			BeneficiarioFilter filterBene= BeneficiarioFilter.builder().identificacion(auspicio.getIdentificacion()).representante("").build();
			List<BeneficiarioResponse> listaBene= this.consultarBeneByFilter(filterBene);
			for(BeneficiarioResponse beneficiario: listaBene) {
				BeneficiarioPersonaResponse persona= this.consultaPersonaBene(beneficiario.getIdentificacion());
//				List<RecompensaResponse> recompensas =  this.consultarRecompensas(auspicio.getNumeroAuspicio());
//				Object torneos = this.consultarTorneos(auspicio.getNumeroAuspicio());
				Map<String, Object>res= new HashMap<>();
				res.put("persona", persona);
				res.put("beneficiario", beneficiario);
				res.put("auspicio", auspicio);	
//				res.put("recompensas", recompensas);
//				res.put("torneos", torneos);
				response.add(res);
			}
			
		}

		return response;
	}
	
	@Override
	@Transactional
	public List<?> consultarAuspiciosByRepre(String ideRepre)throws Exception {
		
		CriteriaQuery<AuspicioResponse> query = cb.createQuery(AuspicioResponse.class);
		Root<Auspicio> root = query.from(Auspicio.class);
		query.multiselect(
				root.get("id"),
				root.get("beneficiario").get("persona").get("tipoPersona"),
				root.get("beneficiario").get("persona").get("identificacion"),
				root.get("beneficiario").get("persona").get("nombres"),
				root.get("beneficiario").get("persona").get("apellidos"),
				root.get("beneficiario").get("persona").get("razonSocial"),
				root.get("beneficiario").get("disciplina").get("nombre"),
				root.get("beneficiario").get("categoria").get("nombre"),
				root.get("beneficiario").get("modalidad").get("nombre"),
				root.get("beneficiario").get("persona").get("nacionalidad"),
				root.get("beneficiario").get("ruta2"),
				root.get("beneficiario").get("ruta1"),
				root.get("beneficiario").get("perfil"),
				root.get("presupuestoSolicitado"),
				root.get("presupuestoRecaudado"),
				root.get("estado").get("detalle")		
				//root.get("beneficiario").get("titulos")
				)
		.where(cb.equal( root.get("beneficiario").get("representanteLegal").get("identificacion"),ideRepre));
	List<AuspicioResponse> auspicio = em.createQuery(query).getResultList();
	
		return auspicio;
	}
	
	
	
	
	@Override
	@Transactional
	public Object consultarValoracion(String identificacion)throws Exception {
		
		CriteriaQuery<ValoracionResponse> query = cb.createQuery(ValoracionResponse.class);
		Root<AuspicioValoracion> root = query.from(AuspicioValoracion.class);
		query.multiselect(
				root.get("id"),
				root.get("calificacion"),
				root.get("fechaCalificacion"),
				root.get("fechaCaducidad"),
				root.get("presupuestoAprobado"),
				root.get("presupuestoRecaudado"),
				root.get("presupuestoRestante"),
				root.get("ruta"),
				root.get("bianual")
				)
		.where(cb.equal(root.get("beneficiario").get("id"),identificacion),cb.equal(root.get("activo"), true));
		ValoracionResponse valoracion = em.createQuery(query).getSingleResult();
		return valoracion;
		
	}

	@Override
	@Transactional
	public List<RecompensaResponse> consultarRecompensas(Long id)throws Exception {
		Auspicio aus = this.findAuspicioById(id);
		
		//RecompensaResponse recom= new RecompensaResponse();
		List<RecompensaResponse>lista= new ArrayList<>();
		
		aus.getRecompesas().stream().forEach((x)->{
			RecompensaResponse recom= new RecompensaResponse();
			recom.setId(x.getId());
			recom.setCategoria(x.getCategoria());			recom.setDetalle(x.getDetalle());
			recom.setPorcentaje(x.getPorcentaje());
			lista.add(recom);
		});
		return lista;
	}

	@Override
	@Transactional
	public Object consultarTorneos(Long id)throws Exception {
		Auspicio aus = this.findAuspicioById(id);
	
	
		List<Map>lista= new ArrayList<>();
		aus.getTorneosParticipar().forEach((x)->{
			Map<String, Object> torneos = new HashMap<>();
			torneos.put("id", x.getId());
			torneos.put("nombreTorneo", x.getNombreTorneo());
			torneos.put("pais", x.getPais().getPais());
			torneos.put("fecha", x.getFecha());
			lista.add(torneos);
		});
		 
		return lista;
	}

	@Override
	@Transactional
	public Object actualizaEstadoAuspicio(Long id, String usuario,String status,String observacion)throws Exception {
		
		Auspicio auspicio=this.findAuspicioById(id);
		auspicio.setEstado(AuspicioEstados.builder().idEstado(status).build());
		auspicio.setObservacion(observacion +" "+ usuario);
		return "Auspicio aprobado correctamente";
		}
	
	
	
	@Override
	@Transactional
	public Object anularAuspicioPorConfirmar(Long id, String usuario,String observacion)throws Exception {
		
		Auspicio auspicio=this.findAuspicioById(id);
		auspicio.setEstado(AuspicioEstados.builder().idEstado("ANU").build());
		auspicio.setObservacion(observacion+": "+usuario);
		return "Auspicio anulado correctamente";
		}

	
	
	
	
	
}

