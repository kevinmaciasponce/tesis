import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Project } from 'src/app/shared/models/Project';

@Component({
  selector: 'app-proyect',
  templateUrl: './proyect.component.html',
  styleUrls: ['./proyect.component.css']
})
export class ProyectComponent implements OnInit {

  ngOnInit(): void {
  }

  private localitems!: any;
  project: Project;
  safeURL: any;
  imgURL: any;
  p1:any = false;
  p2:any= false;
  p3:any= false;
  p4:any= false;
  constructor(private _sanitizer: DomSanitizer) {
    this.localitems = localStorage.getItem("project");
    const data: Project = JSON.parse(this.localitems);
    this.project = data;
    this.safeURL = this._sanitizer.bypassSecurityTrustResourceUrl(data.proyectoRutas[2].ruta);
    this.imgURL = this._sanitizer.bypassSecurityTrustResourceUrl(data.proyectoRutas[1].ruta);
    console.log(this.project)
  }

  convert(percent:any){
    if(percent[0]==','){
      return 0;
    }
     let num;
    try {
      num = Number(percent);
    } catch (error) {
      return 0;
    } 
    return num;
  }
  toggleState(dato:boolean){
    return !dato;
  }

}
