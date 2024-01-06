package com.multiplos.cuentas.pojo.promotor;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ProyectoFormulario {

	private String idProyecto;
//	private LocalDate fechaInicioInversion;
//	private LocalDate fechaLimiteInversion;

	@NotNull(message = "Tasa efectiva anual {NotNull}")
	private BigDecimal tasaEfectivaAnual;

	@Pattern(regexp = "[A-Za-z_ ]+", message = "Destino financiamiento {Pattern.letras}")
	@NotBlank(message = "Destino financiamiento {NotEmpty}")
	@Size(min=3,max = 100,message = "Destino financiamiento debe tener entre 3 y 100 caracteres")
	private String destinoFinanciamiento;

	@NotNull(message = "Monto Solicitado {NotNull}")
	private BigDecimal montoSolicitado;

	@NotNull(message = "Plazo {NotNull}")
	private Integer plazo;

	@NotNull(message = "Periodo de pago {NotNull}")
	private Integer periodoPago;

	
	@NotBlank(message = "Pago intereses no puede ser vacio")
	@Size(min=3,max = 15,message = "Pago intereses debe tener entre 3 y 15 caracteres")
	private String pagoInteres;

	@Size(min=3,max = 50,message = "Pago capital debe tener entre 3 y 50 caracteres")
	@NotBlank(message = "Pago capital {NotEmpty}")
	private String pagoCapital;

	@NotNull(message = "Empresa {NotNull}")
	private Long idEmpresa;

//	@NotNull(message = "Calificacion {NotNull}")
	private Long idCalificacion;

	@Size(min=3,max = 50,message = "usuario debe tener entre 3 y 50 caracteres")
	@NotBlank(message = "usuariol {NotEmpty}")
	private String userCompose;

	
	@Pattern(regexp = "[A-Za-z_ ]+", message = "Tipo de inversion {Pattern.letras}")
	@NotBlank(message = "Tipo de inversion {NotEmpty}")
	private String tipoInversion;

}
