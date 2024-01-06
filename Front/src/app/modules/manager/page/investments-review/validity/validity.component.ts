import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { TransferDetailsModalComponent } from 'src/app/modules/analyst/Investor/page/components/transfer-details-modal/transfer-details-modal.component';
import { UsersDetailsModalComponent } from 'src/app/modules/analyst/Investor/page/components/users-details-modal/users-details-modal.component';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { IntransitManagerInterface } from 'src/app/shared/models/consulta_solicitudes/Intransit.interface';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { ModalmasdetallesComponent } from '../../components/modalmasdetalles/modalmasdetalles.component';

@Component({
  selector: 'app-validity',
  templateUrl: './validity.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class ValidityComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  user!: User;
  isOpenConsulta=false;
  cargando!:boolean;
  ide: string| undefined;
  registrationForm!: FormGroup;
  nums:any;
  aux: null| undefined;
  dataSend!:boolean;
  valid!: any[];
  detallestatus!: detallestatusInterface;
  get db() { return this.registrationForm.controls }
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService, private formBuilder: FormBuilder,
    private localstorage: StorageService, private dataApiPublics: ConsultasPublicasService,
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
      this.dataApiPublics.DetalleStatus('VG').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
          console.log(data);
        });

    this.dataApiRequests.ConsultaVigenteProyectos(cuenta, this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.valid = data;
      }
    )
      
  }

  goDetallesUser(numsol:any){
    this.nums=this.set_data('historial',numsol);
    const modal = this.modalService.open(UsersDetailsModalComponent, { 
      windowClass: 'my-class'
  });
  }

  toggleState(dato:boolean){
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
      identificacion: null,
      codProyecto: this.aux
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

  goMasdetalle2(numsol:any){
    this.nums=this.set_data('historial',numsol);
    const modal = this.modalService.open(ModalmasdetallesAHComponent, { 
      windowClass: 'my-class',
      
  });
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
    this.dataApiRequests.managerValidDetails(requestmanager.codProyecto,this.user).subscribe(
      (data: any)=>{
        console.log(data);
        this.dataSend=data;
      },(error:Error)=>{
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.set_data('requestintransitmanager',JSON.stringify(requestmanager));
        this.set_data('dataModal',JSON.stringify(this.dataSend));
        const modal = this.modalService.open(TransferDetailsModalComponent, { 
          windowClass: 'my-class'
      });
      }
    )


    
  }




   
  
  
  
  goMasdetalle(codp:any, insol:any,nomp:any){
    let  requestmanager:any;
    requestmanager= {
      codProyecto: codp,
      nomProyecto:nomp,
      inSolicitud: insol
    }
    //envia
    this.set_data('requestintransitmanager',JSON.stringify(requestmanager));
    const modal = this.modalService.open(ModalmasdetallesComponent, { 
      windowClass: 'my-class'
  });
  
  }

}
