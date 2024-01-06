import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SponsorAnalystService {
  private API_SERVER= environment.URL_BASE;
  constructor(private http: HttpClient) {
   }

   
  consultaAuspiciosPorConfirmar(cuenta:any): Observable<any>{
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/consultar/auspicios/filter';
    return this.http.post<any>(this.API_SERVER,cuenta).pipe(
      catchError(this.handleError)
    );
  }


 
  EditarBeneficiario(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/modificar/beneficiarios';
    return this.http.put<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  EditarTitulos(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/modificar/beneficiario/titulos';
    return this.http.put<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
   EditarRecompensas(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/modificar/recompensas/auspicio';
    return this.http.put<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  EditarAuspicio(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/modificar/beneficiarios/auspicio';
    return this.http.put<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  EditarTorneos(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/modificar/torneos/auspicio';
    return this.http.put<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  confirmarAuspicios(cuenta:any,user:User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
  };
    const formData = new FormData();
    formData.append('numAus',cuenta.numAus);
    formData.append('usuario', user.user.identificacion);
    formData.append('observacion', cuenta.comentario);
    const httpOptions = {
      headers: new HttpHeaders(headers),
  };
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/proceso/auspicios/aprobarVigente';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  anularAuspicios(cuenta:any,user:User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
      const formData = new FormData();
      formData.append('numAus',cuenta.numAus);
      formData.append('usuario', user.user.identificacion);
      formData.append('observacion', cuenta.comentario);
      const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/proceso/auspicios/anular';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  pendienteAuspicios(cuenta:any,user:User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
  };
    const formData = new FormData();
    formData.append('numAus',cuenta.numAus);
    formData.append('usuario', user.user.identificacion);
    console.log(cuenta.numAuspicio);
    formData.append('observacion', cuenta.comentario);
    const httpOptions = {
      headers: new HttpHeaders(headers),
  };
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/proceso/auspicios/aprobarProximamente';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  editaValoracion(form:any,file:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
  };
  const formData = new FormData();
  console.log(form);
  console.log(file);
  formData.append('request', form);
  formData.append('file', file);
  formData.append('usuario', user.user.usuario);  
    const httpOptions = {
      headers: new HttpHeaders(headers),
  };
    this.API_SERVER = environment.URL_BASE + 'multiplo/analistaAuspicio/modificar/auspicios/valoracion';
    return this.http.put<any>(this.API_SERVER,formData,httpOptions).pipe(
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
