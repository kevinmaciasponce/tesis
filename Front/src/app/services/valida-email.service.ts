import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Mensaje } from '../models/Mensaje';

@Injectable({
  providedIn: 'root'
})

export class ValidaEmailService{
  private API_SERVER = environment.URL_BASE+"multiplo/cuentas/validaCuenta";

  constructor(
    private httpClient : HttpClient
  ){}
 
  public validaCorreoExist2(form:any):Observable<Mensaje>{
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }
    return this.httpClient.post<Mensaje>(this.API_SERVER,form,httpOptions);
  }
}