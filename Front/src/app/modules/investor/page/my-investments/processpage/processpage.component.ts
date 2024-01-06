
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { ActivatedRoute, Router } from '@angular/router';

import { EnprocesoInterface } from 'src/app/shared/models/consulta_Inversiones/enproceso.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';

import { StorageService } from 'src/app/shared/service/storage.service';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import * as XLSX from 'xlsx';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';


import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';


@Component({
  selector: 'app-processpage',
  templateUrl: './processpage.component.html',
  styleUrls: [ '../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class ProcesspageComponent implements OnInit {
  isOpenConsulta=false;
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  enproceso: Array<EnprocesoInterface> = [];
  enprocesosta:any;
  consultar$: Observable<any> | undefined;
  registrationForm!: FormGroup;
  ide: string | undefined;
  aux: any | undefined;
  defect: string | undefined;
  get db() { return this.registrationForm.controls }
  user: User;
  detallestatus!: detallestatusInterface ;


  constructor(private rutaActiva: ActivatedRoute,private router: Router,
    private formBuilder: FormBuilder,
    private dataApiInvestments: ConsultaInversionesService,
    private localstorage: StorageService,
    private dataApiPublics: ConsultasPublicasService) {
    this.user = this.localstorage.getCurrentSession();
    this.getCatalogos();
    localStorage.removeItem('proyecto');
    localStorage.removeItem('ruta');
    localStorage.removeItem('financiamiento');
    localStorage.removeItem('numero_solicitud');
  }

  name = 'Inversiones en proceso.xlsx';
  exportToExcel(): void {
    let element = document.getElementById('season-tble');
    const worksheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element);
    const book: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(book, worksheet, 'Sheet1');
    XLSX.writeFile(book, this.name);
  }

  toggleState(dato:boolean){
    return !dato;
  }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;

    this.registrationForm = this.formBuilder.group({
      codProyecto: [],
      riesgos: [],
      sectors: []
    })
    this.consultar();
  }

  captureScreen() {
    let data = document.getElementById('season-tble');
    
    html2canvas(data as any).then(canvas => {
        var imgWidth = 150;
        var pageHeight = 225;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        let pdfData = new jsPDF('p', 'mm', 'a4');
        var position = 25;
        pdfData.text("Tabla de amortizacion", 5,20);
        pdfData.addImage(contentDataURL, 'PNG', 5, position, imgWidth, imgHeight)
        pdfData.save(`MyPdf.pdf`);
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
      codProyecto: this.aux,
      idTipoCalificacion: this.db['riesgos'].value,
      idActividad: this.db['sectors'].value
    };
    console.log(datos);
    return datos;
  }


  getCatalogos(): void {
    this.dataApiInvestments.ConsultaSector().subscribe(
      (data: Array<SectorInterface>) => {
        this.sectores = data;
      });

    this.dataApiInvestments.ConsultaRiesgo().subscribe(
      (data: Array<RiesgosInterface>) => {
        this.calificaciones = data;
      });

    this.dataApiInvestments.ConsultaEmpresas().subscribe(
      (data: Array<EmpresasInterface>) => {
        this.empresas = data;
      });   
  }
set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    
    } catch(e){
      console.log(e);
    }
  }

  continuar(numsol: any,codope:String): void {
    let datos: any;
    const formData = new FormData();
    formData.append('numSol', numsol);
    // this.dataApiInvestments.ContinuarProcess(formData, this.user).subscribe(
    //   (data: any) => {
    //     this.obtenerLink(data);
    //     this.set_data('proyecto',codope);
    //     this.set_data('numero_solicitud',numsol);   
    //   });
      this.set_data('proyecto',codope);
      this.set_data('numero_solicitud',numsol); 
      this.router.navigate(['investor/complete']);
  }
  obtenerLink(item: any) {
    console.log(item.datosInversionIngresados);
      if (item.tablaAmortizacion == false) {
        this.router.navigate(['investor/indicate']);
        return;
      }
      if (item.formulario == false) {
        this.router.navigate(['investor/complete']);
        return;
      }
      if (item.documentacion == false) {
        // this.router.navigate(['investor/load']);  cambiar por url de formulario
        this.router.navigate(['investor/complete']);
        return;
      }
      if (item.pago == false) {
        // this.router.navigate(['investor/investement']); cambiar por url de formulario
        this.router.navigate(['investor/complete']);
        return;
      }
      
     
    
  }
  consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.dataApiInvestments.ConsultaEnproceso(cuenta, this.user).subscribe(
      (data: Array<EnprocesoInterface>) => {
        this.enproceso = data;
        console.log(data);
        
      });
      this.dataApiPublics.DetalleStatus('bo').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
          console.log(data);
        });
  }
}
