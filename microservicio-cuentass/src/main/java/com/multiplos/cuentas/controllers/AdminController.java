package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.auspicios.controllers.AuspiciosController;
import com.multiplos.cuentas.auspicios.models.Categorias;
import com.multiplos.cuentas.auspicios.models.Disciplina;
import com.multiplos.cuentas.auspicios.models.Modalidad;
import com.multiplos.cuentas.auspicios.services.AuspiciosService;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Banco;
import com.multiplos.cuentas.models.Ciudad;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.FormaPago;
import com.multiplos.cuentas.models.Pais;
import com.multiplos.cuentas.models.PersonaInterna;
import com.multiplos.cuentas.models.RangoPago;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.RolesIntKeys;
import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;
import com.multiplos.cuentas.pojo.empleado.EmpleadoCuentaRequest;
import com.multiplos.cuentas.pojo.empleado.EmpleadoRequest;
import com.multiplos.cuentas.pojo.empleado.PersonaInternaResponse;
import com.multiplos.cuentas.pojo.persona.PersonaRequest;
import com.multiplos.cuentas.pojo.solicitud.filter.FilterIntSolicitudRequest;
import com.multiplos.cuentas.services.BancoService;
import com.multiplos.cuentas.services.CiudadService;
import com.multiplos.cuentas.services.ConciliacionesService;
import com.multiplos.cuentas.services.CuentaInternoService;
import com.multiplos.cuentas.services.FormaPagoService;
import com.multiplos.cuentas.services.PaisService;
import com.multiplos.cuentas.services.PersonaInternaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.services.RolInternoService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.services.TransaccionService;
import com.multiplos.cuentas.utils.ServicesUtils;

import lombok.val;

@RestController
@RequestMapping("${route.service.contextPath}")
public class AdminController {
	private static final Logger LOG = LoggerFactory.getLogger(AuspiciosController.class);
	private static String error="Ha ocurrido un error, intente mas tarde";
	private SolicitudService solicitudService;
	private TransaccionService transaccionService;
	private ConciliacionesService csvService;
	private ProyectoService proyectoService;
	private ServicesUtils utils;
	private AuspiciosService auspiciosServices;
	private PersonaInternaService empleadoServices;
	private RolInternoService rolInternoService;
	private BancoService bancoService;
	private CiudadService ciudadService;
	private PaisService paisService;
	private CuentaInternoService cuentaInternoService;
	private FormaPagoService formaPagoService;

	@Autowired
	public AdminController(SolicitudService solicitudService, TransaccionService transaccionService,
			ConciliacionesService csvService, ProyectoService proyectoService, ServicesUtils utils,
			AuspiciosService auspiciosServices,
			PersonaInternaService empleadoServices,
			RolInternoService rolInternoService,
			BancoService bancoService,
			CiudadService ciudadService,
			PaisService paisService,
			CuentaInternoService cuentaInternoService,
			FormaPagoService formaPagoService) {
		this.solicitudService = solicitudService;
		this.transaccionService = transaccionService;
		this.csvService = csvService;
		this.proyectoService = proyectoService;
		this.utils = utils;
		this.auspiciosServices = auspiciosServices;
		this.empleadoServices= empleadoServices;
		this.rolInternoService= rolInternoService;
		this.bancoService= bancoService;
		this.ciudadService=ciudadService;
		this.paisService=paisService;
		this.cuentaInternoService=cuentaInternoService;
		this.formaPagoService= formaPagoService;
		
	}
	  @GetMapping("/admin/lista/ciudades")
	    public ResponseEntity<List<CiudadResponse>> obtieneCiudades(){
			List<CiudadResponse> ciudad = new ArrayList<>(); 
			ciudad = ciudadService.findAll("A");
			if(ciudad.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(ciudad);
	    }
	
	
	@GetMapping("/admin/lista/disciplinas")
	public ResponseEntity<?> listarDisciplinas (){
		List<?> response= null;
		
		try {
			
			response= this.auspiciosServices.listarDisciplina(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/admin/lista/modalidades")
	public ResponseEntity<?> listarMoladidad (){
		List<?> response= null;
		
		try {
			response= this.auspiciosServices.listarModalidad(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	@GetMapping("/admin/lista/categorias")
	public ResponseEntity<?> listarCategoria (){
		List<?> response= null;
		
		try {
			response= this.auspiciosServices.listarCategorias(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	
	
	@GetMapping("/admin/lista/rol/interno")
	public ResponseEntity<?> listarRolInterno (){
		List<?> response= null;
		
		try {
			response= this.rolInternoService.consultarRolesInternos();
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/admin/lista/bancos")
	public ResponseEntity<?> listarBancos (){
		List<?> response= null;
		
		try {
			response= this.bancoService.findAll(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	@GetMapping("/admin/lista/pais")
	public ResponseEntity<?> listarPais(){
		List<?> response= null;
		
		try {
			response= this.paisService.findAll(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	@GetMapping("/admin/lista/ciudad")
	public ResponseEntity<?> listarCiudad(){
		List<?> response= null;
		
		try {
			response= this.ciudadService.findAll(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	@GetMapping("/admin/lista/formaPago")
	public ResponseEntity<?> listarFormaPago(){
		List<?> response= null;
		
		try {
			response= this.formaPagoService.findAll(null);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	@PostMapping("/admin/lista/rol/empleado")
	public ResponseEntity<?> listarRolEmpleado (@RequestParam String idCuenta){
		List<?> response= null;
		LOG.warn(idCuenta);
		try {
			response= this.rolInternoService.consultarRolesInternosByEmpleado(idCuenta);
		}catch(Exception e) {return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));}
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/admin/consultar/personaInterna")
	public ResponseEntity<?> getEmpleado(@Valid @RequestBody PersonaRequest rqst, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		List<PersonaInternaResponse> response=null;
		try {
			response = this.empleadoServices.getEmpleado(rqst);
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(new BadResponse(c.getMessage()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()) );
		}
		return ResponseEntity.ok().body(response);

	}
	
	

	@PutMapping("/admin/ingresar/personaInterna")
	public ResponseEntity<?> putPersonaInterna(@Valid @RequestBody EmpleadoRequest rqst, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		
		String response=null;
		try {
			response = this.empleadoServices.putEmpleado(rqst);
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(new BadResponse(c.getMessage()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()) );
		}
		return ResponseEntity.ok().body(new Response(response));

	}
	@PutMapping("/admin/ingresar/personaInterna/cuenta")
	public ResponseEntity<?> putPersonaInternaCuenta(@Valid @RequestBody EmpleadoCuentaRequest rqst, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		//rqst.setCuenta(null);
		Object response=null;
		try {
			response = this.empleadoServices.putCuentaEmpleado(rqst);
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(new BadResponse(c.getMessage()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()) );
		}
		return ResponseEntity.ok().body( new Response(response) );

	}
	@PutMapping("/admin/ingresar/personaInterna/cuenta/roles")
	public ResponseEntity<?> putPersonaInternaCuentaRoles(
			@RequestParam Long rol,
			@RequestParam String cuenta,
			@RequestParam String userCompose){
		RolesIntKeys rqst = new RolesIntKeys(rol,cuenta);
		//rqst.setCuenta(null);
		String response=null;
		try {
			response = this.empleadoServices.putCuentaEmpleadoRoles(rqst,userCompose);
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(new BadResponse(c.getMessage()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()) );
		}
		return ResponseEntity.ok().body(new Response(response));

	}
	


	@PutMapping("/admin/parametrizar/auspicios/disciplinas")
	public ResponseEntity<?> putDisciplinas(@Valid @RequestBody Disciplina dis, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {

			response = this.auspiciosServices.putDisciplina(dis);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}

	@PutMapping("/admin/parametrizar/auspicios/categorias")
	public ResponseEntity<?> putCategoria(@Valid @RequestBody Categorias cat, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {

			response = this.auspiciosServices.putCategoria(cat);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}

	@PutMapping("/admin/parametrizar/auspicios/modalidades")
	public ResponseEntity<?> putModalidad(@Valid @RequestBody Modalidad mod, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {

			response = this.auspiciosServices.putModalidad(mod);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PutMapping("/admin/ingresar/banco")
	public ResponseEntity<?> putBanco(@Valid @RequestBody Banco mod, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {

			response = this.bancoService.save(mod);
		} catch (CuentaException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse( e.getMessage()));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	@PutMapping("/admin/parametrizar/ciudad")
	public ResponseEntity<?> putCiudad(@Valid @RequestBody Ciudad mod, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {
		
			response = this.ciudadService.save(mod);
		} catch (CuentaException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse( e.getMessage()));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PutMapping("/admin/parametrizar/pais")
	public ResponseEntity<?> putCiudad(@Valid @RequestBody Pais mod, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {
		
			response = this.paisService.save(mod);
		} catch (CuentaException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse( e.getMessage()));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	@PutMapping("/admin/parametrizar/formaPago")
	public ResponseEntity<?> putCiudad(@Valid @RequestBody FormaPago mod, BindingResult result) {
		if (result.hasErrors()) {
			return this.utils.validar(result);
		}
		String response = null;
		try {
		
			response = this.formaPagoService.save(mod);
		} catch (CuentaException e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse( e.getMessage()));
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
			return ResponseEntity.badRequest()
					.body(new BadResponse("Ha ocurrido un error, intente mas tarde", e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PostMapping("/admin/eliminar/rol/empleado")
	public ResponseEntity<?> deleteRolEmpleado(@RequestParam String cuenta, @RequestParam String rol) {
		String response = null;
		try {
			response = this.cuentaInternoService.eliminarRolEmpleado(cuenta,Long.parseLong(rol));
		}catch(CuentaException c) {
			return ResponseEntity.internalServerError().body(new BadResponse(c.getMessage()));
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()) );
		}
		return ResponseEntity.ok(new Response(response));
	}
	
	@PostMapping("/admin/parametrizar/rangospagos")
	public ResponseEntity<?> parametrizarRangosPagos(@RequestBody RangoPago rango)throws Exception{
		Object response = null;
		response=this.proyectoService.GuardarRangoPago(rango);
		return ResponseEntity.ok().body(new Response(response));
	}
	
	
}
