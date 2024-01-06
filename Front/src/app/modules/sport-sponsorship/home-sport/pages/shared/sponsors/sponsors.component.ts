import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-sponsors',
  templateUrl: './sponsors.component.html',
  styleUrls: ['./sponsors.component.css']
})

export class SponsorsComponent implements OnInit {
  safeURL: any;
  edad:any;
  nombre:any;
  video:any;
  monto:any;
  calificacion:any;
  porcenatjeprueba:any
  porcenatjeprueba2:any
  constructor(private _sanitizer: DomSanitizer ) { 
    this.llenarDatos();
    this.porcenatjeprueba=0;
    this.porcenatjeprueba2=0;
    if(this.get_data('beneficiario')=='DAVID'){
    this.video = this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/upGvOJWvhm0");
    }else{
      this.video=this._sanitizer.bypassSecurityTrustResourceUrl("https://www.youtube.com/embed/99uUSfdgte8");
    }
  }

  llenarDatos(){
    if(this.get_data('beneficiario')=='DAVID'){
      this.calificacion="MD-DAJ-2022-0497-OF";
      this.edad="15 años";
      this.nombre="DAVID ANDRÉS AGÜERO MATEO"
      this.monto="35,200.00"
    }else{
      this.calificacion="MD-DAJ-2022-0496-OF";
      this.edad="13 años";
      this.nombre="DANIEL ANDRÉS AGÜERO MATEO"
      this.monto="36,700.00"
    }
  }
  get_data(key:string){
    return localStorage.getItem(key)?.toString();
  }
  ngOnInit(): void {
  }

}
