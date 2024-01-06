package com.multiplos.cuentas.auspicios.controllers;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.multiplos.cuentas.auspicios.models.AuspicioException;
import com.multiplos.cuentas.auspicios.models.AuspicioFilter;
import com.multiplos.cuentas.auspicios.models.AuspicioRequest;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFormRequest;
import com.multiplos.cuentas.auspicios.models.Categorias;
import com.multiplos.cuentas.auspicios.models.Disciplina;
import com.multiplos.cuentas.auspicios.models.Modalidad;
import com.multiplos.cuentas.auspicios.models.RecompensasAllow;
import com.multiplos.cuentas.auspicios.models.TitulosRequest;
import com.multiplos.cuentas.auspicios.models.TorneosAllow;
import com.multiplos.cuentas.auspicios.models.ValoracionRequest;
import com.multiplos.cuentas.auspicios.services.AuspiciosService;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class AnalistaAuspicioController {
	private static final Logger LOG = LoggerFactory.getLogger(AnalistaAuspicioController.class);
	AuspiciosService auspiciosServices;
	private ServicesUtils utils;
	
	@Autowired
	public AnalistaAuspicioController(AuspiciosService auspiciosServices,
			ServicesUtils utils) {
		this.auspiciosServices=auspiciosServices;
		this.utils=utils;
	}
	
	
	@PostMapping("/analistaAuspicio/consultar/auspicios/filter")
	public ResponseEntity<?> consultaAuspiciosPorConfirmar (@RequestBody AuspicioFilter filter){
		List<?> response= null;
		if (filter.getEstado().isBlank()) {
			filter.setEstado("PC");
		}
		try {
			response= (List<?>) this.auspiciosServices.consultarAuspiciosPorEstado(filter);
	 	}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	
	
	@PostMapping("/analistaAuspicio/proceso/auspicios/aprobarVigente")
	public ResponseEntity<?> aprobarAuspiciosVigente (@RequestParam String numAus,@RequestParam String usuario,@RequestParam String observacion){
		Object response= null;
		
		try {
			response=  this.auspiciosServices.actualizaEstadoAuspicio(Long.parseLong(numAus),usuario,"VG",observacion);
	 	}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(new Response(response.toString()));
	}
	@PostMapping("/analistaAuspicio/proceso/auspicios/aprobarProximamente")
	public ResponseEntity<?> aprobarAuspiciosProximamente (@RequestParam String numAus,@RequestParam String usuario,@RequestParam String observacion){
		Object response= null;
		
		try {
			
			response=  this.auspiciosServices.actualizaEstadoAuspicio(Long.parseLong(numAus),usuario,"PX",observacion);
	 	}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(new Response(response.toString()));
	}
	
	@PostMapping("/analistaAuspicio/proceso/auspicios/anular")
	public ResponseEntity<?> anularAuspiciosPorConfirmar (@RequestParam String numAus,@RequestParam String usuario,@RequestParam String observacion){
		Object response= null;
		
		try {
			response=   this.auspiciosServices.actualizaEstadoAuspicio(Long.parseLong(numAus),usuario,"ANU",observacion);
	 	}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(new Response(response.toString()));
	}
	
	
	
	
	
	
	
	
	@PutMapping("/analistaAuspicio/modificar/beneficiarios")
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
	@PutMapping("/analistaAuspicio/modificar/beneficiario/titulos")
	public ResponseEntity<?> putTitulos(@Valid @RequestBody TitulosRequest request ,BindingResult result)throws Exception{
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
	
	@PutMapping("/analistaAuspicio/modificar/auspicios/valoracion")
	public ResponseEntity<?> modificarAuspicio (@RequestParam String request ,@RequestParam MultipartFile file,@RequestParam String usuario)throws Exception{
		
		ValoracionRequest rq=new ValoracionRequest();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			rq = objectMapper.readValue(request, ValoracionRequest.class);
			if(rq.getIdBene().isBlank()) {
				return ResponseEntity.badRequest().body(new BadResponse("Identificacion de beneficiario no puede estar vacio"));
			}
		} catch (UnrecognizedPropertyException e) {
			
			return ResponseEntity.badRequest().body(new BadResponse("Campo erroneo "+e.getPropertyName() +" no se reconoce"));
		}
		Object response= null;
		
		try {
			response= this.auspiciosServices.aggValoracion(rq,file);
	 	}catch(NumberFormatException e) {return ResponseEntity.internalServerError().body("Error en el formato de numero");}
		catch(NonUniqueResultException e) {return ResponseEntity.internalServerError().body(new BadResponse("Inconsistencia, tiene 2 valoraciones activas"));}
		catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		
		return ResponseEntity.ok(new Response(response.toString()));
	}
	@PutMapping("/analistaAuspicio/modificar/beneficiarios/auspicio")
	public ResponseEntity<?> aggAuspicio(@Valid @RequestBody AuspicioRequest request,BindingResult result)throws Exception{
		if(request.getNumeroAuspicio()==null) {
			return ResponseEntity.badRequest().body(new BadResponse("Numero de Auspicio no puede estar vacio"));
		}
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response= null;
		try {
		response= this.auspiciosServices.aggAuspicio(request,"PC");
		}catch(NonUniqueResultException u) {
			return ResponseEntity.internalServerError().body(new BadResponse("Inconsitencia, existen mas de 2 auspicios en proceso"));
		}catch(Exception e) {
			
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PutMapping("/analistaAuspicio/modificar/torneos/auspicio")
	public ResponseEntity<?> putTorneos(@Valid @RequestBody TorneosAllow request,BindingResult result)throws Exception{
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		if(request.getNumeroAuspicio()==null) {
			return ResponseEntity.badRequest().body(new BadResponse("Numero de Auspicio no puede estar vacio"));
		}
		String response= null;
		try {
		response= this.auspiciosServices.aggTorneos(request);
		}catch(DataIntegrityViolationException t) {
			LOG.warn(""+t.getCause());
			return ResponseEntity.internalServerError().body(new BadResponse("Error, informacion no valida"));
		}
		catch(Exception e) {
			LOG.warn(""+e);
			return ResponseEntity.internalServerError().body(new BadResponse("Ha ocurrido un error, intente mas tarde"));
		}
		return ResponseEntity.ok(new Response(response));
	}
	@PutMapping("/analistaAuspicio/modificar/recompensas/auspicio")
	public ResponseEntity<?> aggAuspicio(@Valid @RequestBody RecompensasAllow request,BindingResult result){
		if(result.hasErrors()) {
			return this.utils.validar(result);
		}
		if(request.getNumeroAuspicio()==null) {
			return ResponseEntity.badRequest().body(new BadResponse("Numero de Auspicio no puede estar vacio"));
		}
		String response= null;
		try {
		response= this.auspiciosServices.aggRecompensas(request);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	

}
