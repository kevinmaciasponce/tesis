import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProyectosService {

  private baseURL: string = environment.URL_BASE;
  private API_SERVER = environment.URL_BASE;
  constructor(private http: HttpClient) { }

  getProjects(): Observable<any> {
    // return this.http.get(
    //     this.baseURL,
    //     {
    //       headers: {
    //         'Content-Type': 'application/json',
    //         mode: 'no-cors'
    //       },
          
    //     }
    //   );
    const headers: any = {
      "Authorization": 'Bearer ' +""
   };
   //Post options pass it to HttpHeaders Class 
    const httpOptions = {
       headers: new HttpHeaders(headers),
   };
    const formData = new FormData();
    formData.append('partitions','0');
    formData.append('group','10');
    this.API_SERVER = environment.URL_BASE + 'multiplo/proyectos';
   return this.http.post<any>(this.API_SERVER,formData,httpOptions).pipe(
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