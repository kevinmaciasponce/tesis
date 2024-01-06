package com.multiplos.cuentas.auspicios.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.QueryTimeoutException;
import javax.validation.ConstraintViolation;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.transform.Source;

import org.hibernate.TransientPropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.multiplos.cuentas.auspicios.models.Auspicio;
import com.multiplos.cuentas.auspicios.models.AuspicioException;
import com.multiplos.cuentas.auspicios.models.AuspicioFilter;
import com.multiplos.cuentas.auspicios.models.AuspicioRequest;
import com.multiplos.cuentas.auspicios.models.AuspicioRequestFilter;
import com.multiplos.cuentas.auspicios.models.AuspicioResponse;
import com.multiplos.cuentas.auspicios.models.Beneficiario;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFilter;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFormRequest;
import com.multiplos.cuentas.auspicios.models.BeneficiarioResponse;
import com.multiplos.cuentas.auspicios.models.RecompensasAllow;
import com.multiplos.cuentas.auspicios.models.TitulosDetalleRequest;
import com.multiplos.cuentas.auspicios.models.TitulosRequest;
import com.multiplos.cuentas.auspicios.models.TorneosAllow;
import com.multiplos.cuentas.auspicios.models.ValoracionRequest;
import com.multiplos.cuentas.auspicios.services.AuspiciosService;
import com.multiplos.cuentas.controllers.SolicitudController;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.ResponseBoolean;
import com.multiplos.cuentas.services.impl.SolicitudServiceImpl;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class AuspiciosController {
	private ServicesUtils utils;
	private AuspiciosService auspiciosServices;
	private static final Logger LOG = LoggerFactory.getLogger(AuspiciosController.class);
	@Autowired
	public AuspiciosController(AuspiciosService auspiciosServices,
			ServicesUtils utils) {
		this.auspiciosServices= auspiciosServices;
		this.utils=utils;
	}
	
	
	@PostMapping("/representante/lista/disciplinas")
	public ResponseEntity<?> listarDisciplinas (){
		List<?> response= null;
		
		try {
			
			response= this.auspiciosServices.listarDisciplina(true);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/representante/lista/modalidades")
	public ResponseEntity<?> listarMoladidad (){
		List<?> response= null;
		
		try {
			response= this.auspiciosServices.listarModalidad(true);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	@PostMapping("/representante/lista/categorias")
	public ResponseEntity<?> listarCategoria (){
		List<?> response= null;
		
		try {
			response= this.auspiciosServices.listarCategorias(true);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	//@PostMapping("/loquesea2")
	@PostMapping("representante/agregar/beneficiarios")
	public ResponseEntity<?> llenarFormulario (@Valid @RequestBody BeneficiarioFormRequest request ,BindingResult result) throws Exception{
		
		if(result.hasErrors()) {
			LOG.warn("entramos con error");
			return utils.validar(result);
		}
		
		String response= null;
		try {
		response= this.auspiciosServices.fomularioBeneficiario(request);	
		}catch(InvalidDataAccessApiUsageException i) {
			LOG.error(""+i.getCause());
			return ResponseEntity.internalServerError().body(new BadResponse("Dato no valido: "));
		}catch(DataIntegrityViolationException t) {
			LOG.error(""+t.getCause());
			return ResponseEntity.internalServerError().body(new BadResponse("La informacion ingresada no es la correcta "));
		}catch(NoResultException r) {
			LOG.error(""+r.getCause());
			return ResponseEntity.internalServerError().body(new BadResponse(r.getMessage()));
		}catch(AuspicioException a) {
			LOG.error(""+a.getCause());
			return ResponseEntity.internalServerError().body(new BadResponse(a.getMessage()));
		}catch(Exception e) {
			LOG.error(""+e);
			return ResponseEntity.internalServerError().body(new BadResponse("Ha ocurrido un error, intente mas tarde"));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	//@PostMapping("/loquesea2")
	@PostMapping("/representante/agregar/beneficiarios/fotos")
    public ResponseEntity<?> subirFotosBeneficiarios(@RequestParam String identificacion, @RequestParam String idRepre,@RequestParam MultipartFile file, @RequestParam MultipartFile file2) {
		String response = null;
		Map<String, Object> mensaje = new HashMap<>();
		try {
			response = this.auspiciosServices.agregarFotosBeneficiario(identificacion, idRepre, file, file2);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		}
        return ResponseEntity.ok(new Response(response));
    }
	
	

	@PostMapping("/representante/agregar/beneficiarios/titulos")
	public ResponseEntity<?> aggTitulos (@Valid @RequestBody TitulosRequest request ,BindingResult result){
		if(result.hasErrors()) {
			LOG.warn("error");
			return this.utils.validar(result);
		}
		String response= null;
	
		try {
		response= this.auspiciosServices.agregarTitulosBeneficiario( request)	;
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PostMapping("/representante/agregar/beneficiarios/titulosDetalles")
	public ResponseEntity<?> aggTitulosDetalles (@Valid @RequestBody TitulosDetalleRequest request ,BindingResult result){
		if(result.hasErrors()) {
			LOG.warn("error");
			return this.utils.validar(result);
		}
		
		return ResponseEntity.ok(new ResponseBoolean(true));
	}
	
	
	@PostMapping("/representante/agregar/beneficiarios/valoracion")
	public ResponseEntity<?> aggValoracion ( @RequestParam String request ,@RequestParam MultipartFile file )throws Exception {
		ValoracionRequest rq=new ValoracionRequest();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			rq = objectMapper.readValue(request, ValoracionRequest.class);
		} catch (UnrecognizedPropertyException e) {
			
			return ResponseEntity.badRequest().body(new BadResponse("Campo erroneo "+e.getPropertyName() +" no se reconoce"));
		}
		System.out.println(rq.getIdBene());
		String response= null;
		try {
			
		response= (String) this.auspiciosServices.aggValoracion( rq,file);
		}catch(NullPointerException e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}catch(NoResultException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}catch(NonUniqueResultException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.internalServerError().body("Inconsistencia, posee mas de una valoracion activa");
		}
		catch(CuentaException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		catch(Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.internalServerError().body(new BadResponse("Ha ocurrido un error, intente mas tarde"));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PostMapping("/representante/validar/beneficiarios/valoracion")
	public ResponseEntity<?> validateValoracion (@RequestParam String identificacion){
		boolean response;
		try {
			response= this.auspiciosServices.validateValoracion(identificacion)	;
			}catch(Exception e) {
				return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
			}
		return ResponseEntity.ok(new ResponseBoolean( response ));
	}
	
	@PostMapping("/representante/agregar/beneficiarios/auspicio")
	public ResponseEntity<?> aggAuspicio(@Valid @RequestBody AuspicioRequest request,BindingResult result)throws Exception{
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response= null;
		try {
		response= this.auspiciosServices.aggAuspicio(request,"BO");
		}catch(NonUniqueResultException u) {
			return ResponseEntity.internalServerError().body(new BadResponse("Inconsitencia, existen mas de 2 auspicios en proceso"));
		}catch(Exception e) {
			
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PostMapping("/representante/agregar/beneficiarios/auspicio/torneos")
	public ResponseEntity<?> aggAuspicio(@Valid @RequestBody TorneosAllow request,BindingResult result){
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response= null;
		try {
		response= this.auspiciosServices.aggTorneos(request);
		}catch(DataIntegrityViolationException t) {
			LOG.warn(""+t.getCause());
			return ResponseEntity.internalServerError().body(new BadResponse("Error, dato no valido"));
		}catch(AuspicioException e) {
			LOG.warn(""+e);
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		catch(Exception e) {
			LOG.warn(""+e);
			return ResponseEntity.internalServerError().body(new BadResponse("Ha ocurrido un error, intente mas tarde"));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PostMapping("/representante/agregar/beneficiarios/auspicio/recompensas")
	public ResponseEntity<?> aggAuspicio(@Valid @RequestBody RecompensasAllow request,BindingResult result){
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response= null;
		try {
		response= this.auspiciosServices.aggRecompensas(request);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	
	@PostMapping("/representante/modificar/auspicio/porConfirmar")
	public ResponseEntity<?> sendAuspicio(@RequestParam String numeroAuspicio){
		
		Long ide=null;
		try {
		ide= Long.parseLong(numeroAuspicio);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse("Error en el formato"));
		}		
		Object response= null;
		try {
		response= this.auspiciosServices.EnviarAuspicio(ide);
		}catch(Exception e) {
			LOG.warn(""+e);
			return ResponseEntity.internalServerError().body(new BadResponse(" Error interno"));
		}
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/representante/consultar/auspicios/porRepresentante")
	public ResponseEntity<?> listarAuspicioByRepre (@RequestParam String idRepre){
		List<?> response= null;
		
		try {
			response= this.auspiciosServices.consultarAuspiciosByRepre(idRepre);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	

	//CONSULTAR AUSPICIOS
	
	@PostMapping("/public/lista/auspicios/vigentes")
	public ResponseEntity<?> listarAuspicio (@Valid @RequestBody AuspicioFilter filter, BindingResult result ){

		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		filter.setEstado("VG");
		Object response= null;
		try {
			response= this.auspiciosServices.consultarAuspiciosPorEstado(filter);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	
	@PostMapping("/representante/consulta/auspicio/porBeneficiario")
	public  ResponseEntity<?> consultarAuspicio(@Valid @RequestBody AuspicioFilter filter,BindingResult result)throws Exception{
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		Object auspicio=null;
		
		try {
			auspicio= this.auspiciosServices.consultarAuspicioByBeneficiario(filter);
			
		}catch(AuspicioException e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}catch(NoResultException e) {
			return ResponseEntity.internalServerError().body(new BadResponse("No existe resultado"));
		}catch(NonUniqueResultException e) {
			return ResponseEntity.internalServerError().body(new BadResponse("Existe 2 o mas resultados"));
		}catch(QueryTimeoutException t) {
			return ResponseEntity.internalServerError().body(new BadResponse("Se ha exedido el tiempo de repuesta"));
		}catch(Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.internalServerError().body(new BadResponse("Error interno intente mas tarde"));
		}
		
		return ResponseEntity.ok( auspicio);
	}
	
	
	
	
	@PostMapping("/public/consulta/auspicio/porNumero")
	public  ResponseEntity<?> consultarAuspicioById(@RequestBody AuspicioRequestFilter request)throws Exception{
		if(request.getNumeroAuspicio()==null) {
			return ResponseEntity.badRequest().body(new BadResponse("El numero de auspicio no puede ser nulo"));
		}
		Object auspicio=null;
		
		
		try {
			auspicio= this.auspiciosServices.consultarAuspicioById(request.getNumeroAuspicio());
		
		}catch(NoResultException e) {
			return ResponseEntity.internalServerError().body(new BadResponse("No existe resultado"));
		}catch(NonUniqueResultException e) {
			return ResponseEntity.internalServerError().body(new BadResponse("Existe 2 o mas resultados"));
		}catch(QueryTimeoutException t) {
			return ResponseEntity.internalServerError().body(new BadResponse("Se ha exedido el tiempo de repuesta"));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse("Error interno intente mas tarde"));
		}
		
		return ResponseEntity.ok( auspicio);
	}
	
	
	
	
	
	@PostMapping("/representante/eliminar/beneficiario/filter")
	public  ResponseEntity<?> eliminarBene(@RequestParam String identificacion)throws Exception{
		
		Object bene=null;
		
		try {
			 this.auspiciosServices.eliminarBene(identificacion);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( bene);
	}
	
	@PostMapping("/representante/consulta/beneficiario/paraAgregar")
	public  ResponseEntity<?> consultarBeneficiario(@Valid @RequestBody BeneficiarioFilter filter, BindingResult result)throws Exception{
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		Object bene;
		
		try {
			bene= this.auspiciosServices.consultaBeneForAgg(filter);
		}catch(AuspicioException a) {
			return ResponseEntity.internalServerError().body(new BadResponse(a.getMessage()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse("Ha ocurrido un error, intente mas tarde :("));
		}
		
		return ResponseEntity.ok( bene);
	}
	
	@PostMapping("/representante/consulta/beneficiarios/porRepresentante")
	public  ResponseEntity<?> consultarBeneficiarioByRepre(@Valid @RequestBody BeneficiarioFilter filter,BindingResult result)throws Exception{
		
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		if(filter.getRepresentante().isEmpty()) {
			return ResponseEntity.badRequest().body(new BadResponse("representante no puede estar vacio"));
		}
		Object benes;
		
		try {
			benes= this.auspiciosServices.consultarBeneByRepre(filter);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( benes);
	}
	@PostMapping("/representante/consulta/beneficiarios/titulos")
	public  ResponseEntity<?> consultarBeneficiarioTitles(@RequestParam String identificacion)throws Exception{
		List<?> objects;
		
		try {
			objects= this.auspiciosServices.consultarTitulos(identificacion);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( objects);
	}
	
	@PostMapping("/representante/consulta/beneficiarios/valoracion")
	public  ResponseEntity<?> consultarValoracion(@RequestParam String identificacion)throws Exception{
		Object object;
		
		try {
			object= this.auspiciosServices.consultarValoracion(identificacion);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( object);
	}
	
	@PostMapping("/representante/consulta/auspicio/recompensas")
	public  ResponseEntity<?> consultarRecompensas(@RequestParam String numeroAuspicio)throws Exception{
		Long ide=null;
		try {
		ide= Long.parseLong(numeroAuspicio);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse("Error en el formato"));
		}	
		Object object;
		
		try {
			object= this.auspiciosServices.consultarRecompensas(ide);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( object);
	}
	
	@PostMapping("/representante/consulta/auspicio/torneos")
	public  ResponseEntity<?> consultarTorneos(@RequestParam String numeroAuspicio)throws Exception{
		
		Long ide=null;
		try {
		ide= Long.parseLong(numeroAuspicio);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse("Error en el formato"));
		}	
		Object object;
		
		try {
			object= this.auspiciosServices.consultarTorneos(ide);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		
		return ResponseEntity.ok( object);
	}
	
	@PostMapping("/loquesea2")
	public  ResponseEntity<?> pedos()throws Exception{
		Object obj=null;
		obj= this.auspiciosServices.setearPersonaAcuentaBancaria();
	return ResponseEntity.ok().body(new Response(""));
	}
}
