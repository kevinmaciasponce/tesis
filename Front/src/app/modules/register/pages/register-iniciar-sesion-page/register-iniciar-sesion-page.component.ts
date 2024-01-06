import { ErrorCorreoInterface } from 'src/app/shared/models/error-correo.interface';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import { Login } from 'src/app/models/Login';
import { ValidaLoginService } from 'src/app/services/valida-login.service';
import { User } from 'src/app/models/user';
import { Router } from '@angular/router';
import { StorageService } from 'src/app/shared/service/storage.service';

@Component({
  selector: 'app-register-iniciar-sesion-page',
  templateUrl: './register-iniciar-sesion-page.component.html',
  styleUrls: ['./register-iniciar-sesion-page.component.css']
})
export class RegisterIniciarSesionPageComponent implements OnInit {
  cargando!:boolean;
  user!:User;
  constructor(private service: ValidaLoginService,
    private router:Router,
    private storage:StorageService) 
    {
    this.cargando=false;
    }

  ngOnInit(): void {
   
  }

  pass_show ="fa fa-eye-slash";
  tipo ='password';
  mostrarPassword(){
    if( this.tipo =="password"){
     this.tipo = "text";
      this.pass_show ="fa fa-eye";
    }else {
      this.tipo = "password";
      this.pass_show ="fa fa-eye-slash";
    }
  } 
 
  registrationForm: FormGroup = new FormGroup({
    correo: new FormControl(null, [
      Validators.required,
      Validators.email
    ]),
    contrasenia: new FormControl(null, [
      Validators.required,
      Validators.pattern('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[*+$¡])[A-Za-z\d*+$¡].{6,}'),
      Validators.minLength(6),
      Validators.maxLength(12),
    ]),
  })

  obtenerEmail(): Login{
    let login: Login;
    login = {
      email:(this.registrationForm.get('correo')?.value as string),
      clave:(this.registrationForm.get('contrasenia')?.value as string)
    }
    return login;
  }

  successNotification(){
    Swal.fire('Exito!', "mensaje", 'success');
  }

  clickprobarservicio(){
    this.cargando=true;
    let email= this.obtenerEmail();
    this.service.validaLogin(email).subscribe(res =>{
      console.log(res);
      this.service.setToken(res.token);
      this.storage.setCurrentSession(res);
      this.user=res;
      }
    ,((err:ErrorCorreoInterface) =>{
      this.cargando=false;
      Swal.fire('Error!',err.error.mensaje, 'error');
      console.log(err);
    }),()=>{
      this.service.setToken(this.user.token);
      this.router.navigate(["/register/modules-access"]);
    }
    );
  }
}