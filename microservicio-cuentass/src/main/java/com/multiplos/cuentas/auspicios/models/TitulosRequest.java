package com.multiplos.cuentas.auspicios.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Data
public class TitulosRequest {
	@NotEmpty(message = "identificacion {NotNull}")
	private String identificacion;
	@NotNull(message = "representante {NotNull}")
	private String idRepre;
	
	@NotNull(message = "detalles {NotNull}")
	@Valid
	private List<TitulosDetalleRequest>titulos= new ArrayList<>(); ; 

	private String usuario;
	
	
//	@Data
//	public static class detalles{
//		@NotEmpty(message = "disciplina {NotNull}")
//		private Long idDisciplina;
//		@NotNull(message = "id titulo {NotNull}")
//		private Long idTitulo;
//		@NotEmpty(message = "año titulo {NotNull}")
//		private int anioTitulo;
//		@NotBlank(message = "nombre de competencia {NotNull}")
//		@Size(min=0, max=50 , message = "nombre de la competencia debe tener entre 0 y 50 caracteres")
//		private String nombreCompetencia;
//		@NotEmpty(message = "rankin nacional {NotNull}")
//		@Size(min=0, max=50 , message = "ranking Nacional debe tener entre 0 y 50 caracteres")
//		private String rankingNacional;
//		@NotEmpty(message = "ranking internacional {NotNull}")
//		@Size(min=0, max=50 , message = "ranking Internacional debe tener entre 0 y 50 caracteres")
//		private String rankingInternacional;
//		@NotEmpty(message = "otros {NotNull}")
//		@Size(min=0, max=50 , message = "otros debe tener entre 0 y 50 caracteres")
//		private String otros;
//	}
	
}



