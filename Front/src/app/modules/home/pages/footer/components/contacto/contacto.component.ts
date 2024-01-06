import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-contacto',
  templateUrl: './contacto.component.html',
  styleUrls: ['./contacto.component.css']
})
export class ContactoComponent implements OnInit {
  mensajeG: any;
  submitted: boolean = false;
  guardando: boolean = false;
  cargando: boolean = false;
  get db() { return this.registrationForm.controls }
  constructor(private dataApiPublics: ConsultasPublicasService,
    private formBuilder: FormBuilder,private router: Router,
    ) {
     
     }

  ngOnInit(): void {
    
  }

  registrationForm: FormGroup = new FormGroup({
    nombres: new FormControl(null, [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
    ]),
    email: new FormControl(null, [
      Validators.required,
      Validators.email
    ]),
    cedula: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(13)
    ]),
    telefono: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10)
    ]),
    ciudad: new FormControl(null, [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(20),
    ]),
    motivo: new FormControl(null, [
      Validators.required,
    ]),
    mensaje: new FormControl(null, [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(100)
    ]),
    registrarse: new FormControl(false, [
      Validators.required,
      Validators.pattern('true')
    ]),
  }
  );
  
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

  enviar(){
    if(this.guardando) {
      return;
    }
    console.warn(this.registrationForm.value);
    this.submitted = true;
    this.cargando = true;
    
    if (!this.registrationForm.valid
    ) {
      this.cargando = false;
      console.log(this.registrationForm.errors)
      return;
    }
   
    let datos: any;
    datos = {
      nombres: this.db['nombres'].value,
      identificacion:this.db['cedula'].value,
      email: this.db['email'].value,
      telefono: this.db['telefono'].value,
      ciudad: this.db['ciudad'].value,
      motivo: this.db['motivo'].value,
      mensaje: this.db['mensaje'].value   
    };
    this.dataApiPublics.saveFormContact(datos).subscribe(
      (data: infom) => {
        this.mensajeG = data;
        this.cargando = false;
        this.success();
        this.router.navigateByUrl('/#choise-oportunit')
      });
  }
  success() {

    Swal.fire({
      icon: 'success',
      title: this.mensajeG.mensaje,
      text: 'Éxito  !',
    })
  }
  keyPressNumbers(event: { which: any; keyCode: any; preventDefault: () => void; }) {
    var charCode = (event.which) ? event.which : event.keyCode;
    // Only Numbers 0-9
    if ((charCode < 48 || charCode > 57)) {
      event.preventDefault();
      return false;
    } else {
      return true;
    }
  }

}
interface infom {
  mensaje: String
}