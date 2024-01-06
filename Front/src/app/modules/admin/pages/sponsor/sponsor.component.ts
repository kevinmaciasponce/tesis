import { Component, OnInit, ViewChild } from '@angular/core';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { AdminService } from '../../services/admin.service';
import {MatTableDataSource} from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TablesaddComponent } from '../../components/tablesadd/tablesadd.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-sponsor',
  templateUrl: './sponsor.component.html',
  styleUrls: ['./sponsor.component.css']
})
export class SponsorComponent implements OnInit  {
  user:User;
  categoria:any=[];
  disciplina:any=[];
  modalidad:any=[];
  dataSource: MatTableDataSource<any>;
  dataSourceCat: MatTableDataSource<any>;
  dataSourceDis: MatTableDataSource<any>;
  displayedColumns: string[] = ['id', 'nombre', 'descripcion', 'activo', 'edit'];
  displayedColumnsCat: string[] = ['id', 'nombre', 'descripcion', 'activo', 'edit'];
  displayedColumnsDis: string[] = ['id', 'nombre', 'descripcion', 'activo', 'edit'];
 

  @ViewChild('modalidadSort') sort1!: MatSort;
  @ViewChild('paginatorMod') paginator1!: MatPaginator;

  @ViewChild('paginatorCat') paginator!: MatPaginator;
  @ViewChild('categoriaSort') sort!: MatSort;

  @ViewChild('disciplinaSort') sort2!: MatSort;
  @ViewChild('paginatorDisciplina') paginator2!: MatPaginator;


  constructor(private admin:AdminService,
    public modalService: NgbModal,
    private storage:StorageService,
    private _snackBar: MatSnackBar) {
    this.user=this.storage.getCurrentSession();
    this.dataSource = new MatTableDataSource(this.modalidad);
    this.dataSourceCat = new MatTableDataSource(this.categoria);
    this.dataSourceDis = new MatTableDataSource(this.disciplina);
   }
  

   eliminar(estado:any,$event:any,idTabla:any){
    console.log($event.checked);
    let reg:any
    $event.checked?reg='Activo':reg='Inactivo';
    let cuenta:any={
      ...estado ,
      activo:$event.checked
    }
    if(estado.descripcion==''||estado.descripcion==null){
      cuenta={
        ...estado ,
        descripcion:"'",
        activo:$event.checked
      }
      console.log('entro');
    }
    
    console.log(cuenta);
    if(idTabla==1){
      this.admin.agregarModalidad(cuenta,this.user).subscribe(
        (data:any)=>{
          console.log(data);
        },(error:Error)=>{
          console.log(error);
        },
        ()=>{
          this._snackBar.open('Registro '+reg,'', {
            duration: 1000,
            panelClass: 'custom-css-class',
            horizontalPosition: 'end',
          });
          this.getElements();
        }
      )
    }else if(idTabla==2){
      this.admin.agregarCategoria(cuenta,this.user).subscribe(
        (data:any)=>{
          console.log(data);
        },(error:Error)=>{
          console.log(error);
        },
        ()=>{
          this._snackBar.open('Registro '+reg,'', {
            duration: 1000,
            panelClass: 'custom-css-class',
            horizontalPosition: 'end',
          });
          this.getElements();
        }
      )
    }else{
      this.admin.agregarDisciplina(cuenta,this.user).subscribe(
        (data:any)=>{
          console.log(data);
        },(error:Error)=>{
          console.log(error);
        },
        ()=>{
          this._snackBar.open('Registro '+reg,'', {
            duration: 1000,
            panelClass: 'custom-css-class',
            horizontalPosition: 'end',
          });
          this.getElements();
        }
      )
    }
    
  }

  ngOnInit(): void {
   this.getElements();
  }

  getElements(){
    this.admin.consultaModalidad(this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.modalidad=data;
        this.dataSource = new MatTableDataSource(this.modalidad);
        this.dataSource.paginator = this.paginator1;
        this.dataSource.sort=this.sort1;
      },(error:Error)=>{

      },()=>{

      }
    )

    this.admin.consultaDisciplina(this.user).subscribe(
      (data:any)=>{
        this.disciplina=data;
        this.dataSourceDis = new MatTableDataSource(this.disciplina);
        this.dataSourceDis.paginator = this.paginator2;
        this.dataSourceDis.sort = this.sort2;
      },(error:Error)=>{
        
      },()=>{

      }
    )

    this.admin.consultaCategoria(this.user).subscribe(
      (data:any)=>{
        this.categoria=data;
        this.dataSourceCat = new MatTableDataSource(this.categoria);
        this.dataSourceCat.paginator = this.paginator;
        this.dataSourceCat.sort= this.sort;
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

  openModal(dato:any,tabla:any){
    let datos = {
      tipotabla:tabla,
      ...dato
    }
    this.set_data('dato',JSON.stringify(datos));
    console.log(datos);
    const modal = this.modalService.open(TablesaddComponent, { 
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
