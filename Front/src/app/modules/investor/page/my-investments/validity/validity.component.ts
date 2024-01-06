import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { delay, Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { EnprocesoInterface } from 'src/app/shared/models/consulta_Inversiones/enproceso.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { proyectoInterface } from 'src/app/shared/models/proyecto.interface';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';

import html2canvas from 'html2canvas';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import { InvestorReportComponent } from 'src/app/modules/reports/components/investor-report/investor-report.component';
@Component({
  selector: 'app-validity',
  templateUrl: './validity.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class ValidityComponent implements OnInit {
  isOpenConsulta!:any;
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  vigentes: Array<any> = [];
  enprocesosta:any;
  consultar$: Observable<any> | undefined;
  registrationForm!: FormGroup;
  ide: string | undefined;
  aux: any | undefined;
  defect: string | undefined;
  get db() { return this.registrationForm.controls }
  user: User;
  detallestatus!: detallestatusInterface ;
  print!:boolean;

  constructor(private rutaActiva: ActivatedRoute,private router: Router,
    private formBuilder: FormBuilder,
    private dataApiInvestments: ConsultaInversionesService,
    private localstorage: StorageService,
    private dataApiPublics: ConsultasPublicasService,
    public modalService: NgbModal) {
    this.user = this.localstorage.getCurrentSession();
    this.getCatalogos();
    localStorage.removeItem('proyecto');
    localStorage.removeItem('ruta');
    localStorage.removeItem('financiamiento');
    localStorage.removeItem('numero_solicitud');
    this.print=false;
  }

  name = 'Inversiones en proceso.xlsx';
  exportToExcel(): void {
    let element = document.getElementById('season-tble');
    const worksheet: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element);
    const book: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(book, worksheet, 'Sheet1');
    XLSX.writeFile(book, this.name);
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
toggleState2(dato:boolean){
  this.print=dato;
  (async () => { 
    this.captureScreen()
    await delay(1000);
    this.print=false;
})();
  return !dato;
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
    datos = {
      numeroSolicitud: numsol,
    };
    this.dataApiInvestments.ContinuarProcess(datos, this.user).subscribe(
      (data: proyectoInterface) => {
        this.obtenerLink(data);
        this.set_data('proyecto',codope);
        this.set_data('numero_solicitud',numsol);   
      });
    
  }
  obtenerLink(item: proyectoInterface) {
    console.log(item.datosInversionIngresados);
      if (item.datosInversionIngresados.tablaAmortizacion == false) {
        this.router.navigate(['investor/indicate']);
        return;
      }
      if (item.datosInversionIngresados.formulario == false) {
        this.router.navigate(['investor/complete']);
        return;
      }
      if (item.datosInversionIngresados.documentacion == false) {
        // this.router.navigate(['investor/load']);  cambiar por url de formulario
        this.router.navigate(['investor/complete']);
        return;
      }
      if (item.datosInversionIngresados.pago == false) {
        // this.router.navigate(['investor/investement']); cambiar por url de formulario
        this.router.navigate(['investor/complete']);
        return;
      }
  }

  goMasdetalle(numsol:any,codproyecto:any){
    let datos: any;
    datos = {
      "numSol": numsol,
      "codProyect": null,
      "tipo": 1
    };
    console.log(datos);
    this.set_data('amortizacion',JSON.stringify(datos));
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class'
  });
  }
  toggleState(dato:boolean){
    return !dato;
  }

  consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.dataApiInvestments.ConsultaVigente(cuenta, this.user).subscribe(
      (data: Array<EnprocesoInterface>) => {
        this.vigentes = data;
        console.log(data);
        
      });
      this.dataApiPublics.DetalleStatus('VG').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
          console.log(data);
        });
  }

  // showreport(){
  //   const modal = this.modalService.open(InvestorReportComponent, { 
  //     windowClass: 'my-class'
  // });
  // }

}
