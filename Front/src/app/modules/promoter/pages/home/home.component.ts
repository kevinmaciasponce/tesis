import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { PromoterService } from '../../service/promoter.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  idEmpresa:any;
  user:User;
  proyectos:any[]=[];
  toggle=false;
  constructor(private promotor:PromoterService,
    private storage:StorageService,
    private router:Router) { 
    this.user=this.storage.getCurrentSession();
    this.consultaIdEmpresa();
  }
 
  ngOnInit(): void {
    
  }

  editarDatos(codProyecto:any){
    this.set_data('codProyecto',codProyecto);
    this.router.navigate(["/promoter/register"]);
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  
  consultaIdEmpresa(){
    this.promotor.ConsultaPromotorxEmpresa(this.user.user.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.idEmpresa = data.idEmpresa 
        this.set_data('idEmpresa',this.idEmpresa);
      },null,()=>{
        this.consultaProyectos(); 
      }
    )
  }

  consultaProyectos(){
    this.promotor.consultarProyectosxPromotor(this.getDataPromotor(),this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.proyectos = data;
      },null,()=>{

      }
    )
  }
  getDataPromotor(){
    return {
      "codigoProyecto":"",
      "idEmpresa":this.idEmpresa,
      "estadoActual":"BO"
  }
  }


  ingresarFacturas(tipo:any){
    this.set_data('tipo',tipo);
    this.router.navigate(["/promoter/invoice"]);
  }
}
