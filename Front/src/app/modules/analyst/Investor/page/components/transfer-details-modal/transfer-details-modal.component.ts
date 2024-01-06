import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { detailsInterface } from 'src/app/shared/models/consulta_solicitudes/detailsIntransit.interface';

import Swal from 'sweetalert2/dist/sweetalert2.js';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { DepreciationTablesComponent } from 'src/app/shared/components/depreciation-tables/depreciation-tables.component';
import { UsersDetailsModalComponent } from '../users-details-modal/users-details-modal.component';
import { AnalystService } from 'src/app/shared/service/analyst.service';

@Component({
  selector: 'app-transfer-details-modal',
  templateUrl: './transfer-details-modal.component.html',
  styleUrls: ['./transfer-details-modal.component.css']
})
export class TransferDetailsModalComponent implements OnInit {
  nums:any;
  nns: any;
  totales: number = 0;
  user: User | undefined;
  details!: detailsInterface;
  solicitudes: any;
  rm!: rikuest;
  numsol: Array<any> = [];
  cuenta!: any;
  nombreEmpresa: String | undefined;
  mensajeG :any;
  dataModal:any;
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService, private localstorage: StorageService,public modalService: NgbModal
    ,private analystService: AnalystService) {
    this.user = this.localstorage.getCurrentSession();

  }

  ngOnInit(): void {
    this.rm = JSON.parse(this.get_data('requestintransitmanager'));
    this.nombreEmpresa = this.rm.nomProyecto;
    console.log(this.rm);
    this.loadSolicitude();
  }

  goDetallesUser(numsol:any,identificacion:any){
    this.nums=this.set_data('investorData',identificacion);
    this.nums=this.set_data('solicitud',numsol);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
    }
  }
  goMasDetalle2(numsol:any){
    this.set_data('amortizacion',numsol);
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class'
  });
  }

  loadSolicitude(){
    this.dataModal = JSON.parse(this.get_data('dataModal'));
    this.details=this.dataModal;
    this.solicitudes = Object.values(this.dataModal.solicitudes);
    let i = 0;
    for (let ito of this.solicitudes) {
      this.numsol[i] = { "numeroSolicitud": ito.numeroSolicitud };
      i++;
    }
  }


  success() {
    Swal.fire({
      icon: 'success',
      title: this.mensajeG,
      text: 'INVERSION APROBADA!',
    })
  }

  goTablaAmortizacionUsuario(numeroSol:string){
    let cuenta:any;
    cuenta={
      numSol:numeroSol,
      tipo:1,
      codProyecto:null,
    }
    this.set_data('amortizacion',JSON.stringify(cuenta));
    console.log(cuenta);
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
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

  goTablaAmortizacionPagare(numeroSol:string){
    let cuenta:any;
    cuenta={
      numSol:numeroSol,
      tipo:2,
      codProyecto:null,
    }
    this.set_data('amortizacion',JSON.stringify(cuenta));
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
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




}

interface rikuest {
  codProyecto: string;
  nomProyecto: String;
  inSolicitud: number;
  estado:string;
}
