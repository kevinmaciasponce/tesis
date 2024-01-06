import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { detallestatusInterface } from '../models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from '../models/consulta_Inversiones/empresas.interface';
import { RiesgosInterface } from '../models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from '../models/consulta_Inversiones/sector.intereface';

@Injectable({
  providedIn: 'root'
})
export class ConsultasPublicasService {
  private API_SERVER= environment.URL_BASE;
  

  constructor(private http: HttpClient) { }

  ConsultaEmpresas(): Observable<Array<EmpresasInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/proyectos/filter';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<EmpresasInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  ConsultaRiesgo(): Observable<Array<RiesgosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/calificaciones';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<RiesgosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  ConsultaSector(): Observable<Array<SectorInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/sector';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<SectorInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  DetalleStatus(ids:any): Observable<detallestatusInterface> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/tipoEstados/'+ids;
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<detallestatusInterface>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  saveFormContact(form: any): Observable<any> {
    this.API_SERVER = environment.URL_BASE + '/multiplo/formcontact';
    return this.http.post(this.API_SERVER, form).pipe(
      catchError(this.handleError)
    );
  }

  activarcuenta(token: string): Observable<any> {
    this.API_SERVER = environment.URL_BASE + '/multiplo/confirmacion-email/'+token;
    return this.http.put(this.API_SERVER, null).pipe(
      catchError(this.handleError)
    );
  }
  olvideMiContraseña(form:any): Observable<any> {
    this.API_SERVER = environment.URL_BASE + '/multiplo/confirmacion-email/forgot-pass';
    return this.http.post<any>(this.API_SERVER,form ).pipe(
      catchError(this.handleError)
    );
  }

  validarCambiarContraseña(form:any): Observable<any> {
    this.API_SERVER = environment.URL_BASE + '/multiplo/confirmacion-email/valid-switch-pass';
    return this.http.post<any>(this.API_SERVER,form ).pipe(
      catchError(this.handleError)
    );
  }
  
  cambiarContraseña(form: any): Observable<any> {
    this.API_SERVER = environment.URL_BASE + '/multiplo/confirmacion-email/switch-pass';
    return this.http.post<any>(this.API_SERVER, form).pipe(
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

  chatbot(param:any): Observable<any> {
    const API_SERVER =  environment.URL_BASE +'multiplo/public/chat';
   const headers: any ={
     "Authorization": 'Bearer ' 
  };
  //Post options pass it to HttpHeaders Class 
   const httpOptions = {
      headers: new HttpHeaders(headers),
  };
   //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  let data=this.http.post<any>( API_SERVER,param,httpOptions).pipe(
    catchError(this.handleError))
   console.log(data)
  
   return data
  }
}
