package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_plantillas_emails", schema = "emails")
public class PlantillaEmail implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_plantilla", length = 20, nullable = false)
	private String idPlantilla;
	
	@Column(name = "nombre", unique = true, length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "asunto", length = 100, nullable = false)
	private String asunto;
	
	@Column(name = "cuerpo", columnDefinition = "json")
	@JsonRawValue
	private String cuerpo;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
}
