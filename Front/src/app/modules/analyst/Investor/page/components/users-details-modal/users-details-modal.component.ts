import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { bancoInterface } from 'src/app/shared/models/banco.interface';
import { ciudadesInterface } from 'src/app/shared/models/ciudades.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { SolicitudInterface } from 'src/app/shared/models/solicitud.interface';
import { datos_ProcesoInterface } from 'src/app/shared/models/tabla_amortizacion/datos_proceso.interaface';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { DataApiClientService } from 'src/app/shared/service/data-api-client.service';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';

@Component({
  selector: 'app-users-details-modal',
  templateUrl: './users-details-modal.component.html',
  styleUrls: ['./users-details-modal.component.css']
})

export class UsersDetailsModalComponent implements OnInit,OnDestroy {
  isOpendatospersonales=false;
  isOpeninfo=false;
  isOpenfuente=false;
  isOpencuenta=false;
  isOpendomicilio=false;
  nombres!:string;
  apellidos!:string;
  user:User | undefined ;
  nacionalidad: any;
  tipo_sexo:string| undefined;
  ciudades:Array<ciudadesInterface>=[];
  persona:any  | undefined ;
  datosdeposito!:any;
  nns:any;
  iden:any;
  nacionalidades: Array<PaisesInterface> = [];

  ngOnInit(): void {
    
  }


  constructor(
    private analystservice: AnalystService,
    private localstorage: StorageService,
    private investorService: InvestorService,
    private dataApiClient: DataApiClientService,
    )
  {
    this.cargardatospago();
  this.cargardatos();
  this.registrationForm.get('nombres')?.setValue(this.nombres);
  this.registrationForm.get('apellidos')?.setValue(this.apellidos);
  this.registrationForm.get('cedula')?.setValue(this.user?.user.identificacion);
  
  
  }
  ngOnDestroy(): void {
    localStorage.removeItem('investorData');
    localStorage.removeItem('solicitud');
  }
  cargardatos(){
    this.iden=this.get_data('investorData');
    let user = this.localstorage.getCurrentSession();
    let cuenta :any;
    
    cuenta={
      token:user.token,
      identificacion:this.iden
    }
    
    this.investorService.consultaDatosInversionista(cuenta).subscribe(
      (data: any) => {
        console.log(data);
       this.persona=data;
      if(this.persona?.datosFormulario?.sexo=="M"){
          this.tipo_sexo="Masculino";
        }else{
          this.tipo_sexo="Femenino";
        }
        if(this.persona?.datosFormulario?.residenteDomicilioFiscal=="N"){
          this.registrationForm.get('noes_residente')?.setValue(true);
        }else{
          this.registrationForm.get('es_residente')?.setValue(true);
        }
      },null,()=>{
        this.dataApiClient.ConsultaPaises().subscribe(
          (data: Array<PaisesInterface>) => {
            this.nacionalidades = data;
            this.nacionalidad = this.nacionalidades.filter((r:any) =>  r.iso === this.persona.nacionalidad).map(function(r:any)  {return  r.gentilicio});

            // this.empresas.filter((r:any) =>  r.codProyecto === data).map(function(r:any)  {return  r.nombreEmpresa});
            console.log( this.nacionalidad);
          });
      });

      
  }

  cargardatospago(){
    this.nns=this.get_data('solicitud');
    let user = this.localstorage.getCurrentSession();
    let cuenta :any;
    cuenta={
      numeroSolicitud: this.nns
    }
    this.analystservice.ConsultadatosdePago(cuenta,user).subscribe(
      (data: any) => {
        console.log(data)
        this.datosdeposito=Object.values(data);
      },null,()=>{
        
      });
  }

  toggleState(dato:boolean){
    return !dato;
  }


  separar_datos(){
    let data;
    data = this.user?.user.nombres.split(" ");

    if(data![2]==undefined){
      this.nombres=data![0];
      this.apellidos=data![data!.length-1];
      return;
    }
    if(data![3]==undefined){
      this.nombres=data![0];
      this.apellidos=data![data!.length-2]+" "+data![data!.length-1]
      return;
    }
    this.nombres=data![0]+" "+data![1];
    this.apellidos=data![data!.length-2]+" "+data![data!.length-1];
  }

  registrationForm: FormGroup = new FormGroup({
    nombres: new FormControl(null, [
    ]),
    apellidos: new FormControl(null, [
    ]),
    cedula: new FormControl(null, [
    ]),
    celular: new FormControl(null, [
    ]),
    pais_residencia: new FormControl(null, [
    ]),
    estado_Civil: new FormControl(null, [
    ]),
    sexo: new FormControl(null, [
    ]),
    nacionalidad: new FormControl(null, [
    ]),
    correo: new FormControl(null, [
    ]),
    ciudad: new FormControl(null, [
    ]),
    direccion_dom: new FormControl(null, [
    ]),
    num_domicilio: new FormControl(null, [
    ]),
    tel_adicional: new FormControl(null, [
    ]),
    fuente_ingreso: new FormControl(null, [
    ]),
    tipo_cargo: new FormControl(null, [
    ]),
    titular: new FormControl(null, [
    ]),
    banco: new FormControl(null, [
    ]),
    tipo_cuenta: new FormControl(null, [
    ]),
    n_cuenta: new FormControl(null, [
    ]),
    es_residente: new FormControl(null, [
    ]),
    pais_fiscal: new FormControl(null, [
    ]),
  }
  );

  get db() { return this.registrationForm.controls }
  get cedula() { return this.registrationForm.get('cedula'); }
  get celular() { return this.registrationForm.get('celular'); }




  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }

}
