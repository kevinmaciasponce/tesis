import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { StorageService } from 'src/app/shared/service/storage.service';

@Component({
  selector: 'app-reward-modal',
  templateUrl: './reward-modal.component.html',
  styleUrls: ['./reward-modal.component.css']
})
export class RewardModalComponent implements OnInit {

  user:User;
  auspicio:any;
  numeroAuspicio:any;

  constructor(
    private storage:StorageService,
    private dataApiClient:BeneficiaryService,
    ) {
    this.user=this.storage.getCurrentSession();
    this.numeroAuspicio = this.get_data('auspicio');
    this.consultarRecompensas();
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

  consultarRecompensas(){
    this.dataApiClient.consultaRecompensas(this.numeroAuspicio,this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        this.auspicio=data; 
      },(error:Error)=>{
        console.log(error.message)
      },()=>{
      });
  }


}
