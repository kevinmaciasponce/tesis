import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-more-details',
  templateUrl: './more-details.component.html',
  styleUrls: ['./more-details.component.css']
})
export class MoreDetailsComponent implements OnInit {
  safeURL: any;
  idAuspicio:any;
  edad:any;
  nombre:any;
  identificacion:any;
  video:any;
  deportista:any;
  monto:any;
  user:User;
  calificacion:any;
  porcenatjeprueba:any
  porcenatjeprueba2:any
  constructor(private _sanitizer: DomSanitizer,private route: ActivatedRoute,
     private dataApiClient: BeneficiaryService,private router:Router,
     private storage:StorageService ) { 
    this.idAuspicio = this.route.snapshot.paramMap.get("id") as string;
    this.user=this.storage.getCurrentSession();
    this.porcenatjeprueba=0;
    this.porcenatjeprueba2=0;
    this.consultarAuspicio()
  }

  getNumeroAuspicio(){
    return {
      "identificacion":" ",
      "numeroAuspicio": this.idAuspicio,
      "Activos":''
      }
  }

  consultarAuspicio(){
    this.dataApiClient.consultaAuspicioxNumeroAuspicio(this.getNumeroAuspicio()).subscribe(
      (data:any)=>{
        this.deportista=data;
        this.identificacion=data.identificacion;
      },()=>{
        Swal.fire({
          title: 'Error',
          text: 'No existen datos del Beneficiario',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.router.navigate(["/sport-sponsorship"]);
          }
        );
      },()=>{
        this.consultarRecompensas();
        this.consultarTorneos();
        this.consultarTitulos();
        this.consultaBeneficiario();
        this.consultaValoracion();
        if(this.deportista.identificacion=='0930071261'){//DAVID AGUERO
          this.video = this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/OQuviMREejU");
        }else if(this.deportista.identificacion=='0950414573') {//DANIEL AGUERO
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/t8053_-L5v4");
        }else if(this.deportista.identificacion=='0920788767') {//URL CAMILA Romero
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/1_u3n6kwIZk");
        }else if(this.deportista.identificacion=='1207826486') {//URL LUCIA YEPEZ
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/BHoGmc25E_Q");
        }else if(this.deportista.identificacion=='1311302408') {//URL CARLOS VAZQUES
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/zFLPiUV0G70");
        }else if(this.deportista.identificacion=='0931425128') {//URL PABLO  VIERA
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/qx12JC7Se10");
        }else if(this.deportista.identificacion=='0950531772') {//URL AARON  VIERA
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/MBN4byiyrjI");
        }else if(this.deportista.identificacion=='1711338655') {//URL ISRAEL TOBAR
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/rPQ5hoWSwos");
        }else if(this.deportista.identificacion=='0992665068001') {//URL GUAYAQUIL CITY
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/7gtG6dnhjPE");
        }else if(this.deportista.identificacion=='0103675138') {//URL ESTEBAN ENDERICA
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/qnmijhDRkdE");
        }else if(this.deportista.identificacion=='1717876112') {//URL BRAD SALAZAR
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/zN3k-acctZE");
        }else if(this.deportista.identificacion=='1726766650') {//URL FELIPE RIVADENEIRA
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/buZUallLSvo");
        }else {
          this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/QU6V5vZlq3E");
        }
      }
    )
  }

  getRecompensas(data:any,categoria:any){
    for(let dato of data){
      if(dato.categoria==categoria && dato.categoria!=''){
        return dato.detalle;
      }
    }
  }

  consultarRecompensas(){
    this.dataApiClient.consultaRecompensasPublico(this.idAuspicio).subscribe(
      (data:any)=>{
        this.deportista.recompensas=data;
      },null,()=>{

      }
    )
  }

  consultarTorneos(){
    this.dataApiClient.consultaTorneosPublico(this.idAuspicio).subscribe(
      (data:any)=>{
        this.deportista.torneos=data;
      },null,()=>{

      }
    )
  }

  consultaBeneficiario(){
    let cuenta ={
      "nomApe":'',
      "identificacion":this.deportista.identificacion,
      "disciplina":''
    }
    this.dataApiClient.consultaAuspiciosVigentes(cuenta).subscribe(
      (data:any)=>{
        this.deportista=data[0];
      },(error:Error)=>{
        console.log(error);
      },()=>{
        this.consultarRecompensas();
        this.consultarTorneos();
        this.consultarTitulos();
        this.consultaValoracion();
      }
    )
  }

  consultarTitulos(){
    this.dataApiClient.consultaTitulosPublico(this.identificacion).subscribe(
      (data:any)=>{
        this.deportista.titulos=data;
      },null,()=>{

      }
    )
  }

  retornarAnio(fecha:any){
    return fecha.split('-')[0];
  }

  consultaValoracion(){
    this.dataApiClient.consultaValoracionPublico(this.identificacion).subscribe(
      (data:any)=>{
        this.deportista.valoracion=data;
      },null,()=>{

      }
    )
  }
 
  CalculateAge(fechanac:any):number {
    if (fechanac) {
        let timeDiff = Math.abs(Date.now() - Date.parse(fechanac));
        return Math.ceil((timeDiff / (1000 * 3600 * 24)) / 365)-1;
    } else {
        return 0;
    } 
  }

  get_data(key:string){
    return localStorage.getItem(key)?.toString();
  }
  ngOnInit(): void {
  }

}
