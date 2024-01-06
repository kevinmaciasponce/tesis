import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-conteiner',
  templateUrl: './menu-conteiner.component.html',
  styleUrls: ['./menu-conteiner.component.css']
})
export class MenuConteinerComponent implements OnInit {
  isOpenMenu=false;
  constructor(private router:Router) { }

  ngOnInit(): void {
  }

  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
  }

  toggleState(dato:boolean){
    return !dato;
  }

  // this.set_data('reportePago','Inversionista');  
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }


  goToRoute(route:String){
    localStorage.removeItem('project');
    localStorage.removeItem('proyecto');
    localStorage.removeItem('numero_solicitud');
    this.router.navigate(["/investor/"+route]);
  }

}
