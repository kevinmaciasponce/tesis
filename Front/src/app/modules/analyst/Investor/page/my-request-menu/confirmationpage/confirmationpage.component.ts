import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
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
import * as XLSX from 'xlsx';
import { UsersDetailsModalComponent } from '../../components/users-details-modal/users-details-modal.component';

@Component({
  selector: 'app-confirmationpage',
  templateUrl: './confirmationpage.component.html',
  styleUrls: ['./confirmationpage.component.css']
})
export class ConfirmationpageComponent implements OnInit, OnDestroy {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  isOpenConsulta=false;
  isOpenExcel=false;
  holderMonto="";
  file?: any;
  selectedRowIndex: number = -1;
  porconfirmar: Array<any> = [];
  ide: string | undefined;
  cargando!:boolean;
  registrationForm!: FormGroup;
  shadow!:boolean;
  aux: any | undefined;
  get db() { return this.registrationForm.controls }
  user: User;
  Successfull:any;
  totalconciliadoTransaction:number=0;
  nums:any;
  archivosSeleccionados:any = [];
  tablaExcel:any;
  monto:any[]=[];
  nombredoc:string="Carga tu documento"
  listaenviar:Array<consolidarInterface> = [];
  detallestatus!: detallestatusInterface ;
  checkbox = document.getElementById(
    'tableid',
  ) as HTMLInputElement;

  
  
  constructor(private dataApiInvestments: ConsultaInversionesService,
    private formBuilder: FormBuilder,
    private localstorage: StorageService,
    private dataApiPublics: ConsultasPublicasService,
    public modalService: NgbModal,
    private router:Router,
    private analystservice: AnalystService) {
    this.user = this.localstorage.getCurrentSession(); this.getCatalogos();
    this.shadow=false;
    this.cargando=false;
  }


  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;
    this.registrationForm = this.formBuilder.group({
      numsol: [null],
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

  focusin(event:any){return event.type}


  registrationForm2: FormGroup = new FormGroup({
    filesa: new FormControl(null, [
      Validators.required,
    ]),    
  }
  );
  

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

  toggleState(dato:boolean){
    return !dato;
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

    this.dataApiInvestments.ConsultaEmpresasxestado("AV").subscribe(
      (data: Array<EmpresasInterface>) => {
        this.empresas = data;
      });
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
    this.analystservice.Consultaconfirmaranalyst(cuenta, this.user).subscribe(
      (data: Array<analystConfirmarInterface>) => {
        this.porconfirmar = data;
        console.log(data);
      },(error : Error)=>{
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });
      this.dataApiPublics.DetalleStatus('pc').subscribe(
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
    let cuenta:any;
    cuenta={
      numSol:numsol,
      tipo:1,
      codProyecto:null,
    }
    this.set_data('amortizacion',JSON.stringify(cuenta));
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class',
      
  });
  }

  goDetallesUser(identificacion:any, solicitud:Number){
    this.set_data('investorData',identificacion);
    this.set_data('solicitud',solicitud);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }

  actualizarTransaccion(numsol:any, numCom:any,fecha:any,monto:any){
    this.cargando= true;
    const formData = new FormData();
   formData.append('usuario',this.user.user.identificacion);
   formData.append('numSolicitud',numsol);
    if(numCom!=''){formData.append('numComprobante',numCom);}
    if(fecha!=''){formData.append('fecha',fecha);}
    
    if(monto!=undefined){
      console.log(monto);
      this.cargando= false;
      Swal.fire({
        title: 'Se enviará la solicitud a PENDIENTE DE REVISIÓN',
        text: "Ingrese el motivo",
        icon: 'warning',
        input: 'text',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Cancelar',
        confirmButtonText: 'Continuar',
        preConfirm: (login) => {
          formData.append('monto',monto);
          formData.append('observacion',login);
          if(login==""){Swal.showValidationMessage(
            `Motivo requerido:`
          )}
        }
      }).then((result) => {
        if (result.isConfirmed) {
          this.cargando=true;
          this.actualizarTransaccionAfter(formData);
        }
        if(result.isDenied){
          return;
        }
      })
    }else{
      this.actualizarTransaccionAfter(formData);
    }
  }
  actualizarTransaccionAfter(datos:any){
    this.analystservice.actualizarTransaccion(datos,this.user).subscribe(
      (data: any) => { 
        this.cargando=false;
        this.Successfull=data;
      },(error:Error)=>{
        this.consultar();
        this.consultaExcel();
        this.cargando=false;
        console.log(error);
        Swal.fire({
          title: 'Error!',
          text: 'Error '+error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.consultar();
        this.cargando=false;
        Swal.fire({
          title: 'Exitoso!',
          text: this.Successfull.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
      })
  }


  onFilechange(event: any) {
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    ){
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos excel",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      event.currentTarget.files = null;
      this.nombredoc = "Cargar documento";
      return;
    }
    if(event.target.files[0].size > 3000000){
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: "Error El archivo no debe superar los 3MB",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    this.file = event.target.files[0];
    this.nombredoc = this.file!.name;
    console.log(event.currentTarget.files);
    event.target.value='';
    return;
  }

  cargarDatosExcel(){
    
    this.cargando=true;
    if (this.file) {
      this.analystservice.subirExcelService(this.user.user.usuarioInterno,this.file!,this.user).subscribe(  
        (data: any) => { 
          this.cargando=false;
          console.log(data);
          this.Successfull=data;
        },(error:Error)=>{
          this.consultar();
          this.consultaExcel();
          this.cargando=false;
          console.log(error);
          Swal.fire({
            title: 'Error!',
            text: 'Error '+error.message,
            icon: 'error',
            confirmButtonText: 'Aceptar'
          });
        },()=>{
          this.consultar();
          this.consultaExcel();
          this.cargando=false;
          Swal.fire({
            title: 'Exitoso!',
            text: this.Successfull.mensaje,
            icon: 'success',
            confirmButtonText: 'Aceptar'
          });
          this.registrationForm.reset();
        })
    }else{
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: "Error Debe subir un documento",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
    }
  }
  consultaExcel(){
    this.file=null;
    this.nombredoc = "Cargar documento";
    this.analystservice.consultarExcelData(this.user).subscribe((datos:any)=>{
      this.tablaExcel=Object.values(datos.detalleConciliaciones);
    },null,()=>{
    }
    )
  }
  


  conciliar(){
    this.cargando=true;
    this.analystservice.conciliarExcelData(this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.Successfull=data;
      },(error:Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Éxito!',
          text: this.Successfull.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
        this.consultaExcel();
      }
    )
  }
  aprobar(){
    this.cargando=true;
    this.analystservice.GuardarConciliacion(this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.Successfull=data;
      },(error:Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Éxito!',
          text: this.Successfull.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
        this.consultaExcel();
        this.consultar();
        this.tablaExcel=null;
      }
    )
  }

  aprobarAfter(){
    Swal.fire({
      title: '¿Estas seguro?',
      text: "Únicamente se aprobarán los registros conciliados correctamente.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Confirmar'
    }).then((result) => {
      if (result.isConfirmed) {
       this.aprobar();
       
      }
    })
  }




}

interface conciliacionrecepterInterface{
  numeroConciliacion: number,
  totalConciliado: string
}
function ConcilationModalComponent(ConcilationModalComponent: any, arg1: { windowClass: string; }) {
  throw new Error('Function not implemented.');
}

