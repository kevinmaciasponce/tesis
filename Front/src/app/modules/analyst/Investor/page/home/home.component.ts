import { Component, OnInit } from '@angular/core';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { StacksInterface } from 'src/app/shared/models/consulta_Inversiones/stacks.interface';
import { Router } from '@angular/router';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  val!:string;
  canp!:number ;
  canc!:number ;
  tranf!:number ;
  fc!:number;
  vigente!:number;
  anuladas!:number;
  conciliacion!:number;
  liquidadas!:number;
  user: User;
  stacks: Array<StacksInterface> = [];
  


  constructor( private dataApiInvestments: ConsultaInversionesService,private storage:StorageService,private router:Router) { 
    this.user=this.storage.getCurrentSession();
    this.consultar();
  }

  ngOnInit(): void {
    this.user = this.storage.getCurrentSession(); 
    this.val="null";
    console.log(this.val);
    console.log(this.stacks);
  }


  consultar(): void {
    this.dataApiInvestments.ConsultaAllStacks(this.user).subscribe(
      (data: Array<StacksInterface>) => {
        this.stacks = data;
        console.log(data);
        for (let numero of data){
          if(numero.estado=="POR CONFIRMAR"){
            this.canp=numero.cantidad;
          }
          if(numero.estado=="PENDIENTE DE APROBACIÓN"){
            this.canc=numero.cantidad;
          }
          if(numero.estado=="APROBADA TRANSFERIR FONDOS(S)"){
            this.tranf=numero.cantidad;
          }
          if(numero.estado=="FIRMA DE CONTRATO(S)"){
            this.fc=numero.cantidad;
          }
          if(numero.estado=="VIGENTES"){
            this.vigente=numero.cantidad;
          }
          if(numero.estado=="LIQUIDADAS"){
            this.liquidadas=numero.cantidad;
          }
          if(numero.estado=="ANULADA"){
            this.anuladas=numero.cantidad;
          }
          if(numero.estado=="CONCILIACIÓN"){
            this.conciliacion=numero.cantidad;
          }
        }
      },(error : Error)=>{
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
      
      
  }

}
