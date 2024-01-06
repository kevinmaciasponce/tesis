import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/models/user';
import { FormaPagoInterface } from 'src/app/shared/models/formaPago.interface';
import { datos_ProcesoInterface } from 'src/app/shared/models/tabla_amortizacion/datos_proceso.interaface';
import { AmortizacionService } from 'src/app/shared/service/amortizacion.service';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-investment',
  templateUrl: './investment.component.html',
  styleUrls: ['./investment.component.css']
})
export class InvestmentComponent implements OnInit {
  maxDate: string;
  nombrebanco!: string;
  cargando!:boolean;
  tipocuenta!:string;
  ncuenta!:string;
  cedula!:string;
  nombre!:string;
  project:any;
  user:User | undefined;
  file?: File;
  correocontacto!:any;
  submitted: boolean = false;
  guardando: boolean = false;
  datos!:any;
  tabla_amortizacion!:datos_ProcesoInterface;
  nombredoc:string="Carga tu documento"
  cmbtransferencia:Array<FormaPagoInterface>=[];
  constructor(
    private investorservice : InvestorService,
    private storage: StorageService,
    private amortizacion:AmortizacionService,
    private router: Router,
    private datePipe: DatePipe
    ) {
    this.project=this.get_data('proyecto');
    this.user=this.storage.getCurrentSession();
    this.registrationForm.get('cliente')?.setValue(this.user?.user.nombres);
    this.obtenerdatoscuenta();
    const dateFormat = 'yyyy-MM-dd';
    const limitDate = new Date();
    limitDate.setFullYear( limitDate.getFullYear());
    this.maxDate = this.datePipe.transform( limitDate, dateFormat ) as string;
    this.cargando=false;
   }

  ngOnInit(): void {
  }

  registrationForm: FormGroup = new FormGroup({
    cliente: new FormControl(null, [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
    ]),
    fpago: new FormControl(null, [
      Validators.required,
    ]),   
    ncomprobante: new FormControl(null, [
      Validators.required,
    ]),
    ftransferencia: new FormControl(null, [
      Validators.required,
    ]),
    valordeposito: new FormControl(null, [
      Validators.required,
      Validators.min(500),
    ]),
    info: new FormControl(null, [
      Validators.required,
      Validators.pattern('true')
    ]),
    valid: new FormControl(null, [
      Validators.required,
      Validators.pattern('true')
    ])
  }
  );
  onSubmit() {
    if(this.guardando) {
      return;
    }
    console.warn(this.registrationForm.value);
    this.submitted = true;
    
    if (!this.registrationForm.valid
    ) {
      console.log(this.registrationForm.errors)
      return;
    }
    this.submitted = false;
    this.upload()
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
  onFilechange(event: any) {
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "image/jpeg" && event.target.files[0].type != "image/png"){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos pdf",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombredoc = "Cargar documento";
      return;
    }
    if(event.target.files[0].size > 3000000){
      Swal.fire({
        title: 'Error!',
        text: "Error El archivo no debe superar los 3MB",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombre = "Cargar documento";
      return;
    }
    this.file = event.target.files[0];
    this.nombredoc = this.file!.name;
  }

  obtenerdatoscuenta(){
    let datos={
      codigoProyecto:this.project,
    }
    this.investorservice.consultaDepositos(datos,this.user!).subscribe(
      (data: any) => {
        console.log(data);
        this.cedula= data.ruc;
        this.nombrebanco= data.banco;
        this.nombre= data.beneficiaria;
        this.ncuenta= data.numeroCuenta;
        this.tipocuenta= data.tipoCuenta;
        this.correocontacto= data.correocontacto;
      }, (error:Error)=>{
        if(error.message=="Forbidden"){
          this.router.navigate(["register/iniciar_sesion"]);
        }
      });

    this.investorservice.ConsultaFormaPago().subscribe(
      (data: Array<FormaPagoInterface>) => {
      this.cmbtransferencia = data;
    });
  }
  

  currentDate():boolean{
    if(this.registrationForm.get('ftransferencia')?.value>this.maxDate){
      return true;
    }
    return false;
  }

  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }
  get db() { return this.registrationForm.controls }

  upload() {
    this.cargando=true;
    if (!this.registrationForm.valid) {
        console.log(this.registrationForm.errors)
        return;
    }
    let user = this.storage.getCurrentSession();
    let datos:any;
    let project=this.get_data('proyecto');
    let respuesta={
      nombre:"",
      ruta:""
    }
    datos={
      numeroSolicitud: Number(this.get_data('numero_solicitud')),
      depositante: this.db['cliente'].value as string,
      formaPago: Number(this.db['fpago'].value),
      numeroComprobante: this.db['ncomprobante'].value as string,
      fechaTransaccion: this.db['ftransferencia'].value as string,
      monto:Number(this.db['valordeposito'].value),
      aceptaLicitudFondos: "S",
      aceptaInformacionCorrecta: "S",
      aceptaIngresarInfoVigente: "S"
    }
    console.log(datos);
    if (this.file) {
      this.investorservice.GuardarComprobante(datos,this.file,user).subscribe(  
        (data: any) => {
          console.log(data);
          respuesta = data;
      },
      (error: Error) => {
        this.cargando=false;
        Swal.fire('Error!',String(error.message) , 'error');
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Perfecto',
          text: 'Hemos recibido tu comprobante, en las próximas 24 horas confirmaremos el estado de tu inversión',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(()=>{
          this.cargando=false;
          this.router.navigateByUrl('investor/home');
        });
      }
      )
    } else {
      alert("Please select a file first");
    }
}
}
