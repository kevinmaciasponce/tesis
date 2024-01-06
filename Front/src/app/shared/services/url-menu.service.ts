import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UrlMenuService {
  private url$ = new Subject<any>();
  private url: any;

  constructor() { }

  agregarurl(url: any) {
    this.url=null;
    this.url=url;
    this.url$.next(this.url);
  }

  eliminarurl(){
    this.url$.unsubscribe();
  }

  geturl$(): Observable<any[]> {
    return this.url$.asObservable();
  }
}
