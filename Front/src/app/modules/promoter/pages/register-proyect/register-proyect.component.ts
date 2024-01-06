import { BreakpointObserver } from '@angular/cdk/layout';
import { StepperOrientation } from '@angular/cdk/stepper';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { MatTableDataSource } from '@angular/material/table';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { map, Observable, windowWhen } from 'rxjs';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { PromoterService } from '../../service/promoter.service';


@Component({
  selector: 'app-register-proyect',
  templateUrl: './register-proyect.component.html',
  styleUrls: ['./register-proyect.component.css']
})
export class RegisterProyectComponent implements OnInit, OnDestroy {
  //Inicializacion de variables 
  stepperOrientation: Observable<StepperOrientation>;
  @ViewChild('stepper') stepper: MatStepper | undefined;

  ciudades:any[]=[];
  ciudad:any;
  user:User;
  paises:any[]=[];
  pais:any;
  sectores:any[]=[];
  sector:any=4;
  cargando:any=false;
  periodoPagos:any[]=[];
  periodoPago:any;
  riesgos:any[]=[];
  riesgo:any=null;
  plazos:any[]=[];
  plazo:any;
  date!: Date;
  actualizarJuridicos=false;
  actualizarFinancieros=false;
//Inicializacion de Datos de formularios
  firstFormGroup = this._formBuilder.group({
    nombre: ['', Validators.required],
    razonSocial: ['', Validators.required],
    ruc: ['', [Validators.required,Validators.minLength(13),Validators.maxLength(13)]],
    correo: ['',[Validators.required,Validators.email]],
    cargo: ['', Validators.required],
    nombreContacto: ['', Validators.required],
    direccion: ['', Validators.required],
    idEmpresa:[null],
    telefonoContacto: ['', [Validators.required,Validators.minLength(10),Validators.maxLength(10)]],
    anioinicio: ['', [Validators.required,Validators.min(1980),Validators.max(2023)]],
    antecedentes: ['', [Validators.required]],
    ventaja: ['', [Validators.required]],
    item1: ['', [Validators.required]],
    item2: ['', [Validators.required]],
    item3: ['', [Validators.required]],
    item4: ['', []],
    actividadDesc:['',],
  });

  get ruc() { return this.firstFormGroup.get('ruc'); }
  get correo() { return this.firstFormGroup.get('correo'); }
  get telefonoContacto() { return this.firstFormGroup.get('telefonoContacto'); }
  get anioinicio() { return this.firstFormGroup.get('anioinicio'); }
  get montoSol() { return this.secondFormGroup.get('montoSol'); }
  get destinoFinanciamiento() { return this.secondFormGroup.get('destinoFinanciamiento'); }
  get descripcionNegocio() { return this.businessform.get('descripcionNegocio'); }
  get tasaEfectivaAnual() { return this.secondFormGroup.get('tasaEfectivaAnual'); }
  get db() { return this.firstFormGroup.controls }
  get db3() { return this.businessform.controls }
  get db2() { return this.secondFormGroup.controls }
  get antecedentes() { return this.firstFormGroup.get('antecedentes'); }
  get ventaja() { return this.firstFormGroup.get('ventaja'); }

  businessform = this._formBuilder.group(
    {
      descripcionNegocio: ['', [Validators.required,Validators.minLength(3)]],
      margenContribucion: [null, Validators.required],
      ventasTotales: [null, Validators.required],
    }
  )

  mostrarForm(){
    console.log(this.db2)
  }
  secondFormGroup = this._formBuilder.group({
    idProyecto: [null],
    tasaEfectivaAnual: [0, [Validators.required,Validators.min(5),Validators.max(30)]],
    destinoFinanciamiento: ['', [Validators.required,Validators.minLength(3)]],
    montoSol: [, [Validators.required,Validators.min(25000),Validators.max(400000)]],
    periodoPago: ['', [Validators.required]],
    pagoInteres: ['', [Validators.required]],
    pagoCapital: ['', [Validators.required]],
  });

  descriptionProject = this._formBuilder.group({
    ciudad: [0, [Validators.required]],
    ventasTotales: ['', [Validators.required]],
    margenUtilidad: [, [Validators.required]],
    descripcionProducto: ['', [Validators.required]],
  });

  codProyecto:any;
  idEmpresa:any;

  thirdFormGroup = this._formBuilder.group({
    thirdCtrl: ['', Validators.required],
  });

  constructor(private _formBuilder: FormBuilder, 
    breakpointObserver: BreakpointObserver,
    private sanitizer: DomSanitizer,
    private storage:StorageService,
    private promotor:PromoterService,
    private router:Router) { 
    
    this.stepperOrientation = breakpointObserver
      .observe('(min-width: 800px)')
      .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
      this.getCatalogos();
      this.user=this.storage.getCurrentSession();
      this.inicializarArreglos();
      this.codProyecto=this.get_data('codProyecto');
      this.idEmpresa=this.get_data('idEmpresa');
      this.ConsultaPromotorxEmpresa();
      console.log(this.idEmpresa);
      console.log(this.padLeadingZeros(198,9))
  }
  ngOnDestroy(): void {
   localStorage.removeItem('codProyecto');
   localStorage.removeItem('idEmpresa');
  }



  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }

  
  ConsultaPromotorxEmpresa(){
    this.promotor.ConsultaPromotorxEmpresa(this.user.user.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        if(data!=null){
          console.log('id Empresa asignado');
          this.idEmpresa=data.idEmpresa;
          if(this.idEmpresa!=null){
            this.consultaEmpresa();
          }
          if(this.codProyecto!=null){
            this.consultaProyecto();
          }
        }
      }
    )
  }
  inicializarArreglos(){
      this.docArchivosJuridicos.push({'fileName':'Cargar documento','nombre':'Escritura','upload':false,'file':null});
      this.docArchivosJuridicos.push({'fileName':'Cargar documento','nombre':'Estatutos Vigentes','upload':false,'file':null});
      this.docArchivosJuridicos.push({'fileName':'Cargar documento','nombre':'Nombramiento RL','upload':false,'file':null});
      this.docArchivosJuridicos.push({'fileName':'Cargar documento','nombre':'Nomina de Accionistas','upload':false,'file':null});
      this.docArchivosJuridicos.push({'fileName':'Cargar documento','nombre':'Cedula Rl','upload':false,'file':null});
      this.docArchivosJuridicos.push({'fileName':'Cargar documento','nombre':'Identificacion Accionistas','upload':false,'file':null});
      this.docArchivosTributario.push({'fileName':'Cargar documento','nombre':'IR Año Anterior','upload':false,'file':null});
      this.docArchivosTributario.push({'fileName':'Cargar documento','nombre':'Ruc Actualizado','upload':false,'file':null});
      this.docArchivosContables.push({'fileName':'Cargar documento','nombre':'Estados Financieros Año Anterior','upload':false,'file':null});
      this.docArchivosContables.push({'fileName':'Cargar documento','nombre':'Estados Financieros Actuales','upload':false,'file':null});
      this.docArchivosContables.push({'fileName':'Cargar documento','nombre':'Anexo cts por cobrar','upload':false,'file':null});
      this.periodoPagos=PERIODO_PAGO;
      this.plazos=PLAZO;
  }

  ngOnInit(): void {
    
  }



  consultaProyecto(){
    this.promotor.ConsultaRiesgo().subscribe(
      (data:any)=>{
        this.riesgos = data;
        console.log(this.riesgos);
      },null,()=>{
        let cuenta={
          "codigoProyecto":this.codProyecto,
          "idEmpresa":this.idEmpresa,
          "estadoActual":"BO"
        }
        this.promotor.consultarProyectosxPromotor(cuenta,this.user).subscribe(
          (data:any)=>{
            console.log(data);
            this.db2['idProyecto'].setValue(this.codProyecto);
            this.db2['destinoFinanciamiento'].setValue(data[0].destinoFinanciamiento);
            this.db2['montoSol'].setValue(data[0].montoSolicitado);
            this.db2['pagoInteres'].setValue(data[0].pagoInteres);
            this.db2['pagoInteres'].setValue(data[0].pagoInteres);
            this.db2['tasaEfectivaAnual'].setValue(data[0].tasaEfectivaAnual);
            this.plazo=data[0].plazo;
            this.db2['pagoCapital'].setValue(data[0].pagoCapital)
            this.periodoPago=this.periodoPagos.filter(elemento=>{return data[0].pagoInteres.toLowerCase() == elemento.name.toLowerCase()}).map(elemento=>{return elemento.id})[0]
            console.log(this.periodoPago);
            this.riesgo=this.riesgos.filter(elemento=>{return data[0].calificacion.toLowerCase() == elemento.nombre.toLowerCase()}).map(elemento=>{return elemento.idTipoCalificacion})[0]
          },null,()=>{
            this.consultarDocumentosFinancieros();
            this.consultarDocumentosJuridicos();             
          }
        )
      }
    )
  }

  
  consultarDocumentosFinancieros(){
    let cuenta:any={
      "idEmpresa":this.idEmpresa,
      "user":this.user
    }
   
    
    this.promotor.consultarDocumentosFinancieros(cuenta,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        if(data==null){
          this.actualizarFinancieros=false;
          return; 
        }
        this.docArchivosContables[0].url=this.sanitizer.bypassSecurityTrustUrl(data.estadoFinancieroAnioAnterior);
        this.docArchivosContables[1].url=this.sanitizer.bypassSecurityTrustUrl(data.estadoFinancieroActuales);
        this.docArchivosContables[2].url=this.sanitizer.bypassSecurityTrustUrl(data.anexoCtsCobrar);
        this.docArchivosTributario[0].url=this.sanitizer.bypassSecurityTrustUrl(data.impuestoRentaAnioAnterior);
        this.actualizarFinancieros=true;
      }
    )
  }

  consultarDocumentosJuridicos(){
    let cuenta:any={
      "idEmpresa":this.idEmpresa,
      "user":this.user
    }
    
    this.promotor.consultarDocumentosJuridicos(cuenta,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        if(data==null){
          this.actualizarJuridicos=false;
          return; 
        }
        this.docArchivosJuridicos[0].url=this.sanitizer.bypassSecurityTrustUrl(data.escritura);
        this.docArchivosJuridicos[1].url=this.sanitizer.bypassSecurityTrustUrl(data.estatutosVigentes);
        this.docArchivosJuridicos[2].url=this.sanitizer.bypassSecurityTrustUrl(data.nombramientoRl);
        this.docArchivosJuridicos[3].url=this.sanitizer.bypassSecurityTrustUrl(data.nominaAccionista);
        this.docArchivosJuridicos[4].url=this.sanitizer.bypassSecurityTrustUrl(data.cedulaRl);
        this.docArchivosJuridicos[5].url=this.sanitizer.bypassSecurityTrustUrl(data.identificacionesAccionista);
        this.docArchivosTributario[1].url=this.sanitizer.bypassSecurityTrustUrl(data.rucVigente);
        this.actualizarJuridicos=true;
      }
    )
  }
 
  consultaDatos(){
    this.cargando=true;
    this.promotor.ConsultaPromotor(this.user.user.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        if(data==null){
          this.cargando=false;
          return;
        }
        this.db['cargo'].setValue(data.cargo);
        this.db['correo'].setValue(data.correo);
        this.db['ruc'].setValue(data.ruc);
        this.db['razonSocial'].setValue(data.razonSocial);
        this.db['anioinicio'].setValue(data.anioInicioAct);
        this.db['telefonoContacto'].setValue(data.telefonoContacto);
        this.db['nombreContacto'].setValue(data.nombreContacto);
        this.pais = this.paises.find((element:any)=>{return element.iso== data.pais}).idNacionalidad;
        
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
      },()=>{
        if(this.pais){
          this.consultaCiudades();

          this.cargando=false;
        }
      }
    )
  }
  control(){
    console.log(this.ciudad);
  }


  // descripcionNegocio: ['', Validators.required],
  // margenContribucion: ['', Validators.required],
  // ventasTotales: 


  consultaEmpresa(){
    let cuenta={
      "id":this.idEmpresa,
      "cuenta":this.user.user.identificacion
  }
  console.log(cuenta);
    this.promotor.ConsultaEmpresa(cuenta,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.db['idEmpresa'].setValue(data.id);
        this.db['direccion'].setValue(data.direccion);
        this.db['nombre'].setValue(data.nombre);
        this.db['antecedentes'].setValue(data.antecedentes.parrafo);
        this.db['ventaja'].setValue(data.ventajaCompetitiva.parrafo);
        this.db['item1'].setValue(data.ventajaCompetitiva.item1);
        this.db['item2'].setValue(data.ventajaCompetitiva.item2);
        this.db['item3'].setValue(data.ventajaCompetitiva.item3);
        this.db['item4'].setValue(data.ventajaCompetitiva.item4);
        this.db3['descripcionNegocio'].setValue(data.descripcionProducto);
        this.db3['margenContribucion'].setValue(data.margenContribucion);
        this.db3['ventasTotales'].setValue(data.ventasTotales);
        this.ciudad= data.ciudad;
      },(error:Error)=>{ 
        console.log(error);
      },()=>{
        
      }
    )
  }

  ingresarEmpresa(){
    this.cargando= true;
    this.promotor.ingresarEmpresa(this.obtenerEmpresa(),this.user).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        this.cargando= false;
        console.log(error);
      },()=>{
        this.cargando= false;
        Swal.fire({
          title: 'Éxito!',
          text: "Los datos de la empresa guardados correctamente",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.ConsultaPromotorxEmpresa();
            this.stepper!.linear=false;
            this.stepper!.next();
            console.log(window.scrollY);
            window.scroll(0,0);
            window.scrollY=0;
            console.log(window.scrollY);
          });
      }
    )
  }

  

  obtenerEmpresa(){
    let cuenta:any = {
      "id":this.idEmpresa,
      "nombre":this.db['nombre'].value,
      "actividad":this.sector,
      "pais":this.pais,
      "ciudad":this.ciudad,
      "cuenta":this.db['ruc'].value,
      "direccion":this.db['direccion'].value,
      "descripcionProducto":this.db3['descripcionNegocio'].value,
      "margenContribucion":this.db3['margenContribucion'].value,
      "ventasTotales":this.db3['ventasTotales'].value,
      "anioInicioActividad":this.db['anioinicio'].value,
      "anio": 2022,
      "estado":"A",
      "userCompose":this.user.user.identificacion,
    }
    let antecedente:any = JSON.stringify(this.getAntecedente());
    let ventajaCompetitiva:any= JSON.stringify(this.getVentaja());
    return cuenta = {
      ...cuenta,
      antecedente,
      ventajaCompetitiva
    }
  }

  getAntecedente(){
    return {
        parrafo:this.db['antecedentes'].value
    }
  }

  getVentaja(){
   return {
      parrafo:this.db['ventaja'].value,
        item1:this.db['item1'].value,
        item2:this.db['item2'].value,
        item3:this.db['item3'].value,
        item4:this.db['item4'].value
    
   } 
  }
  getCatalogos(){
    this.promotor.ConsultaSector().subscribe(
      (data:any)=>{
        this.sectores=data;
      }
    )
    this.promotor.ConsultaPaises().subscribe(
      (data:any)=>{
        this.paises=data;
      },(error:Error)=>{

      },()=>{
        this.consultaDatos();
      }
    )
    this.promotor.ConsultaRiesgo().subscribe(
      (data:any)=>{
        this.riesgos = data;
        console.log(this.riesgos);
      }
    )
  }

  consultaCiudades(){
    if(!this.pais){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar un país",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    this.promotor.ConsultaCiudades(this.pais).subscribe(
      (data:any)=>{
        this.ciudades=data;
      }
    )
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


  changeValidator(){
    if(this.sector==6){
      this.db['actividadDesc'].addValidators(Validators.required);
      return;
    }else{
      this.db['actividadDesc'].removeValidators(Validators.required);
      this.db['actividadDesc'].setValue('');
    }
    this.db['actividadDesc'].removeValidators(Validators.required);

  }

  changePagoCapital(data:any){
    this.db2['pagoCapital'].setValue(data.pagoCapital);
  }
  changePeriodoPago(data:any){
    console.log(data)
    this.db2['pagoInteres'].setValue(data.name);
  }
  consultaSelects(){
    if(!this.ciudad){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar una ciudad",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return false;
    }
    if(!this.pais){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar un pais",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return false;
    }
    if(!this.sector){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar una Actividad",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return false;
    }
    return true;
  }

  enviarDatos(){
    this.consultaSelects();
    console.log(this.firstFormGroup);
    if (!this.firstFormGroup.valid) {
      Swal.fire({
        title: 'Error!',
        text: "Debe completar todos los campos",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    this.ingresarEmpresa();
  }

  enviarDatos2(){
    this.consultaSelects();
    console.log(this.businessform);
    if (!this.businessform.valid) {
      Swal.fire({
        title: 'Error!',
        text: "Debe completar todos los campos",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    this.ingresarEmpresa();
  }

  ingresarProyecto(){
    let data:any
    this.promotor.ingresarProyecto(data,this.user)
  }

  

  cancelUpload($event:any,indice:any,tipo:any){
    if(tipo==1){
      this.docArchivosJuridicos[indice].file = null;
      this.docArchivosJuridicos[indice].fileName = "Cargar documento"
      this.docArchivosJuridicos[indice].upload = false;
      this.docArchivosJuridicos[indice].url = null;
    } else if(tipo==2){
      this.docArchivosTributario[indice].file = null;
      this.docArchivosTributario[indice].fileName = "Cargar documento"
      this.docArchivosTributario[indice].upload = false;
      this.docArchivosTributario[indice].url = null;
    }else{
      this.docArchivosContables[indice].file = null;
      this.docArchivosContables[indice].fileName = "Cargar documento"
      this.docArchivosContables[indice].upload = false;
      this.docArchivosContables[indice].url = null;
    }
  }

  //Carga de Archivos
  dataSource = new MatTableDataSource(ELEMENT_DATA);
  dataSource2 = new MatTableDataSource(ELEMENT_TRIBUTARIO);
  dataSource3 = new MatTableDataSource(ELEMENT_CONTABLE);
  displayedColumns: string[] = ['id', 'name','tipo', 'peso', 'accion','ver'];
  displayedColumns2: string[] = ['id', 'name','tipo', 'peso', 'accion','ver'];
  displayedColumns3: string[] = ['id', 'name','tipo', 'peso', 'accion','ver'];
  docArchivosJuridicos:documentos[]=[];
  docArchivosTributario:documentos[]=[];
  docArchivosContables:documentos[]=[];

  onFilechange(event: any,tipe:number) {
    if(!this.validararchivos(event)){
      return;
    }
      this.docArchivosJuridicos[tipe].file = event.target.files[0];
      this.docArchivosJuridicos[tipe].fileName = event.target.files[0].name;
      this.docArchivosJuridicos[tipe].upload = true;
      let blob = new Blob([this.docArchivosJuridicos[tipe].file!], { type: this.docArchivosJuridicos[tipe].file!.type });
      let url = window.URL.createObjectURL(blob);
      this.docArchivosJuridicos[tipe].url = this.sanitizer.bypassSecurityTrustUrl(url);
      event.target.value='';
      console.log(this.docArchivosJuridicos[tipe]);
  }
  onFilechange2(event: any,tipe:number) {
    if(!this.validararchivos(event)){
      return;
    }
      this.docArchivosTributario[tipe].file = event.target.files[0];
      this.docArchivosTributario[tipe].fileName = event.target.files[0].name;
      this.docArchivosTributario[tipe].upload = true;
      let blob = new Blob([this.docArchivosTributario[tipe].file!], { type: this.docArchivosTributario[tipe].file!.type });
      let url = window.URL.createObjectURL(blob);
      this.docArchivosTributario[tipe].url = this.sanitizer.bypassSecurityTrustUrl(url);
      event.target.value='';
      console.log(this.docArchivosTributario[tipe]);
  }
  onFilechange3(event: any,tipe:number) {
      if(!this.validararchivos(event)){
        return;
      }
      this.docArchivosContables[tipe].file = event.target.files[0];
      this.docArchivosContables[tipe].fileName = event.target.files[0].name;
      this.docArchivosContables[tipe].upload = true;
      let blob = new Blob([this.docArchivosContables[tipe].file!], { type: this.docArchivosContables[tipe].file!.type });
      let url = window.URL.createObjectURL(blob);
      this.docArchivosContables[tipe].url = this.sanitizer.bypassSecurityTrustUrl(url);
      event.target.value='';
      console.log(this.docArchivosContables[tipe]);
  }

  validararchivos(event:any){
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "application/pdf"){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos pdf",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return false;
    }
    if(event.target.files[0].size > 3000000){
      Swal.fire({
        title: 'Error!',
        text: "Error El archivo no debe superar los 3MB",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return false;
    }
    return true;
  }

  enviarArchivos(){
    let documentosEmpresa:documentos[]=[...this.docArchivosJuridicos,this.docArchivosTributario[1]];
    let documentosProyecto:documentos[]=[...this.docArchivosContables,this.docArchivosTributario[0]];
    console.log(this.codProyecto);
    console.log(this.obtenerFactura());
    
    let docEmpresa = {
      "idEmpresa":this.idEmpresa,
      "escritura":documentosEmpresa[0],
      "estatutos":documentosEmpresa[1],
      "nombramientoRl":documentosEmpresa[2],
      "nominaAccionista":documentosEmpresa[3],
      "cedulaRl":documentosEmpresa[4],
      "IdentificacionesAccionista":documentosEmpresa[5],
      "rucVigente":documentosEmpresa[6]
    }
    let docProyecto = {
      "idEmpresa":this.idEmpresa,
      "estadoFinancieroAnioAnterior":documentosProyecto[0],
      "estadoFinancieroActuales":documentosProyecto[1],
      "anexoCtasCobrar":documentosProyecto[2],
      "impuestoRentaAnioAnterior":documentosProyecto[3]
    }

    if( this.comprobarActualizarArchivos(documentosEmpresa) && this.comprobarActualizarArchivos(documentosProyecto)){
        Swal.fire({
          title: 'Error!',
          text: "Error Debe ingresar almenos un archivo",
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
        return;
    }

    if(this.actualizarJuridicos && !this.comprobarActualizarArchivos(documentosEmpresa)){
      this.registrarDocumentosJuridicos(docEmpresa);
      if(!this.comprobarActualizarArchivos(documentosProyecto)){
        this.registraDocumentosFinancieros(docProyecto);
        return;
      }else{
        this.stepper!.linear=false;
        this.stepper!.next();
        // Archivos actualizados
        
        return
      }
    }
    if(this.actualizarFinancieros && !this.comprobarActualizarArchivos(documentosProyecto)){
      this.registraDocumentosFinancieros(docProyecto);
        // Archivos actualizados

      return;
    }

    if(!this.comprobarSubidaArchivos(documentosEmpresa)&&!this.comprobarSubidaArchivos(documentosProyecto)){
      this.functionsubirArchivos(docEmpresa,docProyecto);
      //Crear Archivos

    }
  }

  padLeadingZeros(num:any, size:any) {
    var s = num+"";
    while (s.length < size) s = "0" + s;
    return s;
  }

  obtenerFactura(){
    this.date = new Date();
    //this.date.setDate(this.date.getDate());
    this.date.setDate(this.date.getDate()-5);
    let fact= {
      "pos" : "0a85e0da-6c17-4a0c-af26-2b25935e5c08",
      "fecha_emision": this.date.toLocaleDateString(),
      "tipo_documento": "FAC",
      "documento": "002-834-000000000",
      "estado": "P",
      "electronico" : true,
      "autorizacion": "",
      "vendedor": {
          "ruc": "0993370103001",
          "tipo": "J",
      },
      "descripcion": "FACTURA 78168",
      "subtotal_0": 0.00,
      "subtotal_12": 95.00,
      "iva": 11.40,
      "total": 106.40,
      "detalles": [{
          "producto_id": "ZxgepQG9NkI36a1p",
          "cantidad": 1.00,
          "precio": 95.00,
          "porcentaje_iva": 12,
          "porcentaje_descuento": 0.00,
          "base_gravable": 95.00,
      }]
  }
  console.log(JSON.stringify(fact));
  return fact;
  }

  async functionsubirArchivos (datosEmpresa:any,datosProyecto:any){
    this.cargando=true;

    var data = await this.promotor.registrarDocumentosJuridicos(datosEmpresa, this.user).subscribe(
      (data: any) => {
        console.log(data);
      }, (error: Error) => {
        this.cargando = false;
        console.log(error);
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }, () => {
        console.log('Primero');
        console.log(datosProyecto);
        data = this.promotor.registrarDocumentosFinancieros(datosProyecto,this.user).subscribe(
          (data:any)=>{
            console.log(data);
          },(error:Error)=>{
            this.cargando=false;
            Swal.fire({
              title: 'Error!',
              text: error.message,
              icon: 'error',
              confirmButtonText: 'Aceptar'
            });
          },()=>{
            
            let dato = {
              'idProyecto':this.codProyecto,
              'fact':this.obtenerFactura()
            }
            console.log(dato)
            this.promotor.registrarCorreoPrimeraFactura(dato,this.user).subscribe(
              (data:any)=>{
                this.cargando=false;
                console.log(data);
              },(error:Error)=>{
                this.cargando=false;
                console.log(error);
                Swal.fire({
                  title: 'Error!',
                  text: error.message,
                  icon: 'error',
                  confirmButtonText: 'Aceptar'
                })
              },()=>{
                this.cargando=false;
                console.log('tercero');
                Swal.fire({
                  title: 'Éxito!',
                  text: 'Documentos subidos correctamente Se ha enviado un correo ademas se ha generado la factura',
                  icon: 'success',
                  confirmButtonText: 'Aceptar'
                }).then(
                  ()=>{
                    setTimeout(() => {
                      this.router.navigate(["/promoter/home"]);
                    }, 1000);
                  }
                );
              }
            );


           }
        )
      })
  }

  async registrarDocumentosJuridicos(datosEmpresa:any){
    this.cargando=true;
    let dato = await this.promotor.registrarDocumentosJuridicos(datosEmpresa,this.user).subscribe(
      (data:any)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Éxito!',
          text: data.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        
      }
    )
  }

  async registraDocumentosFinancieros(datosProyecto:any){
    this.cargando=true;
    let dato = await this.promotor.registrarDocumentosFinancieros(datosProyecto,this.user).subscribe(
      (data:any)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Éxito!',
          text: data.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
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
        this.stepper!.linear=false;
        this.stepper!.next();
      }
    )
  }

  comprobarSubidaArchivos(documentos:any){
    console.log(documentos);
    let nombre:string|undefined;
    let datosIncompletos:boolean = documentos.some(
      (elemento:documentos)=>{
        nombre = elemento.nombre;
        return elemento.file==null;
      });
    console.log(datosIncompletos);
    if(datosIncompletos){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar todos los archivos: "+nombre,
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
    }
    return datosIncompletos;
  }

  comprobarActualizarArchivos(documentos:any){
    console.log(documentos);
    let nombre:string|undefined;
    let datosIncompletos:boolean = documentos.every(
      (elemento:documentos)=>{
        nombre = elemento.nombre;
        return elemento.file==null;
      });
    console.log(datosIncompletos);
    return datosIncompletos;
  }

// Obtener Proyectos

getProyectos(){
  return {
    "idProyecto": this.db2['idProyecto'].value,
    "tasaEfectivaAnual":this.db2['tasaEfectivaAnual'].value,
    "destinoFinanciamiento":this.db2['destinoFinanciamiento'].value,
    "montoSolicitado":this.db2['montoSol'].value,
    "plazo":this.plazo,
    "periodoPago":this.periodoPago,
    "pagoInteres":this.db2['pagoInteres'].value,
    "pagoCapital":this.db2['pagoCapital'].value,
    "idEmpresa":this.db['idEmpresa'].value,
    "idCalificacion":null,
    "userCompose":this.user.user.identificacion,
    "tipoInversion":"Reembolsable"
  }
}

ingresarProyectos(){
  this.cargando=true;
  console.log(this.getProyectos());
  this.promotor.ingresarProyecto(this.getProyectos(),this.user).subscribe(
    (data:any)=>{
      console.log(data);
      this.codProyecto=data.mensaje;
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
        text: "Los datos del proyecto se han ingresado correctamente",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      }).then(
        ()=>{
          this.stepper!.linear=false;
          this.stepper!.next();
          // this.stepper!.linear=true;
        });
    }
  )
}

}
interface documentos {
  nombre?:string,
  file?:File|null,
  url?:any,
  fileName:string,
  upload:boolean
}
const ELEMENT_DATA: any[] = [
  {id: 1, name: '', tipo:'Escritura', peso: '3mb', accion: ''},
  {id: 2, name: '', tipo:'Estatutos Vigentes', peso: '3mb', accion: ''},
  {id: 3, name: '', tipo:'Nombramiento RL', peso: '3mb', accion: ''},
  {id: 4, name: '', tipo:'Nómina Accionistas', peso: '3mb', accion: ''},
  {id: 5, name: '', tipo:'Cédula Rl', peso: '3mb', accion: ''},
  {id: 6, name: '', tipo:'Identificación Accionistas', peso: '3mb', accion: ''},
]

const ELEMENT_TRIBUTARIO: any[] = [
  {id: 1, name: '', tipo:'IR Año Anterior', peso: '3mb', accion: ''},
  {id: 2, name: '', tipo:'Ruc Actualizado', peso: '3mb', accion: ''},
]

const ELEMENT_CONTABLE: any[] = [
  {id: 1, name: '', tipo:'Estados Financieros Año Anterior', peso: '3mb', accion: ''},
  {id: 2, name: '', tipo:'Estados Financieros Actuales', peso: '3mb', accion: ''},
  {id: 3, name: '', tipo:'Anexo cts por cobrar', peso: '3mb', accion: ''},
]

const PERIODO_PAGO: any[] = [
  {id: 1, name: 'MENSUAL'},
  {id: 2, name: 'BIMENSUAL'},
  {id: 3, name: 'TRIMESTRAL'}
]

const PLAZO: any[] = [
  {id: 6, name: '6 Meses',pagoCapital:'Mes 6'},
  {id: 12, name: '12 Meses',pagoCapital:'Mes 12'},
  {id: 18, name: '18 Meses',pagoCapital:'Mes 12-18'},
  {id: 24, name: '24 Meses',pagoCapital:'Mes 12-18-24'},
]
