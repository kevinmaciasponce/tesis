package com.multiplos.cuentas.services.impl;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.FormContact;
import com.multiplos.cuentas.models.PlantillaEmail;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.repository.FormContactRepository;
import com.multiplos.cuentas.services.EmailSenderService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.FormContactService;
import com.multiplos.cuentas.services.TokenService;

import ch.qos.logback.classic.Logger;


@Service
public class FormContactServiceImpl implements FormContactService {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(FormContactServiceImpl.class);
	private FormContactRepository repository;
	private EnvioEmailService envioEmailService;
	private EmailSenderService emailSenderService;
	@Autowired
    public FormContactServiceImpl(FormContactRepository repository, 
    		EnvioEmailService envioEmailService,
    		TokenService tokenService,
    		EmailSenderService emailSenderService) {
        this.repository = repository;
        this.envioEmailService= envioEmailService;
        this.emailSenderService= emailSenderService;
        
    }
	
	@Override
	@Transactional
	public String save(FormContact formulario) throws Exception {
		String msj = null;
		PlantillaEmail plantilla = new PlantillaEmail();
		String cabecera;
		String pie;
		String body;
		String bodySeteado = null;
		
		plantilla = envioEmailService.consultaPlantillaEmail("FORMCONTACT");
		cabecera = envioEmailService.extraeValorJson(plantilla.getCuerpo(),"cabecera");
		pie = envioEmailService.extraeValorJson(plantilla.getCuerpo(),"pie");
		body = envioEmailService.extraeValorJson(plantilla.getCuerpo(), "body");
		bodySeteado = body.replace("[nombres]", formulario.getNombres())
				.replace("[cedula]", formulario.getIdentificacion())
				.replace("[email]", formulario.getEmail())
				.replace("[telefono]", formulario.getTelefono())
				.replace("[ciudad]", formulario.getCiudad())
				.replace("[motivo]", formulario.getMotivo())
				.replace("[mensaje]", formulario.getMensaje());
			try {		
				TipoEstado status = new TipoEstado();
				status.setIdEstado("RC");
				formulario.setEstadoActual(status);
				
				if(cabecera!=null && bodySeteado!=null && pie!=null) {
					body = cabecera.concat(bodySeteado).concat(pie);
					emailSenderService.sendTextEmail(plantilla.getAsunto(), "servicioalcliente@multiplolenders.com", body);
					
					repository.save(formulario);
					msj = "Formulario enviado";
				}else {
					msj = "Error al enviar el email";
				}
				
	        } catch (Exception e) {
	           LOG.error("save: problema al crear la cuenta "+ e.getMessage()); 
	           return "No se pudo enviar el formulario";
	        }
		
		
		return msj;
	}
	
}
