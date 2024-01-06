import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {
  private API_SERVER = environment.URL_BASE;

  constructor(private http: HttpClient) {

   }

   reportInvestorMonths(form:any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/consultas/reportes/cuotas/investors';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
   const formData = new FormData();
   formData.append('meses', form.meses);
   formData.append('anio',form.anio);
   formData.append('identificaciones',form.identificacion); 
  // formData.append('meses','1,2,3');
  // formData.append('anio','2023');
  // formData.append('identificacion','0999999999'); 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };

    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  chartInvestorMonths(form:any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/consultas/reportes/cuotas/porMeses';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
   const formData = new FormData();
   formData.append('meses', form.meses);
   formData.append('proyectos','');
   formData.append('anio',form.anio);
   formData.append('identificacion',form.identificacion); 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaPersonas(user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/consultas/filter/persona';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };

    return this.http.get(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }


  reportRetardedState(form:any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/consultas/reportes/cuotas/Mora/porMeses';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
   const formData = new FormData();
   formData.append('meses', form.meses);
   formData.append('anio',form.anio);
   formData.append('codProyect',form.codProyect); 
  // formData.append('meses','1,2,3');
  // formData.append('anio','2023');
  // formData.append('identificacion','0999999999'); 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };

    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
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
