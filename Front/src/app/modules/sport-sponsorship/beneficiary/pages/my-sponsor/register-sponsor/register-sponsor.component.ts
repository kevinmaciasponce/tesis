import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatStepper, StepperOrientation} from '@angular/material/stepper';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { map, Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { bancoInterface } from 'src/app/shared/models/banco.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { InfoModalComponent } from '../../components/info-modal/info-modal.component';
import { ValueModalComponent } from '../../components/value-modal/value-modal.component';

@Component({
  selector: 'app-register-sponsor',
  templateUrl: './register-sponsor.component.html',
  styleUrls: ['./register-sponsor.component.css']
})
export class RegisterSponsorComponent implements OnInit, OnDestroy {
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
 
  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }
  stepperOrientation: Observable<StepperOrientation>;

  constructor(private _formBuilder: FormBuilder, 
    breakpointObserver: BreakpointObserver,
    private dataApiClient: BeneficiaryService,
    public modalService: NgbModal,
    private storage:StorageService,
    private sanitizer: DomSanitizer,
    private _snackBar: MatSnackBar,
    private router: Router,
    ) {
    this.stepperOrientation = breakpointObserver
      .observe('(min-width: 800px)')
      .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
      this.user=this.storage.getCurrentSession();
      let date = new Date();
      this.fechaActual=date.toISOString().split('T')[0];
  }
  ngOnDestroy(): void {
    localStorage.removeItem('ced');
  }
  ngOnInit(): void {
    this.getCatalogos();
    this.inicializarTitulos();
    this.inicializarTorneos();
    const modal = this.modalService.open(InfoModalComponent, { 
      windowClass: 'my-class',
      backdrop  : 'static',
      keyboard  : false
  });
    
  }
  inicializarTitulos(){
    this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
    this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
    this.words2.push({nacional: '',internacional:'',otros:'', nombreCompetencia:'',anio:'',idTitulo:''});
  }
  inicializarTorneos(){
    this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0},fecha:''});
    this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0},fecha:''});
    this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0 },fecha:''});
  }
  isOpenContact:boolean = false;
  p1:boolean = false;
  p2:boolean = false;
  p3:boolean = false;
  toggleState(dato:boolean){
    return !dato;
  }

  getCategoria(data:any){
    return this.categorias.filter((r:any) =>  r.id === data).map(function(r:any)  {return  r.nombre});
  }

  getDisciplina(data:any){
    return this.disciplinas.filter((elemento:any) =>  elemento.id === data).map(function(r:any)  {return  r.nombre});
  }
  
  getPais(data:any){
    return this.nacionalidades.filter((r:any) =>  r.iso === data).map(function(r:any)  {return  r.pais});
  }

  getIdPais(data:any):number{
    return this.nacionalidades.filter((r:any) =>  r.pais === data).map(function(r:any)  {return  r.idNacionalidad})[0];
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
          this.tipo_cuenta=this.tipo_cuentas.filter((d:ParametrosInterface)=> d.valor==this.datosDeportista.beneficiario.tipoCuenta).map((d:ParametrosInterface)=>{return d.valor})[0];
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
       this.consultaBeneficiarioAuspicio();
       this.cargando=false;
      }
    )
  }

  consultaBeneficiarioAuspicio(){
    let numeroAuspicio:any
    this.cargando=true;
    this.dataApiClient.consultaBeneficiarioAuspicio(this.datosdeportista.get('cedula')?.value,'BO',this.user).subscribe(
      (data:any)=>{
        console.log(data[0]);
        if(data[0]==undefined){
          numeroAuspicio='';
          this.dbauspicio['presupuesto'].setValue(0);
          this.dbauspicio['id'].setValue('');
        }else{
          numeroAuspicio=data[0].numeroAuspicio;
          this.dbauspicio['presupuesto'].setValue(data[0].montoSolicitado);
          this.dbauspicio['id'].setValue(data[0].numeroAuspicio); 
        }
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
      },()=>{
        // consultaRecompensas y torneo
        this.cargando=true;
        let datos:any;
        this.dataApiClient.consultaTorneos(numeroAuspicio,this.user).subscribe(
          (data:any)=>{
            console.log(data)
            datos=data
          },(error:Error)=>{
            this.cargando=false;
          },()=>{
            if(datos.length!=0){
              this.torneos.length=0;
            }
            for(let dato of datos){
              this.torneos.push({nombreTorneo: dato.nombreTorneo,pais:{idNacionalidad: this.getIdPais(dato.pais)
              },fecha:dato.fecha});   
            }
          }
        )
        this.cargando=true;
        this.dataApiClient.consultaRecompensas(numeroAuspicio,this.user).subscribe(
          (data:any)=>{
            console.log(data)
            for(let dato of data){
              switch(dato.categoria){
                case "ORO":
                  this.dbauspicio['categoria1'].setValue(dato.categoria);
                  this.dbauspicio['porcentaje1'].setValue(dato.porcentaje);
                  this.dbauspicio['detalle1'].setValue(dato.detalle);
                  this.dbauspicio['id1'].setValue(dato.id);
                  break;
                  case "PLATA":
                    this.dbauspicio['categoria2'].setValue(dato.categoria);
                    this.dbauspicio['porcentaje2'].setValue(dato.porcentaje);
                    this.dbauspicio['detalle2'].setValue(dato.detalle);
                    this.dbauspicio['id2'].setValue(dato.id);
                  break;
                  case "BRONCE":
                    this.dbauspicio['categoria3'].setValue(dato.categoria);
                    this.dbauspicio['porcentaje3'].setValue(dato.porcentaje);
                    this.dbauspicio['detalle3'].setValue(dato.detalle);
                    this.dbauspicio['id3'].setValue(dato.id);
                  break;
              }
            }
          },(error:Error)=>{
            this.cargando=false;
          },()=>{
            this.cargando=false;
            this._snackBar.open("Se han cargado todos los datos del formulario", "Aceptar",{
              duration: 3000
            });
          }
        )
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
      this.torneos.length=0
      this.inicializarTitulos();
      this.inicializarTorneos();
      this.dbauspicio['presupuesto'].setValue(0);
      this.dbauspicio['id1'].setValue('');
      this.dbauspicio['categoria1'].setValue('ORO');
      this.dbauspicio['detalle1'].setValue('');
      this.dbauspicio['porcentaje1'].setValue('> 35%');
      this.dbauspicio['id2'].setValue('');
      this.dbauspicio['categoria2'].setValue('PLATA');
      this.dbauspicio['porcentaje2'].setValue('Entre 16% y 34%');
      this.dbauspicio['detalle2'].setValue('');
      this.dbauspicio['id3'].setValue('');
      this.dbauspicio['categoria3'].setValue('BRONCE');
      this.dbauspicio['porcentaje3'].setValue('Menor 15%');
      this.dbauspicio['detalle3'].setValue('');
  }

  //Datos deportista
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

imprimir(daata:any){
  console.log(daata);
  daata.target.name
  let elemnt = document.getElementById(daata.target.name);
  elemnt!.ariaReadOnly = 'true';
}
  filefrontal?: File;
  fileposterior?: File;
  nombredocfrontal:string="Carga tu foto"
  nombredocposterior:string="Carga tu foto"
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

siguienteForm(){
  this.guardarAuspicio();
  // if(this.dbauspicio['id'].value!=''){
  //   console.log('entro')
  //   this.consultaBeneficiarioAuspicio();
  //   this.agregarRecompensas();
  // }else{
  //  this.guardarAuspicio();
  // }
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
            this.stepper!.linear=false;
            this.stepper!.next();
            this.stepper!.linear=true;
            const modal = this.modalService.open(ValueModalComponent, { 
              windowClass: 'my-class2',
              // backdrop  : 'static',
              // keyboard  : false
          });
          }
        );
      }
    )
  }


  gotoStepper(index:any){
    this.stepper!.linear=false;
    let currentStep = index;
    while (this.stepper!.selectedIndex < currentStep) {
        this.stepper!.selectedIndex! += 1;
    }
    this.stepper!.selectedIndex = currentStep;
    this.stepper!.linear=true;
  }


//Auspicios Deportivos
thirdFormGroup = this._formBuilder.group({
  presupuesto: [0, Validators.required],
  id1:['', Validators.required],
  categoria1:['ORO', Validators.required],
  porcentaje1:['> 35%', Validators.required],
  detalle1:['', Validators.required],
  id2:['', Validators.required],
  categoria2:['PLATA', Validators.required],
  porcentaje2:['Entre 16% y 34%', Validators.required],
  detalle2:['', Validators.required],
  id3:['', Validators.required],
  categoria3:['BRONCE', Validators.required],
  porcentaje3:['Menor 15%', Validators.required],
  detalle3:['', Validators.required],
  id:[null, Validators.required]
});
get dbauspicio() { return this.thirdFormGroup.controls }
get presupuesto() { 
  
  if(this.thirdFormGroup.get('presupuesto')?.value){
    return this.thirdFormGroup.get('presupuesto')?.value.toLocaleString("en-US", {
      style: "currency",
      currency: "USD"
      });
  }
  return 0;
}
torneos:torneos[] = [];
addtorneos() { 
  if(this.torneos.length>=10){
    console.log("No se puede agregar mas torneos")
    return;
  }
  this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0},fecha:''});
  console.log(this.torneos);
}
tipoPersona:any;
guardarAuspicio(){
  this.cargando=true;
  this.dataApiClient.AgregrarAuspicio(this.ObtenerAuspicio(),this.user).subscribe(
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
      let numeroAuspicio:any
      this.cargando=true;
      this.dataApiClient.consultaBeneficiarioAuspicio(this.datosdeportista.get('cedula')?.value,'BO',this.user).subscribe(
        (data:any)=>{
          console.log(data);
          numeroAuspicio=data[0].numeroAuspicio;
          this.dbauspicio['id'].setValue(data[0].numeroAuspicio); 
          this.dbauspicio['presupuesto'].setValue(data[0].montoSolicitado); 
        },(error:Error)=>{
          this.cargando=false;
        },()=>{
          this.cargando=false;
          this.agregarRecompensas();
        }
      );
    }
  )
}

agregarRecompensas(){
  console.log(this.getRecompensas());
  this.dataApiClient.AgregrarRecompensas(this.getRecompensas(),this.user).subscribe(
    (data:any)=>{
      console.log(data);
    },(error:Error)=>{
      console.log(error);
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
        text: "Se han guardado los datos correctamente",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      });
      this.stepper!.linear=false;
      this.stepper!.next();
      this.stepper!.linear=true;
    }
  )
}

validarTorneos(){

}

agregarTorneos(){
  this.dataApiClient.AgregrarTorneos(this.getTorneos(),this.user).subscribe(
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
        text: "Se han guardado los datos correctamente",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      });
      this.stepper!.linear=false;
      this.stepper!.next();
      this.stepper!.linear=true;
    }
  )
}


ObtenerAuspicio(){
  let data= {
  "idBene": this.db['cedula'].value as string,
  "presupuestoSolicitudo": this.dbauspicio['presupuesto'].value as string,
  'numeroAuspicio':this.dbauspicio['id'].value as string,
  }
  console.log(data);
  return data;
}

getRecompensas(){
  return  {
    'numeroAuspicio':this.dbauspicio['id'].value as number,
    'identificacion':this.db['cedula'].value as string,
    "recompensas":[
    {
      // "id":this.dbauspicio['id1'].value as number,
      "categoria":this.dbauspicio['categoria1'].value as string,
      "porcentaje":this.dbauspicio['porcentaje1'].value as string,
      "detalle":this.dbauspicio['detalle1'].value as string
  },
  {
      // "id":this.dbauspicio['id2'].value as number,
      "categoria":this.dbauspicio['categoria2'].value as string,
      "porcentaje":this.dbauspicio['porcentaje2'].value as string,
      "detalle":this.dbauspicio['detalle2'].value as string
  },
  {
      // "id":this.dbauspicio['id3'].value as number,
      "categoria":this.dbauspicio['categoria3'].value as string,
      "porcentaje":this.dbauspicio['porcentaje3'].value as string,
      "detalle":this.dbauspicio['detalle3'].value as string
  } 
]
}
}

getTorneos(){
  let torneos1:any[]=[];
  console.log(this.torneos);
  for( let word of this.torneos){
    torneos1.push(word);
  }
  console.log(torneos1);
return {
  'numeroAuspicio':this.dbauspicio['id'].value as string,
  'identificacion':this.db['cedula'].value as string,
  "torneos":torneos1
}
}

confirmarDatos(){
let mensaje:any;
this.cargando=true;
  this.dataApiClient.ConfirmarAuspicio( this.dbauspicio['id'].value ,this.user).subscribe(
    (data:any)=>{
      mensaje = data;
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
      console.log(mensaje);
      Swal.fire({
        title: 'Exito!',
        text: "Su auspicio se envió para la revisión",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      }).then(
        ()=>{
          this.router.navigateByUrl('/agent/home');
        }
      );
    }
  )
}
}

export interface torneos{
  nombreTorneo:string;
  pais:{idNacionalidad:number| string};
  fecha:string;
}