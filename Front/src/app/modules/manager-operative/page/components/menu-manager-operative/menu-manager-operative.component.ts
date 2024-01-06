import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-menu-manager-operative',
  templateUrl: './menu-manager-operative.component.html',
  styleUrls: ['./menu-manager-operative.component.css']
})
export class MenuManagerOperativeComponent implements OnInit {

  
  constructor(private router:Router) { }

  ngOnInit(): void {
  }

  goToRoute(route:String){
    this.router.navigate(["/managerOper/"+route]);
  }

  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
  }

}
