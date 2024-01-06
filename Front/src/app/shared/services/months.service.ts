import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { delay, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MonthsService {

  constructor() { 
  }

  getMonth(term: String = '' ): Observable<any[]> {
    let items = getMockMonth();
    if (term == '') {
        items = items.filter(x => x.month.toLocaleLowerCase().indexOf(term.toLocaleLowerCase()) > -1);
    }
    return of(items).pipe(delay(500));
  }


}
function getMockMonth() {
  return [
      {
        'id': 1,
        'month': 'Enero',
        'todos':'todos'
      },
      {
        'id': 2,
        'month': 'Febrero',
        'todos':'todos'
      },
      {
        'id': 3,
        'month': 'Marzo',
        'todos':'todos'
     },
      {
        'id': 4,
        'month': 'Abril',
        'todos':'todos'
      },
      {
        'id': 5,
        'month': 'Mayo',
        'todos':'todos'
      }
      ,
      {
        'id': 6,
        'month': 'Junio',
        'todos':'todos'
      }
      ,
      {
        'id': 7,
        'month': 'Julio',
        'todos':'todos'
      }
      ,
      {
        'id': 8,
        'month': 'Agosto',
        'todos':'todos'
      }
      ,
      {
        'id': 9,
        'month': 'Septiembre',
        'todos':'todos'
      }
      ,
      {
        'id': 10,
        'month': 'Octubre',
        'todos':'todos'
      }
      ,
      {
        'id': 11,
        'month': 'Noviembre',
        'todos':'todos'
      }
      ,
      {
        'id': 12,
        'month': 'Diciembre',
        'todos':'todos'
      }
  ]
}