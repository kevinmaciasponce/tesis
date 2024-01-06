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
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { CargaArchivosModalComponent } from '../../components/carga-archivos-modal/carga-archivos-modal.component';
import { TransferDetailsModalComponent } from '../../components/transfer-details-modal/transfer-details-modal.component';
import * as XLSX from 'xlsx';
import { TransferFundsModalComponent } from '../../components/transfer-funds-modal/transfer-funds-modal.component';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
@Component({
  selector: 'app-proyectsmodals',
  templateUrl: './proyectsmodals.component.html',
  styleUrls: ['./proyectsmodals.component.css']
})
export class ProyectsmodalsComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  selectedRowIndex: number = -1;
  valid: Array<any> = [];
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
    private datePipe: DatePipe,
    private dataApiPublics: ConsultasPublicasService
    ) {
    this.user = this.localstorage.getCurrentSession(); 
    
    this.shadow=false;

  }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;
    this.registrationForm = this.formBuilder.group({
      codProyecto2: [null],
      riesgos: [null],
      sectors: [null]
    })
    this.getCatalogos();
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
    console.log(this.db['codProyecto2'].value);
    if (this.db['codProyecto2'].value == 'null') {
      this.aux = "";
    }
    else {
      this.aux = this.db['codProyecto2'].value;
    }
    let datos: any;

    datos = {
      codProyecto: this.aux,
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

      this.dataApiPublics.ConsultaEmpresas().subscribe(
        (data: Array<EmpresasInterface>) => {
          this.empresas = data;
        });
    // this.dataApiInvestments.ConsultaEmpresasxestado("PFC").subscribe(
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

  goMasdetalle3(codp:any, insol:any,nomp:any){
    let  requestmanager:any;
    
    requestmanager= {
      codProyecto: codp,
      nomProyecto:nomp,
      inSolicitud: insol,
      estado:'VG'
    }
    console.log(requestmanager);
    //envia


    let  dataSend:any;
    this.analystservice.analystValidDetails(requestmanager.codProyecto,this.user).subscribe(
      (data: any)=>{
        console.log(data);
        dataSend=data;
      },(error:Error)=>{
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.set_data('requestintransitmanager',JSON.stringify(requestmanager));
        this.set_data('dataModal',JSON.stringify(dataSend));
        const modal = this.modalService.open(TransferDetailsModalComponent, { 
          windowClass: 'my-class'
      });
      }
    )
  
  }


  goTablaAmortizacion(codProyecto:any){
    console.log(codProyecto);
    let cuenta:any;
    cuenta={
      numSol:null,
      tipo:3,
      codProyecto:codProyecto,
    }
    console.log(cuenta);
    this.set_data('amortizacion',JSON.stringify(cuenta));
    this.set_data('tipoTabla',3);
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class'
  });
  }

  consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
    this.analystservice.ConsultaVigenteAgrupadas(cuenta, this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.valid = data;
      },(error : Error)=>{
        console.log(error.message);
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
      this.dataApiPublics.DetalleStatus('ex').subscribe(
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
    if(this.db['codProyecto'].value==null||this.db['codProyecto'].value=='null'||this.valid==null){
      Swal.fire({
        title: 'Error!',
        text: "Debe consultar por nombre de empresa",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    let form={
      codProyecto: this.valid[0].codProyecto,
      nomProyecto: this.valid[0].nombreEmpresa,
      montoRecaudado:this.valid[0].inversionRealizada
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
 

  goMasdetalle2(codp:any, insol:any,nomp:any){
    let  requestmanager:any;
    requestmanager= {
      codProyecto: codp,
      nomProyecto:nomp,
      inSolicitud: insol,
      estado:'SFC'
    }
    //envia
    this.set_data('requestintransitmanager',JSON.stringify(requestmanager));
    const modal = this.modalService.open(TransferDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  
  }
}

interface conciliacionrecepterInterface{
  numeroConciliacion: number,
  totalConciliado: string
}
