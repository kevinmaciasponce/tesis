import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatStepper, StepperOrientation} from '@angular/material/stepper';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { map, Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { bancoInterface } from 'src/app/shared/models/banco.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { InfoModalComponent } from '../../components/info-modal/info-modal.component';

@Component({
  selector: 'app-edit-beneficiary',
  templateUrl: './edit-beneficiary.component.html',
  styleUrls: ['./edit-beneficiary.component.css']
})
export class EditBeneficiaryComponent implements OnInit {
  @ViewChild('stepper') stepper: MatStepper | undefined;
  disciplina:any;
  fechaActual:any;
  tipoCliente:any='NAT';
  disciplinas:any[] =[];
  modalidad:any;
  cargando:any;
  modalidades:any[] =[];
  categoria:any;
  categorias:any[] =[];
  banco:any;
  bancos:any[] =[];
  tipo_cuenta:any;
  tipo_cuentas:any[] =[];
  nacionalidad:any;
  nacionalidades:PaisesInterface[] =[];
  user:User;
  submitted:any;
  actualizarFotos:any=false;
  actualizar:any=false;
  datoscompletos:boolean=false;
  filefrontal3?: File;
  urlphoto3:any;
  checked:any=false;
  nombredocfrontal3:string="Carga tu documento"
  anio:any=2022;
  //Formularios
  datosdeportista = this._formBuilder.group({
    nombre: ['',],
    apellido: ['',],
    celular: ['', [
    Validators.required,
    Validators.minLength(10),
    Validators.maxLength(15)],],
    cedula: [null, [
    Validators.required,
    Validators.minLength(10),
    Validators.maxLength(13)],],
    fechanacimiento: ['', ],
    razonSocial: ['', ],
    anioActividad: [0, ],
    correo: ['', [Validators.required,Validators.email]],
    perfil: ['', Validators.required],
    tituloAct: ['', Validators.required],
    cuenta: ['', Validators.required],
    // titular: ['', Validators.required],
  });
  secondFormGroup = this._formBuilder.group({
    secondCtrl: ['', Validators.required],
  });
  stepperOrientation: Observable<StepperOrientation>;
  thirdFormGroup = this._formBuilder.group({
    selectyear: ['2022', [Validators.required]],
    cod: ['', [Validators.required]],
    preAprobado1: [0, [Validators.required]],
    valRecibido: [0, [Validators.required]],
    fechaVigencia: ['', [Validators.required]],
    fechaCalificacion: ['', [Validators.required]],
  });
  constructor(private _formBuilder: FormBuilder, 
    breakpointObserver: BreakpointObserver,
    private dataApiClient: BeneficiaryService,
    public modalService: NgbModal,
    private storage:StorageService,
    private sanitizer: DomSanitizer,
    private _snackBar: MatSnackBar,
    private router: Router,
    ){
    this.stepperOrientation = breakpointObserver
      .observe('(min-width: 800px)')
      .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
      this.user=this.storage.getCurrentSession();
      let date = new Date();
      this.fechaActual=date.toISOString().split('T')[0];
  }

  
  get anioreg() { return this.thirdFormGroup.get('selectyear')?.value as string; }
  get db2() { return this.thirdFormGroup.controls }
  actualizarAnio(){
    this.anio=this.anioreg;
  }

  IsInvalid2(control: string): boolean {
    if (this.thirdFormGroup.get(control) != null) {
      let controlForm = this.thirdFormGroup.get(control);
      return controlForm != null
            ? ((controlForm.invalid && (controlForm.dirty || controlForm.touched))
               || controlForm.invalid && this.submitted )
            : false;
    }
    return false;
 }


 existeDoc(){
  if(!this.filefrontal3){
    Swal.fire({
      title: 'Error!',
      text: "Error Debe ingresar un documento",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  return true;
 }
  onFilechange2(event: any,tipe:number) {
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "application/pdf"){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos pdf",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombredocfrontal3 = "Cargar documento";
      return;
    }
    if(event.target.files[0].size > 3000000){
      Swal.fire({
        title: 'Error!',
        text: "Error El archivo no debe superar los 3MB",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombredocfrontal3 = "Cargar documento";
      return;
    }
    if(tipe==1){
      this.filefrontal3 = event.target.files[0];
      this.nombredocfrontal3 = this.filefrontal3!.name;
      console.log(this.nombredocfrontal3);
      let blob = new Blob([this.filefrontal3!], { type: this.filefrontal3!.type });
      let url = window.URL.createObjectURL(blob);
      this.urlphoto3 = this.sanitizer.bypassSecurityTrustUrl(url);
      console.log(this.urlphoto3);
    }
  }

 onSubmit2() {
    this.submitted = true;
    this.cargando=true;
    if (!this.thirdFormGroup.valid) {
      this.cargando=false;
      console.log(this.thirdFormGroup.errors)
      return;
    }
    this.submitted = false;
  }
  message:any;
  guardaValoracion(){
    if(!this.existeDoc()){
      return;
    }
    this.cargando=true;
    console.table(this.obtenerDatos());
    this.dataApiClient.guardaValoracion(JSON.stringify(this.obtenerDatos()) ,this.filefrontal3!,this.user).subscribe( 
      (data:any)=>{
        console.log(data)
        this.message=data.mensaje
      },(error:Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        })
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Exito!',
          text: this.message,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.cargando=true;
            this.stepper!.linear=false;
            this.stepper!.next();
            this.stepper!.linear=true;
            setTimeout(() => {
              this.cargando=false;
              this.router.navigate(["/agent/show"]);
            }, 1000);
          }
        )
      }
    )
  }
  regresar(){
    this.stepper?.previous();
  }
  consultarValoracion(){
    console.table(this.obtenerDatos());
    this.dataApiClient.consultarValoracion(this.datosdeportista.get('cedula')?.value,this.user).subscribe( 
      (data:any)=>{
        console.table(data);
        this.db2['cod'].setValue(data.calificacion);
        this.db2['fechaCalificacion'].setValue(data.fechaCalificacion);
        this.db2['fechaVigencia'].setValue(data.fechaCaducidad);
        this.db2['preAprobado1'].setValue(data.presupuestoAprobado);
        this.db2['valRecibido'].setValue(data.presupuestoRecaudado);
        this.checked=data.bianual;
        this.urlphoto3=data.ruta;
      },(error:Error)=>{
        console.log(error);
      }
    )
  }

 
  obtenerDatos(){
    return{
      "idBene":this.datosdeportista.get('cedula')?.value,
      "anio":this.anio as string,
      "calificacion":this.db2['cod'].value as string,
      "fechaCalificacion":this.db2['fechaCalificacion'].value as Date,
      "fechaCaducidad":this.db2['fechaVigencia'].value as string,
      "presupuestoAprobado":this.db2['preAprobado1'].value as number,
      "presupuestoRecaudado":this.db2['valRecibido'].value as number,
      "bianual":this.checked as boolean
    }
  }
  existeValoracion():boolean{
    this.dataApiClient.validaValoracion(this.datosdeportista.get('cedula')?.value,this.user).subscribe( 
      (data:any)=>{
        console.log(data);
        this.datoscompletos=data.mensaje;
      },(error:Error)=>{
        console.log(error);
        return false;
      },()=>{
        return this.datoscompletos;
      }
    )
    return this.datoscompletos;
  }

  continuarBtn(){
    if(this.existeValoracion()){
      Swal.fire({
        title: 'Exito!',
        text: "Datos completos",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      }).then(
        ()=>{
          this.cargando=true;
          this.stepper!.linear=false;
          this.stepper!.next();
          this.stepper!.linear=true;
          setTimeout(() => {
            this.cargando=false;
            this.router.navigate(["/agent/show"]);
          }, 1000);
        }
      )
    }else{
      Swal.fire({
        title: 'Exito!',
        text: "Debe subir la valoración",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      })
    }
  }
  
 
  
  ngOnInit(): void {
    this.getCatalogos();
    this.inicializarTitulos();
    if(this.get_data('cedula')){
      this.datosdeportista.get('cedula')?.setValue(this.get_data('cedula'));
      this.setearData();
      this.consultarBeneficiario();
    }else{
      const modal = this.modalService.open(InfoModalComponent, { 
        windowClass: 'my-class',
        backdrop  : 'static',
        keyboard  : false
    });
    }
  }

  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }
  inicializarTitulos(){
    this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
    this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
    this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
  }
  datosDeportista:any;
  consultarBeneficiario(){
    this.cargando=true;
    this.dataApiClient.consultaBeneficiarioAgregar(this.datosdeportista.get('cedula')?.value,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.datosDeportista=data;
        if(data.persona){
          this.datosdeportista.get('nombre')?.setValue(this.datosDeportista.persona.nombres);
          this.datosdeportista.get('apellido')?.setValue(this.datosDeportista.persona.apellidos);
          this.datosdeportista.get('celular')?.setValue(this.datosDeportista.persona.numeroCelular);
          if(this.datosDeportista.persona.razonSocial==null){
            this.datosdeportista.get('razonSocial')?.setValue('');
          }else{
            this.datosdeportista.get('razonSocial')?.setValue(this.datosDeportista.persona.razonSocial);
          }
          this.datosdeportista.get('anioActividad')?.setValue(this.datosDeportista.persona.anioInicio);
          if(this.datosDeportista.persona.anioInicio==null||this.datosDeportista.persona.anioInicio==undefined){
            this.datosdeportista.get('anioActividad')?.setValue(0);
          }
          this.datosdeportista.get('fechanacimiento')?.setValue(this.datosDeportista.persona.fechaNacimiento);
          if(this.datosDeportista.persona.fechaNacimiento==null||this.datosDeportista.persona.fechaNacimiento==''){
            this.datosdeportista.get('fechanacimiento')?.setValue(this.fechaActual);
          }else{
            this.datosdeportista.get('fechanacimiento')?.setValue(this.datosDeportista.persona.fechaNacimiento);
          }
        }
        if(data.beneficiario){
          this.urlphoto=data.beneficiario.ruta1;
          this.urlphoto2= data.beneficiario.ruta2;
          this.disciplina=this.disciplinas.filter((d:any)=> d.nombre==this.datosDeportista.beneficiario.disciplina).map((d:any)=>{return d.id})[0];
          this.categoria=this.categorias.filter((d:any)=> d.nombre==this.datosDeportista.beneficiario.categoria).map((d:any)=>{return d.id})[0];
          this.modalidad=this.modalidades.filter((d:any)=> d.nombre==this.datosDeportista.beneficiario.modalidad).map((d:any)=>{return d.id})[0];
          this.nacionalidad=this.datosDeportista.persona.nacionalidad;
          this.datosdeportista.get('correo')?.setValue(this.datosDeportista.beneficiario.correo);
          this.datosdeportista.get('perfil')?.setValue(this.datosDeportista.beneficiario.perfil);
          this.datosdeportista.get('tituloAct')?.setValue(this.datosDeportista.beneficiario.tituloActual);
          this.datosdeportista.get('cuenta')?.setValue(this.datosDeportista.beneficiario.numeroCuenta);
          this.banco=this.bancos.filter((d:bancoInterface)=> d.nombre==this.datosDeportista.beneficiario.banco).map((d:bancoInterface)=>{return d.idBanco})[0];
          this.tipo_cuenta=this.tipo_cuentas.filter((d:ParametrosInterface)=> d.valor.toLocaleLowerCase()==this.datosDeportista.beneficiario.tipoCuenta.toLocaleLowerCase()).map((d:ParametrosInterface)=>{return d.valor})[0];
        }
        this._snackBar.open("Datos de Usuario cargados correctamente", "Aceptar",{
          duration: 3000
        });
      },(error:Error)=>{
        console.log(error);
        this.cargando=false;
        this._snackBar.open("Datos de Usuario no encontrados", "Aceptar",{
          duration: 3000
        });
        this.actualizar=false;
      },()=>{
        this.existeValoracion();
        this.consultarBeneficiarioTitulos();
        this.actualizar=true;
        this.cargando=false;
      }
    )
  }

  consultarBeneficiarioTitulos(){
    this.cargando=true;
    this.dataApiClient.consultaBeneficiarioTitulos(this.datosdeportista.get('cedula')?.value,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        if(data.length!=0){
          this.words2.length=0;
        }
        for(let dato of data){
          this.words2.push({nacional: dato.rankingNacional,
            internacional:dato.rankingInternacional,otros:dato.otros, 
            nombreCompetencia:dato.nombreCompetencia,anio:dato.anioTitulo,idTitulo:dato.id});
        }
      },(error:Error)=>{
        console.log(error);
        this.cargando=false;
      },()=>{
        this.consultarValoracion();
       this.cargando=false;
      }
    )
  }
  getCatalogos(): void {
    this.dataApiClient.consultaDisciplina(this.user).subscribe(
      (data: Array<any>) => {
        this.disciplinas = data;
      });

    this.dataApiClient.consultaModalidad(this.user).subscribe(
      (data: Array<any>) => {
        this.modalidades = data;
      });

    this.dataApiClient.consultaCategoria(this.user).subscribe(
      (data: Array<any>) => {
        this.categorias = data;
      });
    this.dataApiClient.consultaPaises().subscribe(
      (data: Array<PaisesInterface>) => {
        this.nacionalidades = data;
      });
    this.dataApiClient.ConsultaBanco(this.user).subscribe(
      (data: Array<bancoInterface>) => {
        this.bancos = data;
      },(error:Error)=>{
        if(error.message=="Forbidden"){
          this.router.navigate(["sport-sponsorship/register/iniciar_sesion"]);
        }
      });
    this.dataApiClient.ConsultaTipoCuentas().subscribe(
      (data: Array<ParametrosInterface>) => {
        this.tipo_cuentas = data;
      });
  }

  get db() { return this.datosdeportista.controls }
  get cedula() { return this.datosdeportista.get('cedula'); }
  get celular() { return this.datosdeportista.get('celular'); }
  get nombre() { return this.datosdeportista.get('nombre')?.value; }
  get apellido() { return this.datosdeportista.get('apellido')?.value; }
  get razonSocial() { return this.datosdeportista.get('razonSocial')?.value; }
  ObtenerTipoCliente(){
    console.log(this.db['cedula'].value.length);
    if(this.db['cedula'].value.length==10){
      return 'NAT';
    }else{
      return 'JUR';
    }
  }

  getDatosNatural(){
    return {
      identificacion: this.db['cedula'].value as string,
      idRepresentante:  this.user.user.identificacion as string,
      disciplina:  this.disciplina as number,
      categoria:  this.categoria as number,
      modalidad:  this.modalidad as number,
      nacionalidad: this.nacionalidad as number,
      celular: this.db['celular'].value as string,
      apellidos: this.db['apellido'].value as string,
      nombres: this.db['nombre'].value as string,
      fechaNacimiento:  this.db['fechanacimiento'].value as string,
      tipoCliente: this.ObtenerTipoCliente(),
      razonSocial: this.db['razonSocial'].value as string,
      annioInicioAct:this.db['anioActividad'].value as number,
      correo:this.db['correo'].value as string,
      perfil:this.db['perfil'].value as string,
      tituloActual:this.db['tituloAct'].value as string,
      numeroCuenta: this.db['cuenta'].value as string,
      idBanco:this.banco,
      tipoCuenta:this.tipo_cuenta,
    }
  }

  setearData(){
    if(this.db['cedula'].value.length==10){
      this.tipoCliente = 'NAT'
      this.db['anioActividad'].setValue(0);
      this.db['razonSocial'].setValue('');
      return;
    }
    if(this.db['cedula'].value.length==13){
      this.tipoCliente = 'JUR'
      this.db['nombre'].setValue('');
      this.db['apellido'].setValue('');
      return;
    }
    this.resetearFormularios();
  }

  resetearFormularios(){
    this.db['apellido'].setValue('');
      this.db['nombre'].setValue('');
      this.db['anioActividad'].setValue(0);
      this.db['razonSocial'].setValue('');
      this.db['celular'].setValue('');
      this.db['fechanacimiento'].setValue('');
      this.db['correo'].setValue('');
      this.db['perfil'].setValue('');
      this.db['tituloAct'].setValue('');
      this.db['cuenta'].setValue('');
      this.banco=null;
      this.tipo_cuenta=null;
      this.disciplina=null;
      this.nacionalidad=null;
      this.categoria=null;
      this.modalidad=null;
      this.urlphoto=null;
      this.urlphoto2=null;
      this.words2.length=0
     
  }
  onSubmit() {
    this.submitted = true;
    this.cargando=true;
    if (!this.datosdeportista.valid) {
      console.log(this.datosdeportista.errors)
      this.cargando=false;
      return;
    }
    this.guardarDatos(this.getDatosNatural())
    this.submitted = false;
  }

  IsInvalid(control: string): boolean {
    if (this.datosdeportista.get(control) != null) {
      let controlForm = this.datosdeportista.get(control);
      return controlForm != null
            ? ((controlForm.invalid && (controlForm.dirty || controlForm.touched))
               || controlForm.invalid && this.submitted )
            : false;
    }
    return false;
 }
  guardarDatos(cuenta:any){
    console.log(cuenta);
    if(cuenta.fechaNacimiento==''){
      cuenta.fechaNacimiento=this.fechaActual;
    }
    if(cuenta.nombres==null){
      cuenta.nombres='';
    }
    if(cuenta.apellidos==null){
      cuenta.apellidos='';
    }
    this.cargando=true;
    this.dataApiClient.AgregrarBeneficiario(cuenta,this.user).subscribe(
      (data:any)=>{
       console.log(data);
      }, (error:Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }, ()=>{  
        this.cargando=false;
        if(this.actualizarFotos&&this.actualizar){
          this.agregarfotosBeneficiario(cuenta);
        }else{
          Swal.fire({
            title: 'Multiplo',
            text: "Desea actualizar la foto",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#e9a723',
            confirmButtonText: 'Actualizar',
            cancelButtonText:'Continuar' 
          }).then((result) => {
            if (result.isConfirmed) {
              if(this.validarfotos(this.filefrontal)&&this.validarfotos(this.fileposterior)){
                this.agregarfotosBeneficiario(cuenta);
              }
            }else {
              this.stepper!.linear=false;
              this.stepper!.next();
              this.stepper!.linear=true;
              return;
            }
          })
        }
      }
    )
  }
  agregarfotosBeneficiario(cuenta:any){
    this.dataApiClient.agregarfotosBeneficiario(cuenta.identificacion,this.filefrontal!
      ,this.fileposterior!,this.user).subscribe(
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
          this.cargando=false;
          Swal.fire({
            title: 'Éxito!',
            text: 'Se han guardado los datos del deportista correctamente.',
            icon: 'success',
            confirmButtonText: 'Aceptar'
          }).then(
            ()=>{
              this.cargando=false;
              this.stepper!.linear=false;
              this.stepper!.next();
              this.stepper!.linear=true;
            });
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

validar(){
  if(!this.verificardatosSelect()){
    return;
  }
  this.cargando=true;
   if(this.actualizar){
      if(this.filefrontal&&this.fileposterior){
        this.actualizarFotos=true;
      }
      this.onSubmit(); 
   }else{
    this.cargando=true;
    console.log( this.validarfotos(this.filefrontal));
    console.log( this.validarfotos(this.fileposterior));
    console.log(this.datosdeportista.valid);
   
    this.validarfotos(this.filefrontal);
    this.validarfotos(this.fileposterior);
    let validar = this.validarfotos(this.filefrontal)&&this.validarfotos(this.fileposterior);
    validar = validar && this.categoria && this.disciplina
     && this.modalidad && this.nacionalidad && this.datosdeportista.valid

    console.log(validar);
    if(validar){
      this.cargando=true;
      this.actualizarFotos=true;
      this.actualizar=true;
      this.onSubmit() 
    }else{
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: "Error Debe completar todos los campos",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
    }
   }
}

verificardatosSelect(){
  if(!this.disciplina){
    Swal.fire({
      title: 'Error!',
      text: "Error debe ingresar la disciplina",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  if(!this.categoria){
    Swal.fire({
      title: 'Error!',
      text: "Error debe ingresar la categoría ",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  if(!this.modalidad){
    Swal.fire({
      title: 'Error!',
      text: "Error debe ingresar la modalidad",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  if(!this.nacionalidad){
    Swal.fire({
      title: 'Error!',
      text: "Error debe ingresar la nacionalidad",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  if(!this.tipo_cuenta){
    Swal.fire({
      title: 'Error!',
      text: "Error debe ingresar el tipo de cuenta",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  if(!this.banco){
    Swal.fire({
      title: 'Error!',
      text: "Error debe ingresar el banco",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  return true;
}

validarfotos(file:any){ 
  console.log(!file||file==undefined)
  if(!file||file==undefined){
    Swal.fire({
      title: 'Error!',
      text: "Error Debe cargar las fotos",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return false;
  }
  return true;
}
RegresarVentana(){
  this.router.navigate(["/agent/show"]);
}

imprimir(daata:any){
  console.log(daata);
  daata.target.name
  let elemnt = document.getElementById(daata.target.name);
  elemnt!.ariaReadOnly = 'true';
}
  filefrontal?: File;
  fileposterior?: File;
  nombredocfrontal:string="Carga foto"
  nombredocposterior:string="Carga foto"
  urlphoto:any;
  urlphoto2:any;
onFilechange(event: any,tipe:number) {
  console.log(event.target.files[0]);
  if(event.target.files[0].type != "image/jpeg" && event.target.files[0].type != "image/png"){
    this.cargando=false;
    Swal.fire({
      title: 'Error!',
      text: "Error Debe ingresar solo archivos jpg o png",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    tipe==1?this.nombredocfrontal = "Cargar documento":this.nombredocposterior = "Cargar documento";
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
    tipe==1?this.nombredocfrontal = "Cargar documento":this.nombredocposterior = "Cargar documento";
    return;
  }
  if (tipe==1){
    this.filefrontal = event.target.files[0];
    this.nombredocfrontal = this.filefrontal!.name;
    console.log(this.nombredocfrontal);
    let blob = new Blob([this.filefrontal!], { type: this.filefrontal!.type });
    let url = window.URL.createObjectURL(blob);
    this.urlphoto = this.sanitizer.bypassSecurityTrustUrl(url);
    this.getImageSize(this.urlphoto).subscribe(response => { console.log(response); });
    console.log(this.urlphoto);
    
  }else if(tipe==2){
    this.fileposterior = event.target.files[0];
    this.nombredocposterior = this.fileposterior!.name;
    console.log(this.nombredocposterior);
    let blob = new Blob([this.fileposterior!], { type: this.fileposterior!.type });
    let url2=window.URL.createObjectURL(blob);
    this.urlphoto2 = this.sanitizer.bypassSecurityTrustUrl(url2);
    console.log(this.urlphoto2);
    
  this.getImageSize(this.urlphoto2).subscribe(response => { console.log(response); });
  }
 
}

getImageSize(url: string): Observable<any> 
{ 
  return new Observable(
    observer => 
    {
       var image = new Image(); 
       image.src = url; 
       image.onload = (e: any) => 
       { 
        var height = e.path[0].height; 
        var width = e.path[0].width; 
        observer.next(width + 'x' + height); 
        observer.complete(); 
      };
    }
  ); 
}




//TITULOS DEPORTIVOS



words2:any = [];
add(formulariovalido:any) {
  console.log(formulariovalido)
  // if(!formulariovalido){
  //   Swal.fire({
  //     title: 'Error!',
  //     text: "Se debe Agregar minimo 3 titulos y completar todos los campos",
  //     icon: 'error',
  //     confirmButtonText: 'Aceptar'
  //   });
  //   return;
  // }
  if(this.words2.length>=10){
    Swal.fire({
      title: 'Error!',
      text: "No se puede agregar mas títulos",
      icon: 'error',
      confirmButtonText: 'Aceptar'
    });
    return;
  }
  this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
  console.log(this.words2)
}



set_data(key:string,data:any){
  try{
    localStorage.setItem(key,data);
  } catch(e){
    console.log(e);
  }
}

regresarForm(){
  this.stepper!.linear=false;
  this.stepper!.previous();
}
validarForm2(){
  
//   this.set_data('identBeneficiario',this.db['cedula'].value);
//   const modal = this.modalService.open(ValueModalComponent, { 
//     windowClass: 'my-class',
// });
// this.stepper!.linear=false;
// this.stepper!.next();
// this.stepper!.linear=true;

console.log(this.comprobarNombreCompetencia());
  if(!this.comprobarNombreCompetencia()){
    return;
  }
  this.cargando=true;
  this.set_data('identBeneficiario',this.db['cedula'].value);
  let flag:boolean = true;
  for( let word of this.words2){
    flag = this.comprobarExistente(word.nombreCompetencia);
    flag = flag&&this.comprobarExistente(word.anio);
    console.log(word.idTitulo)
  }
  if(flag){
    this.enviardatos();
   }else{
    this.cargando=false;
     Swal.fire({
       title: 'Error!',
       text: "Error Debe completar todos los campos",
       icon: 'error',
       confirmButtonText: 'Aceptar'
     });
   }
  }

  comprobarNombreCompetencia(){
    console.log(this.words2[0].nombreCompetencia.length);
    let index=1;
    for( let word of this.words2){
      if(word.nombreCompetencia.length>=50){
        console.log(word.nombreCompetencia.length);
        Swal.fire({
          title: 'Error!',
          text: "Error el nombre de competencia del título: " +index +" No puede superar los 50 caracteres",
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
        return false;
      }
      if(word.nombreCompetencia.length==0){
        console.log(word.nombreCompetencia.length);
        Swal.fire({
          title: 'Error!',
          text: "Error el nombre de competencia del título: " +index +" No puede estar vacío",
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
        return false;
      }
      index++;
    }
    return true;
  }

  porcenatjeprueba:any=0;

  comprobarExistente(dato:any){
    if(dato){
      return true;
    }else{
      return false;
    }
  }
  getTitulos(titulo:any){
    return {
      idDisciplina: this.disciplina as number ,
      anioTitulo: titulo.anio,
      nombreCompetencia:  titulo.nombreCompetencia,
      rankingNacional: titulo.nacional,
      rankingInternacional: titulo.internacional,
      otros:titulo.otros,
      idTitulo:titulo.idTitulo
    }
  }

  enviardatos(){
    let titulos:any[]=[];
    for( let word of this.words2){
      titulos.push(this.getTitulos(word));
    }
    let cuenta= {
        identificacion: this.db['cedula'].value as string,
        idRepre:  this.user.user.identificacion as string,
        titulos:titulos
    }
    this.cargando=true;
    this.dataApiClient.AgregrarBeneficiarioTitulos(cuenta,this.user).subscribe(
      (datos:any)=>{
        console.log(datos);
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
          text: "Se han agregados los títulos",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.cargando=false;
            this.stepper!.linear=false;
            this.stepper!.next();
            this.stepper!.linear=true;
          }
        );
      }
    )
  }


}
