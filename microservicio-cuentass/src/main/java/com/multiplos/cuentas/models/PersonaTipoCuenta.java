package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_pers_cuentas", schema = "cuenta")
public class PersonaTipoCuenta implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pers_cuenta")
	private Long idPersCuenta;
	
	@Column(name = "titular", length = 100, nullable = false)
	private String titular;
	
	@OneToOne
	@JoinColumn(name = "id_banco", nullable = false)
	private Banco banco;
	
	@OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "persona",  nullable = false, unique=true)
	private Persona persona;
	
	@Column(name = "tipo_cuenta", length = 10, nullable = false)
	private String tipoCuenta;
	
	@Column(name = "numero_cuenta", length = 20, nullable = false)
	private String numeroCuenta;

	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}

}
