import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-deprecation-date-modal',
  templateUrl: './deprecation-date-modal.component.html',
  styleUrls: ['./deprecation-date-modal.component.css']
})
export class DeprecationDateModalComponent implements OnInit,OnDestroy {
  tabla:any;
  user:User;
  date:any;
  metodo:any;
  cargando:any;
  nombrebtn:any
  mensaje:any;
  mensaje_error:any;
  constructor(
    private analystservice:AnalystService,
    private localstorage: StorageService,
    public activeModal: NgbActiveModal,) {
      this.cargando=false;
      this.user = this.localstorage.getCurrentSession(); 
      this.tabla= JSON.parse(this.get_data('tablaAmortizacion'));
      this.metodo=this.tabla.metodo;
      this.metodo=='put'?this.nombrebtn="Actualizar":this.nombrebtn='Crear';
      console.log(this.tabla);
   }

  ngOnDestroy(): void {
    localStorage.removeItem('tablaAmortizacion');
  }

  ngOnInit(): void {
  }

  get_data(key:string):any{
    return localStorage.getItem(key)?.toString();
  }

  registrationForm: FormGroup = new FormGroup({
    dateDeprecation: new FormControl(null, [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
    ]),
  });

  onSubmit(){
    this.metodo=='put'?this. actualizarFechas():this.crearFechas();
  }


  get db() { return this.registrationForm.controls };

  crearFechas(){
    this.cargando=true;
    this.tabla.fechaGeneracion=this.db['dateDeprecation'].value;
    console.log(this.tabla);
    this.analystservice.fechaGeneracionTablaAmortizacion(this.tabla,this.user).subscribe(
      (data: any) => {
        this.mensaje=data;
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
        
      },()=>{
        let res =this.mensaje.mensaje.search("Existen Solicitudes pendientes de aprobar por parte de Gerencia");
       if(res==0){
        this.cargando=false;
        Swal.fire({
          title: 'Exitoso!',
          text: this.mensaje.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.activeModal.dismiss();
          }
        );
        return;
       }
        this.analystservice.generaTablaAmortizacion(this.tabla,this.user).subscribe(
          (data:any)=>{
            this.mensaje = data;
            
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
              title: 'Exitoso!',
              text: this.mensaje.mensaje,
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
      );

  }

  actualizarFechas(){
    this.cargando=true;
    this.tabla.fechaGeneracion=this.db['dateDeprecation'].value;
    console.log(this.tabla);
    this.analystservice.fechaActualizaTablaAmortizacion(this.tabla,this.user).subscribe(
      (data:any)=>{
        this.mensaje=data;
        console.log(data);
      } , (error:Error)=>{
        this.cargando=false;
        this.mensaje_error=error.message;
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.cargando=false;
      let res =this.mensaje.mensaje.search("Existen Solicitudes pendientes de aprobar por parte de Gerencia");
       if(res==0){
        Swal.fire({
          title: 'Exitoso!',
          text: this.mensaje.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.activeModal.dismiss();
          }
        );
        return;
       }
        this.analystservice.actualizaTablaAmortizacion(this.tabla,this.user).subscribe(
          (data:any)=>{
            this.mensaje = data;
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
              title: 'Exitoso!',
              text: this.mensaje.mensaje,
              icon: 'success',
              confirmButtonText: 'Aceptar'
            }).then(
              ()=>{
                this.activeModal.dismiss();
              }
            )
          }
        )
      }
    );
  }
}
