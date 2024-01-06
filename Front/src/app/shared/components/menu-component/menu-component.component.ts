import { ThisReceiver } from '@angular/compiler';
import { Component, DoCheck, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { StorageService } from '../../service/storage.service';
import { MenuService } from '../../services/menu.service';
import { UrlMenuService } from '../../services/url-menu.service';

@Component({
  selector: 'app-menu-component',
  templateUrl: './menu-component.component.html',
  styleUrls: ['./menu-component.component.css']
})
export class MenuComponentComponent implements OnInit,DoCheck {
  isOpenMenu=false;
  constructor(private router: Router,
    private storage:StorageService,
    private menus:MenuService,
    private urls:UrlMenuService) { }
  
  user!:User;
  url:any;
  home:any;
  menu:any[]=[];
  usermenu!:any;
  rutaRol!:any;
  ngOnInit(): void {
    this.user=this.storage.getCurrentSession();
    this.home=Object.values(this.user.user.menu).filter( function (data){
      return data.nombre == "INICIO"
    });
    this.usermenu=Object.values(this.user.user.menu).filter( function (data){
      return data.nombre == "SOLICITUDES"
    });
    this.usermenu=Object.values(this.user.user.menu).filter( function (data){
      return data.nombre == "AUSPICIOS"
    });
    console.log(this.usermenu)
    //ARREGLAR ERROR
    this.rutaRol=this.user.user.ruta;
    this.menu=this.usermenu;
    for(let menu of this.usermenu){
      this.menu=Object.values(menu.subMenu);
    }
    this.menu.sort((n1,n2)=>{
      return n1.orden - n2.orden;
    });
  }
  
  ngDoCheck(): void {
    this.menus.getMenu$().subscribe(
      (data:any)=>{
        this.menu=data;
        this.menu.sort((a:any,b:any)=>{return a.orden-b.orden});
      },(error:Error)=>{
        console.log(error);
      }
    )
    this.urls.geturl$().subscribe(
      (data:any)=>{
        console.log(data);
        this.url=data;
      },(error:Error)=>{
        console.log(error);
      }
    )
  }

  urlActual:any;
  goToRoute(route:String){
    var arrayDeCadenas = this.url.split("/");
    this.urlActual= route;
    console.log( this.urlActual);
    this.router.navigate([ '/'+arrayDeCadenas[1]+''+route]);
  }

  toggleState(dato:boolean){
    return !dato;
  }
  
  obtenerurlactual():string{
   return this.urlActual;
  }
}

export interface menu {
  nombre: string;
  descripcion: string;
  url: string;
  orden: number;
  subMenu: any;
  operacion: string;
}