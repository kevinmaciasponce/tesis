import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProyectosService } from 'src/app/shared/service/proyectos.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit  {
  open:any=false;
  public projects: any;
  constructor(private proyectosService: ProyectosService,private router:Router) {
    localStorage.removeItem('project');
    localStorage.removeItem('proyecto');
    localStorage.removeItem('ruta');
    localStorage.removeItem('financiamiento');
    localStorage.removeItem('numero_solicitud');
    localStorage.removeItem('id ');
   }
  status:string="AVANCE:";
  ngOnInit(): void {
    this.proyectosService.getProjects().subscribe(res => {
      this.projects = res.map((element: any) => {
        console.log(element.avanceInversion.porcentaje);
      
        return element
      });
      console.log(this.projects)
    }
    );
  }

  filtrarRutas(rutas:any){
    let ruta:any=rutas.filter(
      (element:any)=>{
        return element.name=='img_logo'
      }
    )[0].ruta;
    return ruta;
  }

  toggleState(dato:boolean){
    return !dato;
  }

  classtooltip ="tooltip_hide";
  tooltipshow(){
    if( this.classtooltip =="tooltip_"){
      this.classtooltip ="tooltip_hide";
    }else {
      this.classtooltip ="tooltip_";
    }
  }


  obtenernumero(numero:any):string{
    var arrayDeCadenas = numero.split(",");
   return arrayDeCadenas[0];
  }
  convert(percent:any){
    percent= percent+'';
    if(percent[0]==','){
      return 0;
    }
     let num;

    try {
      num = Number(this.obtenernumero(percent));
    } catch (error) {
      console.log(error)
      return 0;
    }
    return num;
  }

  handleViewMore(project: any) {
    localStorage.setItem('project', JSON.stringify(project));
    window.open('statics/proyects', '_blank')
  }
  
  set_data(key:string,data:any,idEmpresa:any){
    try{
      localStorage.setItem(key,data);
      localStorage.setItem('id',idEmpresa);
      this.router.navigate(["/investor/indicate"]);
    } catch(e){
      console.log(e);
    }
  }

  set_data2(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }
  
  goto_notify(){
    this.set_data2('notify','notify');
    this.router.navigateByUrl('register/iniciar_sesion');
  }

}
