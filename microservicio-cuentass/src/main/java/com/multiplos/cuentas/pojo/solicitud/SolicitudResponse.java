package com.multiplos.cuentas.pojo.solicitud;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.pojo.amortizacion.filter.TblAmortizacionResponse;
import com.multiplos.cuentas.pojo.formulario.FormDatosIngresadoResponse;
import com.multiplos.cuentas.pojo.persona.DocIdentificacionResponse;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;

import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@SuppressWarnings("javadoc")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudResponse implements Serializable {
	
	private static final long serialVersionUID=2599368614755368860L;
	

	private Long numeroSolicitud;
	
	private String identificacion;
	
	private String nombreEmpresa;
	
	private BigDecimal inversion;
	
	private int plazo;
	
	private String estado;
	
	private String codProyecto;
	
	private BigDecimal montoPago;
	
	private BigDecimal montoProyecto;
	
	
	
	//private PersonaResponse persona;
	//private TblAmortizacionResponse datosAmortizacion;
//	private List<DocIdentificacionResponse> comprobantesPagos;
//	private FormDatosIngresadoResponse datosInversionIngresados;
}
