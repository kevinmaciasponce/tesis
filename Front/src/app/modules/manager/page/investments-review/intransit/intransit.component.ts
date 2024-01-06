import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { detallestatusInterface } from 'src/app/shared/models/consulta_Inversiones/detallestatus.interface';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { RiesgosInterface } from 'src/app/shared/models/consulta_Inversiones/riesgos.interface';
import { SectorInterface } from 'src/app/shared/models/consulta_Inversiones/sector.intereface';
import { IntransitManagerInterface } from 'src/app/shared/models/consulta_solicitudes/Intransit.interface';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { ModalmasdetallesComponent } from '../../components/modalmasdetalles/modalmasdetalles.component';

@Component({
  selector: 'app-intransit',
  templateUrl: './intransit.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/tablasStyle.css']
})
export class IntransitComponent implements OnInit {
  empresas: Array<EmpresasInterface> = [];
  sectores: Array<SectorInterface> = [];
  calificaciones: Array<RiesgosInterface> = [];
  user!: User;
  isOpenConsulta=false;
  cargando!:boolean;
  ide: string| undefined;
  registrationForm!: FormGroup;
  aux: null| undefined;
  intransit!: IntransitManagerInterface[];
  detallestatus!: detallestatusInterface;
  get db() { return this.registrationForm.controls }
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService, private formBuilder: FormBuilder,private localstorage: StorageService, private dataApiPublics: ConsultasPublicasService,
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
    this.dataApiRequests.ManagerIntransit(cuenta, this.user).subscribe(
      (data: Array<IntransitManagerInterface>) => {
        console.log(data);
        this.intransit = data;
      });
      this.dataApiPublics.DetalleStatus('TN').subscribe(
        (data: detallestatusInterface) => {
          this.detallestatus = data;
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
  
  AprobarModal(codp:any, insol:any,nomp:any){
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
