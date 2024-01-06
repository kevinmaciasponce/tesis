export interface detailsIntransitInterface {
    usuario: String,
    codigoProyecto: String,
    montoSolicitado:number,
    montoAprobado: number,
    observacion: String,
    solicitudes: [
        {
            numeroSolicitud: number
        }
        
    ]

}