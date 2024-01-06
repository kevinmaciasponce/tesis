import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { AdminService } from '../../services/admin.service';
import { AcountModalComponent } from '../acount-modal/acount-modal.component';
import { RoleModalComponent } from '../role-modal/role-modal.component';

@Component({
  selector: 'app-createaccount',
  templateUrl: './createaccount.component.html',
  styleUrls: ['./createaccount.component.css']
})
export class CreateaccountComponent implements OnInit {
  @ViewChild('AcountSort') sort!: MatSort;
  @ViewChild('paginatorAcount') paginator!: MatPaginator;
  displayedColumnsAcount: string[] = ['id', 'nombres', 'apellidos', 'correo','activo','acciones'];
  dataSourceAcount: MatTableDataSource<any>;
  user:User;
  persona:any;

  constructor(public modalService: NgbModal, 
    private storage:StorageService,
    private admin:AdminService,
    private _snackBar: MatSnackBar) {
      this.consultarPersonas();
      this.dataSourceAcount = new MatTableDataSource(this.persona);
      this.user=this.storage.getCurrentSession();

     }

  ngOnInit(): void {
  }

  



  eliminar(estado:any,$event:any){
    console.log($event.checked);
    let reg:any;
    $event.checked?reg='Activa':reg='Inactiva';
    let cuenta:any = {
      "email":estado.correo,
      "clave":'Multiplo.1',
      "usuarioCreacion":this.user.user.identificacion,
      "personalInterno":estado.idPersInterno,
      "idCuenta":estado.usuario
    }
    if($event.checked){
      cuenta = {...cuenta, 
        "cuentaActiva":'S'
      }
    }else{
      cuenta = {...cuenta, 
        "cuentaActiva":'N'
      }
    }
    console.log(cuenta);
    this.admin.administraCuentaInterna(cuenta,this.user).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        console.log(error);
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this._snackBar.open('Cuenta '+reg,'', {
          duration: 1000,
          panelClass: 'custom-css-class',
          horizontalPosition: 'end',
        });
        this.consultarPersonas();
      }
    )
  }

  asignarRoles(cedula:any,usuario:any){
    this.set_data('roles',cedula);
    this.set_data('usuario',usuario);
    const modal = this.modalService.open(RoleModalComponent, { 
      windowClass: 'my-class'
    }).result.finally(
    ()=>{
      this.consultarPersonas();
    }
  );
  }


  applyFilterAcount(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceAcount.filter = filterValue.trim().toLowerCase();
  }
  openModal(cedula:any){
    this.set_data('ced',cedula);
    const modal = this.modalService.open(AcountModalComponent, { 
      windowClass: 'my-class'
  }).result.finally(
    ()=>{
      this.consultarPersonas();
    }
  );
  }
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  consultarPersonas(){
    this.admin.consultarPersonalInterno({},this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.persona=data;
      },(error:Error)=>{
        
      },()=>{
        this.dataSourceAcount = new MatTableDataSource(this.persona);
        this.dataSourceAcount.sort= this.sort;
        this.dataSourceAcount.paginator= this.paginator;
      }
    )
  }

}
