package com.multiplos.cuentas.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mult_historial_conciliacion_detalle", schema = "historicas")
public class HistorialConciliacionDetalle {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Historial", nullable = false)
    private HistorialConciliacion idHistorial;

    @Column(name = "comprobante",length = 50, nullable = false)
    private String comprobante;
    
    @Column(name = "monto_file",length = 50, nullable = false)
    private BigDecimal montoFile;

    @Column(name = "monto_transaccion",length = 50, nullable = false)
    private BigDecimal montoTransaccion;
    
    @Column(name = "fecha_transaccion",length = 50, nullable = false)
    private LocalDate fechaTransaccion;

    @Column(name = "fecha_file",length = 50, nullable = false)
    private LocalDate fechaFile; 
    
    @Column(name = "observacion",length = 50, nullable = false)
    private String observacion; 

}
