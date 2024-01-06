import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AnalystService {
  
  private API_SERVER = environment.URL_BASE;
  constructor(private http: HttpClient) { }


  Consultaconfirmaranalyst(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/consultas/int/analista/porConfirmar';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaPendienteRevisionAnalyst(form: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   let formData = new FormData();
   formData.append('CodProyecto', form.codigoProyecto);
   formData.append('usuario', user.user.usuarioInterno);
   formData.append('observacion',form.observacion);

   let data:any;
   data={

   }
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/consultas/int/analista/enRevision';
    return this.http.post<any>(this.API_SERVER,data,httpOptions).pipe(
      catchError(this.handleError)
    );
  }


  Consultasolicitudxnumero(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/consultas/solicitud';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  ConsultadatosdePago(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/consultas/formularios/documentos/deposito';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  //solicitud/inversiones/consultas/proyectoPorEstados
  ConsultaFirmaContratoanalyst(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/consultas/int/analista/firmaContrato';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

    //solicitud/inversiones/consultas/proyectoPorEstados
    ConsultaVigente(form: any,user:User): Observable<any> {
      this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/consultas/int/analista/vigente';
      const headers: any = {
        "Authorization": 'Bearer ' +user.token
      };
     //Post options pass it to HttpHeaders Class 
      const httpOptions = {
         headers: new HttpHeaders(headers),
     };
      return this.http.post(this.API_SERVER, form,httpOptions).pipe(
        catchError(this.handleError)
      );
    }


  Consultaprobadoporaconfirmaranalyst(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/consultas/int/analista/aprobada';//
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  fechaGeneracionTablaAmortizacion(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/generacion/fechaAmortizacion';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  fechaActualizaTablaAmortizacion(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/generacion/fechaAmortizacion';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.put(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  generaTablaAmortizacion(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/generacion/fechaEfectiva';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }


  actualizaTablaAmortizacion(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/generacion/fechaEfectiva';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.put(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  cambiarEstadoFirmadeContrato(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/consultas/int/firmaDeContrato';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  actualizarTransaccion(formData: any,user: User){
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/procesos/int/analista/actualizarTransation';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.put(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  subirExcelService(datos: any,files:File,user: User){
    this.API_SERVER = environment.URL_BASE + 'multiplo/conciliaciones/EstadoCuenta/upload';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
   const formData = new FormData();
   formData.append('usuario', JSON.stringify(datos));
   formData.append('file',files);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  consultarExcelData(user: User){
    this.API_SERVER = environment.URL_BASE + 'multiplo/conciliaciones/consultarDatos';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.get(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  conciliarExcelData(user: User){
    this.API_SERVER = environment.URL_BASE + 'multiplo/conciliaciones/conciliarDatos';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.get(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  GuardarConciliacion(user: User){
    this.API_SERVER = environment.URL_BASE + 'multiplo/conciliaciones/aprobarDatos';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
   const formData = new FormData();
   formData.append('usuario', JSON.stringify(user.user.usuarioInterno));
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  cargarDatosxSolicitud(observaciones: any, nSolicitud:any,
    datosInversionista:File,contratoPrenda:File,
    modeloContrato:File,pagare:File,
    tablaAmortizacion:File,acuerdoUso:File,
    user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('datosInversionista',datosInversionista);
   formData.append('contratoPrenda',contratoPrenda);
   formData.append('modeloContrato',modeloContrato);
   formData.append('pagare',pagare);
   formData.append('tablaAmortizacion',tablaAmortizacion);
   formData.append('acuerdoUso',acuerdoUso);
   formData.append('observacion', observaciones);
   formData.append('numeroSolicitud', nSolicitud);
   formData.append('usuarioInterno', user.user.usuarioInterno);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(formData);
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/int/documentos/contratos';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)  
    );
  }

  aprobarSolicitudesaFirmaContrato(codProyect: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('codProyecto', codProyect);
   formData.append('usuarioInterno',user.user.usuarioInterno);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/int/proyectos/aprobar';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  tranferirFondos(form: any,file:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('file', file);
   formData.append('CodProyecto', form.codigoProyecto);
   formData.append('usuario', user.user.usuarioInterno);
   formData.append('observacion',form.observacion);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/procesos/int/analista/transferirFondos';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  registrarPagoInversionista(form: any,file:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('file', file);
   formData.append('numSolicitud', form.numSolicitud);
   formData.append('cuota',form.cuota);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/procesos/int/analista/registra/pagos/vigente';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }





  
private handleError(error: HttpErrorResponse) {
  if (error.status === 0) {
    // A client-side or network error occurred. Handle it accordingly.
    console.error('An error occurred:', error.error);
  } else {
    // The backend returned an unsuccessful response code.
    // The response body may contain clues as to what went wrong.
    console.error(
      `Backend returned code ${error.status}, body was: `, error.error);
  }
  // Return an observable with a user-facing error message.
  return throwError(() => new Error(error.error?.error? error.error.error : 'Ha ocurrido un error por favor intente más tarde.'));
}

}
