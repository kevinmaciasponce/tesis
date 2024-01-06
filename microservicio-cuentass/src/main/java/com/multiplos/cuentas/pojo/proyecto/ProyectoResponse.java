package com.multiplos.cuentas.pojo.proyecto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.multiplos.cuentas.models.Calificacion;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.Indicador;
import com.multiplos.cuentas.models.ProyectoRuta;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProyectoResponse {



	public ProyectoResponse(String codigoOperacion, LocalDate fechaInicioInversion, LocalDate fechaLimiteInversion,
			BigDecimal tasaEfectivaAnual, String tipoInversion, String destinoFinanciamiento,
			BigDecimal montoSolicitado, BigDecimal avanceInversion, int plazo, String pagoInteres, String pagoCapital,
			Indicador indicadores, Calificacion calificacion, String estado, Long idEmpresa) {
		super();
		this.codigoOperacion = codigoOperacion;
		this.fechaInicioInversion = fechaInicioInversion;
		this.fechaLimiteInversion = fechaLimiteInversion;
		this.tasaEfectivaAnual = tasaEfectivaAnual;
		this.tipoInversion = tipoInversion;
		this.destinoFinanciamiento = destinoFinanciamiento;
		this.montoSolicitado = montoSolicitado;
		this.avanceInversion = avanceInversion;
		this.plazo = plazo;
		this.pagoInteres = pagoInteres;
		this.pagoCapital = pagoCapital;
		this.indicadores = indicadores;
		this.calificacion = calificacion;
		this.estado = estado;
		this.idEmpresa = idEmpresa;
	}

	private String codigoOperacion;

	@JsonFormat(pattern="dd 'de' MMMM 'del' yyyy", locale = "es")
	private LocalDate fechaInicioInversion;
	@JsonFormat(pattern="dd 'de' MMMM 'del' yyyy", locale = "es")
	private LocalDate fechaLimiteInversion;
	private BigDecimal tasaEfectivaAnual;
	private String tipoInversion;
	private String destinoFinanciamiento;
	private BigDecimal montoSolicitado;
	private BigDecimal avanceInversion;
	private int plazo;
	private String pagoInteres;
	private String pagoCapital;
	private Indicador indicadores;
	private Calificacion calificacion;
	private String estado;
	private Long idEmpresa;
	
	
	private Collection<ProyectoRutaResponse> proyectoRutas;
	
	private EmpresaResponse empresa;
	
	
//	@JsonIgnoreProperties(value = {"cuenta"}, allowSetters = true)
	
//	@JsonIgnoreProperties(value = {"proyecto"}, allowSetters = true)
	


}
