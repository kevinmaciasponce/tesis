export interface PersonaJuridicaInterface {
    tipoCliente: string;
    tipoPersona: string;
    tipoIdentificacion: string;
    razonSocial: string;
    identificacion: string;
    nombreContacto: string;
    cargoContacto: string;
    emailContacto:String;
    email?: string;
    numeroCelular: string;
    nacionalidad: string;
    clave: string;
    tipoContacto: string;
    aceptaPoliticaPrivacidad: string;
    aceptaRecibirInformacion:string;
    aceptaTerminoUso: string;
    usuarioContacto?: string;
    usuarioCreacion:string;
}