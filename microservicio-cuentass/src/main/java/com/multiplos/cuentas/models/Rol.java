package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mult_roles", schema = "cuenta",
		indexes = {@Index(name="idx01_rol_nombreEstado", columnList = "nombre, estado")})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rol implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_rol")
	private Long idRol;
	
	@Column(name = "nombre",length = 50, unique = true, nullable = false)
	private String nombre;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
	@Column(name = "ruta", length = 250, nullable = true)
	private String ruta;
	
	@JsonIgnoreProperties(value = {"rol"}, allowSetters = true)
	@OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<MenuOperacion> menuOperaciones;
	
}
