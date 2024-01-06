import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, retry, throwError } from 'rxjs';
import { User } from 'src/app/models/user';
import { ciudadesInterface } from 'src/app/shared/models/ciudades.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})

export class PromoterService {
  private API_SERVER = environment.URL_BASE;
  
  constructor(private http: HttpClient,) { }

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
  ConsultaRiesgo(): Observable<Array<RiesgosInterface>> {
    this.API_SERVER = environment.URL_BASE + 'multiplo/calificaciones';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.get<Array<RiesgosInterface>>(this.API_SERVER).pipe(
      catchError(this.handleError)
    );
  }
  
  // /promotor/consultar/representante/0926801380001

  ConsultaPromotor(id:any,user:User): Observable<Array<any>> {
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   console.log(id);
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + '/multiplo/promotor/consultar/representante/'+id;
    return this.http.get<Array<any>>(this.API_SERVER,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  ConsultaPromotorxEmpresa(id:any,user:User): Observable<Array<any>> {
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   console.log(id);
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    this.API_SERVER = environment.URL_BASE + '/multiplo/promotor/consultar/representante/porEmpresa/'+id;
    return this.http.get<Array<any>>(this.API_SERVER,httpOptions).pipe(
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

  
  ConsultaEmpresa(datos:any,user:User): Observable<Array<SectorInterface>> {
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
  console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/consultar/empresa';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.post<Array<SectorInterface>>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  ingresarEmpresa(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token,
      'Content-Type': 'application/json'      
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/ingresar/empresa';
    //const service = this.dataObj.restAPI.filter((m: any) => m.name === 'Paises')[0];
    return this.http.put<Array<any>>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  ingresarProyecto(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/ingresar/proyecto';
    return this.http.put<Array<any>>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarFacturasClientes(datos:any,user:User){
    console.log(user);
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };  //Post options pass it to HttpHeaders Class 

    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('ced',datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/consultar/facturas/porCedula';
    return this.http.post<Array<any>>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  
  consultarFacturasDocumentos(idDocumento:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('id',idDocumento);
   this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/consultar/facturas/porId';
    return this.http.post<Array<any>>( this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarProyectosxPromotor(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/consultar/proyectos/filter';
    return this.http.post<Array<any>>(this.API_SERVER,datos,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  registrarDocumentosJuridicos(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('idEmpresa',datos.idEmpresa);
   formData.append('usuarioCompose',user.user.identificacion);
   formData.append('escritura',datos.escritura.file||null);
   formData.append('estatutosVigentes',datos.estatutos.file||null);
   formData.append('rucVigente',datos.rucVigente.file||null);
   formData.append('nombramientoRl',datos.nombramientoRl.file||null);
   formData.append('cedulaRl',datos.cedulaRl.file||null);
   formData.append('nominaAccionista',datos.nominaAccionista.file||null);
   formData.append('identificacionesAccionista',datos.IdentificacionesAccionista.file||null);
  
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/ingresar/documentos/juridicos';
    return this.http.put<Array<any>>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }
  registrarDocumentosFinancieros(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('idEmpresa',datos.idEmpresa);
   formData.append('impuestoRentaAnioAnterior',datos.impuestoRentaAnioAnterior.file||null);
   formData.append('estadoFinancieroAnioAnterior',datos.estadoFinancieroAnioAnterior.file||null);
   formData.append('estadoFinancieroActuales',datos.estadoFinancieroActuales.file||null);
   formData.append('anexoCtsCobrar',datos.anexoCtasCobrar.file||null);
   formData.append('usuarioCompose',user.user.identificacion);
   console.log(datos);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/ingresar/documentos/financieros';
    return this.http.put<Array<any>>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  
  registrarCorreoPrimeraFactura(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('idProyecto',datos.idProyecto);
   formData.append('fact',JSON.stringify(datos.fact));

    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/generar/factura1';
    return this.http.post<Array<any>>(this.API_SERVER,formData,httpOptions).pipe( retry(3),
      catchError(this.handleError)
    );
  }

  registrarPagoPrimeraFact(pago:any,file:File,codFactura:any,tipoDoc:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('pago',pago);
   formData.append('codFact',codFactura);
   formData.append('file',file);
   formData.append('tipoDocumento',tipoDoc);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/registrar/pago/factura';
    return this.http.post<Array<any>>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  
  
  

  consultarDocumentosFinancieros(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('idEmpresa',datos.idEmpresa);
   formData.append('user',datos.user.user.identificacion);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/consultar/documentos/financieros';
    return this.http.post<Array<any>>(this.API_SERVER,formData,httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  consultarDocumentosJuridicos(datos:any,user:User){
    const headers: any = {
      "Authorization": 'Bearer ' +user.token
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
   const formData = new FormData();
   formData.append('idEmpresa',datos.idEmpresa);
   formData.append('user',datos.user.user.identificacion);
    this.API_SERVER = environment.URL_BASE + 'multiplo/promotor/consultar/documentos/juridicos';
    return this.http.post<Array<any>>(this.API_SERVER,formData,httpOptions).pipe(
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
