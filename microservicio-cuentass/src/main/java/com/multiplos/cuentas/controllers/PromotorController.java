package com.multiplos.cuentas.controllers;


import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.Factura;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.pojo.promotor.EmpresaFormulario;
import com.multiplos.cuentas.pojo.promotor.EmpresaRequest;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;
import com.multiplos.cuentas.pojo.promotor.PromotorResponse;
import com.multiplos.cuentas.pojo.promotor.ProyectoFormulario;
import com.multiplos.cuentas.pojo.proyecto.ProyectoRequest;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.services.ContificoService;
import com.multiplos.cuentas.services.EmpresaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.utils.ServicesUtils;

import net.bytebuddy.asm.Advice.This;

@RestController
@RequestMapping("${route.service.contextPath}")
public class PromotorController {
	
	private static final String serviceContifico="https://api.contifico.com";
	private static final Logger LOG = LoggerFactory.getLogger(PromotorController.class);
	private static String error = "Ha ocurrido un error, intente mas tarde";
	private static String errorContifico = "Ha ocurrido un error con contifico, intente mas tarde";
	private EmpresaService empresaService;
	private ProyectoService proyectoService;
	private ServicesUtils utils;
	private HttpEntity<String> dataHttpEntity ;
	private HttpHeaders headers;
	private ContificoService contificoService;

	@Autowired
	@Lazy
	public PromotorController(
			EmpresaService empresaService,
			@Lazy ProyectoService proyectoService,
			ServicesUtils utils,
			HttpEntity<String> dataHttpEntity,
			HttpHeaders headers,
			ContificoService contificoService
			) {
		this.empresaService = empresaService;
		this.proyectoService = proyectoService;
		this.utils = utils;
		this.contificoService=contificoService;
		this.dataHttpEntity=dataHttpEntity;
		this.headers=headers;
		
	}


	@PostMapping("promotor/prueba/destino")
	public ResponseEntity<?> getDestinoPrueba(@RequestParam String idProyecto ){
		
		Object respon = null;
		try {
		respon=	proyectoService.findPrueba(idProyecto);
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		}
		return ResponseEntity.ok().body(respon);
		
	}
	
	
	@PostMapping("promotor/consultar/empresa")
	public ResponseEntity<?> getPromotor(@Valid @RequestBody EmpresaRequest filter, BindingResult result)
			throws Exception {
		if (result.hasErrors()) {
			return utils.validar(result);
		}
		EmpresaResponse promo = empresaService.getByfilter(filter);
		return ResponseEntity.ok(promo);
	}

	@GetMapping("promotor/consultar/representante/porEmpresa/{id}")
	public ResponseEntity<?> getRepresentante(@PathVariable String id) throws Exception {

		PromotorResponse promo = empresaService.getPromotor(id);
		return ResponseEntity.ok(promo);
	}

	@GetMapping("promotor/consultar/representante/{id}")
	public ResponseEntity<?> findRepresentante(@PathVariable String id) throws Exception {

		PromotorResponse promo = empresaService.findPromotor(id);
		return ResponseEntity.ok(promo);
	}
	
	@PostMapping("promotor/consultar/proyectos/filter")
	public ResponseEntity<?> getProyectos(@RequestBody ProyectoRequest filter) throws Exception {

		List<proyectosResponse> promo = (List<proyectosResponse>) this.proyectoService.getProyectoResponse(filter);
		return ResponseEntity.ok(promo);
	}

	@PutMapping("/promotor/ingresar/documentos/juridicos")
	public ResponseEntity<?> cargaDocumentos(@RequestParam String idEmpresa, @RequestParam String usuarioCompose,
			@RequestParam(required = false) MultipartFile escritura,
			@RequestParam(required = false) MultipartFile estatutosVigentes,
			@RequestParam(required = false) MultipartFile rucVigente,
			@RequestParam(required = false) MultipartFile nombramientoRl,
			@RequestParam(required = false) MultipartFile cedulaRl,
			@RequestParam(required = false) MultipartFile nominaAccionista,
			@RequestParam(required = false) MultipartFile identificacionesAccionista) {
		String response = null;

		List<MultipartFile> file = new ArrayList<>();
			file.add(escritura) ;
			file.add(estatutosVigentes) ;
			file.add(rucVigente) ;
			file.add(nombramientoRl) ;
			file.add(cedulaRl) ;
			file.add(nominaAccionista) ;
			file.add(identificacionesAccionista) ;
		try {
			response = (String) this.empresaService.CargarDocumentosJuridicos(Long.parseLong(idEmpresa), file,
					usuarioCompose);
		} catch (CuentaException e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(error, e.getMessage()));
		}

		return ResponseEntity.ok(new Response(response));
	}

	@PutMapping("/promotor/ingresar/documentos/financieros")
	public ResponseEntity<?> cargaDocumentosFinancieros(@RequestParam String idEmpresa,
			@RequestParam String usuarioCompose,
			@RequestParam(required = false) MultipartFile impuestoRentaAnioAnterior,
			@RequestParam(required = false) MultipartFile estadoFinancieroAnioAnterior,
			@RequestParam(required = false) MultipartFile estadoFinancieroActuales,
			@RequestParam(required = false) MultipartFile anexoCtsCobrar) {
		String response = null;
		List<MultipartFile> file = new ArrayList<>();
		file.add(impuestoRentaAnioAnterior) ;
		file.add(estadoFinancieroAnioAnterior);
		file.add(estadoFinancieroActuales);
		file.add(anexoCtsCobrar);

		try {
			response = (String) this.empresaService.CargarDocumentosFinancieros(Long.parseLong(idEmpresa), file,
					usuarioCompose);
		} catch (CuentaException e) {
			return ResponseEntity.badRequest().body(new BadResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new BadResponse(error, e.getMessage()));
		}

		return ResponseEntity.ok(new Response(response));
	}

	@PutMapping("promotor/ingresar/empresa")
	public ResponseEntity<?> putEmpresa(@Valid @RequestBody EmpresaFormulario filter, BindingResult result)
			throws Exception {
		if (result.hasErrors()) {
			return utils.validar(result);
		}
		
		Object response;
		try {
			filter.validaDatosAnual();
			response = empresaService.putEmpresa(filter);
		} catch (CuentaException e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error, e.getMessage()));
		}

		return ResponseEntity.ok(new Response(response));
	}

	@PutMapping("promotor/ingresar/proyecto")
	public ResponseEntity<?> putProyecto(@Valid @RequestBody ProyectoFormulario form, BindingResult result)
			throws Exception {
		if (result.hasErrors()) {
			return utils.validar(result);
		}
		Object response;
		try {
			response = proyectoService.putProyecto(form);
		} catch (CuentaException e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error, e.getMessage()));
		}
		return ResponseEntity.ok(new Response(response));
	}

	@PostMapping("promotor/consultar/documentos/juridicos")
	public ResponseEntity<?> getDocJur(@RequestParam String idEmpresa, @RequestParam String user) throws Exception {
		Object response;
		try {
			response = empresaService.getDocumentosJurResponse(Long.parseLong(idEmpresa), user);
		} catch (CuentaException e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error, e.getMessage()));
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping("promotor/consultar/documentos/financieros")
	public ResponseEntity<?> getDocFin(@RequestParam String idEmpresa, @RequestParam String user) throws Exception {
		Object response;
		try {
			response = empresaService.getDocumentosFinResponse(Long.parseLong(idEmpresa), user);
		} catch (CuentaException e) {
			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new BadResponse(error, e.getMessage()));
		}

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("promotor/consultar/facturas/porCedula")
	public ResponseEntity<?> findFacturasByCedula(@RequestParam String ced)throws Exception {
//		  RestTemplate rt = new RestTemplate();
//		  String url = serviceContifico + "/sistema/api/v1/documento?persona_identificacion="+ced;
//		  HttpHeaders headers = new HttpHeaders();
//		  headers.setContentType(MediaType.APPLICATION_JSON);
//		  headers.set( "Authorization", "FrguR1kDpFHaXHLQwplZ2CwTX3p8p9XHVTnukL98V5U");
//		  HttpEntity<String> dataHttpEntity = new HttpEntity<>(headers);
//		   
//		    ResponseEntity<String> exchange = rt.exchange(url, HttpMethod.GET,
//		    		dataHttpEntity, String.class);
		
		List<Factura> fact = (List<Factura>) this.empresaService.consultarFacturaPorCliente(ced);
		return ResponseEntity.ok(fact);
	}
	@PostMapping("promotor/consultar/facturas/porId")
	public ResponseEntity<?> findFacturaByNumero(@RequestParam String id)throws Exception {
		  Object  response = null;
			 try {
				 response= this.contificoService.consultarFacturaPorId(id);
			 }
			 catch(HttpStatusCodeException e) {
				  return ResponseEntity.internalServerError().body(new BadResponse(error,e.getRawStatusCode()+"",null,e.getResponseBodyAsString()));
			 }
			 catch(CuentaException e) {
				  return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
			 }
			 catch(Exception e) {
				  return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()));
			 }
			  return ResponseEntity.ok(response);
	}
	
//	@PostMapping("promotor/enviar/mail/1factura")
//	public ResponseEntity<?> sendMailPromotor1Fact(@RequestParam String idProyecto) throws Exception {
//		Object response = null;
//		try {
//			response = empresaService.enviarMailPagoFactura1(idProyecto,"PRIMERA_FACTURAPROM");
//		} catch (CuentaException e) {
//			return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
//		} catch (Exception e) {
//			return ResponseEntity.internalServerError().body(new BadResponse(error, e.getMessage()));
//		}
//
//		return ResponseEntity.ok(new Response(response));
//	}
	
	@PostMapping("promotor/generar/factura1")
	
	public ResponseEntity<?> generateFactura(@RequestParam String fact, @RequestParam String idProyecto)throws Exception {
		 
		  Object  response = null;
		
		 try {
			 JSONObject jsonFactura= new JSONObject(fact);
			 response= this.contificoService.crearFactura(jsonFactura,idProyecto);
		 }
		 catch(HttpStatusCodeException e) {
			 if(e.getResponseBodyAsString().isBlank()) {
				  return ResponseEntity.internalServerError().body(new BadResponse(errorContifico,e.getRawStatusCode()+"",null,e.getMessage().toString()));
			 }else {
				  return ResponseEntity.internalServerError().body(new BadResponse(errorContifico,e.getRawStatusCode()+"",null,e.getResponseBodyAsString()));
			 }
		 }
		 catch(CuentaException e) {
			  return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		 }
		 catch(Exception e) {
			  return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()));
		 }
		  return ResponseEntity.ok(new Response(response));
	}
	
		@PostMapping("promotor/registrar/pago/factura")
	
			public ResponseEntity<?> registrarPagoFactura(@RequestParam String pago, @RequestParam String codFact,
		@RequestParam MultipartFile file, @RequestParam String tipoDocumento)throws Exception {
		 
		  Object  response = null;
		
		 try {
			
			 response= this.proyectoService.guardaDocumentoComprobantePago(pago,codFact,file,tipoDocumento);
		 }
		 catch(HttpStatusCodeException e) {
			  return ResponseEntity.internalServerError().body(new BadResponse(error,e.getRawStatusCode()+"",null,e.getResponseBodyAsString()));
		 } catch(CuentaException e) {
			  return ResponseEntity.internalServerError().body(new BadResponse(e.getMessage()));
		 }
		 catch(Exception e) {
			  return ResponseEntity.internalServerError().body(new BadResponse(error,e.getMessage()));
		 }
		  return ResponseEntity.ok(new Response(response));
	}
	
	
}
