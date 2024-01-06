package com.multiplos.cuentas.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RolesKeys implements Serializable {
	
		private static final long serialVersionUID = 1L;
		
		
		@Column(name = "rol", nullable = false)
		private Long rolKey;

		
		@Column(name = "cuenta", nullable = true)
		private String cuentaKey;
		
	
//	

}
