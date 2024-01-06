import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { User } from 'src/app/models/user';
import { PorconfirmarInterface } from 'src/app/shared/models/consulta_Inversiones/porconfirmar.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { StorageService } from 'src/app/shared/service/storage.service';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import * as XLSX from 'xlsx';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';


@Component({
  selector: 'app-confirmationpage',
  templateUrl: './confirmationpage.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class ConfirmationpageComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  porconfirmar: Array<PorconfirmarInterface> = [];
  ide: string | undefined;
  registrationForm!: FormGroup;
  aux: any | undefined;
  get db() { return this.registrationForm.controls }
  user: User;
  isOpenConsulta=false;
  
  detallestatus!: detallestatusInterface;
  constructor(private dataApiPublics: ConsultasPublicasService,
    private dataApiInvestments: ConsultaInversionesService,
    private formBuilder: FormBuilder,
    private localstorage: StorageService,
    public modalService: NgbModal) {
    this.user = this.localstorage.getCurrentSession();
   
  }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;
    
    this.getCatalogos();
    this.registrationForm = this.formBuilder.group({
      numsol: [null],
      codProyecto: [null],
      riesgos: [null],
      sectors: [null]
    });
    this.consultar();
  }
  name = 'Inversiones por confirmar.xlsx';
  exportToExcel(): void {
    let element = document.getElementById('season-tble');
    const worksheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element);
    const book: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(book, worksheet, 'Sheet1');
    XLSX.writeFile(book, this.name);
  }
  consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.dataApiInvestments.ConsultaPorconfirmar(cuenta, this.user).subscribe(
      (data: Array<PorconfirmarInterface>) => {
        this.porconfirmar = data;
        console.log(data);
      });
      this.dataApiPublics.DetalleStatus('cn').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
        });
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
      identificacion: this.ide,
      numeroSolicitud: this.db['numsol'].value,
      codProyecto: this.aux,
      idTipoCalificacion: this.db['riesgos'].value,
      idActividad: this.db['sectors'].value
    };
    console.log(datos);
    return datos;
  }

  getCatalogos(): void {
    this.dataApiPublics.ConsultaSector().subscribe(
      (data: Array<SectorInterface>) => {
        this.sectores = data;
      });

    this.dataApiPublics.ConsultaRiesgo().subscribe(
      (data: Array<RiesgosInterface>) => {
        this.calificaciones = data;
      });

    this.dataApiPublics.ConsultaEmpresas().subscribe(
      (data: Array<EmpresasInterface>) => {
        this.empresas = data;
      });
  }
  toggleState(dato:boolean){
    return !dato;
  }
  


  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }
  goMasdetalle(numsol:any,codproyecto:any){
    let cuenta:any;
    cuenta={
      numSol:numsol,
      tipo:1,
      codProyecto:null,
    }
    this.set_data('amortizacion',JSON.stringify(cuenta));
    this.set_data('proyecto',codproyecto);
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class'
  });
  }

}
