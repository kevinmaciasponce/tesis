package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mult_roles_cuentas_internas", schema = "multiplo")
public class RolesInternos implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
    private RolesIntKeys id;
	
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="cuentaIntKey",column=@Column(name="cuenta_int"))
//	    })
//	private RolesIntKey rint;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("rolKey")
	@JoinColumn(name = "rol", nullable = false)
	private RolInt rol;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("cuentaInternaKey")
	@JoinColumn(name = "cuenta_interna", nullable = true)
	private CuentaInterno cuentaInterna;
	
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
