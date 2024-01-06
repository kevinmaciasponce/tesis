package com.multiplos.cuentas.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_menu", schema = "cuenta")
public class Menu implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_menu")
	private Long idMenu;
	
	@Column(name = "nombre", length = 50, unique = true, nullable = false)
	private String nombre;
	
	@Column(name = "descripcion", length = 150, nullable = false)
	private String descripcion;
	
	@Column(name = "url", length = 250)
	private String url;
	
	@Column(name = "url_icono", length = 250, nullable = true)
	private String urlIcono;
	
	@Column(name = "orden", nullable = false)
	private int orden;
	
	@Column(name = "fecha_creacion", nullable = false)
	private Date fechaCreacion;
	
	@Column(name = "estado", length = 1, nullable = false)
	private String estado;
	
    @OneToMany
    @JoinColumn(name = "id_padre")
    private List<Menu> subMenu;
    
    @JsonIgnoreProperties(value = {"menu","rol"}, allowSetters = true)
	@OneToMany(mappedBy = "menu")
    private List<MenuOperacion> menuOperaciones;

}
