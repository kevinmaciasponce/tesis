import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { environment } from 'src/environments/environment';
import { solicitud_tablaInterface } from '../models/solicitud_tablaInterface';
import { datos_ProcesoInterface } from '../models/tabla_amortizacion/datos_proceso.interaface';
import { Tabla_amortizacionInterface } from '../models/tabla_amortizacion/tabla_amortizacion.interface';

@Injectable({
  providedIn: 'root'
})
export class AmortizacionService {


  
  private API_SERVER = environment.URL_BASE;
  constructor(private http: HttpClient) { }

  

  ConsultaTabla(datos: solicitud_tablaInterface): Observable<Tabla_amortizacionInterface>{
    this.API_SERVER = environment.URL_BASE + 'multiplo/amortizacion/simulador';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Parametros')[0];
    return this.http.post<Tabla_amortizacionInterface>(this.API_SERVER,datos).pipe(
      catchError(this.handleError)
    );
  }
    consultaAmortizacion(cuenta:any , user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   if(cuenta.numSol!=null){
    formData.append('numSol',cuenta.numSol);
   }
    if(cuenta.codProyecto!=null){
      formData.append('codProyect',cuenta.codProyecto);
    }
   formData.append('tipo',cuenta.tipo);
   
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/amortizacion/consulta';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Parametros')[0];
    let data :any;
    return  this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
 

  ConsultarTablaxSolicitud( user: User): Observable<datos_ProcesoInterface>{

    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/amortizacion/consulta';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Parametros')[0];
    return this.http.post<datos_ProcesoInterface>(this.API_SERVER, httpOptions).pipe(
      catchError(this.handleError)
    );
  }


  GuardarTabla(datos: solicitud_tablaInterface,user: User): Observable<Tabla_amortizacionInterface>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(user);
    this.API_SERVER = environment.URL_BASE + 'multiplo/amortizacion';
    return this.http.post<Tabla_amortizacionInterface>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarTablaGeneral(datos: solicitud_tablaInterface,user: User): Observable<Tabla_amortizacionInterface>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(user);
    this.API_SERVER = environment.URL_BASE + 'multiplo/amortizacion/consulta';
    return this.http.post<Tabla_amortizacionInterface>(this.API_SERVER,datos,httpOptions).pipe(
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
    return throwError(() => new Error(error.error?.error? error.error.error :  error.error['mensaje']));
  }

}
