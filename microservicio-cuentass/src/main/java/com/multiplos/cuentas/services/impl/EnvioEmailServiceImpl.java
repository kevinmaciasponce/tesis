package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.InfoEmail;
import com.multiplos.cuentas.models.PlantillaEmail;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoSolicitud;
import com.multiplos.cuentas.pojo.proyecto.ProyectoResponse;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.EmailSenderService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.InfoEmailService;
import com.multiplos.cuentas.services.PlantillaEmailService;
import com.multiplos.cuentas.services.TokenService;
import com.multiplos.cuentas.utils.ServicesUtils;

@Service
public class EnvioEmailServiceImpl implements EnvioEmailService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CuentaServiceImpl.class);
	
	private CuentaService cuentaService;
	private TokenService tokenService;
	private PlantillaEmailService plantillaEmailService;
	private InfoEmailService infoEmailService; 
	private EmailSenderService emailSenderService;
	
	@Autowired
    public EnvioEmailServiceImpl(CuentaService cuentaService, ServicesUtils utils, TokenService tokenService,
    		PlantillaEmailService plantillaEmailService,InfoEmailService infoEmailService,
    		EmailSenderService emailSenderService) {
        this.cuentaService = cuentaService;
        this.tokenService = tokenService;
        this.plantillaEmailService = plantillaEmailService;
        this.emailSenderService= emailSenderService;}
	@Transactional
	public String enviaEmails(String idPlantilla) throws Exception {
		String mensaje = null;
		PlantillaEmail plantillaEmail = new PlantillaEmail();
		
		plantillaEmail = this.consultaPlantillaEmail(idPlantilla);
		if(plantillaEmail != null) {
			if(idPlantilla.contains("SALUD_FINAN")) {
				mensaje = this.enviaEmailBaseRegistrada(plantillaEmail);
			}else if(idPlantilla.contains("OPORT_INVERSION")) {
				mensaje = this.enviaEmailUsuariosRegistrados(plantillaEmail);
			}
		}else {
			LOG.warn("enviaEmails: No existe plantilla");
			mensaje = "No existe plantilla";
		}
		
		return mensaje;
	}
	
	public String enviaEmailUsuariosRegistrados(PlantillaEmail plantillaEmail) {
		String mensaje;
		List<Cuenta> listCuentas = new ArrayList<>();
		String body;
		
		listCuentas = cuentaService.consultaCuentasRegistradas();
		if(listCuentas.isEmpty()) {
			LOG.info("enviaEmailUsuariosRegistrados: No se encontro emails registrados.");
			mensaje = "No se encontro emails registrados.";
		}else {
			body = this.extraeValorJson(plantillaEmail.getCuerpo(),"body");
			for(Cuenta c : listCuentas){
				try{
					emailSenderService.sendTextEmail(plantillaEmail.getAsunto(),  c.getEmail(), body);
				
				}catch(Exception ex) {
					LOG.warn("enviaEmailUsuariosRegistrados: Error envioMail "+c.getEmail());
				}	
			}
			mensaje = "ok";
		}
		return mensaje;
	}

	public String enviaEmailBaseRegistrada(PlantillaEmail plantillaEmail) {
		String mensaje;
		List<InfoEmail> listEmails = new ArrayList<>();
		List<Long> listIdEmailEnviado = new ArrayList<>();
		String body;
		
		listEmails = infoEmailService.consultaBaseEmails();
		if(listEmails.isEmpty()) {
			LOG.info("enviaEmailBaseRegistrada: No se encontro base de emails.");
			mensaje = "No se encontro base de emails.";
		}else {
			body = this.extraeValorJson(plantillaEmail.getCuerpo(),"body");
			for(InfoEmail e : listEmails){
				try{
					
					emailSenderService.sendTextEmail(plantillaEmail.getAsunto(), e.getEmail(), body);
					listIdEmailEnviado.add(e.getIdEmail());
				}catch(Exception ex) {
					LOG.warn("enviaEmailBaseRegistrada: Error envioMail "+e.getEmail());
				}
			}
			//actualiza emails enviados
			if(!listIdEmailEnviado.isEmpty()) {
				for(Long i : listIdEmailEnviado) {
					infoEmailService.actualizaEmailEnviado("S", i);
				}
				listIdEmailEnviado.clear();
			}
			mensaje = "ok";
		}
		return mensaje;
	}
	
	@Override
	@Transactional(readOnly = true)
	public PlantillaEmail consultaPlantillaEmail(String idPlantilla) throws Exception {
		return plantillaEmailService.consultaPlantillaEmail(idPlantilla);
	}
	
	@Override
	public String extraeValorJson(String json, String objeto) {
		String valor;
		JSONObject jsonObject = new JSONObject(json);
		valor = jsonObject.getString(objeto);
		return valor;
	}

	@Override
	@Transactional
	public String enviaEmailSolPorConfirmar(String idPlantilla, DatoSolicitud datosSolicitud, String toEmail) throws Exception {
		String msj = null;
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String pie;
		String body;
		String bodySeteado = null;
		
		plantilla = this.consultaPlantillaEmail(idPlantilla);
		if(plantilla != null) {
			
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			if(idPlantilla.contains("CN_ANALISTA")) {
				//envia al analista para la revision de la solicitud en estado por confirmar
				body = this.extraeValorJson(plantilla.getCuerpo(), "body");
				bodySeteado = body.replace("[IDENTIFICACION]", datosSolicitud.getIdentificacion())
						.replace("[INVERSIONISTA]", datosSolicitud.getInversionista())
						.replace("[SOLICITUD]", datosSolicitud.getNumeroSolicitud().toString())
						.replace("[EMPRESA]", datosSolicitud.getEmpresa())
						.replace("[CODPROYECTO]", datosSolicitud.getCodigoProyecto())
						.replace("[MONTO]", datosSolicitud.getMontoInversion().toString())
						.replace("[PLAZO]", String.valueOf(datosSolicitud.getPlazo()))
						.replace("[RENTABILIDAD]", datosSolicitud.getRentabilidad().toString());
				
			}else if(idPlantilla.contains("CN_INVERSIONISTA")) {
				//envia al inversionista con solicitud en estado por confirmar
				body = this.extraeValorJson(plantilla.getCuerpo(), "body");
				bodySeteado = body.replace("[SOLICITUD]", datosSolicitud.getNumeroSolicitud().toString())
						.replace("[EMPRESA]", datosSolicitud.getEmpresa())
						.replace("[CODPROYECTO]", datosSolicitud.getCodigoProyecto())
						.replace("[MONTO]", datosSolicitud.getMontoInversion().toString())
						.replace("[PLAZO]", String.valueOf(datosSolicitud.getPlazo()))
						.replace("[RENTABILIDAD]", datosSolicitud.getRentabilidad().toString());
			}
		
			try{
				if(cabecera!=null && bodySeteado!=null && pie!=null) {
					body = cabecera.concat(bodySeteado).concat(pie);
					emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
				
					msj = "ok";
				}else {
					msj = "Error al enviar el email";
				}
				
			}catch(Exception ex) {
				LOG.warn("enviaEmailSolPorConfirmar: Error envioMail "+ex.getMessage());
				return msj = "Error al enviar el Email";
			}	
		}else {
			LOG.warn("enviaEmailSolPorConfirmar: No existe plantilla ".concat(idPlantilla));
			msj = "Error no existe plantilla";
		}
		return msj;
	}

	@Override
	public String enviaEmailInvestor(String idPlantilla, String urlToken, String toEmail)throws Exception {
		String msj = null;
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String pie;
		String body;
		String bodySeteado = null;
		
		plantilla = plantillaEmailService.consultaPlantillaEmail(idPlantilla);
		LOG.info("plantilla encontrada");
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			bodySeteado = body.replace("[EMAIL]", toEmail)
								.replace("[TOKEN]", urlToken);

			LOG.info("plantilla seteada");
		if(cabecera==null || bodySeteado==null || pie==null) {
		throw new Exception ("Plantilla incompleta: ");
		}
		try{
			body = cabecera.concat(bodySeteado).concat(pie);
			emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
			LOG.info("Se envió un correo a su Email");
			msj = "Se envió un correo a su Email";	
		}catch(Exception ex) {
			LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
			throw new Exception ("enviaEmail: Error envioMail : "+ex);
		}
		
		return msj;
	}
	
	@Override
	public String enviaEmailPromotor(String idPlantilla, String url, String toEmail)throws Exception {
		String msj = null;
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String pie;
		String body;
		String bodySeteado = null;
		
		plantilla = this.consultaPlantillaEmail(idPlantilla);
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			bodySeteado = body.replace("[URL]", url);
		if(cabecera==null || bodySeteado==null || pie==null) {
		throw new Exception ("Plantilla incompleta: ");
		}
		try{
			body = cabecera.concat(bodySeteado).concat(pie);
			emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
			msj = "Se envió un correo a su Email";	
		}catch(Exception ex) {
			LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
			throw new Exception ("enviaEmail: Error envioMail : "+ex);
		}
		
		return msj;
	}
	
	@Override
	public String enviaEmailBeneficiario( String toEmail)throws Exception{
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String msj = null;
		String pie;
		String body;
		plantilla = this.consultaPlantillaEmail("AD_REG_BENEFICIARIO");
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			if(cabecera==null || body==null || pie==null) {
				throw new Exception ("Plantilla incompleta: ");
				}
				try{
					body = cabecera.concat(body).concat(pie);
					emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
					msj = "Se envió un correo a su Email";	
				}catch(Exception ex) {
					LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
					throw new Exception ("enviaEmail: Error envioMail : "+ex);
				}
		return null;
		
	}
	@Override
	public String enviaEmailAus_pc_representante( String toEmail)throws Exception{
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String msj = null;
		String pie;
		String body;
		plantilla = this.consultaPlantillaEmail("AUS_PC_REPRESENTANTE");
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			if(cabecera==null || body==null || pie==null) {
				throw new Exception ("Plantilla incompleta: ");
				}
				try{
					body = cabecera.concat(body).concat(pie);
					emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
					msj = "Se envió un correo a su Email";	
				}catch(Exception ex) {
					LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
					throw new Exception ("enviaEmail: Error envioMail : "+ex);
				}
		return msj;
		
	}
	@Override
	public String enviaEmailAus_pc_beneficiario( String toEmail)throws Exception{
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String msj = null;
		String pie;
		String body;
		plantilla = this.consultaPlantillaEmail("AUS_PC_BENEFICIARIO");
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			if(cabecera==null || body==null || pie==null) {
				throw new Exception ("Plantilla incompleta: ");
				}
				try{
					body = cabecera.concat(body).concat(pie);
					emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
					msj = "Se envió un correo a su Email";	
				}catch(Exception ex) {
					LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
					throw new Exception ("enviaEmail: Error envioMail : "+ex);
				}
		return msj;
		
	}
	@Override
	public String enviaEmailAus_pc_analista( String toEmail)throws Exception{
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String msj = null;
		String pie;
		String body;
		plantilla = this.consultaPlantillaEmail("AUS_PC_ANALISTA");
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			if(cabecera==null || body==null || pie==null) {
				throw new Exception ("Plantilla incompleta: ");
				}
				try{
					body = cabecera.concat(body).concat(pie);
					emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
					msj = "Se envió un correo a su Email";	
				}catch(Exception ex) {
					LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
					throw new Exception ("enviaEmail: Error envioMail : "+ex);
				}
		return msj;
		
	}
	
	@Override
	public String enviaEmailPagoCuotas(String idPlantilla, String numSolicitud,String nombreEmpresa, 
			String codProyecto,DetalleAmortizacion dt, String toEmail)throws Exception {
		String msj = null;
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String pie;
		String body;
		String bodySeteado = null;
		plantilla = this.consultaPlantillaEmail(idPlantilla);
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
			cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
			pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
			body = this.extraeValorJson(plantilla.getCuerpo(), "body");
			bodySeteado = body.replace("[SOLICITUD]", codProyecto)
								.replace("[EMPRESA]",numSolicitud)
								.replace("[CODPROYECTO]",nombreEmpresa)
								.replace("[FECHAPAGO]",dt.getFechaEstimacion().toString())
								.replace("[MONTO]",dt.getTotalRecibir()+"" );
		if(cabecera==null || bodySeteado==null || pie==null) {
		throw new Exception ("Plantilla incompleta: ");
		}
		try{
			body = cabecera.concat(bodySeteado).concat(pie);
			emailSenderService.sendTextEmail(plantilla.getAsunto(), toEmail, body);
			msj = "Se envió un correo a su Email";	
		}catch(Exception ex) {
			LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
			throw new Exception ("enviaEmail: Error envioMail : "+ex);
		}
		return msj;
	}
	
	@Override
	public String enviaEmailPromotor_registroProyecto (Proyecto proyecto,String idplantilla) throws Exception {
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String msj = null;
		String pie;
		String body;
		String bodySeteado;
		plantilla = this.consultaPlantillaEmail(idplantilla);
		if(plantilla == null) {
			throw new Exception ("La plantilla esta vacia ");
		}
		cabecera = this.extraeValorJson(plantilla.getCuerpo(),"cabecera");
		pie = this.extraeValorJson(plantilla.getCuerpo(),"pie");
		body = this.extraeValorJson(plantilla.getCuerpo(), "body");
		bodySeteado = body.replace("[CODPROYECTO]", proyecto.getIdProyecto())
				.replace("[EMPRESA]",proyecto.getEmpresa().getNombre())
				.replace("[RUC]",proyecto.getEmpresa().getRuc())
				.replace("[MONTOSOL]",proyecto.getMontoSolicitado()+"")
				.replace("[NOMBRECONT]",proyecto.getEmpresa().getNombre())
				.replace("[CORREOCONT]",proyecto.getEmpresa().getCuenta().getEmail())
				.replace("[PLAZO]",proyecto.getPlazo()+"")
				.replace("[TASA]", proyecto.getTasaEfectivaAnual()+"")
				;
		if(cabecera==null || bodySeteado==null || pie==null) {
			throw new Exception ("Plantilla incompleta: ");
			}
			try{
				bodySeteado = cabecera.concat(bodySeteado).concat(pie);
				emailSenderService.sendTextEmail(plantilla.getAsunto(), proyecto.getEmpresa().getCuenta().getEmail(), bodySeteado);
				msj = "Se envió un correo a su Email";	
			}catch(Exception ex) {
				LOG.warn("enviaEmailActivaCuenta: Error envioMail "+ex.getMessage());
				throw new Exception ("enviaEmail: Error envioMail : "+ex);
			}
	return msj;
	}
	

}
