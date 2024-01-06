import { Component, OnInit, ViewChild } from '@angular/core';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { AdminService } from '../../services/admin.service';
import {MatTableDataSource} from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ParametersModalComponent } from '../../components/parameters-modal/parameters-modal.component';
@Component({
  selector: 'app-investor',
  templateUrl: './investor.component.html',
  styleUrls: ['./investor.component.css']
})
export class InvestorComponent implements OnInit {
  user:User;
  pais:any=[];
  ciudad:any=[];
  paisSelect:any=52;
  banco:any=[];
  dataSource: MatTableDataSource<any>;
  dataSourceCat: MatTableDataSource<any>;
  dataSourceDis: MatTableDataSource<any>;
  displayedColumns: string[] = ['id', 'nombre', 'activo', 'edit'];
  displayedColumnsCat: string[] = ['id', 'pais', 'gentilicio', 'iso', 'estado','edit'];
  displayedColumnsDis: string[] = ['id', 'ciudad', 'activo', 'edit'];
 

  @ViewChild('bancoSort') sort1!: MatSort;
  @ViewChild('paginatorMod') paginator1!: MatPaginator;

  @ViewChild('paginatorCat') paginator!: MatPaginator;
  @ViewChild('paisSort') sort!: MatSort;

  @ViewChild('ciudadSort') sort2!: MatSort;
  @ViewChild('paginatorciudad') paginator2!: MatPaginator;


  constructor(private admin:AdminService,
    public modalService: NgbModal,
    private storage:StorageService,
    private _snackBar: MatSnackBar) {
    this.user=this.storage.getCurrentSession();
    this.dataSource = new MatTableDataSource(this.banco);
    this.dataSourceCat = new MatTableDataSource(this.pais);
    this.dataSourceDis = new MatTableDataSource(this.ciudad);
   }
  
   
  ngOnInit(): void {
    this.getElements();
   }
 

   eliminar(estado:any,$event:any,idTabla:any){
    console.log($event.checked);
    let reg:any;
    let status:any;
    $event.checked?reg='A':reg='I';
    $event.checked?status='Activado':status='Inactivado';
    if(idTabla=='pais'){
      console.log(this.getPais(estado,reg));
      this.admin.agregarPais(this.getPais(estado,reg),this.user).subscribe(
        (data:any)=>{
          console.log(data);
        },(error:Error)=>{
          console.log(error);
        },()=>{
          this._snackBar.open('Registro '+status,'', {
            duration: 1000,
            panelClass: 'custom-css-class',
            horizontalPosition: 'end',
          });
          this.getElements();
        }
      )
    }else if(idTabla=='ciudad'){
      this.admin.agregarCiudad(this.getCiudad(estado,reg),this.user).subscribe(
        (data:any)=>{
        },(error:Error)=>{
          console.log(error);
        },()=>{
          this._snackBar.open('Registro '+status,'', {
            duration: 1000,
            panelClass: 'custom-css-class',
            horizontalPosition: 'end',
          });
          this.getElements();
        }
      )
      
    }else if(idTabla=='banco'){
      this.admin.agregarBanco(this.getBanco(estado,reg),this.user).subscribe(
        (data:any)=>{
        },(error:Error)=>{
          console.log(error);
        },()=>{
          this._snackBar.open('Registro '+status,'', {
            duration: 1000,
            panelClass: 'custom-css-class',
            horizontalPosition: 'end',
          });
          this.getElements();
        }
      )
    }
  }

  getPais(dato:any,estado:any){
    return {
      "pais":dato.pais,
      "gentilicio":dato.gentilicio,
      "iso":dato.iso,
      "estado":estado,
      "idNacionalidad":dato.idNacionalidad,
    }
  }

  getCiudad(dato:any,estado:any){
    return {
      "ciudad":dato.ciudad,
      "idCiudad":dato.id,
      "pais":{
          "idNacionalidad":this.paisSelect
      },
      "estado":estado
    }
  }
  getBanco(dato:any,estado:any){
    return {
      "nombre":dato.nombre,
      "estado":estado,
      "idBanco":dato.idBanco,
      }
  }

 


  getElements(){
    this.admin.consultaBanco(this.user).subscribe(
      (data:any)=>{
        this.banco=data;
        this.dataSource = new MatTableDataSource(this.banco);
        this.dataSource.paginator = this.paginator1;
        this.dataSource.sort=this.sort1;
      },(error:Error)=>{

      },()=>{

      }
    )


    this.admin.consultaPais().subscribe(
      (data:any)=>{
        this.pais=data;
        this.dataSourceCat = new MatTableDataSource(this.pais);
        this.dataSourceCat.paginator = this.paginator;
        this.dataSourceCat.sort= this.sort;
      },(error:Error)=>{
        
      },()=>{
        this.consultarCiudad();
      }
    )
  }

  consultarCiudad(){
    this.admin.consultaCiudad(this.paisSelect).subscribe(
      (data:any)=>{
        this.ciudad=data;
        this.dataSourceDis = new MatTableDataSource(this.ciudad);
        this.dataSourceDis.paginator = this.paginator2;
        this.dataSourceDis.sort = this.sort2;
      },(error:Error)=>{
        
      },()=>{

      }
    )
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  openModal(accion:any,tipotabla:any,dato:any){
    this.set_data('accion',accion);
    this.set_data('tipo',tipotabla);
    console.log(dato);
    this.set_data('dato',JSON.stringify(dato));
    const modal = this.modalService.open(ParametersModalComponent, { 
      windowClass: 'my-class'
  }).result.finally(
    ()=>{
      this.getElements();
    }
  );
  
  }


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  applyFilterCat(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceCat.filter = filterValue.trim().toLowerCase();
  }
  applyFilterDis(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceDis.filter = filterValue.trim().toLowerCase();
  }
}
