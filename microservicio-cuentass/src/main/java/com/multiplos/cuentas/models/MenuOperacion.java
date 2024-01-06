package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_menu_operacion", schema = "cuenta")
public class MenuOperacion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
    private MenuOperacionKey id;

    @ManyToOne
    @MapsId("menuId")
    @JoinColumn(name = "id_menu")
    private Menu menu;

    @ManyToOne
    @MapsId("operacionId")
    @JoinColumn(name = "id_operacion")
    private Operacion operacion;
    
   
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId")
    @JoinColumn(name = "id_rol")
    private Rol rol;
    
    
}
