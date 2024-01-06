package com.multiplos.cuentas.auspicios.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class TitulosDetalleRequest {

		@NotNull(message = "disciplina {NotNull}")
		private Long idDisciplina;
		
		private Long idTitulo;
		@NotNull(message = "año titulo {NotNull}")
		private int anioTitulo;
		@NotBlank(message = "nombre de competencia {NotNull}")
		@Size(min=0, max=50 , message = "nombre de la competencia debe tener entre 0 y 50 caracteres")
		private String nombreCompetencia;
		@NotNull(message = "rankin nacional {NotNull}")
		@Size(min=0, max=50 , message = "ranking Nacional debe tener entre 0 y 50 caracteres")
		private String rankingNacional;
		@NotNull(message = "ranking internacional {NotNull}")
		@Size(min=0, max=50 , message = "ranking Internacional debe tener entre 0 y 50 caracteres")
		private String rankingInternacional;
		@NotNull(message = "otros {NotNull}")
		@Size(min=0, max=50 , message = "otros debe tener entre 0 y 50 caracteres")
		private String otros;
	
}
