export interface datos_ProcesoInterface{
codigoProyecto: string;
comprobantesPagos: string;
datosAmortizacion: null,
datosInversionIngresados:[
    documentacion: boolean,
    formulario: boolean,
    pago: boolean,
    tablaAmortizacion: boolean ,
],
estadoActual:[
    descripcion: string,
    detalle: string,
    estado: string,
    idEstado: string,
],
estadoAnterior: [
    fechaCreacion: string,
    nombreProyecto: string,
    numeroSolicitud: string,
    observacion?: string,
],
persona:[
    anioInicioActividad: number,
    apellidos: string,
    cargoContacto: string,
    datosFormulario: string,
    email?: string,
    emailContacto?: string,
    fechaNacimiento?: string,
    identificacion?: string,
    nacionalidad?: string,
    nombreContacto?: string,
    nombres?: string,
    numeroCelular?: string,
    razonSocial?: string,
    tipoCliente?: string,
    tipoContacto?: string,
    tipoIdentificacion?: string,
    tipoPersona?: string,
    usuario?: string,
    usuarioContacto?: string,
]
tipoContacto: string;
tipoPersona:  string;
tipoTransaccion: string;
usuario:  string;
usuarioContacto:  string;
usuarioCreacion:  string;
}
