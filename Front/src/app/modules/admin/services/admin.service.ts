import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { bancoInterface } from 'src/app/shared/models/banco.interface';
import { ciudadesInterface } from 'src/app/shared/models/ciudades.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private API_SERVER= environment.URL_BASE;
  constructor(private http: HttpClient) { }


  consultaDisciplina(user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/disciplinas';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  consultaModalidad(user:User): Observable<Array<any>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/modalidades';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<any>>(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  consultaCategoria(user:User): Observable<Array<any>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/categorias';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<any>>(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  agregarModalidad(form:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/parametrizar/auspicios/modalidades';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  agregarCategoria(form:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/parametrizar/auspicios/categorias';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  agregarDisciplina(form:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/parametrizar/auspicios/disciplinas';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarPersonalInterno(form:any,user:User){
    const headers: any = {
      // "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/consultar/personaInterna';
    return this.http.post<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarRolxEmpleado(form:any,user:User){
    const headers: any = {
       "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    let formData = new FormData();
    formData.append('idCuenta', form);
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/rol/empleado';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  agregarRoles(form:any,user:User){
    const headers: any = {
       "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    let formData = new FormData();
    formData.append('rol', form.idRol);
    formData.append('cuenta', form.cuenta);
    formData.append('userCompose', user.user.identificacion);
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/ingresar/personaInterna/cuenta/roles';
    return this.http.put<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  eliminarRoles(form:any,user:User){
    const headers: any = {
       "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    let formData = new FormData();
    formData.append('rol', form.idRol);
    formData.append('cuenta', form.cuenta);
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/eliminar/rol/empleado';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarRolSistema(user:User){
    const headers: any = {
       "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/rol/interno';
    return this.http.get<any>(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  administraCuentaInterna(form:any,user:User){
    const headers: any = {
      // "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/ingresar/personaInterna/cuenta';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  administraPersonaInterna(form:any,user:User){
    const headers: any = {
      // "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/ingresar/personaInterna';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaBanco(user:User): Observable<Array<bancoInterface>> {
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };

   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/bancos';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<bancoInterface>>(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  agregarBanco(form:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/ingresar/banco';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaPais(): Observable<Array<PaisesInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/pais';
    return this.http.get<Array<PaisesInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  agregarPais(form:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/parametrizar/pais';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaCiudad(id:number): Observable<Array<ciudadesInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/lista/ciudad';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ciudadesInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

  agregarCiudad(form:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
    };
    const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    this.API_SERVER = environment.URL_BASE + 'multiplo/admin/parametrizar/ciudad';
    return this.http.put<any>(this.API_SERVER,form,httpOptions).pipe(
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
