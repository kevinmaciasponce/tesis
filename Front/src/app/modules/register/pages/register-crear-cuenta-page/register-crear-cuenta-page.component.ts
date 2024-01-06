import { ErrorCorreoInterface } from 'src/app/shared/models/error-correo.interface';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ValidaEmailService } from 'src/app/services/valida-email.service';
import { ValidarCuentaInterface } from 'src/app/shared/models/validarcuenta.interface';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import { ComunicationService } from 'src/app/services/comunication.service';

@Component({
  selector: 'app-register-crear-cuenta-page',
  templateUrl: './register-crear-cuenta-page.component.html',
  styleUrls: ['./register-crear-cuenta-page.component.css']
})
export class RegisterCrearCuentaPageComponent implements OnInit {
  cargando!:boolean;
  url:string="register/";
  text_placeholder: String = "";
  select_mensaje:string="";

  constructor(private service: ValidaEmailService,private router:Router, 
   ) {
    this.cargando=false;
   }
   

   obtenerurlactual():string{
     var url = this.router.url;
     var arrayDeCadenas = url.split("/");
    return arrayDeCadenas[arrayDeCadenas.length-1];
   }


  ngOnInit(): void {
  }
 
  registrationForm: FormGroup = new FormGroup({
    registrarCorreo: new FormControl(null, [
      Validators.required,
      Validators.email
    ]),
  })

  

  obtenerEmail(): ValidarCuentaInterface{
    let email: ValidarCuentaInterface;
    email = {
      email:(this.registrationForm.get('registrarCorreo')?.value as string)
    }
    return email;
  }

  successNotification(){
    Swal.fire('Exito!', "mensaje", 'success');
  }

  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }

  set_data(key:string,data:string){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  clickprobarservicio(){
    if (!this.registrationForm.valid
      ) {
        console.log(this.registrationForm.errors);
        Swal.fire('Error!',"Debe ingresar un correo electronico valido", 'error');
        return;
      }
      this.cargando=true;
    let email= this.obtenerEmail();
    this.service.validaCorreoExist2(email).subscribe(res =>{
      this.cargando=false;
        console.log(res.mensaje);
        if(res.mensaje=="ok"){
          var url= this.url+this.obtenerurlactual();
          this.set_data('url',this.obtenerEmail().email);
          console.log(url);
          this.router.navigate([url]);
        } 
      }
    ,((err:ErrorCorreoInterface) =>{
      this.cargando=false;
      Swal.fire('Error!',err.error.mensaje, 'error');
    }),()=>{
      this.cargando=false;
    }
    );
  }
}
