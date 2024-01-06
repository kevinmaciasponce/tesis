export interface Tabla_amortizacionInterface{
    cliente: string;
    pais: string;
    nombreProyecto: string;
    codigoProyecto: string;
    montoInversion: string;
    fechaGeneracion: string;
    rendimientoNeto: string;
    rendimientoTotalInversion: string;
    totalRecibir: string;
    numeroSolicitud: string;
    fechaEfectiva: string;
    detallesTblAmortizacion: [
        detalleCobro: string,
        rendimientoMensual: string,
        saldoCapital: string,
        cobrosCapital: string,
        totalRecibir: string,
        fechaCobro: string,
        cuota: number
    ]
    plazo: string;
}