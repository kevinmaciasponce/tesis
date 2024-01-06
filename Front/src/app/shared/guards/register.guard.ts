import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import Swal from 'sweetalert2';
import { StorageService } from '../service/storage.service';

@Injectable({
  providedIn: 'root'
})

export class RegisterGuard implements CanActivate {
  user!:User;
  constructor (private router:Router,private store:StorageService){
    this.user = this.store.getCurrentSession();
  }
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      this.user = this.store.getCurrentSession();
      if(!this.store.isAuthenticated()){
        this.router.navigate(["/register/iniciar_sesion"]);
        return false;
      }
      console.log("/"+route.routeConfig?.path);
      //Si algun rol del usuario se encuentra dentro de la ruta a la que se desea ingresar 
      if(this.user.user.roles.some((data:any)=>
      {
        console.log(data.ruta);
        return data.ruta==="/"+route.routeConfig?.path;
      }
      ))
      {
        return true;  
      }else{
        Swal.fire({
          title: 'Alerta',
          text: "No tiene permisos para acceder a este módulo",
          icon: 'warning',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.router.navigate(["/"]);
          }
        )
        return false;  
      }
  }
}
