package com.multiplos.cuentas.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.multiplos.cuentas.models.Parametro;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;

@Component
public class ServicesUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServicesUtils.class);
	
	public List<ParametroResponse> getParametroResponse(List<Parametro> parametros){
		List<ParametroResponse> listResponse = new ArrayList<>();
		for (Parametro param : parametros) {
			ParametroResponse response = new ParametroResponse();
			response.setIdParametro(param.getIdParametro());
			response.setCodParametro(param.getCodParametro());
			response.setValor(param.getValor());
			response.setDescripcion(param.getDescripcion());
			
			listResponse.add(response);
		}
		return listResponse;
	}
	
	public String tipoTextoValidaCuenta(String tipo, String toEmail,String token, String url) throws IOException {
		String texto = null;
		
		texto = "Estimado usuario:<br><br>"+
				"Su cuenta se creo con el usuario: "+toEmail;
		
		if(tipo.contains("NORMAL")) {//Cuerpo de correo normal, solo mensaje de activacion 
			texto = texto + "<br><br>"+
							"Su cuenta ha sido activada.";
		}else {
			texto = texto + "<br><br>"+
							"Por favor, haga clic en el siguiente enlace para validar su cuenta: <br>"+
							url+"="+token;
		}
		texto = texto + this.saludoFinal();
		
		return texto;
	}
	
	public String saludoFinal() {
		return "<br><br><br>"+
			    "Saludos cordiales, <br>"+
			    "Administrador del Sistema.";
	}
	
	public ResponseEntity<?> validar(BindingResult result){
		Map<String, Object> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put("error", " El campo "+err.getDefaultMessage());
		});
		
		return ResponseEntity.badRequest().body(errores);
	}
	
	public Date configuraSumaDia(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Date nuevaFecha = new Date();
		LocalDate fechaConv = LocalDate.parse(format.format(fecha), formateador);
		fechaConv = fechaConv.plusDays(1);				
		try {
			nuevaFecha = format.parse(fechaConv.toString());
		} catch (ParseException e) {
			LOG.error("Error fecha "+e.getMessage());
		}
		return nuevaFecha;	
	}
	
	public String obtenerFechaFormateada(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate fechaConv = LocalDate.parse(format.format(fecha), formateador);
		return fechaConv.format(formateador);
	}

	public int calculaEdad(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fechaNac = LocalDate.parse(format.format(fecha), fmt);
		LocalDate ahora = LocalDate.now();
		
		Period periodo = Period.between(fechaNac, ahora);
		return periodo.getYears();
	}
	
	public LocalDate getLocalDate() {
		LocalDate currentDate = null;
		currentDate = LocalDate.now();
		return currentDate;
	}
	
	public LocalDateTime getLocalDateTime() {
		LocalDateTime currentDateTime = null;
		currentDateTime = LocalDateTime.now();
		return currentDateTime;
	}
}
