import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { bancoInterface } from 'src/app/shared/models/banco.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BeneficiaryService {
  private API_SERVER= environment.URL_BASE;
  constructor(private http: HttpClient) { }
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
  ConsultaTipoCuentas(): Observable<Array<ParametrosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/parametros/TIPO_CUENTAS';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<ParametrosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  consultaModalidad(user:User): Observable<Array<any>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/lista/modalidades';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.post<Array<any>>(this.API_SERVER,null,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaCategoria(user:User): Observable<Array<any>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/lista/categorias';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.post<Array<any>>(this.API_SERVER,null,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  consultaAuspiciosVigentes(cuenta:any): Observable<any>{
    this.API_SERVER = environment.URL_BASE + 'multiplo/public/lista/auspicios/vigentes';
    return this.http.post<any>(this.API_SERVER,cuenta).pipe(
      catchError(this.handleError)
    );
  }

  consultaAuspiciosxRepresentante(user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('idRepre', user.user.identificacion);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consultar/auspicios/porRepresentante';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaBeneficiariosxRepresentante(identificacion:any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   let cuenta = {
    "identificacion":identificacion,
    "representante":user.user.identificacion
   }
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiarios/porRepresentante';
    return this.http.post<any>(this.API_SERVER,cuenta,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  agregarfotosBeneficiario(identificacion: any,file:File,file2:File,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   const formData = new FormData();
   formData.append('file', file);
   formData.append('file2', file2);
   formData.append('identificacion', identificacion);
   formData.append('idRepre', user.user.identificacion);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/fotos';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  validaValoracion(identificacion:any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   console.log(identificacion)
   const formData = new FormData();
   formData.append('identificacion', identificacion);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/validar/beneficiarios/valoracion';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarValoracion(identificacion:any,user: User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   console.log(identificacion)
   const formData = new FormData();
   formData.append('identificacion', identificacion);
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiarios/valoracion';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  guardaValoracion(form:any,file:File,user: User): Observable<any>{
      const headers: any = {
        "Authorization": 'Bearer ' +user.token
    };
    const formData = new FormData();
    formData.append('request', form);
    formData.append('file', file);
      const httpOptions = {
        headers: new HttpHeaders(headers),
    };
      this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/valoracion';
      return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
        catchError(this.handleError)
      );
  }
  consultaDisciplina(user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/lista/disciplinas';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.post(this.API_SERVER,null,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaBeneficiarioAgregar(cuenta:any,user:User): Observable<any> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiario/paraAgregar';
    const headers: any ={
      "Authorization": 'Bearer ' +user.token
   };
   let body = {
    "identificacion":cuenta,
    "representante":user.user.identificacion  
  }
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.post(this.API_SERVER,body,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaPaises(): Observable<Array<PaisesInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/paises';
    return this.http.get<Array<PaisesInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }

consultaBeneficiarioTitulos(cuenta:any,user:User): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiarios/titulos';
  const headers: any ={
    "Authorization": 'Bearer ' +user.token
 };
 const formData = new FormData();
 console.log(cuenta);
 formData.append('identificacion', cuenta);
 //Post options pass it to HttpHeaders Class 
  const httpOptions = {
     headers: new HttpHeaders(headers),
 };
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData,httpOptions).pipe(
    catchError(this.handleError)
  );
}

consultaBeneficiarioAuspicio(identificacion:any, estado:any,user:User): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/auspicio/porBeneficiario';
  const headers: any ={
    "Authorization": 'Bearer ' +user.token
 };
 let cuenta ={
  "identificacion":identificacion,
  "estado":estado
  }
 //Post options pass it to HttpHeaders Class 
  const httpOptions = {
     headers: new HttpHeaders(headers),
 };
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,cuenta,httpOptions).pipe(
    catchError(this.handleError)
  );
}

consultaRecompensas(cuenta:any,user:User): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/auspicio/recompensas';
  const headers: any ={
    "Authorization": 'Bearer ' +user.token
 };
 const formData = new FormData();
 console.log(cuenta);
 formData.append('numeroAuspicio', cuenta);
 //Post options pass it to HttpHeaders Class 
  const httpOptions = {
     headers: new HttpHeaders(headers),
 };
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData,httpOptions).pipe(
    catchError(this.handleError)
  );
}
//
consultaTorneos(cuenta:any,user:User): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/auspicio/torneos';
  const headers: any ={
    "Authorization": 'Bearer ' +user.token
 };
 const formData = new FormData();
 console.log(cuenta);
 formData.append('numeroAuspicio', cuenta);
 //Post options pass it to HttpHeaders Class 
  const httpOptions = {
     headers: new HttpHeaders(headers),
 };
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData,httpOptions).pipe(
    catchError(this.handleError)
  );
}

consultaBeneficiariopublico(cuenta:any): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiario/filter';
 const formData = new FormData();
 console.log(cuenta);
 formData.append('identificacion', cuenta);
 formData.append('representante', cuenta);
 //Post options pass it to HttpHeaders Class 
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData).pipe(
    catchError(this.handleError)
  );
}
consultaValoracionPublico(cuenta:any): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiarios/valoracion';
 const formData = new FormData();
 console.log(cuenta);
 formData.append('identificacion', cuenta);
 //Post options pass it to HttpHeaders Class 
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData).pipe(
    catchError(this.handleError)
  );
}
consultaTitulosPublico(cuenta:any): Observable<any> {
//   const headers: any ={
//     "Authorization": 'Bearer ' +user.token
//  };

//   const httpOptions = {
//      headers: new HttpHeaders(headers),
//  };
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/beneficiarios/titulos';
 const formData = new FormData();
 console.log(cuenta);
 formData.append('identificacion', cuenta);
 //Post options pass it to HttpHeaders Class 
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData).pipe(
    catchError(this.handleError)
  );
}
consultaTorneosPublico(cuenta:any): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/auspicio/torneos';
 const formData = new FormData();
 console.log(cuenta);
 formData.append('numeroAuspicio', cuenta);
 //Post options pass it to HttpHeaders Class 
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData).pipe(
    catchError(this.handleError)
  );
}
consultaRecompensasPublico(cuenta:any): Observable<any> {
  this.API_SERVER = environment.URL_BASE + 'multiplo/representante/consulta/auspicio/recompensas';
 
  const formData = new FormData();
  console.log(cuenta);
  formData.append('numeroAuspicio', cuenta);
  //Post options pass it to HttpHeaders Class 
  //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
  return this.http.post(this.API_SERVER,formData).pipe(
    catchError(this.handleError)
  );
}
  AgregrarBeneficiario(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios';
    return this.http.post<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  AgregrarBeneficiarioTitulos(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/titulos';
    return this.http.post<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  AgregrarBeneficiarioValoracion(datos: any,file:File,user: User): Observable<any>{
    const headers: any = {
        "Authorization": 'Bearer ' +user.token
    };
    //Post options pass it to HttpHeaders Class 
      const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    const formData = new FormData();
    formData.append('file', file);
    formData.append('request', datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/valoracion';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  AgregrarAuspicio(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/auspicio';
    return this.http.post<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  AgregrarRecompensas(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/auspicio/recompensas';
    return this.http.post<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  AgregrarTorneos(datos: any,user: User): Observable<any>{
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/agregar/beneficiarios/auspicio/torneos';
    return this.http.post<any>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  ConfirmarAuspicio(datos: any,user: User): Observable<any>{
    const headers: any = {
        "Authorization": 'Bearer ' +user.token
    };
    //Post options pass it to HttpHeaders Class 
      const httpOptions = {
        headers: new HttpHeaders(headers),
    };
    const formData = new FormData();
    formData.append('numeroAuspicio', datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/representante/modificar/auspicio/porConfirmar';
    return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultaAuspicioxNumeroAuspicio(datos: any): Observable<any>{
    //Post options pass it to HttpHeaders Class 
    this.API_SERVER = environment.URL_BASE + 'multiplo/public/consulta/auspicio/porNumero';
    return this.http.post<any>(this.API_SERVER,datos).pipe(
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
