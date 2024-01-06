import { ThisReceiver } from '@angular/compiler';
import { Component, OnInit } from '@angular/core';
import { MenuItem } from './menu-item';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  menuItems: MenuItem[] = [
    {
      label: 'Inicio',
      class:'btn_home',
      direccion: '/',
      showOnMobile: false,
      showOnTablet: true,
      showOnDesktop: true
    },
    {
      label: 'Quienes somos',
      class:'btn_menu',
      direccion: "#quienes_somos",
      showOnMobile: false,
      showOnTablet: true,
      showOnDesktop: true
    },
    {
      label: 'Invierte',
      class:'btn_menu',
      direccion: '#proyectos',
      showOnMobile: false,
      showOnTablet: true,
      showOnDesktop: true
    },
    {
      label: 'Solicitar financiamiento',
      class:'btn_menu',
      direccion: '/',
      showOnMobile: false,
      showOnTablet: true,
      showOnDesktop: true
    },
    {
      label: 'Iniciar Sesión',
      class:'btn_menu',
      direccion: "/register/iniciar_sesion",
      showOnMobile: false,
      showOnTablet: true,
      showOnDesktop: true
    },
    {
      label: 'Regístrate',
      class:'btn_registro',
      direccion: "/register",
      showOnMobile: false,
      showOnTablet: true,
      showOnDesktop: true
    },
  ];
  constructor() {
    this.set_data('ruta','');

   }

   set_data(key:string,data:string){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }


  ngOnInit(): void {
  }
}
