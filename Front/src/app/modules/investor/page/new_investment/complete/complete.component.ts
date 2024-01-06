import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, Observable, Subscription } from 'rxjs';
import { User } from 'src/app/models/user';
import { bancoInterface } from 'src/app/shared/models/banco.interface';
import { ciudadesInterface } from 'src/app/shared/models/ciudades.interface';
import { formularioPersonaNaturalInterface } from 'src/app/shared/models/formulario_persona_natural.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { datos_ProcesoInterface } from 'src/app/shared/models/tabla_amortizacion/datos_proceso.interaface';
import { DataApiClientService } from 'src/app/shared/service/data-api-client.service';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-complete',
  templateUrl: './complete.component.html',
  styleUrls: ['./complete.component.css']
})

export class CompleteComponent implements OnInit {
  @ViewChild("si_residente") sies!: ElementRef;
  @ViewChild("no_residente") noes!: ElementRef;
  es_residentechek!: boolean;
  no_residente: boolean=false;
  solicitudNueva!: boolean;
  si_residente: boolean=false;
  cargando!:boolean;
  residente!:string;
  isOpendatospersonales=false;
  isOpeninfo=false;
  isOpenfuente=false;
  isOpencuenta=false;
  isOpendomicilio=false;
  numeroSolicitud!: any;
  submitted: boolean = false;
  guardando: boolean = false;
  guardar$: Observable<any> | undefined;
  constructor(
    private dataApiClient: DataApiClientService,
    private investorService: InvestorService,
    private storage:StorageService,
    private router: Router
    )
  {
  this.getCatalogos();
  this.user=this.storage.getCurrentSession();
  this.separar_datos();
  this.registrationForm.get('nombres')?.setValue(this.nombres);
  this.registrationForm.get('apellidos')?.setValue(this.apellidos);
  this.registrationForm.get('cedula')?.setValue(this.user?.user.identificacion);
  this.registrationForm.get('correo')?.setValue(this.user?.user.usuario);
  this.cargando=false;
  }

  tabla_amortizacion!:datos_ProcesoInterface;
  datos!:any;
  nombres!:string;
  apellidos!:string;
  user:User | undefined ;
  Nciudad!:number;
  private subscriptions: Array<Subscription> = [];
  nacionalidades: Array<PaisesInterface> = [];
  
  nacionalidad!:PaisesInterface;
  pais!:PaisesInterface;
  domiciliofiscal!:PaisesInterface;

  tipo_sexo:Array<ParametrosInterface>=[];
  estado_civil :Array<ParametrosInterface>=[];
  fuente_ingresos:Array<ParametrosInterface>=[];
  tipo_cargos:Array<ParametrosInterface>=[];
  tipo_cuentas:Array<ParametrosInterface>=[];
  ciudades:Array<ciudadesInterface>=[];
  
  ciudad!:ciudadesInterface;
  
  banco:Array<bancoInterface>=[];
  
  bancoact!:bancoInterface;
  
  tiempoTranscurrido = Date.now();
  hoy = new Date(this.tiempoTranscurrido);
  fecha=this.hoy.toLocaleDateString();
  ngOnInit(): void {
    this.existeSolicitud();
  
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

  getCatalogos(): void {
    this.dataApiClient.ConsultaPaises().subscribe(
      (data: Array<PaisesInterface>) => {
        this.nacionalidades = data;
      });
    this.dataApiClient.ConsultaSexo().subscribe(
      (data: Array<ParametrosInterface>) => {
        this.tipo_sexo = data;
      });
    this.dataApiClient.ConsultaEstadoCivil().subscribe(
      (data: Array<ParametrosInterface>) => {
        this.estado_civil = data;
      });
    this.dataApiClient.ConsultaFuenteIngresos().subscribe(
      (data: Array<ParametrosInterface>) => {
        this.fuente_ingresos = data;
      });
    this.dataApiClient.ConsultaTipoCargos().subscribe(
      (data: Array<ParametrosInterface>) => {
        this.tipo_cargos = data;
      });
    this.dataApiClient.ConsultaTipoCuentas().subscribe(
      (data: Array<ParametrosInterface>) => {
        this.tipo_cuentas = data;
      });
      let users= this.storage.getCurrentSession();
    this.dataApiClient.ConsultaBanco(users).subscribe(
        (data: Array<bancoInterface>) => {
          this.banco = data;
        },(error: Error)=>{
          if(error.message=="Forbidden"){
            this.router.navigate(["register/iniciar_sesion"]);
          }
        });
      // https://multiploservicesqa.azurewebsites.net/multiplo/bancos
      
  }

  registrationForm: FormGroup = new FormGroup({
    nombres: new FormControl(null, [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
    ]),
    apellidos: new FormControl(null, [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(50),
    ]),
    cedula: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10),
    ]),
    celular: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10),
    ]),
    pais_residencia: new FormControl(null, [
      Validators.required,
    ]),
    estado_Civil: new FormControl(null, [
      Validators.required,
    ]),
    sexo: new FormControl(null, [
      Validators.required,
    ]),
    nacionalidad: new FormControl(null, [
      Validators.required,
    ]),
    correo: new FormControl(null, [
      Validators.required,
      Validators.email
    ]),
    ciudad: new FormControl(null, [
      Validators.required,
    ]),
    direccion_dom: new FormControl(null, [
      Validators.required,
      Validators.maxLength(200),
    ]),
    num_domicilio: new FormControl(null, [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(10),
    ]),
    tel_adicional: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10),
    ]),
    fuente_ingreso: new FormControl(null, [
      Validators.required,
    ]),
    tipo_cargo: new FormControl(null, [
      Validators.required,
    ]),
    titular: new FormControl(null, [
      Validators.required,
    ]),
    banco: new FormControl(null, [
      Validators.required,
    ]),
    tipo_cuenta: new FormControl(null, [
      Validators.required,
    ]),
    n_cuenta: new FormControl(null, [
      Validators.required,
    ]),
    es_residente: new FormControl(null, [
      Validators.required,
    ]),
    pais_fiscal: new FormControl(null, [
    ]),
  }
  );

  get db() { return this.registrationForm.controls }
  get cedula() { return this.registrationForm.get('cedula'); }
  get celular() { return this.registrationForm.get('celular'); }
  get tel_adicional() { return this.registrationForm.get('tel_adicional'); }


  llenar_ciudades(){
    this.dataApiClient.ConsultaCiudades(this.Nciudad).subscribe(
      (data: Array<ciudadesInterface>) => {
        this.ciudades = data;
      }),(error:Error)=>{
        console.error(error);
      };
  }


  llenar_ciudad(n_ciudad:number){
    this.dataApiClient.ConsultaCiudades(n_ciudad).subscribe(
      (data: Array<ciudadesInterface>) => {
        this.ciudades = data;
      }),(error:Error)=>{
        console.error(error);
      };
  }

  IsInvalid(control: string): boolean {
     if (this.registrationForm.get(control) != null) {
       let controlForm = this.registrationForm.get(control);
       return controlForm != null
             ? ((controlForm.invalid && (controlForm.dirty || controlForm.touched))
               || controlForm.invalid && this.submitted )
             : false;
     }
     return false;
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
      this.router.navigate(["/investor/indicate"]);
    } catch(e){
      console.log(e);
    }
  }

  

  private _crearCuenta(cuenta: formularioPersonaNaturalInterface, users: User){
    this.guardar$ = this.investorService.GuardarFormulario(cuenta,users).pipe(
      finalize(() => { } )
    );
    let crear :boolean =true;
    this.guardando = true;
    console.log(cuenta);
    this.cargando=true;
    this.subscriptions.push(this.guardar$.subscribe(
      (result: any) => {
        console.warn(result);
        this.guardando = false;
        Swal.fire({
          title: 'Éxito!',
          text: 'Se han guardado los datos correctamente.',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
        this.router.navigateByUrl('investor/load');
      },
      (error: Error) => {
        this.guardando = false;
        this.cargando=false;
        Swal.fire({
          title: 'Error!',
          text: 'Error'+ error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.cargando=false;
        this.router.navigateByUrl('investor/load');
      }
    ));
  }
  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }




  existeSolicitud(){
    this.cargando=true;
    let cuenta :any;
    cuenta={
      token:this.user?.token,
      identificacion:this.user?.user.identificacion
    }
    this.investorService.consultaDatosInversionista(cuenta).subscribe(
      (data:any)=>{
        console.log(data);
        this.registrationForm.get('nombres')?.setValue(data.nombres);
        this.registrationForm.get('apellidos')?.setValue(data.apellidos);
        this.registrationForm.get('celular')?.setValue(data.numeroCelular);
        if(data.datosFormulario){
          this.registrationForm.get('estado_Civil')?.setValue(data.datosFormulario.estadoCivil);
          this.registrationForm.get('sexo')?.setValue(data.datosFormulario.sexo);
          this.registrationForm.get('pais_residencia')?.setValue(data.datosFormulario.personaDomicilio.pais.pais);
          this.registrationForm.get('ciudad')?.setValue(data.datosFormulario.personaDomicilio.ciudad.ciudad); 
          this.registrationForm.get('direccion_dom')?.setValue(data.datosFormulario.personaDomicilio.direccion);
          this.registrationForm.get('num_domicilio')?.setValue(data.datosFormulario.personaDomicilio.numeroDomicilio);
          this.registrationForm.get('tel_adicional')?.setValue(data.datosFormulario.numeroTelefono);
          this.registrationForm.get('fuente_ingreso')?.setValue(data.datosFormulario.fuenteIngresos);
          this.registrationForm.get('tipo_cargo')?.setValue(data.datosFormulario.cargoPersona);
          this.registrationForm.get('titular')?.setValue(data.datosFormulario.bancaria.titular);
          this.registrationForm.get('banco')?.setValue(data.datosFormulario.bancaria.banco);
          this.registrationForm.get('tipo_cuenta')?.setValue(data.datosFormulario.bancaria.tipoCuenta);
          this.registrationForm.get('n_cuenta')?.setValue(data.datosFormulario.bancaria.numeroCuenta);
          if(data.datosFormulario.residenteDomicilioFiscal=='N'){
            this.no_residente= true;
            this.db['es_residente'].setValue("N");
          }else{
            this.si_residente= true;
            this.db['es_residente'].setValue("S");
          }
          this.domiciliofiscal =this.nacionalidades.find(e => e.idNacionalidad == Number(data.datosFormulario.paisDomicilioFiscal))!;
          this.registrationForm.get('pais_fiscal')?.setValue(this.domiciliofiscal.pais);
          this.nacionalidad =this.nacionalidades.find(e => e.iso == data.nacionalidad as string)!;
          console.log(this.nacionalidad);
          this.registrationForm.get('nacionalidad')?.setValue(this.nacionalidad.gentilicio); 
        }
       
        this.registrationForm.get('correo')?.setValue(this.user?.user.usuario);
       
             
       
        this.cargando=false;
      }
    ,(error:Error)=>{
      this.cargando=false;
    },()=>{
      this.cargando=false;
      if(this.nacionalidad){
        console.log(this.nacionalidad);
        this.llenar_ciudad(this.nacionalidad.idNacionalidad);
        this.db['nacionalidad'].setValue(this.nacionalidad.gentilicio);
      }
      
     
      this.solicitudNueva=false;
      if (this.registrationForm.valid) {
        this.solicitudNueva = true;
      }
    })
  }

  changeGender(e:any) {
    console.log(e.target.value);
    if(e.target.value=='S'){
      this.si_residente = true;
    }else{
      this.si_residente = false;
    }
  }
  
  obtenerCuenta(users: User):formularioPersonaNaturalInterface{
    let cuenta!:formularioPersonaNaturalInterface;
    let project=this.get_data('proyecto');
    this.nacionalidad =this.nacionalidades.find(e => e.gentilicio == this.db['nacionalidad'].value as string)!;
    if(typeof (this.db['pais_residencia'].value)=='string'){
      this.pais =this.nacionalidades.find(e => e.pais == this.db['pais_residencia'].value as string)!;
    }else{
      this.pais =this.nacionalidades.find(e => e.idNacionalidad == this.db['pais_residencia'].value as number)!;
    }
    this.Nciudad=this.pais.idNacionalidad;
    
    if(typeof (this.db['ciudad'].value)=='string'){
      this.ciudad =this.ciudades.find(e => e.ciudad == this.db['ciudad'].value)!;
    }else{
      this.ciudad =this.ciudades.find(e => e.id == this.db['ciudad'].value)!;
    }
    this.bancoact =this.banco.find(e => e.nombre == this.db['banco'].value)!;
    if(typeof (this.db['pais_fiscal'].value)=='string'){
      this.domiciliofiscal =this.nacionalidades.find(e => e.pais == this.db['pais_fiscal'].value)!;
    }else{
      this.domiciliofiscal =this.nacionalidades.find(e => e.idNacionalidad == this.db['pais_fiscal'].value)!;
    }
    if(!this.es_residentechek){
      this.residente="N";
      this.si_residente=false;
    }else{
      this.residente="S";
      this.si_residente=true;
    }
    cuenta={
    identificacion: this.db['cedula'].value as string,
    numeroCelular:  this.db['celular'].value as string,
    estadoCivil:  this.db['estado_Civil'].value as string,
    sexo: this.db['sexo'].value as string,
    nacionalidad: this.pais.idNacionalidad as number,
    telefonoAdicional: this.db['tel_adicional'].value as string,
    fuenteIngresos: this.db['fuente_ingreso'].value as string,
    cargo: this.db['tipo_cargo'].value as string,
    aceptaLicitudFondos: "S",
    residenteDomicilioFiscal: this.residente,
    paisDomicilioFiscal : null,
    aceptaInformacionCorrecta: "S",
    tipoCuenta: {
        titularCuenta: this.db['titular'].value as string,
        bancoCuenta: this.bancoact.idBanco as number,
        tipoCuenta: this.db['tipo_cuenta'].value as string,
        numeroCuenta: this.db['n_cuenta'].value as string,
    },
    domicilio: {
        pais: this.pais.idNacionalidad as number,
        ciudad: this.ciudad.id as number,
        direccion: this.db['direccion_dom'].value as string,
        numeroDomicilio: this.db['num_domicilio'].value as string,
    }
    }
    if (this.residente=='S') {
      cuenta = {
        ...cuenta,
        paisDomicilioFiscal: this.domiciliofiscal.idNacionalidad
      }
    }
    this._crearCuenta(cuenta,users);

    return cuenta;
  }
  onSubmit() {
    this.cargando=true;
    let users= this.storage.getCurrentSession();
    this.submitted = true;
    if (!this.registrationForm.valid) {
      console.log(this.registrationForm.errors)
      this.cargando=false;
      return;
    }
    this.obtenerCuenta(users);
    if(this.guardando) {
      return;
    }
    console.warn(this.registrationForm.value);
    this.submitted = false;
  }

  actualizarCuenta(users: User):formularioPersonaNaturalInterface{
    let cuenta!:formularioPersonaNaturalInterface;
    let project=this.get_data('proyecto');
        this.nacionalidad =this.nacionalidades.find(e => e.gentilicio == this.db['nacionalidad'].value as string)!;
        if(typeof (this.db['pais_residencia'].value)=='string'){
          this.pais =this.nacionalidades.find(e => e.pais == this.db['pais_residencia'].value as string)!;
        }else{
          this.pais =this.nacionalidades.find(e => e.idNacionalidad == this.db['pais_residencia'].value as number)!;
        }
        this.Nciudad=this.pais.idNacionalidad;
        
        if(typeof (this.db['ciudad'].value)=='string'){
          this.ciudad =this.ciudades.find(e => e.ciudad == this.db['ciudad'].value)!;
        }else{
          this.ciudad =this.ciudades.find(e => e.id == this.db['ciudad'].value)!;
        }
        this.bancoact =this.banco.find(e => e.nombre == this.db['banco'].value)!;
        if(typeof (this.db['pais_fiscal'].value)=='string'){
          this.domiciliofiscal =this.nacionalidades.find(e => e.pais == this.db['pais_fiscal'].value)!;
        }else{
          this.domiciliofiscal =this.nacionalidades.find(e => e.idNacionalidad == this.db['pais_fiscal'].value)!;
        }
        if(!this.es_residentechek){
          this.residente="N";
          this.si_residente=false;
        }else{
          this.residente="S";
          this.si_residente=true;
        }
        cuenta={
        identificacion: this.db['cedula'].value as string,
        numeroCelular:  this.db['celular'].value as string,
        estadoCivil:  this.db['estado_Civil'].value as string,
        sexo: this.db['sexo'].value as string,
        nacionalidad: this.pais.idNacionalidad as number,
        telefonoAdicional: this.db['tel_adicional'].value as string,
        fuenteIngresos: this.db['fuente_ingreso'].value as string,
        cargo: this.db['tipo_cargo'].value as string,
        aceptaLicitudFondos: "S",
        residenteDomicilioFiscal: this.residente,
        paisDomicilioFiscal : null,
        aceptaInformacionCorrecta: "S",
        tipoCuenta: {
            titularCuenta: this.db['titular'].value as string,
            bancoCuenta: this.bancoact.idBanco as number,
            tipoCuenta: this.db['tipo_cuenta'].value as string,
            numeroCuenta: this.db['n_cuenta'].value as string,
        },
        domicilio: {
            pais: this.pais.idNacionalidad as number,
            ciudad: this.ciudad.id as number,
            direccion: this.db['direccion_dom'].value as string,
            numeroDomicilio: this.db['num_domicilio'].value as string,
        }
        }
        if (this.residente=='S') {
          cuenta = {
            ...cuenta,
            paisDomicilioFiscal: this.domiciliofiscal.idNacionalidad
          }
        }
        this._actualizarCuenta(cuenta,users);

    return cuenta;
  }

  private _actualizarCuenta(cuenta: formularioPersonaNaturalInterface, users: User){
    this.guardando = true;
    this.cargando=true;
    console.log(cuenta);
    this.investorService.ActualizarFormulario(cuenta,users).subscribe(
      (result: any) => {
        console.log(result);
        Swal.fire({
          title: 'Éxito!',
          text: 'Se han actualizado los datos correctamente.',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
      },
      (error: Error) => {
        
      },()=>{
        this.cargando=false;
        this.router.navigateByUrl('investor/load');
      }
    );
  }

  onUpdate(){
    let users= this.storage.getCurrentSession();
    this.submitted = true;
    if (!this.registrationForm.valid) {
      console.log(this.registrationForm.errors)
      return;
    }
    this.actualizarCuenta(users);
    if(this.guardando) {
      return;
    }
    console.warn(this.registrationForm.value);
    this.submitted = false;
  }

  keyPressNumbers(event: { which: any; keyCode: any; preventDefault: () => void; }): boolean {
    var charCode = (event.which) ? event.which : event.keyCode;
    // Only Numbers 0-9
    if ((charCode < 48 || charCode > 57)) {
      event.preventDefault();
      return false;
    } else {
      return true;
    }
  }
}
