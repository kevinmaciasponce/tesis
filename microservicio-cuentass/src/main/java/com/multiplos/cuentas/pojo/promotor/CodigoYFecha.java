package com.multiplos.cuentas.pojo.promotor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodigoYFecha {

	private String codigo;
	private LocalDateTime fecha;
	
}
