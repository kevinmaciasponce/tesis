import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, Observable, Subscription } from 'rxjs';
import { matchValidator } from 'src/app/providers/CustomValidators';
import { IEmpleadosVentas } from 'src/app/shared/models/empleados-ventas.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { PersonaJuridicaInterface } from 'src/app/shared/models/persona-juridica-interface';
import { DataApiClientService } from 'src/app/shared/service/data-api-client.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register-juridica-page',
  templateUrl: './register-juridica-page.component.html',
  styleUrls: ['./register-juridica-page.component.css']
})

//this.get_data('url')

export class RegisterJuridicaPageComponent implements OnInit,OnDestroy {
  @ViewChild('ngForm')
  financiamiento!:boolean;
  formDirective!: FormGroupDirective;
  cargando!:boolean;
  submitted: boolean = false;
  guardando: boolean = false;
  nacionalidades: Array<PaisesInterface> = [];
  analistas: Array<IEmpleadosVentas> = [];
  comoNosContacto: Array<ParametrosInterface> = [];
  private subscriptions: Array<Subscription> = [];
  guardar$: Observable<any> | undefined;
  email?:string="";
  tipocliente?:string="";
  registrationForm: FormGroup = new FormGroup({
    razonSocial: new FormControl(null, [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(100),
    ]),
    ruc: new FormControl(null, [
      Validators.required,
      Validators.minLength(13),
      Validators.maxLength(13),
    ]),
    telefono: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10)
    ]),
    nacionalidad: new FormControl(null, [
      Validators.required
    ]),
    nombreContacto: new FormControl(null, [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(100),
      Validators.pattern('^[a-zA-Z ]*$')
    ]),
    cargoContacto: new FormControl(null, [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(100)
    ]),
    correoElectronico: new FormControl(null, [
      Validators.required,
      Validators.email
    ]),
    contrasenia: new FormControl(null, [
      Validators.required,
      Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$.,#/<>;:*!%?&])[A-Za-z\d$@$.,#/<>;:*!%?&].{6,}$'),
      Validators.minLength(6),
      Validators.maxLength(12),
      matchValidator('confirmarContrasenia', true)
    ]),
    confirmarContrasenia: new FormControl(null, [
      Validators.required,
      matchValidator('contrasenia')
    ]),
    comoContactaste: new FormControl(null, [
      Validators.required
    ]),
    analista: new FormControl(null, [
      //Validators.required
    ]),
    registrarse: new FormControl(false, [
      Validators.required,
      Validators.pattern('true')
    ]), 
    acuerdo: new FormControl(false, [
      Validators.required,
      Validators.pattern('true')
    ]),      
  }
  );


  
  get db() { return this.registrationForm.controls }
  get contrasenia() { return this.registrationForm.get('contrasenia'); }
  get confirmarContrasenia() { return this.registrationForm.get('confirmarContrasenia'); }
  get ruc() { return this.registrationForm.get('ruc'); }
  get inicioActividad() { return this.registrationForm.get('inicioActividad'); }
  get nombreContacto() { return this.registrationForm.get('nombreContacto'); }
  get cargoContacto() { return this.registrationForm.get('cargoContacto'); }
  get isAnalista() { return this.registrationForm.get('comoContactaste')?.value === 'ANALISTAOP' }
  get analista() { return this.registrationForm.get('analista')?.value }
  
  constructor(private formBuilder: FormBuilder,
    private dataApiClient: DataApiClientService,
    private router: Router
    ) {
      this.email=this.get_data('url');
      this.tipocliente=this.get_data('financiamiento');
      this.cargando=false;
     }
     
  ngOnDestroy(): void {
    this.clear_data();
  }

    clear_data(){
      try{
        localStorage.clear();
      } catch(e){
        console.log(e);
      }
    }
     
  ngOnInit(): void {
    this.getCatalogos();
    this.registrationForm.get('comoContactaste')?.valueChanges.subscribe(value=>{
      if (value && this.isAnalista) {
        this.registrationForm.get('analista')?.setValidators(Validators.required);
      } else {
        this.registrationForm.get('analista')?.setValue('');
        this.registrationForm.get('analista')?.clearValidators(); 
      }
    })
  }

  toogleValue(event:any){
    console.log(event.target.value);
    if (event.target.value !='2: ANALISTAOP') {
      this.registrationForm.get('analista')?.setValidators(Validators.required);
    } else {
      this.registrationForm.get('analista')?.setValue('');
      this.registrationForm.get('analista')?.clearValidators(); 
    }
    
  }

  pass_hidden ="fa fa-eye-slash";
  pass_show ="fa fa-eye";
  tipo ='password';
  mostrarPassword(){
    if( this.tipo =="password"){
     this.tipo = "text";
      this.pass_show ="fa fa-eye";
    }else {
      this.tipo = "password";
      this.pass_hidden ="fa fa-eye-slash";
    }
  } 

  onSubmit() {
    this.cargando=false;
    if(this.guardando) {
      return;
    }
    console.warn(this.registrationForm.value);
    this.submitted = true;
    if (!this.registrationForm.valid) {
        console.log(this.registrationForm.errors)
        return;
    }
    this.submitted = false;
    let cuenta = this.obtenerCuenta();
    console.log(cuenta);
    this._crearCuenta(cuenta);    
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

  getCatalogos(): void {
    this.dataApiClient.ConsultaPaises().subscribe(
      (data: Array<PaisesInterface>) => {
        this.nacionalidades = data;
      });
      
      this.dataApiClient.ConsultaItems("tipo_contacto").subscribe(
        (data: Array<ParametrosInterface>) => {
          this.comoNosContacto = data;
        });
      this.dataApiClient.ConsultaEmpleadosVentas().subscribe(
        (data: Array<IEmpleadosVentas>) => {
          this.analistas = data;
        });
  }

  get_data(key:string){
      return localStorage.getItem(key)?.toString();
  }
  
  obtenerCuenta(): PersonaJuridicaInterface{
    let cuenta: PersonaJuridicaInterface;
    if(this.tipocliente!='financiamiento'){
      cuenta = {
        tipoCliente: 'INVERSIONISTA',
        tipoPersona: 'JUR',
        tipoIdentificacion: 'RUC',
        razonSocial: (this.db['razonSocial'].value as string).toUpperCase(),
        identificacion: this.db['ruc'].value,
        nombreContacto: (this.db['nombreContacto'].value as string).toUpperCase(),
        cargoContacto: (this.db['cargoContacto'].value as string).toUpperCase(),
        emailContacto: this.db['correoElectronico'].value,
        email: this.email ,
        numeroCelular: this.db['telefono'].value,
        nacionalidad: this.db['nacionalidad'].value,
        clave: this.db['contrasenia'].value,
        tipoContacto: 'PUB',
        aceptaPoliticaPrivacidad: 'S',
        aceptaRecibirInformacion: 'S',
        aceptaTerminoUso:'S',
        usuarioCreacion:this.db['ruc'].value,
        usuarioContacto: ''
      };
    }else{
      cuenta = {
        tipoCliente: 'PROMOTOR',
        tipoPersona: 'JUR',
        tipoIdentificacion: 'RUC',
        razonSocial: (this.db['razonSocial'].value as string).toUpperCase(),
        identificacion: this.db['ruc'].value,
        nombreContacto: (this.db['nombreContacto'].value as string).toUpperCase(),
        cargoContacto: (this.db['cargoContacto'].value as string).toUpperCase(),
        emailContacto: this.db['correoElectronico'].value,
        email: this.email ,
        numeroCelular: this.db['telefono'].value,
        nacionalidad: this.db['nacionalidad'].value,
        clave: this.db['contrasenia'].value,
        tipoContacto: 'PUB',
        aceptaPoliticaPrivacidad: 'S',
        aceptaRecibirInformacion: 'S',
        aceptaTerminoUso:'S',
        usuarioCreacion:this.db['ruc'].value,
        usuarioContacto:''
      };
    }
    if (this.isAnalista) {
      cuenta = {
        ...cuenta,
        usuarioContacto: this.db['analista'].value
      }
    }
    return cuenta;
  }

  private _crearCuenta(cuenta: PersonaJuridicaInterface){    
    this.guardar$ = this.dataApiClient.RegistrarPersonaJuridica(cuenta).pipe(
      finalize(() => { } )
    );
    this.cargando=true;
    this.guardando = true;
    console.log(cuenta);
    this.subscriptions.push(this.guardar$.subscribe(
      (result: any) => {
        console.warn(result);
        this.guardando = false;
        this.cargando=false;
        if(this.tipocliente!='financiamiento'){
          Swal.fire({
            title: 'Éxito',
            //text: 'Su cuenta está activada. Puede iniciar sesión con el correo y clave registrada.',
            text: 'Se le ha enviado un correo, para activar su cuenta.',
            icon: 'success',
            confirmButtonText: 'Aceptar'
          });
        }
        else{
          Swal.fire({
            title: 'Gracias por Crear tu cuenta con Múltiplo',
            //text: 'Su cuenta está activada. Puede iniciar sesión con el correo y clave registrada.',
            text: 'Por favor revise su correo para continuar con el proceso de registro.',
            icon: 'success',
            confirmButtonText: 'Aceptar'
          });
        }
        this.router.navigateByUrl('/');
      },
      (error: Error) => {
        this.guardando = false;
        this.cargando=false;
        if(error.message=="ok"){
          Swal.fire({
            title: 'Éxito!',
            text: 'Se le ha enviado un correo, para activar su cuenta.',
            //text: 'Su cuenta está activada. Puede iniciar sesión con el correo y clave registrada.',
            icon: 'success',
            confirmButtonText: 'Aceptar'
          }).then(()=>{
            //this.resetForm();
            this.router.navigateByUrl('/')
          });
        }else if(error.message=="Es promotor"){
            Swal.fire({
              title: 'Gracias por Crear tu cuenta con Múltiplo',
              //text: 'Su cuenta está activada. Puede iniciar sesión con el correo y clave registrada.',
              text: 'Por favor revise su correo para continuar con el proceso de registro.',
              icon: 'success',
              confirmButtonText: 'Aceptar'
            }).then(()=>{
              //this.resetForm();
              this.router.navigateByUrl('/')
            });
        }else{
          Swal.fire({
            title: 'Error!',
            text: error.message,
            icon: 'error',
            confirmButtonText: 'Aceptar'
          });
        }
        
      },()=>{
        this.cargando=false;
      }
    ));
  }

  resetForm() : void{
    this.formDirective.resetForm();
    this.registrationForm.reset();
  }

  keyPressNumbers(event: { which: any; keyCode: any; preventDefault: () => void; }) {
    var charCode = (event.which) ? event.which : event.keyCode;
    // Only Numbers 0-9
    if ((charCode < 48 || charCode > 57)) {
      event.preventDefault();
      return false;
    } else {
      return true;
    }
  }

  keyPressAlpha(event: { keyCode: number; preventDefault: () => void; }) {

    var inp = String.fromCharCode(event.keyCode);

    if (/^[a-zA-Z ]*$/.test(inp)) {
      return true;
    } else {
      event.preventDefault();
      return false;
    }
  }

}
