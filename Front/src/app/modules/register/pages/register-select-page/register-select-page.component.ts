import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ComunicationService } from 'src/app/services/comunication.service';

@Component({
  selector: 'app-register-select-page',
  templateUrl: './register-select-page.component.html',
  styleUrls: ['./register-select-page.component.css']
})
export class RegisterSelectPageComponent implements OnInit {
  RegisterRoutingModule: any;

  constructor(public activeModal: NgbActiveModal, private servicoComunicacion: ComunicationService) { 
    
  }

  ngOnInit(): void {
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
      this.activeModal.dismiss();
    } catch(e){
      console.log(e);
    }
  }

  enviarMsn(mensaje:string){
    this.servicoComunicacion.addItemToBasket(mensaje);
    this.activeModal.dismiss();
  }
  //
}
