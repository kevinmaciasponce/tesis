import { DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { PromoterService } from '../../service/promoter.service';

@Component({
  selector: 'app-register-paid-invoice',
  templateUrl: './register-paid-invoice.component.html',
  styleUrls: ['./register-paid-invoice.component.css']
})
export class RegisterPaidInvoiceComponent implements OnInit,OnDestroy {
  cargando:any=false;
  maxDate: string;
  nombre!:string;
  file?: File;
  nombredoc:string="Carga tu documento";
  submitted: boolean = false;
  codFactura:any;
  user:User;
  idEmpresa:number;
  idProyecto:string;
  constructor(private datePipe: DatePipe, private promotor:PromoterService,
    private storage:StorageService
    ) {
    const dateFormat = 'yyyy-MM-dd';
    const limitDate = new Date();
    limitDate.setFullYear( limitDate.getFullYear());
    this.maxDate = this.datePipe.transform( limitDate, dateFormat ) as string;
    this.codFactura=this.get_data('idFactura');
    this.user= this.storage.getCurrentSession();
    this.idEmpresa=this.get_data('idEmpresa');
    this.consultaProyectos();
    this.idProyecto='';
   }
  ngOnDestroy(): void {
    localStorage.removeItem('idFactura');
  }


  registrationForms: FormGroup = new FormGroup({
    cliente: new FormControl(null, [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
    ]),   
    ncomprobante: new FormControl(null, [
      Validators.required,
    ]),
    ftransferencia: new FormControl(null, [
      Validators.required,
    ]),
    valordeposito: new FormControl(null, [
      Validators.required,
      Validators.min(0),
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

  get dbreg() { return this.registrationForms.controls }

  
  ngOnInit(): void {
  }

  currentDate():boolean{
    if(this.registrationForms.get('ftransferencia')?.value>this.maxDate){
      return true;
    }
    return false;
  }

  IsInvalid(control: string): boolean {
    if (this.registrationForms.get(control) != null) {
      let controlForm = this.registrationForms.get(control);
      return controlForm != null
            ? ((controlForm.invalid && (controlForm.dirty || controlForm.touched))
              || controlForm.invalid  && this.submitted )
            : false;
    }
    return false;
 }

 get_data(key: any): any {
  return localStorage.getItem(key);
  }

 onFilechange(event: any) {
  console.log(event.target.files[0]);
  if(event.target.files[0].type != "application/pdf" && event.target.files[0].type != "application/pdf"){
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

registrarPago(){
  console.log(this.obtenerPago());
  console.log(this.file!);
  console.log(this.idProyecto);
  console.log(this.codFactura);
  this.promotor.registrarPagoPrimeraFact(JSON.stringify(this.obtenerPago()),this.file!,this.codFactura,'pago',this.user).subscribe(
    (data:any)=>{
      console.log(data);
    },(error:any)=>{
      Swal.fire({
        title: 'Error!',
        text: error.message,
        icon: 'error',
        confirmButtonText: 'Aceptar'
      })
    },()=>{

    }
  )
}
obtenerPago(){
  return {
    "codProyecto": this.idProyecto,
    "depositante":this.dbreg['cliente'].value,
    "formaPago":1,
    "numeroComprobante":this.dbreg['ncomprobante'].value,
    "fechaTransaccion":this.dbreg['ftransferencia'].value,
    "monto":this.dbreg['valordeposito'].value,
    "aceptaLicitudFondos":"s",
    "aceptaInformacionCorrecta":"s",
    "aceptaIngresarInfoVigente":"s"
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

  consultaProyectos(){
    this.promotor.consultarProyectosxPromotor(this.getDataPromotor(),this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.idProyecto=data[0].idProyecto;
      },null,()=>{
        console.log(this.idProyecto);
      }
    )
  }
  getDataPromotor(){
    return {
      "codigoProyecto":"",
      "idEmpresa":this.idEmpresa,
      "estadoActual":"BO"
  }
  }

}



