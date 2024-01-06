package com.multiplos.cuentas.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Formatter;
import java.util.HashMap;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.multiplos.cuentas.controllers.PromotorController;
import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.Factura;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.repository.FacturaRepository;
import com.multiplos.cuentas.services.ContificoService;
import com.multiplos.cuentas.services.EmpresaService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.ProyectoService;

@Service
public class ContificoServiceImpl implements ContificoService {

	private static final String serviceContifico = "https://api.contifico.com";
	private static final Logger LOG = LoggerFactory.getLogger(ContificoServiceImpl.class);
	private static String error = "Ha ocurrido un error, intente mas tarde";
	private EmpresaService empresaService;
	private FacturaRepository factRepository;
	private ProyectoService proyectoservice;
	private ParametroService parametroService;
	@Value("${contifico.Api.Key}")
	private String contificoApiKey;
	@Value("${contifico.Api.Token}")
	private String contificoApiToken;
	
	@Autowired
	public ContificoServiceImpl(EmpresaService empresaService, FacturaRepository factRepository,
			ProyectoService proyectoservice,ParametroService parametroService) {
		this.empresaService = empresaService;
		this.factRepository = factRepository;
		this.proyectoservice = proyectoservice;
		this.parametroService=parametroService;

	}


	@Override
	public Object consultarFacturaPorId(String id) throws Exception {
		RestTemplate rt = new RestTemplate();
		  ResponseEntity<String> exchange ;
		  String url = serviceContifico + "/sistema/api/v1/documento/"+id;
		  HttpHeaders headers = new HttpHeaders();
		  headers.setContentType(MediaType.APPLICATION_JSON);
		  headers.set( "Authorization", contificoApiKey );
		  HttpEntity<String> dataHttpEntity = new HttpEntity<>(headers);
		  try {
		 exchange = rt.exchange(url, HttpMethod.GET,
		    		dataHttpEntity, String.class);
		  } catch(HttpStatusCodeException e) {
			  return ResponseEntity.internalServerError().body(new BadResponse(error,e.getRawStatusCode()+"",null,e.getResponseBodyAsString()));
		 }
		return exchange.getBody();
	}


	public String generateNoFact() {
	String lastDoc = this.factRepository.LastNoDoc(PageRequest.of(0, 1));
	Integer noSeq;
	LOG.info(lastDoc+"");
		Formatter fmt = new Formatter();
		if(lastDoc==null) {
			noSeq=0;
		}else {
	 noSeq=Integer.parseInt(lastDoc)	;
	noSeq++;
		}
	LOG.info(noSeq+" sumando");
		fmt.format("%09d", noSeq);
	return fmt.toString() ;
	
	}
	
	
	
	public Object validarcalificacion(Empresa empresa) throws Exception {
		
		ParametroResponse resp= this.parametroService.findByParametroCodParametro("VALANIOEMPRESA");
		if( (LocalDate.now().getYear() -empresa.getCuenta().getPersona().getAnioInicioActividad())<=Integer.parseInt(resp.getValor())) {
			throw new CuentaException("La empresa no califica por motivo que tiene menos de 2 años de activida");
		}
		resp= this.parametroService.findByParametroCodParametro("VALVENTASEMPRESA");
		if(empresa.getDatosAnualActual().getVentasTotales().doubleValue()<Integer.parseInt(resp.getValor())) {
			throw new CuentaException("La empresa no califica por motivo que tiene menos de $25000 en ventas totales ");
		}
		return null;
	}
	
	
	@Override
	@Transactional
	public Object crearFactura(JSONObject fact, String idProyecto) throws Exception {
		Proyecto proyecto;
		
		RestTemplate rt = new RestTemplate();
		String url = serviceContifico + "/sistema/api/v1/documento/";
		Object response = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);	
		headers.set("Authorization", contificoApiKey);
		
		ResponseEntity<String> exchange = null;
//		try {		
		
			proyecto = this.proyectoservice.consultaProyecto(idProyecto);
			LOG.info("pryecto");
			if (proyecto == null) {
				throw new CuentaException("Proyecto no existe con id: " + idProyecto);
			}
			Cuenta cuentaClie= proyecto.getEmpresa().getCuenta();
			String numfact=generateNoFact();
			Formatter fmt = new Formatter();
			fmt.format("%03d", 1);
			fact.put("documento", fmt+"-"+fmt+"-"+numfact);
			HashMap<String,Object>clienteContifico= new HashMap<>();
			clienteContifico.put("tipo", "J");
			clienteContifico.put("nombre_comercial",proyecto.getEmpresa().getNombre());
			clienteContifico.put("telefonos", cuentaClie.getPersona().getNumeroCelular());
			clienteContifico.put("ruc", cuentaClie.getPersona().getIdentificacion());
			clienteContifico.put("razon_social", cuentaClie.getPersona().getRazonSocial());
			clienteContifico.put("direccion", proyecto.getEmpresa().getDireccion());
			clienteContifico.put("es_cliente", true);
			fact.put("cliente", clienteContifico);
			fact.put("pos", contificoApiToken);
			HttpEntity<String> dataHttpEntity = new HttpEntity<>(fact.toString(),headers);
			exchange = rt.postForEntity(url, dataHttpEntity, String.class);
			
			JSONObject jsonObject = new JSONObject(exchange.getBody());
			String codFact = jsonObject.getString("id");
			String numDoc = jsonObject.getString("documento");
			//String urlSri= serviceContifico +"/sistema/api/v1/documento/"+codFact+"/sri/";
		//	rt.put(urlSri,  String.class);
			BigDecimal totalFact=new BigDecimal( jsonObject.getString("total"));
			LOG.info(codFact);
			Factura factura = Factura.builder().codFact(codFact).totalFactura(totalFact).numeroFacturero(fmt.toString()).numeroEstablecimiento(fmt.toString()).numeroDoc(numfact).idCliente(proyecto.getEmpresa().getCuenta().getPersona().getIdentificacion()).build();
			this.factRepository.save(factura);
			response = empresaService.enviarMailPagoFactura1(proyecto, codFact, numDoc, "PRIMERA_FACTURAPROM");
//		} catch (HttpStatusCodeException e) {
//			return ResponseEntity.internalServerError()
//					.body(new BadResponse(error, e.getRawStatusCode() + "", e.getMessage(), e.getResponseBodyAsString()));
//		}
		validarcalificacion(proyecto.getEmpresa());
		return response;
	}

}
