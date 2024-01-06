import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { catchError, Observable, throwError } from 'rxjs';

import { User } from 'src/app/models/user';
import { ConfigService } from 'src/app/config.service';
import { RiesgosInterface } from '../models/consulta_Inversiones/riesgos.interface';
import { EmpresasInterface } from '../models/consulta_Inversiones/empresas.interface';
import { SectorInterface } from '../models/consulta_Inversiones/sector.intereface';

@Injectable({
  providedIn: 'root'
})
export class ConsultaInversionesService {
  private API_SERVER = environment.URL_BASE;
  constructor(private http: HttpClient,
    private configService: ConfigService) { }

  ConsultaEmpresas(): Observable<Array<EmpresasInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/proyectos/filter';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<EmpresasInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  ConsultaEmpresasxestado(estado: string): Observable<Array<EmpresasInterface>> {
    const formData = new FormData();
    formData.append('estado', estado);
    this.API_SERVER = environment.URL_BASE + 'multiplo/proyectos/filter/estado';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.post<Array<EmpresasInterface>>(this.API_SERVER,formData).pipe(
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
  
  ConsultaVigente(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/vigente';
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
  
  ConsultaEnproceso(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/enProceso';
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
  ContinuarProcess(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/consultas/solicitud';
    const headers: any = {"Authorization": 'Bearer ' +user.token};
    const httpOptions = {headers: new HttpHeaders(headers),};
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(catchError(this.handleError));
  }
  ConsultaPorconfirmar(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/porConfirmar';
    const headers: any = {"Authorization": 'Bearer ' +user.token };
    const httpOptions = {headers: new HttpHeaders(headers),};
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(catchError(this.handleError));
  }
  ConsultaIntransit(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/enTransito';
    const headers: any = {"Authorization": 'Bearer ' +user.token };
    const httpOptions = {headers: new HttpHeaders(headers),};
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(catchError(this.handleError));
  }
  ConsultaStacks(user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/dashboard';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, {"identificacion": user.user.identificacion,"tipoCliente": user.user.tipoCliente},httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  ConsultaAllStacks(user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/dashboard';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    return this.http.post(this.API_SERVER, {"identificacion": null,"tipoCliente": user.user.tipoCliente},httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  Consultarhistorial(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/solicitud/inversiones/filter/historialEstados';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
    const httpOptions = {headers: new HttpHeaders(headers),};
    return this.http.post(this.API_SERVER, form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  ConsultarAmortizacion(form: any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/amortizacion/consulta';
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
    const httpOptions = {headers: new HttpHeaders(headers),};
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
