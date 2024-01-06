import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-role-modal',
  templateUrl: './role-modal.component.html',
  styleUrls: ['./role-modal.component.css']
})
export class RoleModalComponent implements OnInit {
  ced:any;
  cargando:any=false;
  user:User;
  rolesUsuario = new MatTableDataSource();
  displayedColumns: string[] = ['idRol', 'nombre', 'eliminar'];
  rolesSistema = new MatTableDataSource();
  displayedColumnsSistema: string[] = ['idRol', 'nombre', 'agregar'];
  usuario:any
  @ViewChild('paginatorUser') paginatorUser!: MatPaginator;
  @ViewChild('paginatorSystem') paginatorSystem!: MatPaginator;

  constructor(
    private admin:AdminService,
    private storage:StorageService,
    private _snackBar: MatSnackBar
    ) { 
      this.ced=this.get_data('roles');
      this.usuario=this.get_data('usuario');
      this.user=this.storage.getCurrentSession();
      this.consultarRoles();
    }

  ngOnInit(): void {

  }
  
  consultarRoles(){
    console.log(this.usuario);
    this.admin.consultarRolxEmpleado(this.usuario,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.rolesU=data;
        this.rolesU.sort((a,b)=>{ return a.idRol-b.idRol})
        this.rolesUsuario = new MatTableDataSource(this.rolesU);
        this.rolesUsuario.paginator= this.paginatorUser;
      },null,()=>{
        this.consultarRolesSistema();
      }
    )
  }
  rolesU:any[]=[];
  rolesSis:any[]=[];
  consultarRolesSistema(){
    this.admin.consultarRolSistema(this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.rolesSis=data;
        let dat:any[]=[];
        for(let i =0;i<this.rolesSis.length;i++){
          if(!this.rolesU.some((item:any)=>{return item.idRol==this.rolesSis[i].idRol})){
            dat.push(this.rolesSis[i]);
          }
        }
        console.log(dat);
        console.log(this.rolesUsuario);
        this.rolesSistema = new MatTableDataSource(dat);
        this.rolesSistema.paginator= this.paginatorSystem;
        if(this.rolesU.length==0){
          this.rolesSistema = new MatTableDataSource(data);
          this.rolesSistema.paginator= this.paginatorSystem;
          return;
        }
      }
    )
  }


  agregarRol(rol:any){
    let form={
      idRol:rol,
      cuenta:this.usuario
    }
    this.cargando=true;
    console.log(form)
    this.admin.agregarRoles(form,this.user).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
      },()=>{
        this.cargando=false;

        this._snackBar.open('Se ha asignado el rol ','', {
          duration: 1000,
          panelClass: 'custom-css-class',
          horizontalPosition: 'end',
        });


        this.consultarRoles();
      }
    )
  }

  eliminarRol(rol:any){
    let form={
      idRol:rol,
      cuenta:this.usuario
    }
    this.cargando=true;
    this.admin.eliminarRoles(form,this.user).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
      },()=>{
        this.cargando=false;
        this._snackBar.open('Se ha eliminado el rol ','', {
          duration: 1000,
          panelClass: 'custom-css-class',
          horizontalPosition: 'end',
        });

        this.consultarRoles();
      }
    )
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }

}
