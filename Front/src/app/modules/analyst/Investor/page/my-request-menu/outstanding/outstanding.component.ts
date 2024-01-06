import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import * as XLSX from 'xlsx';
@Component({
  selector: 'app-outstanding',
  templateUrl: './outstanding.component.html',
  styleUrls: ['./outstanding.component.css']
})
export class OutstandingComponent implements OnInit {
  detallestatus!: any ;
  isOpenConsulta:any;
  registrationForm!: FormGroup;
  solicitudes: Array<any> = [];
  empresas: Array<any> = [];
  user: User;
  aux: any | undefined;
  datos:any;
  get db() { return this.registrationForm.controls }
  constructor(
    private formBuilder: FormBuilder, 
    private analystservice: AnalystService,
    private dataApiPublics: ConsultasPublicasService,
    private localstorage: StorageService,
    private router:Router,) {
      this.user = this.localstorage.getCurrentSession(); 
     }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.registrationForm = this.formBuilder.group({
      codProyecto: [null],
      riesgos: [null],
      sectors: [null]
    })
    this.consultar();
  }


  obtenerData(): any {
    if (this.db['codProyecto'].value == 'null') {
      this.aux = null;
    }
    else {
      this.aux = this.db['codProyecto'].value;
    }
    let datos: any;
    datos = {
      codProyecto: this.aux,
    /*  numeroSolicitud: this.db['numsol'].value,
      idTipoCalificacion: this.db['riesgos'].value,
      idActividad: this.db['sectors'].value*/
    };
    console.log(datos);
    return datos;
  }
  consultar(){
    
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.analystservice.consultaPendienteRevisionAnalyst(cuenta,this.user).subscribe(
      (data: Array<any>) => {
        this.solicitudes = data;
        console.log(data);
      },(error : Error)=>{
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
      this.dataApiPublics.DetalleStatus('prg').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
        });
      this.getCatalogos();


}
getCatalogos(): void {
 /* this.dataApiPublics.DetalleStatus('sfc').subscribe(
    (data: any) => {
      this.detallestatus = data;
    }); */
  this.dataApiPublics.ConsultaEmpresas().subscribe(
    (data: Array<any>) => {
      this.empresas = data;
    });
}

toggleState(dato:boolean){
  return !dato;
}
name = 'ExcelSheet.xlsx';
exportToExcel(): void {
  let element = document.getElementById('season-tble');
  const worksheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element);

  const book: XLSX.WorkBook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(book, worksheet, 'Sheet1');

  XLSX.writeFile(book, this.name);
}
}
