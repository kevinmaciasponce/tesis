import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ComunicationService {

  private stored: any[] = [];


  addItemToBasket(item: any) {
      this.stored=item;
  }

  getItemsFromBasket() : any[] {
      return this.stored;
  }
  removeItemfromBasket(){
    this.stored.push();
  }
}
