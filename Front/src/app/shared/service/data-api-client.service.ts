import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from 'src/app/config.service';
import { catchError, Observable, throwError } from 'rxjs';
import { ParametrosInterface } from '../models/parametros-interface';
import { PaisesInterface } from '../models/paises-interface';
import { IEmpleadosVentas } from '../models/empleados-ventas.interface';
import { environment } from 'src/environments/environment';
import { ciudadesInterface } from '../models/ciudades.interface';
import { User } from 'src/app/models/user';
import { bancoInterface } from '../models/banco.interface';

@Injectable({
  providedIn: 'root'
})
export class DataApiClientService {
  api: any;
  dataObj: any;
  private API_SERVER = environment.URL_BASE;
  
  constructor(private http: HttpClient,
    private configService: ConfigService) { 
    //this.api = this.configService.get('api');
    //this.dataObj = this.api.endpoints.filter((c: { restAPI: any; }) => c.restAPI)[0];
  }
  
  
  
 

  ConsultaItems(catalogo: string): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/'+catalogo;
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Parametros')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  ConsultaCiudades(id:number): Observable<Array<ciudadesInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/ciudades/ciudades-pais/'+id;
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ciudadesInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  
  ConsultaPaises(): Observable<Array<PaisesInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/paises';
    return this.http.get<Array<PaisesInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  

  ConsultaSexo(): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/TIPO_SEXO';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  ConsultaEstadoCivil(): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/ESTADO_CIVIL';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  ConsultaFuenteIngresos(): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/FUENTE_INGRESOS';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  ConsultaTipoCargos(): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/TIPO_CARGOS';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  ConsultaTipoCuentas(): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/TIPO_CUENTAS';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }


  ConsultaBanco(user:User): Observable<Array<bancoInterface>> {
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/bancos';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<bancoInterface>>(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  ConsultaEmpleadosVentas(): Observable<Array<IEmpleadosVentas>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/empleados/ventas';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'EmpleadosVentas')[0];
    return this.http.get<Array<IEmpleadosVentas>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  RegistrarPersonaJuridica(form: any): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/cuentas/';
    //const registerEndpoint = this.dataObj.restAPI.filter((m: any) => m.name === 'RegistroPersonaJuridica')[0];
    return this.http.post(this.API_SERVER, form).pipe(
      catchError(this.handleError)
    );
  }

  RegistrarPersonaNatural(form: any): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/cuentas/';
    return this.http.post(this.API_SERVER, form).pipe(
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
