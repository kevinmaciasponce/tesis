package com.multiplos.cuentas.auspicios.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

@Data
@Valid
public class ValoracionRequest {
	
	private Long id;
	
	@NotNull(message = "identificacion {NotNull}")
	private String idBene;
	
	@NotNull(message = "año {NotNull}")
	private int anio;
	
	@NotNull(message = "calificación {NotNull}")
	private String calificacion;
	
	@NotNull(message = " fecha calificación {NotNull}")
	@JsonDeserialize(using = LocalDateDeserializer.class)  
	@JsonSerialize(using = LocalDateSerializer.class)  
	private LocalDate fechaCalificacion;
	
	@NotEmpty(message = "fecha caducidad {NotNull}")
	@JsonDeserialize(using = LocalDateDeserializer.class)  
	@JsonSerialize(using = LocalDateSerializer.class)  
	private LocalDate fechaCaducidad;
	
	@NotNull(message = "presupuesto aprobado {NotNull}")
	private BigDecimal presupuestoAprobado;
	
	@NotNull(message = "presupuesto recaudado {NotNull}")
	private BigDecimal presupuestoRecaudado;
	
	@NotNull(message = "bianual {NotNull}")
	private Boolean bianual;
	
	
	public void validar()throws Exception {
		if(this.fechaCalificacion.isAfter(LocalDate.now())) {
			throw new Exception(  "La fecha de calificacion no puede ser despues a la fecha actual");
		}
		if(this.anio<LocalDate.now().getYear()) {
			throw new Exception( "El año no puede ser menor que el actual");
		}
		if(this.fechaCaducidad.isBefore(LocalDate.now())) {
			throw new Exception(  "La fecha de caducidad no puede ser anterior a la fecha actual");
		}
		
		
	}

	
	
//	public LocalDate assigFecCalificacion() throws Exception{
//		
//		LocalDate fecha= null;
//		 DateTimeFormatter JEFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
////		try {
////			fecha= LocalDate.parse(this.fechaCalificacion,JEFormatter);
////			System.out.println(fecha);
////		}catch(Exception e){
////			throw new Exception("Formato erroneo en la fecha de calificacion");
////		}
//		return fecha;
//	}
//
//	public LocalDate assigFecCaducidad() throws Exception{
//		
//		LocalDate fecha= null;
//		 DateTimeFormatter JEFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		try {
//			fecha= LocalDate.parse(this.fechaCaducidad,JEFormatter);
//		}catch(Exception e){
//			throw new Exception("Formato erroneo en la fecha de caducidad");
//		}
//		return fecha;
//	}
}
