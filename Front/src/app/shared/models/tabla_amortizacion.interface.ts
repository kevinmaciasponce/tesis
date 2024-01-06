export interface Tabla_amortizacionInterface{
    cliente: string;
    pais: string;
    nombreProyecto: string;
    codigoProyecto: string;
    inversion: string;
    fechaGeneracion: string;
    rendimientoNeto: string;
    rendimientoTotal: string;
    totalRecibir: string;
    numeroSolicitud: string;
    detalle: [
        detalleCobro: string,
        rendimientoMensual: string,
        saldoCapital: string,
        cobrosCapital: string,
        totalRecibir: string
    ]
    plazo: string;
}