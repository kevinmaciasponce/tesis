package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_historial_conciliacion", schema = "historicas")
public class HistorialConciliacion implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "usuario", length = 50, nullable = false)
	private String usuario;
	
	@Column(name = "fecha", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	private Date fecha;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_file", nullable = false, unique = false)
	private ConciliacionXls idFile;
	
	@JsonIgnoreProperties(value = {"id"}, allowSetters = true)
	@OneToMany(mappedBy = "id", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<HistorialConciliacionDetalle> detalleHistorial;
	
	@PrePersist
	public void prePersist() {
		this.fecha=new Date();
	}
}