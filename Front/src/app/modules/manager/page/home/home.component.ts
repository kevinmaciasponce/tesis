import { Component, OnInit } from '@angular/core';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { StacksInterface } from 'src/app/shared/models/consulta_Inversiones/stacks.interface';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  user: User;
  stacks: Array<StacksInterface> = [];
  cani!:number ;
  canc!:number ;
  cvig!:number ;
  constructor(private dataApiPublics: ConsultaInversionesService,
    private storage:StorageService) { 
    this.user=this.storage.getCurrentSession();
  }

  ngOnInit(): void {
    this.consultar();
  }

  consultar(): void {
    
    this.dataApiPublics.ConsultaAllStacks(this.user).subscribe(
      (data: Array<StacksInterface>) => {
        this.stacks = data;
        console.log(data);
        for (let numero of data){
          if(numero.estado=="EN TRÁNSITO"){
            this.cani=numero.cantidad;
          }
          if(numero.estado=="POR CONFIRMAR"){
            this.canc=numero.cantidad;
          }
          if(numero.estado=="VIGENTES"){
            this.cvig=numero.cantidad;
          }
        }
      },
      (error:Error) =>
      {
        console.log(error);
      });
      
      
  }

}
