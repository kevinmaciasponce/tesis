import { Component, DoCheck, Input, OnInit} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { User } from 'src/app/models/user';
import { ValidaLoginService } from 'src/app/services/valida-login.service';
import { StorageService } from '../../service/storage.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})

export class NavbarComponent implements OnInit,DoCheck {
  @Input() nombre:string | undefined;
  val:string | undefined;
  user:User | undefined;
  registrationForm!: FormGroup;
  showMenu = false;
  usuarioAutenticado:boolean=false;
  isOpenConsulta=false;
  isOpenAutenticated=false;
  constructor(private router: Router,private storage:StorageService,
    private formBuilder: FormBuilder,private loginservice: ValidaLoginService) { 
    this.isConected();
    this.isOpenConsulta=false;
    this.isOpenAutenticated=false;
  }
  
  isConected(){
    if(this.loginservice.getToken()){
      this.user=this.storage.getCurrentSession();
      this.usuarioAutenticado=this.storage.isAuthenticated();
      let data = this.user?.user.nombres.split(" ");
      if(this.user?.user.identificacion.length==13){
        this.val= this.user?.user.nombres;
        return
      }
      if(data){
        this.val= data![0]+' '+data![data!.length-1];
      }
     
    }else{
      this.user=undefined;
      this.usuarioAutenticado=false;
      this.val=undefined;
    }
  }


  // ngOnDestroy(): void {
  //   this.storage.removeCurrentSession();
  // }

  ngDoCheck(): void {
    this.isConected(); 
    //this.separar_datos();
  }

  toggleState(dato:boolean){
    return !dato;
  }

  goToRoute(){
    this.router.navigate(["/register/modules-access"]);
  }

 
  goto(data:string){
    window.location.replace("#"+data);
  }

  separar_datos(){
    let data;
    data = this.user?.user.nombres.split(" ");
    this.nombre=data![0];
  }

  // revisar para cerrar sesion cuando se cierre la pagina
  // @HostListener('window:beforeunload')
  // onUnload() {
  //   this.storage.removeCurrentSession();
  //   return false;
  // }

  ngOnInit(): void {
    this.registrationForm= this.formBuilder.group({
      identificacion: this.val,
    });
    console.log(this.val);
    console.log(this.user);
    console.log(this.val);
  }

  cerrarsession(){
    this.loginservice.deleteToken();
    this.isOpenAutenticated=false;
    this.storage.removeCurrentSession();
    this.nombre=undefined;
    this.router.navigate(["/"]);
  }


  goto_register(){
    this.router.navigateByUrl('register');
  }
  set_data(key:string,data:string){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  financiamiento(){
      this.set_data('financiamiento','financiamiento');
      this.router.navigateByUrl('register/crear-cuenta/juridica');
  }

  onClickMenu(e:any, isMenuOpen: any) {
    if ( e.target.id === 'dot-menu') {
      console.log(e);
      console.log(isMenuOpen)
      let navbar = document.getElementById('menuMobile')
      if (isMenuOpen) {
        navbar?.classList.add('show-menu')
        navbar?.classList.remove('hide-menu')
      } else {
        navbar?.classList.add('hide-menu')
        navbar?.classList.remove('show-menu')
      }
    }
  }

  closeMenu(){
    this.isOpenConsulta=false;
    this.isOpenAutenticated=false;
  }

  
  isMatMenuOpen:any = false;
  isMatMenu2Open:any= false;
  enteredButton:any= false;
  prevButtonTrigger:any;
  menuenter() {
    console.log('menu enter');
    this.isMatMenuOpen = true;
    if (this.isMatMenu2Open) {
      this.isMatMenu2Open = false;
    }
  }

  menuLeave(trigger:any, button:any) {
    console.log('menu leave');
    setTimeout(() => {
      if (!this.isMatMenu2Open && !this.enteredButton) {
        this.isMatMenuOpen = false;
        trigger.closeMenu();
        
      } else {
        this.isMatMenuOpen = false;
      }
    }, 80);
  }

  menu2enter() {
    this.isMatMenu2Open = true;
  }

  menu2Leave(trigger1:any, trigger2:any, button:any) {
    setTimeout(() => {
      if (this.isMatMenu2Open) {
        trigger1.closeMenu();
        this.isMatMenuOpen = false;
        this.isMatMenu2Open = false;
        this.enteredButton = false;
       
      } else {
        this.isMatMenu2Open = false;
        trigger2.closeMenu();
      }
    }, 100);
  }

  buttonEnter(trigger:any) {
    console.log('button enter');
    setTimeout(() => {
      if (this.prevButtonTrigger && this.prevButtonTrigger != trigger) {
        this.prevButtonTrigger.closeMenu();
        this.prevButtonTrigger = trigger;
        this.isMatMenuOpen = false;
        this.isMatMenu2Open = false;
        trigger.openMenu();
      } else if (!this.isMatMenuOpen) {
        this.enteredButton = true;
        this.prevButtonTrigger = trigger;
        trigger.openMenu();
      } else {
        this.enteredButton = true;
        this.prevButtonTrigger = trigger;
      }
    });
  }

  buttonLeave(trigger:any, button:any) {
    console.log('button leave');
    setTimeout(() => {
      if (this.enteredButton && !this.isMatMenuOpen) {
        trigger.closeMenu();
      }
      if (!this.isMatMenuOpen) {
        trigger.closeMenu();
      } else {
        this.enteredButton = false;
      }
    }, 100);
  }
}
