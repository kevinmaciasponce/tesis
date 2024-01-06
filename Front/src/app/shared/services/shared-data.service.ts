import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { User } from 'src/app/models/user';

@Injectable({
  providedIn: 'root'
})
export class SharedDataService {
  private userEndSource = new Subject<void>();
  public userEnd$ = this.userEndSource.asObservable();

  constructor() { }

  

}
