import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { detailsInterface } from 'src/app/shared/models/consulta_solicitudes/detailsIntransit.interface';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { ConsultaSolicitudesManagerService } from 'src/app/shared/service/consulta-solicitudes-manager.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-transfer-funds-modal',
  templateUrl: './transfer-funds-modal.component.html',
  styleUrls: ['./transfer-funds-modal.component.css']
})
export class TransferFundsModalComponent implements OnInit,OnDestroy {
  cargando:any;
  submitted:boolean = false;
  monto:any;
  montoRecaudado:any;
  observacion:any;
  solicitudes: any;
  nombreEmpresa: String | undefined;
  cuenta!: any;
  file!:File;
  nombredoc:string= "Cargar documento";
  details!: detailsInterface ;
  rm!: any;
  numsol: Array<any> = [];
  mensaje:any;
  user: User | undefined;
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService,
    private localstorage: StorageService,
    private router:Router,
    private analystService:AnalystService,
    public activeModal: NgbActiveModal,) { 
    this.cargando=false;
    this.user = this.localstorage.getCurrentSession();
  }
  ngOnDestroy(): void {
    
  }

  get db() { return this.registrationForm.controls }

  registrationForm: FormGroup = new FormGroup({
    observacion: new FormControl(null, [
      Validators.required,
      Validators.minLength(5),
    ])
  })
  ngOnInit(): void {
    this.rm = JSON.parse(this.get_data('transferfound'));
    this.nombreEmpresa = this.rm.nomProyecto;
    this.montoRecaudado= this.rm.montoRecaudado;
    this.consultar();
  }
  get_data(key: any): any {
    return localStorage.getItem(key);
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
        text: "Error Debe ingresar solo archivos jpg/png",
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
      this.nombredoc = "Cargar documento";
      return;
    }
    this.file = event.target.files[0];
    this.nombredoc = this.file!.name;
  }


  submit(){
    this.submitted = true;
    
    if (!this.registrationForm.valid) {
      console.log(this.registrationForm.errors)
      return;
    }

    if(!this.file){
      Swal.fire({
        title: 'Error!',
        text: "Debe ingresar un archivo",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }  

    if(!this.observacion){
      Swal.fire({
        title: 'Error!',
        text: "Debe ingresar una observacion",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }  
    let cuenta={
      codigoProyecto: this.rm.codProyecto,
      observacion: this.observacion,
    }
    console.log(cuenta);
    this.analystService.tranferirFondos(cuenta,this.file,this.user!).subscribe(
      (data:any)=>{ 
        this.mensaje=data
      },(error:Error)=>{
        Swal.fire({
          title: 'Error',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        })
      },()=>{
        Swal.fire({
          title: 'Exito',
          text: "Se ha registrado el depósito al Promotor",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then( ()=>{
          this.submitted = false;
          let currentUrl = this.router.url;
          this.router.routeReuseStrategy.shouldReuseRoute = () => false;
          this.router.onSameUrlNavigation = 'reload';
          this.router.navigate([currentUrl]);
          this.activeModal.dismiss();
        }
        );
      }
    )
  }

  

  consultar(): void {
    this.cuenta = {
      codigoProyecto: this.rm.codProyecto,
      estado: "SFC"
    }
    this.analystService.analystSignDetails(this.cuenta.codigoProyecto, this.user!).subscribe(
      (data: detailsInterface) => {
        this.details = data;
        this.solicitudes = Object.values(this.details.solicitudes);
        console.log(data);
        
      },(error:Error)=>{

      },()=>{
       
      });
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
}
