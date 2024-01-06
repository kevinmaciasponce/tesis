import { Component, OnInit } from '@angular/core';
import {  FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute,  Router } from '@angular/router';
import { matchValidator } from 'src/app/providers/CustomValidators';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register-switch-password',
  templateUrl: './register-switch-password.component.html',
  styleUrls: ['./register-switch-password.component.css']
})
export class RegisterSwitchPasswordComponent implements OnInit {
  token!: string;
  mensaje!: any;
  validacion!: any;
  cargando!:boolean;
  submitted!: boolean;
  get db() { return this.registrationForm.controls }
  get conf() { return this.registrationForm.get('conf'); }
  constructor(private route: ActivatedRoute,private routers: Router, public consultas: ConsultasPublicasService) {
    this.token = this.route.snapshot.paramMap.get("token") as string;
  }

  ngOnInit(): void {
    this.validarToken();
  }
  registrationForm: FormGroup = new FormGroup({
    pass: new FormControl(null, [
      Validators.required,
      Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$.,#/<>;:*!%?&])[A-Za-z\d$@$.,#/<>;:*!%?&].{6,}$'),
      Validators.minLength(3),
      Validators.maxLength(50),
      matchValidator('conf', true)

    ]),
    conf: new FormControl(null, [
      Validators.required,
      matchValidator('pass')
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
    pass_hidden ="fa fa-eye-slash";
    pass_show ="fa fa-eye";
    tipo ='password';
    mostrarPassword(){
      if( this.tipo =="password"){
       this.tipo = "text";
        this.pass_show ="fa fa-eye";
      }else {
        this.tipo = "password";
        this.pass_hidden ="fa fa-eye-slash";
      }
    } 
  cambiarContrasenia() {
    this.submitted=true;
    if (!this.registrationForm.valid) {
      console.log(this.registrationForm.errors)
      return;
    }
    this.submitted=false;
    this.cargando=true;
    const formData = new FormData();
    formData.append('token', this.token);
    formData.append('pass', this.db['conf'].value);
    this.consultas.cambiarContraseña(formData).subscribe(
      (response: any) => {
        this.cargando=false;
        this.validacion = response;
      }, (error: Error) => {
        this.cargando=false;
        Swal.fire(
          'Error',
          error.message,
          'error'
        ).then(
          ()=>{this.routers.navigate(["/"])}
        )
      }, () => {
        this.cargando=false;
        Swal.fire(
          'Éxito',
          this.validacion.mensaje,
          'success'
        ).then(
          ()=>{this.routers.navigate(["/"])}
        )
      }
    );
  }
  validarToken() {


    const formData = new FormData();
    formData.append('token', this.token);

    this.consultas.validarCambiarContraseña(formData).subscribe(
      (response: any) => {
        this.validacion = response.mensaje;
      }, (error: Error) => {
        Swal.fire(
          'Good job!',
          error.message,
          'success'
        ).then(
          ()=>{this.routers.navigate(["/"])}
        )
      }, () => {

      }
    )
  }

}
