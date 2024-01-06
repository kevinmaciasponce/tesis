import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-parameters-modal',
  templateUrl: './parameters-modal.component.html',
  styleUrls: ['./parameters-modal.component.css']
})
export class ParametersModalComponent implements OnInit {
  dato:any;

  lenght:any=1;

  //Datos formulario pais banco ciudad
  nombre:any='';
  id:any;
  gentilicio:any='';
  iso:any='';
  estado:any='';
  descripcion:any='';

  user:User;
  //cargando
  cargando:any=false;
  
  tipotabla:any;
  tipoAccion:any;

  pais:any;
  paisSelect:any=52;

  constructor(private activeModal:NgbActiveModal,
    private admin:AdminService,
    private storage:StorageService) {
      this.user = this.storage.getCurrentSession();
      this.tipoAccion = this.get_data('accion');
      this.tipotabla = this.get_data('tipo');
      this.dato=JSON.parse(this.get_data('dato'));
      if(this.tipotabla=='ciudad'){
        this.consultarPais();
        this.nombre=this.dato.ciudad;
        this.id=this.dato.id;
        this.dato.estado=='A'?this.estado=true:this.estado=false;
      }
      if(this.tipotabla=='pais'){
        this.nombre=this.dato.pais;
        this.gentilicio=this.dato.gentilicio;
        this.iso=this.dato.iso;
        this.id=this.dato.idNacionalidad;
        this.dato.estado=='A'?this.estado=true:this.estado=false;
      }
      if(this.tipotabla=='banco'){
        this.nombre=this.dato.nombre;
        this.id=this.dato.idBanco;
        this.dato.estado=='A'?this.estado=true:this.estado=false;
      }

    }

  ngOnInit(): void {
    localStorage.removeItem('accion');
    localStorage.removeItem('tipo');
    localStorage.removeItem('dato');
  }

  close(){
    this.activeModal.dismiss();
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }

  add(){
    if(this.tipotabla=='pais'){
      this.addPais();
    }else if(this.tipotabla=='ciudad'){
      this.addCiudad();
    }else if(this.tipotabla=='banco'){
      this.addBanco();
    }
  }

  addBanco(){
    let cuenta:any={
      "nombre":this.nombre,
      "estado":this.estado==true?'A':'I'
    }
    if(this.tipoAccion=='actualizar'){
      cuenta = {
        "idBanco":this.id,
        ...cuenta
      }
    }
    this.admin.agregarBanco(cuenta,this.user).subscribe(
      (data:any)=>{
        Swal.fire({
          title: 'Éxito!',
          text: "Se ha agregado la información",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.activeModal.dismiss();
          }
        )
      },(error:Error)=>{
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        })
      },()=>{

      }
    )
  }

  addPais(){
    let cuenta:any={
      "pais":this.nombre,
      "gentilicio":this.gentilicio,
      "iso":this.iso,
      "estado":this.estado==true?'A':'I'
    }
    if(this.tipoAccion=='actualizar'){
      cuenta = {
        "idNacionalidad":this.id,
        ...cuenta
      }
    }

    this.admin.agregarPais(cuenta,this.user).subscribe(
      (data:any)=>{
        Swal.fire({
          title: 'Éxito!',
          text: "Se ha agregado la información",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.activeModal.dismiss();
          }
        )
      },(error:Error)=>{
        console.log(error);
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        })
      },()=>{
        
      }
    )
  }

  addCiudad(){
    let cuenta:any={
      
      "ciudad":this.nombre,
      "pais":{
          "idNacionalidad":this.paisSelect
      },
      "estado":this.estado==true?'A':'I'
    }
    if(this.tipoAccion=='actualizar'){
      cuenta = {
        "idCiudad":this.id,
        ...cuenta
      }
    }

    console.log(cuenta);
    this.admin.agregarCiudad(cuenta,this.user).subscribe(
      (data:any)=>{
        Swal.fire({
          title: 'Éxito!',
          text: "Se ha agregado la información",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.activeModal.dismiss();
          }
        )
      },(error:Error)=>{
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        })
      },()=>{
        
      }
    )
  }

  consultarPais(){
    this.admin.consultaPais().subscribe(
      (data:any)=>{
        this.pais=data;
      },(error:Error)=>{
        
      },()=>{

      }
    )
  }

}
