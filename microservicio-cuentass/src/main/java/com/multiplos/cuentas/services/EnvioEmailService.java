package com.multiplos.cuentas.services;

import com.multiplos.cuentas.models.DetalleAmortizacion;
import com.multiplos.cuentas.models.PlantillaEmail;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.pojo.plantilla.solicitud.DatoSolicitud;
import com.multiplos.cuentas.pojo.proyecto.ProyectoResponse;

public interface EnvioEmailService {
	
	String extraeValorJson(String json, String objeto);
	PlantillaEmail consultaPlantillaEmail(String idPlantilla) throws Exception ;
	String enviaEmails(String idPlantilla)throws Exception;
	String enviaEmailSolPorConfirmar(String idPlantilla, DatoSolicitud datosSolicitud, String toEmail)throws Exception;
	String enviaEmailInvestor(String idPlantilla, String urlToken, String toEmail)throws Exception;
	String enviaEmailPagoCuotas(String idPlantilla, String numSolicitud,String nombreEmpresa, String codProyecto, DetalleAmortizacion dt, String toEmail)throws Exception;
	
	String enviaEmailPromotor(String idPlantilla, String url, String toEmail)throws Exception;
	String enviaEmailBeneficiario( String toEmail)throws Exception;
	
	String enviaEmailAus_pc_representante( String toEmail)throws Exception;
	String enviaEmailAus_pc_beneficiario( String toEmail)throws Exception;
	String enviaEmailAus_pc_analista( String toEmail)throws Exception;
	
	
	
	String enviaEmailPromotor_registroProyecto (Proyecto proyecto,String plantilla)throws Exception;
}
