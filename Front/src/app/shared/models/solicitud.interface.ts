export interface SolicitudInterface {
    numeroSolicitud: string,
    tipoPersona: string,
    usuario: string,
    codigoProyecto: string,
    nombreProyecto: string,
    tipoTransaccion: string,
    tipoContacto: string,
    usuarioContacto: string,
    observacion: null,
    estadoActual: {
        idEstado: string,
        descripcion: string,
        estado: string,
        detalle: string,
    },
    estadoAnterior: {
        idEstado: string,
        descripcion: string,
        estado: string,
        detalles: string,
    },
    fechaCreacion: string,
    usuarioCreacion: string,
    persona: {
        identificacion: string,
        tipoCliente: string,
        tipoPersona: string,
        tipoIdentificacion: string,
        nacionalidad: string,
        nombres: string,
        apellidos: string,
        fechaNacimiento: string,
        numeroCelular: string,
        razonSocial: string,
        nombreContacto: string,
        cargoContacto: string,
        emailContacto: string,
        anioInicioActividad: number,
        email: string,
        usuario: string,
        tipoContacto: string,
        usuarioContacto: string,
        datosFormulario: {
            estadoCivil: string,
            sexo: string,
            numeroTelefono: string,
            fuenteIngresos: string,
            cargoPersona: string,
            residenteDomicilioFiscal: string,
            paisDomicilioFiscal: string,
            personaDomicilio: {
                pais: {
                    id: number,
                    pais: string,
                },
                ciudad: {
                    id: number,
                    ciudad: string,
                },
                direccion: string,
                numeroDomicilio: string,
                sector: string,
            },
            personaCuenta: {
                idPersCuenta: number,
                titular: string,
                banco: {
                    idBanco: number,
                    nombre: string,
                    estado: string,
                },
                tipoCuenta: string,
                numeroCuenta: string,
                estado: string,
            },
            personaEstadoFinanciero: string,
            personaRepresentanteLegal: string,
            personaFirmaAutorizada: string,
            personaDocumento: {
                nombre: string,
                ruta: string,
            }
        }
    },
    datosAmortizacion: {
        id: number,
        fechaEfectiva: string,
        fechaGeneracion: string,
        montoInversion: number,
        plazo: number,
        rendimientoNeto: number,
        rendimientoTotalInversion: number,
        totalRecibir: number,
    },
    comprobantesPagos: [
        {
            nombre: string,
            ruta: string,
        }
    ],
    datosInversionIngresados: {
        tablaAmortizacion: boolean,
        formulario: boolean,
        documentacion: boolean,
        pago: boolean,
    }
}