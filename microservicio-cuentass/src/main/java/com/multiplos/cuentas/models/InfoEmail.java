package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_info_emails", schema = "emails")
public class InfoEmail implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_email")
	private Long idEmail;
	
	@Column(name = "email", unique = true, length = 100, nullable = false)
	private String email;
	
	@Column(name = "enviado", length = 1, nullable = true)
	private String enviado;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
}
