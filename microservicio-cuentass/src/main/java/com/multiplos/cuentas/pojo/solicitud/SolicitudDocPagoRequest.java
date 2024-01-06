package com.multiplos.cuentas.pojo.solicitud;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SolicitudDocPagoRequest {

	private Long numeroSolicitud;
	private String codProyecto;
	private String depositante;
	private Long formaPago;
	private String numeroComprobante;
	private String fechaTransaccion;
	private BigDecimal monto;
	private String aceptaLicitudFondos;
	private String aceptaInformacionCorrecta;
	private String aceptaIngresarInfoVigente;
	
}
