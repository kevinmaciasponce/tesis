package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_bitacora_procesos", schema = "historicas")
public class BitacoraProceso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "tipo",length = 5, nullable = false)
	private String tipo; //ERROR INFO
	
	@Column(name = "proceso", length = 50, nullable = false)
	private String proceso; //nombre proceso que se ejecuto
	
	@Column(name = "identificador", length = 25, nullable = true)
	private String identificador;//solicitud/idProyecto - opcional
	
	@Column(name = "fecha",nullable = false)
	private LocalDateTime fecha;
	
	@Column(name = "descripcion", length = 400, nullable = false)
	private String descripcion;
	
}
