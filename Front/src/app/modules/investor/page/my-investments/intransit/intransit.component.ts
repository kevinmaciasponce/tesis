import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { intransitInvestmentInterface } from 'src/app/shared/models/consulta_Inversiones/entransito.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import * as XLSX from 'xlsx';

import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';


@Component({
  selector: 'app-intransit',
  templateUrl: './intransit.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class IntransitComponent implements OnInit {
  sectores: SectorInterface[]| undefined;
  calificaciones: RiesgosInterface[]| undefined;
  empresas: EmpresasInterface[]| undefined;
  registrationForm!: FormGroup;
  aux: any | undefined;
  isOpenConsulta!:any;
  user!: User;
  solicitudes!: Array<any>;
  detallestatus!: detallestatusInterface;
  get db() { return this.registrationForm.controls }
  constructor(private dataApiPublics: ConsultasPublicasService,
    private dataApiInvestments: ConsultaInversionesService,
    private formBuilder: FormBuilder,
    private localstorage: StorageService,
    public modalService: NgbModal) { }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    
    this.getCatalogos();
    
    this.registrationForm = this.formBuilder.group({
      numsol: [null],
      codProyecto: [null],
      riesgos: [null],
      sectors: [null]
    });
    this.consultar();
  }

  toggleState(dato:boolean){
    return !dato;
  }
  name = 'Inversiones en transito.xlsx';
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
    this.dataApiInvestments.ConsultaIntransit(cuenta, this.user).subscribe(
      (data: Array<any>) => {
        this.solicitudes = data;
        console.log(data);
      });
      this.dataApiPublics.DetalleStatus('TN').subscribe(
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
      identificacion: this.user.user.identificacion,
      numeroSolicitud: this.db['numsol'].value,
      codProyecto: this.aux,
      idTipoCalificacion: this.db['riesgos'].value,
      idActividad: this.db['sectors'].value
      
    };
    console.log(datos);
    return datos;
  }
  set_data(key:string,codp:any){
    try{
      localStorage.setItem(key,codp);
    } catch(e){
      console.log(e);
    }
  }
  
  captureScreen() {
    let data = document.getElementById('season-tble');
    html2canvas(data as any).then(canvas => {
        var imgWidth = 210;
        var pageHeight = 295;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        let pdfData = new jsPDF('p', 'mm', 'a4');
        var position = 0;
        pdfData.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight)
        pdfData.save(`MyPdf.pdf`);
    });
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
}
