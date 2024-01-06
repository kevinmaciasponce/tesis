import { Component, OnDestroy, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { StorageService } from 'src/app/shared/service/storage.service';

@Component({
  selector: 'app-torneos-modal',
  templateUrl: './torneos-modal.component.html',
  styleUrls: ['./torneos-modal.component.css']
})
export class TorneosModalComponent implements OnInit, OnDestroy {
  user:User;
  auspicio:any;
  numeroAuspicio:any;

  constructor(
    private storage:StorageService,
    private dataApiClient:BeneficiaryService,
    ) {
    this.user=this.storage.getCurrentSession();
    this.numeroAuspicio = this.get_data('auspicio');
    this.consultarTorneos();
   }
  ngOnDestroy(): void {
    localStorage.removeItem('auspicio');
  }

  ngOnInit(): void {
  }

  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }

  consultarTorneos(){
    this.dataApiClient.consultaTorneos(this.numeroAuspicio,this.user).subscribe(
      (data:any)=>{
        this.auspicio=data; 
      },(error:Error)=>{
        console.log(error.message)
      },()=>{

      });
  }

}
