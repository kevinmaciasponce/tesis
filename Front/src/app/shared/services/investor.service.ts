import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { formularioPersonaNaturalInterface } from '../models/formulario_persona_natural.interface';
import { environment } from 'src/environments/environment';
import { datosDepositoInterface } from '../models/tabla_amortizacion/investor/datosDeposito.interface';

@Injectable({
  providedIn: 'root'
})
export class InvestorService {

  private API_SERVER = environment.URL_BASE;
  constructor(private http: HttpClient) { }

  GuardarFormulario(datos: any,user: User): Observable<formularioPersonaNaturalInterface>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/formularios/nat';
    return this.http.post<formularioPersonaNaturalInterface>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  ActualizarFormulario(datos: any,user: User): Observable<formularioPersonaNaturalInterface>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/formularios/nat';
    return this.http.put<formularioPersonaNaturalInterface>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  GuardarIdentificaion(datos: any,files:File,filepost:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('solicitud', JSON.stringify(datos));
   formData.append('file',files);
   formData.append('filepost',filepost);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/formularios/documentos/cedula';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  ActualizarIdentificaion(datos: any,files:File,filepost:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('solicitud', JSON.stringify(datos));
   formData.append('file',files);
   formData.append('filepost',filepost);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/formularios/documentos/cedula';
    return this.http.put<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  GuardarComprobante(datos: any,files:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('solicitud', JSON.stringify(datos));
   formData.append('file',files);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/formularios/documentos/deposito';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  consultaDepositos(datos: any,user: User): Observable<datosDepositoInterface>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/proyectos/cuenta-habilitada';
    return this.http.post<datosDepositoInterface>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  ConsultaFormaPago(): Observable<Array<any>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/formaPagos';
    return this.http.get<Array<any>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  //Cambiar a formdata
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
    return throwError(() => new Error(error.error?.error? error.error.error : error.error.mensaje ));
  }
  
}
