package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mult_roles_cuentas", schema = "cuenta")
@JsonIgnoreProperties(value = "cuenta, rol")

public class Roles implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
    private RolesKeys id;
	
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="cuentaIntKey",column=@Column(name="cuenta_int"))
//	    })
//	private RolesIntKey rint;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("rolKey")
	@JoinColumn(name = "rol", nullable = false)
	private Rol rol;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("cuentaKey")
	@JoinColumn(name = "cuenta", nullable = true)
	private Cuenta cuenta;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@MapsId("cuentaIntKey")
//	@JoinColumn(name = "cuenta_interna", nullable = true)
//	private CuentaInterno cuentaInterna;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuari_creacion", nullable = false)
	private CuentaInterno UsuarioCreacion;
	
	@Column(name = "fecha_creacion")
	private Date fechaCreacion;
	
	@PrePersist
	private void PrePersist() {
		this.fechaCreacion= new Date();
	}

}
