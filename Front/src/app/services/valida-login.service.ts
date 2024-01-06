import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})

export class ValidaLoginService{
  private API_SERVER = environment.URL_BASE+"multiplo/login";

  constructor(
    private httpClient : HttpClient,
    private cookies: CookieService
  ){}
  public validaLogin(form:any):Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }
    return this.httpClient.post<any>(this.API_SERVER,form,httpOptions);
  }

  setToken(token: string) {
    this.cookies.set("token",token);
  }

  deleteToken() {
    this.cookies.delete("token");
  }

  getToken() {
    return this.cookies.get("token");
  }
  
  
}