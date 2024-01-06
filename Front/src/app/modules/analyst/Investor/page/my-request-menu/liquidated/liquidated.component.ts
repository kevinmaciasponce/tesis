import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { consolidarInterface } from 'src/app/shared/models/analyst_interfaces/consolidar.interface';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import * as XLSX from 'xlsx';
import { UsersDetailsModalComponent } from '../../components/users-details-modal/users-details-modal.component';

@Component({
  selector: 'app-liquidated',
  templateUrl: './liquidated.component.html',
  styleUrls: ['./liquidated.component.css']
})
export class LiquidatedComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  selectedRowIndex: number = -1;
  liquidated: Array<any> = [];
  isOpenConsulta=false;
  ide: string | undefined;
  registrationForm!: FormGroup;
  shadow!:boolean;
  aux: any | undefined;
  get db() { return this.registrationForm.controls }
  user: User;
  totalconciliado:number=0;
  nums:any;
  listaenviar:Array<consolidarInterface> = [];
  detallestatus!: detallestatusInterface ;
  checkbox = document.getElementById(
    'tableid',
  ) as HTMLInputElement;
  constructor(
    private dataApiInvestments: ConsultaInversionesService, 
    private formBuilder: FormBuilder, 
    private localstorage: StorageService,
    public modalService: NgbModal,
    private router:Router,
    private analystservice: AnalystService,
    private dataApiPublics: ConsultasPublicasService
    ) {
    this.user = this.localstorage.getCurrentSession();  
  }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;
    this.registrationForm = this.formBuilder.group({
      codProyecto: [null],
      riesgos: [null],
      sectors: [null]
    })
    this.getCatalogos();
    this.consultar();
  }

 
  name = 'ExcelSheet.xlsx';
  exportToExcel(): void {
    let element = document.getElementById('season-tble');
    const worksheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element);

    const book: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(book, worksheet, 'Sheet1');

    XLSX.writeFile(book, this.name);
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
      idTipoCalificacion: this.db['riesgos'].value,
      idActividad: this.db['sectors'].value
    };
    console.log(datos);
    return datos;
  }


  toggleState(dato:boolean){
    return !dato;
  }
  // sfc

  getCatalogos(): void {
    this.dataApiInvestments.ConsultaSector().subscribe(
      (data: Array<SectorInterface>) => {
        this.sectores = data;
      });

    this.dataApiInvestments.ConsultaRiesgo().subscribe(
      (data: Array<RiesgosInterface>) => {
        this.calificaciones = data;
      });

      this.dataApiInvestments.ConsultaEmpresasxestado("EXI").subscribe(
        (data: Array<EmpresasInterface>) => {
          this.empresas = data;
        });
  }

  consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.analystservice.ConsultaLiquidadas(cuenta, this.user).subscribe(
      (data: Array<any>) => {
        this.liquidated = data;
        console.log(data);
      },(error : Error)=>{
        console.log(error.message);
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
      this.dataApiPublics.DetalleStatus('sfc').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
        });
        this.listaenviar=[];
  }
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  goDetallesUser(identificacion:any, solicitud:Number){
    this.set_data('investorData',identificacion);
    this.set_data('solicitud',solicitud);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }

 

}

