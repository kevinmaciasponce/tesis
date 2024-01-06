import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { User } from 'src/app/models/user';
import { RiesgosInterface } from '../models/consulta_Inversiones/riesgos.interface';
import { EmpresasInterface } from '../models/consulta_Inversiones/empresas.interface';
import { SectorInterface } from '../models/consulta_Inversiones/sector.intereface';
import { catchError, Observable, throwError  } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class ConsultaSolicitudesManagerService {
  
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
  ManagerIntransit(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/manager/consultas/proyectos/enTransito/filter';
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
  managerIntransitDetails(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/manager/consultas/solicitud/enTransito/porProyecto';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('codProyect', form.codigoProyecto);
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  managerValidDetails(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/manager/consultas/solicitud/vigente/porProyecto';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('codProyect', form);
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }


  managerValid(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/manager/filter/vigente';
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

  ConsultaVigenteProyectos(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/manager/consultas/proyectos/exitoso/filter';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    console.log(form)
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  ManagerIntransitAprove(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/inversiones/int/porcentajeAprobado';
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
    return throwError(() => new Error(error.error?.error? error.error.error : 'Ha ocurrido un error por favor intente más tarde.'));
  }
}
