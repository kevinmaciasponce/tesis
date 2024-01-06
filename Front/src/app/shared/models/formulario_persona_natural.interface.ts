export interface formularioPersonaNaturalInterface
{
    //numeroSolicitud: Number,
    //tipoPersona: string,
    //tipoCliente: string,
    //usuario: string,
    //nombres: string,
    //apellidos: string,
    identificacion: string,
    numeroCelular: string,
    estadoCivil: string,
    sexo: string,
    nacionalidad: number,
    telefonoAdicional: string | null,
    fuenteIngresos: string,
    cargo: string,
    aceptaLicitudFondos: string,
    residenteDomicilioFiscal: string,
    paisDomicilioFiscal: number | null,
    aceptaInformacionCorrecta: string,
    tipoCuenta: {
        titularCuenta: string,
        bancoCuenta: number,
        tipoCuenta: string,
        numeroCuenta: string
    },
    domicilio: {
        pais: number,
        ciudad: number,
        direccion: string,
        numeroDomicilio: string
    }
}