import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private menu$ = new Subject<any>();
  private menu: any;

  constructor() { }

  agregarMenu(menu: any) {
    this.menu=null;
    this.menu=menu;
    this.menu$.next(this.menu);
  }

  eliminarMenu(){
    this.menu$.unsubscribe();
  }


  getMenu$(): Observable<any[]> {
    return this.menu$.asObservable();
  }
}
