import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ValidaEmailService } from 'src/app/services/valida-email.service';
import { ErrorCorreoInterface } from 'src/app/shared/models/error-correo.interface';
import { ValidarCuentaInterface } from 'src/app/shared/models/validarcuenta.interface';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-crear-cuenta',
  templateUrl: './crear-cuenta.component.html',
  styleUrls: ['./crear-cuenta.component.css']
})
export class CrearCuentaComponent implements OnInit {
  cargando:any;
  url:string="register/";
  text_placeholder: String = "";
  select_mensaje:string="";
  constructor(private service: ValidaEmailService,private router:Router,) { 
    
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
  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
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
          this.set_data('url',this.obtenerEmail().email);
          this.router.navigate(['/sport-sponsorship/register/form']);
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
