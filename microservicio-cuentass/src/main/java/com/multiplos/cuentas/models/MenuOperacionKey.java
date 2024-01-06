package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class MenuOperacionKey implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "id_menu")
	private Long menuId;

    @Column(name = "id_operacion")
    private Long operacionId;
    
    @Column(name = "id_rol")
    private Long rolId;
}
