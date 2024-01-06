
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { StacksInterface } from 'src/app/shared/models/consulta_Inversiones/stacks.interface';

import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';


import { StorageService } from 'src/app/shared/service/storage.service';

@Component({
  selector: 'app-status-cards',
  templateUrl: './status-cards.component.html',
  styleUrls: ['./status-cards.component.css']
})
export class StatusCardsComponent implements OnInit {
  val:string | undefined;
  canp!:number ;
  canc!:number ;
  tran!:number ;
  vig!:number ;
  liq!:number ;
  user: User;
  stacks: Array<StacksInterface> = [];
  


  constructor( private dataApiInvestments: ConsultaInversionesService,
    private storage:StorageService,
    private router:Router
    ) { 
    this.user=this.storage.getCurrentSession();
 
  }
  ngOnInit(): void {
    this.val=this.user?.user.identificacion;
    console.log(this.val);
    console.log(this.stacks);
    this.consultar();
  }


  consultar(): void {
    
    this.dataApiInvestments.ConsultaStacks(this.user).subscribe(
      (data: Array<StacksInterface>) => {
        this.stacks = data;
        console.log(data);
        for (let numero of data){
          if(numero.estado=="EN PROCESO"){
            this.canp=numero.cantidad;
          }
          if(numero.estado=="POR CONFIRMAR"){
            this.canc=numero.cantidad;
          }
          if(numero.estado=="VIGENTES"){
            this.vig=numero.cantidad;
          }
          if(numero.estado=="EN TRÁNSITO"){
            this.tran=numero.cantidad;
          }
          if(numero.estado=="LIQUIDADAS"){
            this.liq=numero.cantidad;
          }
        }
      },
      (error:Error) =>
      {
        if(error.message=="Forbidden"){
          this.storage.removeCurrentSession();
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
  }

  goToRoute(route:String){
    localStorage.removeItem('project');
    localStorage.removeItem('proyecto');
    localStorage.removeItem('numero_solicitud');
    this.router.navigate(["/investor/"+route]);
  }
}
