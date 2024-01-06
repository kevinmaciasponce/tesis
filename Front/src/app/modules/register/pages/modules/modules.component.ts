import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { data } from 'jquery';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { MenuService } from 'src/app/shared/services/menu.service';
import { UrlMenuService } from 'src/app/shared/services/url-menu.service';

@Component({
  selector: 'app-modules',
  templateUrl: './modules.component.html',
  styleUrls: ['./modules.component.css']
})
export class ModulesComponent implements OnInit {
  user: User;
  modules:any=[];
  constructor(
    private storage : StorageService,
    private router:Router,
    private menu:MenuService,
    private url:UrlMenuService
  ) {
    this.user = this.storage.getCurrentSession();
    this.modules.push({nombre:'INVERSIONISTA', descripcion:'INVERSIONISTA',ruta:'/investor/home',urlIco:'assets/images/register/modules/inversionista.png', active: false, menu:[]} );
    this.modules.push({nombre:'PROMOTOR', descripcion:'PROMOTOR',ruta:'/promoter/home',urlIco:'assets/images/register/modules/promotor.png', active: false, menu:[]} );
    this.modules.push({nombre:'ANALISTAOPER', descripcion:'ANALISTA OPERACIONES',ruta:'/analyst/home',urlIco:'assets/images/register/modules/analista.png', active: false, menu:[]});
    this.modules.push({nombre:'GERENTEGENERAL', descripcion:'GERENTE GENERAL',ruta:'/manager/home',urlIco:'assets/images/register/modules/gerentegeneral.png', active: false, menu:[]});
    this.modules.push({nombre:'GERENTEOPER', descripcion:'GERENTE OPERACIONES',ruta:'/managerOper/home',urlIco:'assets/images/register/modules/gerenteoper.png', active: false, menu:[]});
    this.modules.push({nombre:'REPRESENTANTE', descripcion:'AUSPICIO DEPORTIVO',ruta:'/agent/home',urlIco:'assets/images/register/modules/representante.png', active: false, menu:[]});
    this.modules.push({nombre:'ANALISTAAUSPICIO', descripcion:'ANALISTA AUSPICIO',ruta:'/sponsor-analyst/home',urlIco:'assets/images/register/modules/analistaauspicio.png', active: false, menu:[]});
    this.modules.push({nombre:'ADMINISTRADOR', descripcion:'ADMINISTRADOR',ruta:'/admin',urlIco:'assets/images/register/modules/boss.png', active: false, menu:[]});
    this.llenarmenu();
  }

  llenarmenu(){
    this.modules = this.modules.filter((data:any)=>{
      return this.user.user.roles.some((r:any)=> r.nombre === data.nombre)
    }).map(
      (data:any)=>{
        let aux:any;
        data.active = true;
        aux = this.user.user.roles.find((dato:any)=>{
          if(dato.nombre===data.nombre){
            return dato.menu
          }
         })
         if(aux.menu[0]){
          data.menu = aux.menu.filter((r:any)=>{
           if(r.nombre!='INICIO'){
            console.log(r.subMenu);
            return r.subMenu;
           };
          });
         }
        console.log(data);
        return data;
      }
    )
    console.log(this.modules);
  }

  gotoRoute(ruta:any,menu:any){
    console.log(ruta);
    this.user.user.menu = menu;
    this.url.agregarurl(ruta);
    if(menu[0]){
      this.menu.agregarMenu(menu[0].subMenu);
    }
    this.storage.setCurrentSession(this.user);
    this.router.navigate([ruta]);
  }
  ngOnInit(): void {
  }
}
