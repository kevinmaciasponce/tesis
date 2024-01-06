import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { ModalmasdetallesComponent } from 'src/app/modules/manager/page/components/modalmasdetalles/modalmasdetalles.component';
import { analystConfirmarInterface } from 'src/app/shared/models/analyst_interfaces/confirm.interface';
import { consolidarInterface } from 'src/app/shared/models/analyst_interfaces/consolidar.interface';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { CargaArchivosModalComponent } from '../../components/carga-archivos-modal/carga-archivos-modal.component';
import * as XLSX from 'xlsx';
import { TransferFundsModalComponent } from '../../components/transfer-funds-modal/transfer-funds-modal.component';
import { RegisterpaymentsComponent } from '../../components/registerpayments/registerpayments.component';
import { DepreciationTablesComponent } from 'src/app/shared/components/depreciation-tables/depreciation-tables.component';
import { UsersDetailsModalComponent } from '../../components/users-details-modal/users-details-modal.component';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { ProyectsmodalsComponent } from '../../components/proyectsmodals/proyectsmodals.component';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import * as moment from 'moment';

@Component({
  selector: 'app-validpage',
  templateUrl: './validpage.component.html',
  styleUrls: ['./validpage.component.css']
})
export class ValidpageComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  selectedRowIndex: number = -1;
  solicitudes: Array<any> = [];
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
  isOpenConsulta=false;

  constructor(
    private formBuilder: FormBuilder, 
    private localstorage: StorageService,
    public modalService: NgbModal,
    private router:Router,
    private analystservice: AnalystService,
    
    private datePipe: DatePipe,
    private dataApiPublics: ConsultasPublicasService
    ) {
    this.user = this.localstorage.getCurrentSession(); 
    this.getCatalogos();
    this.shadow=false;

  }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;
    this.registrationForm = this.formBuilder.group({
      codProyecto: [null],
      riesgos: [null],
      sectors: [null]
    })
    this.consultar();
  }

  ngOnDestroy() {
    //borrar localstorage con la llave
    localStorage.removeItem("historial");
    localStorage.removeItem("conciliacion");
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

      // this.dataApiInvestments.ConsultaEmpresasxestado("EXI").subscribe(
      //   (data: Array<EmpresasInterface>) => {
      //     this.empresas = data;
      //   });
  }
  
  // @param string (string) : Fecha en formato YYYY-MM-DD
  // @return (string)       : Fecha en formato DD/MM/YYYY
  convertDateFormat(date: string) {
    var info = date.split('/');
    return info[2] + '-' + info[1] + '-' + info[0];
  }

  consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.analystservice.ConsultaVigente(cuenta, this.user).subscribe(
      (data: Array<analystConfirmarInterface>) => {
        this.solicitudes = data;
      },(error : Error)=>{
        console.log(error.message);
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
      this.dataApiPublics.DetalleStatus('EXI').subscribe(
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
  goMasdetalle(numsol:any){
    this.nums=this.set_data('historial',numsol);
    const modal = this.modalService.open(ModalmasdetallesComponent, { 
      windowClass: 'my-class'
  });
  }

  goCargaArchivos(){
    const modal = this.modalService.open(CargaArchivosModalComponent, { 
      windowClass: 'my-class'
  });
  }

  goGenerarTablaAmortizacion(codigo: string){
    var date = new Date();
    
    
    let tabla={
      "fechaGeneracion": this.datePipe.transform(date,"yyyy-MM-dd"),
      "codigoProyecto": codigo,
      "usuario": this.user.user.usuarioInterno
    }
    console.log(tabla);
    this.analystservice.fechaGeneracionTablaAmortizacion(tabla,this.user).subscribe(
      (data: any) => {
        Swal.fire({
          title: 'Exito!',
          text: data.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        Swal.fire({
          title: 'Error!',
          text: "Ya existe una fecha de generación de tabla de amortización",
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }
      );
  }

  goTransferModal(){
    let codigo=this.db['codProyecto'].value;
    if(this.db['codProyecto'].value==null||this.db['codProyecto'].value=='null'||this.solicitudes==null){
      Swal.fire({
        title: 'Error!',
        text: "Debe consultar por nombre de empresa",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    let form={
      codProyecto: this.solicitudes[0].codProyecto,
      nomProyecto: this.solicitudes[0].nombreEmpresa,
    }
    this.set_data('transferfound',JSON.stringify(form))
    const modal = this.modalService.open(TransferFundsModalComponent, { 
      windowClass: 'my-class'
  });
  }

  obtenerconciliacion():any{
    const conciliacion={
      usuario: this.user.user.usuarioInterno,
      solicitudes: this.listaenviar
    }
    return conciliacion;
  }
  

  goDetallesUser(numsol:any,identificacion:any){
    this.set_data('investorData',identificacion);
    this.set_data('solicitud',numsol);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }
  
  registerPayment(codp:any, insol:any,nomp:any,cuota:any,cuotaPago:any,plazo:any){
    let  requestmanager:any;
    requestmanager= {
      codProyecto: codp,
      nomProyecto: nomp,
      numSolicitud: insol,
      cuota:cuota,
      plazo:plazo,
      cuotaPago:cuotaPago,
      estado:'VG'
    }
    //envia
   
    this.set_data('registerPayments',JSON.stringify(requestmanager));
    const modal = this.modalService.open(RegisterpaymentsComponent, { 
      windowClass: 'my-class'
  }).result.finally(
    ()=>{
      this.consultar();
    }
  );
  }

  goTablaAmortizacionUsuario(numeroSol:string){
    let cuenta = this.obtenerTablaUsuario(numeroSol);
    console.log(cuenta);
    localStorage.removeItem('amortizacion');
    this.set_data('amortizacion',JSON.stringify(cuenta));
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class'
  });
  }
  functions(event:any){
    console.log(event.type);
    return event.type;
  }
  obtenerTablaUsuario(numSolicitud:string): any {
    let datos = {
      "numSol": numSolicitud,
      "codProyect": null,
      "tipo": 1
    };
    console.log(datos);
    return datos;
  }

  openProyectsvalidy(){
    const modal = this.modalService.open(ProyectsmodalsComponent, { 
      windowClass: 'my-class'
    });
  }

  goTablaAmortizacionPagare(numeroSol:string){
    let cuenta = this.obtenerTablaPagare(numeroSol);
    console.log(cuenta);
    localStorage.removeItem('cuenta');
    this.set_data('cuenta',JSON.stringify(cuenta));
    const modal = this.modalService.open(DepreciationTablesComponent, { 
      windowClass: 'my-class'
  });
  }

  sumFechas(f:any):String{
    var formato = f[5]+f[6]+" / "+f[8]+f[9]+" / "+f[0]+f[1]+f[2]+f[3];
    let date2 = new Date(formato);
    date2.setDate(date2.getDate() + 30);
    let parsedDate = moment(date2,"YYYY-MM-DD");
    return parsedDate.format("YYYY-MM-DD");
  }

  obtenerTablaPagare(numSolicitud:string): any {
    let datos = {
      "numSol": numSolicitud,
      "codProyect": null,
      "tipo": 2
    };
    console.log(datos);
    return datos;
  }

}

interface conciliacionrecepterInterface{
  numeroConciliacion: number,
  totalConciliado: string
}
