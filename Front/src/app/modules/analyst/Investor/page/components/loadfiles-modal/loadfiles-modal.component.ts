import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { detailsInterface } from 'src/app/shared/models/consulta_solicitudes/detailsIntransit.interface';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { DepreciationTablesComponent } from 'src/app/shared/components/depreciation-tables/depreciation-tables.component';
import { CargaArchivosModalComponent } from '../carga-archivos-modal/carga-archivos-modal.component';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { Router } from '@angular/router';
import { UsersDetailsModalComponent } from '../users-details-modal/users-details-modal.component';

@Component({
  selector: 'app-loadfiles-modal',
  templateUrl: './loadfiles-modal.component.html',
  styleUrls: ['./loadfiles-modal.component.css']
})
export class LoadfilesModalComponent implements OnInit {
  nns: any;
  cargando:any;
  totales: number = 0;
  user: User | undefined;
  details!: detailsInterface ;
  solicitudes: any;
  rm!: rikuest;
  numsol: Array<any> = [];
  cuenta!: any;
  nombreEmpresa: String | undefined;
  mensajeG :any;
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService, private router:Router,
    public activeModal: NgbActiveModal,
    private localstorage: StorageService,
    public modalService: NgbModal,
    public analystservice: AnalystService) {
      this.cargando=false;
    this.user = this.localstorage.getCurrentSession();
  }

  ngOnInit(): void {
    this.rm = JSON.parse(this.get_data('requestintransitmanager'));
    this.nombreEmpresa = this.rm.nomProyecto;
    this.consultar();
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }
  goMasDetalle2(numsol:any){
    this.set_data('historial',numsol);
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class'
  });
  }
  consultar(): void {
    this.cuenta = {
      codigoProyecto: this.rm.codProyecto,
      estado: "SATF"
    }
    console.log(this.cuenta);
    this.analystservice.analystAprobadaDetails(this.cuenta.codigoProyecto, this.user!).subscribe(
      (data: detailsInterface) => {
        console.log(data);
        this.details = data;
        this.solicitudes = Object.values(this.details.solicitudes);
        
        let i = 0;
        for (let ito of this.solicitudes) {
          this.numsol[i] = { "numeroSolicitud": ito.numeroSolicitud };
          i++;
        }
      });
  }

  goTablaAmortizacionUsuario(numeroSol:string){
    let cuenta = this.obtenerTablaUsuario(numeroSol);
    console.log(cuenta);
    localStorage.removeItem('cuenta');
    this.set_data('cuenta',JSON.stringify(cuenta));
    const modal = this.modalService.open(DepreciationTablesComponent, { 
      windowClass: 'my-class'
  });
  }

  obtenerTablaUsuario(numSolicitud:string): any {
    let datos = {
      "numeroSolicitud": numSolicitud,
      "codigoProyecto": null,
      "idTipoTabla": 1
    };
    console.log(datos);
    return datos;
  }

  goDetallesUser(identificacion:any, solicitud:Number){
    this.set_data('investorData',identificacion);
    this.set_data('solicitud',solicitud);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }

  goTablaAmortizacionPagare(numeroSol:string){
    let cuenta = this.obtenerTablaUsuario(numeroSol);
    console.log(cuenta);
    localStorage.removeItem('cuenta');
    this.set_data('cuenta',JSON.stringify(cuenta));
    const modal = this.modalService.open(DepreciationTablesComponent, { 
      windowClass: 'my-class'
  });
  }

  obtenerTablaPagare(numSolicitud:string): any {
    let datos = {
      "numeroSolicitud": numSolicitud,
      "codigoProyecto": null,
      "idTipoTabla": 2
    };
    console.log(datos);
    return datos;
  }


  cargararchivos(codSolicitud:string){
    let datos={
      codProyecto: this.rm.codProyecto,
      nomEmpresa: this.rm.nomProyecto,
      NumSolicitud:codSolicitud
    }
    this.set_data('cargaDatos',JSON.stringify(datos))
    const modal = this.modalService.open(CargaArchivosModalComponent, { 
      windowClass: 'my-class'
    });
  }

  aprobarProyecto(){
    this.cargando=true;
    this.analystservice.aprobarSolicitudesaFirmaContrato(this.rm.codProyecto,this.user!).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Exitoso!',
          text: "Se subieron correctamente los archivos y el proyecto cambio a estado exitoso",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then( ()=>{
          let currentUrl = this.router.url;
          this.router.routeReuseStrategy.shouldReuseRoute = () => false;
          this.router.onSameUrlNavigation = 'reload';
          this.router.navigate([currentUrl]);
          this.activeModal.dismiss();
        }
        );
      }
    )
  }

}

interface rikuest {
  codProyecto: string;
  nomProyecto: String;
  inSolicitud: number;
}
