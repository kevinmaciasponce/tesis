import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu-investor',
  templateUrl: './menu-investor.component.html',
  styleUrls: ['./menu-investor.component.css']
})
export class MenuInvestorComponent implements OnInit {

  estado:boolean=false;
  
  constructor(private router:Router) { 
   
  }

  ngOnInit(): void {
  }


  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
  }
  
}
