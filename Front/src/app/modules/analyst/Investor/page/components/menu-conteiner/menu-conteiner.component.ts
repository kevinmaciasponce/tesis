import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-conteiner',
  templateUrl: './menu-conteiner.component.html',
  styleUrls: ['./menu-conteiner.component.css']
})
export class MenuConteinerComponent implements OnInit {
  isOpenMenu=false;
  constructor(private router: Router) { }

  ngOnInit(): void {
    
  }

  goToRoute(route:String){
    this.router.navigate(["/analyst/"+route]);
  }

  
  toggleState(dato:boolean){
    return !dato;
  }

  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
  }
}
