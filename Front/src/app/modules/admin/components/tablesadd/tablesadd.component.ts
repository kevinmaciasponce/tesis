import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-tablesadd',
  templateUrl: './tablesadd.component.html',
  styleUrls: ['./tablesadd.component.css']
})
export class TablesaddComponent implements OnInit, OnDestroy {
  dato:any;
  lenght:any=0;
  nombre:any='';
  id:any;
  user:User;
  descripcion:any='';
  cargando:any=false;
  constructor( private activeModal:NgbActiveModal,
    private admin:AdminService,
    private storage:StorageService
   ) { 
    this.dato = JSON.parse(this.get_data('dato'));
    this.user = this.storage.getCurrentSession();
    console.log(this.dato);
    this.dato.tipotabla=="Disciplina"? this.lenght=15:this.lenght=25;
    console.log(this.lenght);
    if(this.dato.id){
      this.id=this.dato.id;
      this.nombre=this.dato.nombre;
      this.descripcion=this.dato.descripcion;
      if(this.descripcion==null){
        this.descripcion='';
      }
      console.log(this.dato);
    }else{
      console.log('No tiene id');
    }
  }
  ngOnDestroy(): void {
    localStorage.removeItem('dato');
  }

  ngOnInit(): void {
  }

  add(){
    this.cargando=true;
    console.log(this.getData());
    if(this.dato.tipotabla=='Categoría'){
      this.admin.agregarCategoria(this.getData(),this.user).subscribe(
        (data:any)=>{
          console.log(data);
          this.cargando=false;
          Swal.fire({
            title: 'Éxito!',
            text: "Se ha agregado la información",
            icon: 'success',
            confirmButtonText: 'Aceptar'
          }).then(
            ()=>{
              this.activeModal.dismiss();
            }
          );
        },(error:Error)=>{
          this.cargando=false;
          console.log(error);
          Swal.fire({
            title: 'Error!',
            text: error.message,
            icon: 'error',
            confirmButtonText: 'Aceptar'
          }
          );
        },()=>{
          
        }
      )
    }
    if(this.dato.tipotabla=='Modalidad'){
      this.admin.agregarModalidad(this.getData(),this.user).subscribe(
        (data:any)=>{
          console.log(data);
          this.cargando=false;
          Swal.fire({
            title: 'Éxito!',
            text: "Se ha agregado la información",
            icon: 'success',
            confirmButtonText: 'Aceptar'
          }).then(
            ()=>{
              this.activeModal.dismiss();
            }
          );
        },(error:Error)=>{
          console.log(error);
          this.cargando=false;
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
    if(this.dato.tipotabla=='Disciplina'){
     
      this.admin.agregarDisciplina(this.getData(),this.user).subscribe(
        (data:any)=>{
          console.log(data);
          this.cargando=false;
          Swal.fire({
            title: 'Éxito!',
            text: "Se ha agregado la información",
            icon: 'success',
            confirmButtonText: 'Aceptar'
          }).then(
            ()=>{
              this.activeModal.dismiss();
            }
          );
        },(error:Error)=>{
          console.log(error);
          this.cargando=false;
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
  }

  getData(){
    if(this.dato.id){
     return{
      id:this.id,
      nombre:this.nombre,
      descripcion:this.descripcion,
      activo:true
     }
    }else{
      return{
        nombre:this.nombre,
        descripcion:this.descripcion
       }
    }
  }

  close(){
    this.activeModal.dismiss();
  }
  get_data(key: any): any {
    return localStorage.getItem(key);
  }

}
