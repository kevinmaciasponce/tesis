import { Component, OnInit } from '@angular/core';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
@Component({
  selector: 'app-register-forgot-password',
  templateUrl: './register-forgot-password.component.html',
  styleUrls: ['./register-forgot-password.component.css']
})
export class RegisterForgotPasswordComponent implements OnInit {
  submitted!: boolean;
  mensaje: any;
  cargando!:boolean;
  get db() { return this.registrationForm.controls }
  constructor(public consultas: ConsultasPublicasService,
    private routers: Router) { }
  registrationForm: FormGroup = new FormGroup({
    mail: new FormControl(null, [
      Validators.required,
      Validators.email
    ]),
    ide: new FormControl(null, [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
    ])});
    IsInvalid(control: string): boolean {
      if (this.registrationForm.get(control) != null) {
        let controlForm = this.registrationForm.get(control);
        return controlForm != null 
              ? ((controlForm.invalid && (controlForm.dirty || controlForm.touched)) 
                || controlForm.invalid && this.submitted )
              : false;
      }
      return false;
    }
  ngOnInit(): void {
    
  }

  forgot(){
    this.cargando=true;
    const formData = new FormData();
    formData.append('mail', this.db['mail'].value);
    formData.append('identificacion', this.db['ide'].value);

    this.submitted= true;
  this.consultas.olvideMiContraseña(formData).subscribe(
    (response: any) => {
      this.mensaje = response;
    }, (error: Error) => {
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: 'Error '+error.message,
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });

    }, () => {
      this.cargando=false;
      Swal.fire({
        title: 'Éxito!',
        text: this.mensaje.mensaje,
        icon: 'success',
        confirmButtonText: 'Aceptar'
      }).then(
        ()=>{this.routers.navigate(["/"])}
      )
    }
  )


  }
 


}
