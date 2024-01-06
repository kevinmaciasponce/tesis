import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { detailsInterface } from 'src/app/shared/models/consulta_solicitudes/detailsIntransit.interface';

import Swal from 'sweetalert2/dist/sweetalert2.js';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { Router } from '@angular/router';


@Component({
  selector: 'app-modalmasdetalles',
  templateUrl: './modalmasdetalles.component.html',
  styleUrls: ['./modalmasdetalles.component.css']
})
export class ModalmasdetallesComponent implements OnInit {
  cargando!:boolean;
  nns: any;
  totales: number = 0;
  user: User | undefined;
  details!: detailsInterface;
  solicitudes: any;
  rm!: rikuest;
  numsol: Array<any> = [];
  cuenta!: any;
  nombreEmpresa: String | undefined;
  mensajeG: any;
  modal=NgbModalRef;
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService,
    private localstorage: StorageService, 
    public modalService: NgbModal,
    public activeModal: NgbActiveModal,
    private router: Router) {
    this.user = this.localstorage.getCurrentSession();
    this.cargando=false;
  }

  ngOnInit(): void {
    this.rm = JSON.parse(this.get_data('requestintransitmanager'));
    this.nombreEmpresa = this.rm.nomProyecto;
    this.consultar();
  }
 
  get_data(key: any): any {
    return localStorage.getItem(key);
  }
  set_data(key: string, data: any) {
    try {
      localStorage.setItem(key, data);
    } catch (e) {
      console.log(e);
    }
  }
  goMasDetalle2(numsol: any) {
    let cuenta = {
      numSol:numsol,
      tipo:1
    }
    this.set_data('amortizacion',JSON.stringify(cuenta) );
    const modal = this.modalService.open(ModalmasdetallesAHComponent, {
      windowClass: 'my-class'
    });
  }
  consultar(): void {
    this.cuenta = {
      codigoProyecto: this.rm.codProyecto,
    }
    this.dataApiRequests.managerIntransitDetails(this.cuenta, this.user!).subscribe(
      (data: detailsInterface) => {
        this.details = data;
       
        this.solicitudes = Object.values(this.details.solicitudes);
        let i = 0;
        for (let ito of this.solicitudes) {
          this.numsol[i] = { "numeroSolicitud": ito.numeroSolicitud };
          i++;
        }
      });
  }
  guardar(obs: String): void {
    this.cargando=true;
    if(this.details==null){
      this.cargando=false;
      return;
    }
    this.cuenta = {
      usuario: this.user?.user.identificacion,
      codigoProyecto: this.rm.codProyecto,
      montoSolicitado: this.rm.inSolicitud,
      montoAprobado: this.details.totalInversion,
      observacion: obs,
      solicitudes: this.numsol
    }
    console.log(this.cuenta);
    this.dataApiRequests.ManagerIntransitAprove(this.cuenta, this.user!).subscribe(
      (data: infom) => {
        this.mensajeG = data;
      },(error :Error)=>{
        this.cargando=false;
      },()=>{
        this.cargando=false;
       
        this.success();
      });
      this.cargando=false;
  }
  cerrar() {
    let currentUrl = this.router.url;
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate([currentUrl]);
    this.activeModal.dismiss();
  }
  success() {

    Swal.fire({
      icon: 'success',
      title: this.mensajeG.mensaje,
      text: 'INVERSION APROBADA!',
    }).then(()=>{
      this.cerrar();
    });
  }
}
interface infom {
  mensaje: String
}
interface rikuest {
  codProyecto: string;
  nomProyecto: String;
  inSolicitud: number;
}
