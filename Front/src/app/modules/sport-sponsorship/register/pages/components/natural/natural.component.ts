import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, Observable, Subscription } from 'rxjs';
import { matchValidator } from 'src/app/providers/CustomValidators';
import { IEmpleadosVentas } from 'src/app/shared/models/empleados-ventas.interface';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { ParametrosInterface } from 'src/app/shared/models/parametros-interface';
import { PersonaNaturalInterface } from 'src/app/shared/models/persona-natural-interface';
import Swal from 'sweetalert2'
import { DatePipe } from '@angular/common';
import { DataApiClientService } from 'src/app/shared/service/data-api-client.service';

@Component({
  selector: 'app-natural',
  templateUrl: './natural.component.html',
  styleUrls: ['./natural.component.css']
})
export class NaturalComponent implements OnInit {
  @ViewChild('ngForm')
  formDirective!: FormGroupDirective;
  maxDate: string;
  cargando!:boolean;
  isShowPassword: boolean = false;
  submitted: boolean = false;
  guardando: boolean = false;
  nacionalidades: Array<PaisesInterface> = [];
  analistas: Array<IEmpleadosVentas> = [];
  comoNosContacto: Array<ParametrosInterface> = [];
  private subscriptions: Array<Subscription> = [];
  guardar$: Observable<any> | undefined;
  email?:string="";
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
    fechaNacimiento: new FormControl(null, [
      Validators.required,
    ]),
    telefono: new FormControl(null, [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10)
    ]),
    nacionalidad: new FormControl(null, [
      Validators.required
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
    analista: new FormControl('', [
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

  get db() { return this.registrationForm.controls }
  get contrasenia() { return this.registrationForm.get('contrasenia'); }
  get confirmarContrasenia() { return this.registrationForm.get('confirmarContrasenia'); }
  get cedula() { return this.registrationForm.get('cedula'); }
  get comoContactaste() { return this.registrationForm.get('comoContactaste'); }
  get isAnalista() { return this.registrationForm.get('comoContactaste')?.value === 'ANALISTAOP' }
  get analista() { return this.registrationForm.get('analista')?.value }

  constructor(private dataApiClient: DataApiClientService,
    private router: Router,
    private datePipe: DatePipe
    ) {
      const dateFormat = 'yyyy-MM-dd';
      const limitDate = new Date();
      limitDate.setFullYear( limitDate.getFullYear() - 18 );
      this.maxDate = this.datePipe.transform( limitDate, dateFormat ) as string;
      this.email=this.get_data('url');
      console.log(this.email);
      this.cargando=false;
    }
    

  ngOnInit(): void {
    this.getCatalogos();
    this.registrationForm.get('comoContactaste')?.valueChanges.subscribe(value=>{
      if (value && this.isAnalista) {
        this.registrationForm.get('analista')?.setValidators(Validators.required);
      } else {
        this.registrationForm.get('analista')?.setValue(null);
        this.registrationForm.get('analista')?.clearValidators(); 
      }
    })
  }
  
  set_data(key:string,data:string){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  get_data(key:string){
    return localStorage.getItem(key)?.toString();
  }

  isgreatherthan18():boolean{
    if(this.registrationForm.get('fechaNacimiento')?.value>this.maxDate){
      return true;
    }
    return false;
  }

  clear_data(){
    try{
      localStorage.clear();
    } catch(e){
      console.log(e);
    }
  }

  onSubmit() {
    this.cargando=false;
    if(this.guardando) {
      return;
    }
    console.warn(this.registrationForm);
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
    return controlForm != null ? 
    ((controlForm.invalid && (controlForm.dirty || controlForm.touched)) || controlForm.invalid && this.submitted)
      : false;
    }
    return false;
  }

  isEmpty(control: string): boolean {
   
    if (this.registrationForm.get(control)?.value != null) {
      return false;
    }else{
    return true;
   }
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


  obtenerCuenta(): PersonaNaturalInterface{
    let cuenta: PersonaNaturalInterface;
    cuenta = {
      tipoCliente: 'REPRESENTANTE',
      tipoPersona: 'NAT',
      tipoIdentificacion: 'CED',
      identificacion: this.db['cedula'].value as string,
      nacionalidad: this.db['nacionalidad'].value,
      nombres: (this.db['nombres'].value as string).toUpperCase(),
      apellidos: (this.db['apellidos'].value as string).toUpperCase(),
      fechaNacimiento: this.db['fechaNacimiento'].value,
      email:  this.email,
      numeroCelular: this.db['telefono'].value as string,
      clave: this.db['contrasenia'].value,
      tipoContacto: this.db['comoContactaste'].value,
      aceptaPoliticaPrivacidad: 'S',
      aceptaRecibirInformacion: 'S',
      aceptaTerminoUso: 'S',
      usuarioCreacion:this.db['cedula'].value,
      usuarioContacto: ''
    };
    if (this.isAnalista) {
      cuenta = {
        ...cuenta,
        usuarioContacto: this.db['analista'].value
      }
    }
    return cuenta;
  }

  private _crearCuenta(cuenta: PersonaNaturalInterface){    
    this.guardar$ = this.dataApiClient.RegistrarPersonaNatural(cuenta).pipe(
      finalize(() => { } )
    );
    this.cargando=true;
    this.guardando = true;
    this.subscriptions.push(this.guardar$.subscribe(
      (result: string) => {
        console.warn(result);
        this.guardando = false;
        Swal.fire({
          title: 'Éxito!',
          // text: 'Gracias por crear su cuenta, Nos pondremos en contacto con usted.',
          text: 'Se le ha enviado un correo, para activar su cuenta.',
          //text: 'Su cuenta está activada. Puede iniciar sesión con el correo y clave registrada.',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(()=>{
          //this.resetForm();
          this.router.navigateByUrl('/')
        });
      },
      (error: Error) => {
        this.guardando = false;
        this.cargando=false;
        if(error.message=="ok"){
          Swal.fire({
            title: 'Éxito!',
            // text: 'Gracias por crear su cuenta, Nos pondremos en contacto con usted.',
            text: 'Se le ha enviado un correo, para activar su cuenta.',
            //text: 'Su cuenta está activada. Puede iniciar sesión con el correo y clave registrada.',
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


  toogleValue(event:any){
    console.log(event.target.value);
    if (event.target.value !='2: ANALISTAOP') {
      this.registrationForm.get('analista')?.setValidators(Validators.required);
    } else {
      this.registrationForm.get('analista')?.setValue('');
      this.registrationForm.get('analista')?.clearValidators(); 
    }
    
  }

  // Only Integer Numbers
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
  

}