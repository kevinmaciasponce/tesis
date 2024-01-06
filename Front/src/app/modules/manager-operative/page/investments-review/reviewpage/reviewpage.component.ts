import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { TransferDetailsModalComponent } from 'src/app/modules/analyst/Investor/page/components/transfer-details-modal/transfer-details-modal.component';
import { UsersDetailsModalComponent } from 'src/app/modules/analyst/Investor/page/components/users-details-modal/users-details-modal.component';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { ManagerOperativeService } from 'src/app/shared/services/manager-operative.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reviewpage',
  templateUrl: './reviewpage.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class ReviewpageComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  user!: User;
  mensaje!:any;
  isOpenConsulta=false;
  cargando!:boolean;
  ide: string| undefined;
  registrationForm!: FormGroup;
  nums:any;
  aux!: null| string;
  valid!: any[];
  detallestatus!: detallestatusInterface;
  get db() { return this.registrationForm.controls }
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService, private router:Router,
   
    private formBuilder: FormBuilder,private localstorage: StorageService, 
    private dataApiPublics: ConsultasPublicasService, private managerOper: ManagerOperativeService,
    public modalService: NgbModal) {   
  }

  ngOnInit(): void {
    this.user = this.localstorage.getCurrentSession();
    this.ide = this.user?.user.identificacion;
    this.getCatalogos();
    this.registrationForm = this.formBuilder.group({
      codProyecto: [null],
      riesgos: [null],
      sectors: [null]
    });
    this.consultar();
  }
  getCatalogos(): void {
    this.dataApiRequests.ConsultaSector().subscribe(
      (data: Array<SectorInterface>) => {
        this.sectores = data;
      });
    this.dataApiRequests.ConsultaRiesgo().subscribe(
      (data: Array<RiesgosInterface>) => {
        this.calificaciones = data;
      });
    this.dataApiRequests.ConsultaEmpresas().subscribe(
      (data: Array<EmpresasInterface>) => {
        this.empresas = data;
      });
  }
  public consultar(): void {
    let cuenta = this.obtenerData();
    this.user = this.localstorage.getCurrentSession();
      this.dataApiPublics.DetalleStatus('PAP').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
          
        });
        //Cambiar servicio
        this.managerOper.pendindgReviews(cuenta, this.user).subscribe(
          (data:any)=>{
            console.log(data);
            this.valid = data;
          }
        )
  }
  goMasdetalle3(codp:any, insol:any,nomp:any){
    let  requestmanager:any;
    requestmanager= {
      codProyecto: codp,
      nomProyecto:nomp,
      inSolicitud: insol,
      estado:'VG'
    }
    //envia
    this.set_data('requestintransitmanager',JSON.stringify(requestmanager));
    const modal = this.modalService.open(TransferDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }
  
  AprobarSolicitud(numSol:any){
    const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger' 
      },
      buttonsStyling: false
    })
    swalWithBootstrapButtons.fire({
      title: '¿Está seguro que desea aprobar el documento?:',
      text: "La opción no se puede desahacer",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Aprobar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        
        this.managerOper.aprobarSolicitud(numSol, this.user).subscribe(
          (data:any)=>{
            this.mensaje= data.mensaje;
          },(error:Error)=>{
            Swal.fire({
              title: 'Error!',
              text: error.message,
              icon: 'error',
              confirmButtonText: 'Aceptar'
            });
            },()=>{
              Swal.fire({
                title: 'Success!',
                text: this.mensaje,
                icon: 'success',
                confirmButtonText: 'Aceptar'
              }).then(()=>{
                this.consultar();
              });
            }
        )

      } else if (
        result.dismiss === Swal.DismissReason.cancel
      ) {
        
        this.cargando=false;
        swalWithBootstrapButtons.fire(
          'Cancelado',
          'Solicitud en estado de revisión',
          'error'
        )
      }
    })
    
  }

  goDetallesUser(identificacion:any, solicitud:Number){
    this.set_data('investorData',identificacion);
    this.set_data('solicitud',solicitud);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }

  AnularSolicitud(numSol:any){
    const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger' 
      },
      buttonsStyling: false
    })
    swalWithBootstrapButtons.fire({
      title: '¿Está seguro que desea anular el documento?:',
      text: "La opción no se puede desahacer",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Anular',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.managerOper.anularSolicitud(numSol, this.user).subscribe(
          (data:any)=>{
            Swal.fire({
              title: 'Success!',
              text: data.mensaje,
              icon: 'success',
              confirmButtonText: 'Aceptar'
            });
          },(error:Error)=>{
            Swal.fire({
              title: 'Error!',
              text: error.message,
              icon: 'error',
              confirmButtonText: 'Aceptar'
            });
            },()=>{
              this.consultar();
            }
        )
      } else if (
        result.dismiss === Swal.DismissReason.cancel
      ) {
        this.cargando=false;
        swalWithBootstrapButtons.fire(
          'Cancelado',
          'Solicitud en estado de revisión',
          'error'
        )
      }
    })
  }

  toggleState(dato:boolean){
    return !dato;
  }

  porcentaje(num:any,num2:any):String{
    return Number(num/num2*100).toFixed(2);
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
      identificacion: null,
      codProyecto: this.aux,
      idTipoCalificacion: this.db['riesgos'].value,
      idActividad: this.db['sectors'].value
    };
    return datos;
  }
  set_data(key:string,codp:any){
    try{
      localStorage.setItem(key,codp);
    } catch(e){
      console.log(e);
    }
  }
}
