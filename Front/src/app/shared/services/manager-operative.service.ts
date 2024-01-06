import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ManagerOperativeService {
  private API_SERVER = environment.URL_BASE;
  constructor(private http: HttpClient) { }


  pendindgReviews(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/managerOper/consultas/solicitud/enRevision/filter';
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
  



  aprobarSolicitud(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/managerOper/procesos/solicitud/aprobar/filter';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('numSol', form);
   formData.append('usuario', user.user.identificacion);
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  anularSolicitud(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/managerOper/procesos/solicitud/anular/filter';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   
   const formData = new FormData();
   formData.append('numSol', form);
   formData.append('usuario', user.user.identificacion);
   //Post options pass it to HttpHeaders Class 
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
