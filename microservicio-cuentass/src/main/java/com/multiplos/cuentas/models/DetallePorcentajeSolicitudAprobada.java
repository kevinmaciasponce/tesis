package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_detalle_porc_sol_aprobadas", schema = "promotor")
public class DetallePorcentajeSolicitudAprobada implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long idDetalle;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_porc_sol_aprobada", nullable = false)
    private PorcentajeSolicitudAprobada porcentajeSolicitudAprobada;

	@Column(name = "numero_solicitud",length = 20, nullable = false)
	private String numeroSolicitud;
	
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;
	
	@Column(name = "estado",length = 1, nullable = false)
	private String estado;
	
	@PrePersist
	public void prePersist() {
		this.estado = "A";
	}
}
