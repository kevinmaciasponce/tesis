import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-acount-modal',
  templateUrl: './acount-modal.component.html',
  styleUrls: ['./acount-modal.component.css']
})
export class AcountModalComponent implements OnInit, OnDestroy {
  cargando:any=false;
  idPersonaInterna:any=null;
  cuentaActiva:any='';
  user:User;
  regExpres:any =/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$.,#/<>;:*!%?&])[A-Za-z\d$@$.,#/<>;:*!%?&].{6,}$/;
  hide = true;
  persona:any;
  mensaje:any;
  constructor(private activeModal:NgbActiveModal,
    private adminService:AdminService,
    private storage:StorageService) {
      this.user=this.storage.getCurrentSession();
      this.idPersonaInterna=this.get_data('ced');
      this.consultarPersonaInterna();
   }
  ngOnDestroy(): void {
    localStorage.removeItem('ced');
  }

  ngOnInit(): void {
  }
  cedula:any = new FormControl('', [Validators.required]);
  nombres:any = new FormControl('', [Validators.required]);
  apellidos:any = new FormControl('', [Validators.required]);
  iniciales:any = new FormControl('', [Validators.required]);
  usuario:any = new FormControl('', );
  email = new FormControl('', [Validators.required, Validators.email]);
  clave = new FormControl('', [Validators.minLength(6), Validators.maxLength(12),
    Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$.,#/<>;:*!%?&])[A-Za-z\d$@$.,#/<>;:*!%?&].{6,}$')]);
  
    getErrorClaveMessage() {
    if (this.clave.hasError('pattern')) {
      return 'Contraseña no cumple con los requisitos';
    }
    return this.clave.hasError('required') ? 'Campo obligatorio' : '';
  }

  getErrorMessage() {
    if (this.email.hasError('required')) {
      return 'Campo obligatorio';
    }
    return this.email.hasError('email') ? 'Debe tener formato de correo' : '';
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }

  comprobar(){
    if(this.regExpres.test(this.clave)){
      console.log('clave correcta');
      return {
        estado:true,
        mensaje:'Clave correcta'
      }
    }else{
      console.log('clave incorrecta');
      return {
        estado:false,
        mensaje:'Clave incorrecta'
      }
    }
  }

  consultarPersonaInterna(){
    let cuenta={
      'identificacion':this.idPersonaInterna
    }
    this.adminService.consultarPersonalInterno(cuenta,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.persona = data[0];
      },null,()=>{
        this.cedula.setValue(this.persona.idPersInterno)
        this.nombres.setValue(this.persona.nombres);
        this.apellidos.setValue(this.persona.apellidos);
        this.email.setValue(this.persona.correo);
        this.persona.activo=='S'? this.cuentaActiva=true: this.cuentaActiva=false;
        this.usuario.setValue(this.persona.usuario);
        this.iniciales.setValue(this.persona.iniciales);
      }
    )
  }

  close(){
    this.activeModal.dismiss();
  }
  
  add(){
    console.log(this.getPersonaInterna());
    console.log(this.getCuentaInterna());
    this.cargando=true;
    this.adminService.administraPersonaInterna(this.getPersonaInterna(),this.user).subscribe(
        (data:any)=>{
          console.log(data);
        },(error:Error)=>{
          this.cargando=false;
          Swal.fire({
            title: 'Error!',
            text: error.message,
            icon: 'error',
            confirmButtonText: 'Aceptar'
          });
        },()=>{
          this.adminService.administraCuentaInterna(this.getCuentaInterna(),this.user).subscribe(
            (data:any)=>{
              console.log(data);
              this.mensaje=data;
            },(error:Error)=>{
              this.cargando=false;
              Swal.fire({
                title: 'Error!',
                text: error.message,
                icon: 'error',
                confirmButtonText: 'Aceptar'
              });
            },()=>{
              this.cargando=false;
              Swal.fire({
                title: 'Éxito!',
                text: 'Se creó la cuenta exitosamente',
                icon: 'success',
                confirmButtonText: 'Aceptar'
              }).then(
                ()=>{
                  this.activeModal.dismiss();
                }
              );
            }
          )
        }
      )
    

    
    
  }

  getPersonaInterna(){
    let cuenta:any ={
      "idPersInterno":this.cedula.value,
      "nombres": this.nombres.value,
      "apellidos": this.apellidos.value,
      "iniciales":this.iniciales.value,
      "usuarioCreacion":this.user.user.identificacion
    }
    return cuenta;
  }

  getCuentaInterna(){
    let cuenta:any ={
      "email":this.email.value,
      "usuarioCreacion":this.user.user.identificacion,
      "personalInterno":this.cedula.value
    }
    if(this.idPersonaInterna!='nuevo'){
      cuenta={...cuenta,
        "idCuenta":this.usuario.value,
      }
    }
    if(this.cuentaActiva){
      cuenta={
        ...cuenta,
        "cuentaActiva":'S',
      }
    }else{
      cuenta={
        ...cuenta,
        "cuentaActiva":'N',
      }
    }
    if(this.clave.value==''){
      cuenta={
        ...cuenta,
        "clave":'Multiplo.1',
      }
    }else{
      cuenta={
        ...cuenta,
        "clave":this.clave.value,
      }
    }
    return cuenta;
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
