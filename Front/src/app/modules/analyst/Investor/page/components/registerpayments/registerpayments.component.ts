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
  selector: 'app-registerpayments',
  templateUrl: './registerpayments.component.html',
  styleUrls: ['./registerpayments.component.css']
})
export class RegisterpaymentsComponent implements OnInit {
  cargando:any;
  submitted:boolean = false;
  monto:any;
  observacion:any;
  solicitudes: any;
  nombreEmpresa: String | undefined;
  cuenta!: any;
  file!:File;
  nombredoc!:string;
  details!: detailsInterface ;
  rm!: any;
  fechaReg!: any;
  numsol: Array<any> = [];
  mensaje:any;
  user: User | undefined;
  constructor(private dataApiRequests: ConsultaSolicitudesManagerService,
    private localstorage: StorageService,
    private analystService:AnalystService,
    public activeModal: NgbActiveModal,
    private router :Router
    ) { 
    this.cargando=false;
    this.user = this.localstorage.getCurrentSession();
  }
  ngOnDestroy(): void {
    
  }

  get db() { return this.registrationForm.controls }

  registrationForm: FormGroup = new FormGroup({
    observacion: new FormControl(null, [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(100),
    ]),
    file: new FormControl(null, [
      Validators.required,
    ]),
    fecharegistro: new FormControl(null, [
      Validators.required,
    ])
  })
  ngOnInit(): void {
    this.rm = JSON.parse(this.get_data('registerPayments'));
    this.nombreEmpresa = this.rm.nomProyecto;
    console.log(this.rm);
    // this.consultar();
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

  onFilechange(event: any,doc:number) {
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "image/jpeg"&&event.target.files[0].type != "image/png"){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos jpg",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
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
    if(!this.fechaReg){
      Swal.fire({
        title: 'Error!',
        text: "Debe ingresar una fecha",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    let cuenta={
      numSolicitud: this.rm.numSolicitud,
      cuota: this.rm.cuota,
      fechaRealizada:this.fechaReg,
      usuarioModificacion:this.user?.user.identificacion
    }
    this.cargando=true;
    console.log(cuenta);
    this.analystService.registrarPagoInversionista(cuenta,this.file,this.user!).subscribe(
      (data:any)=>{ 
        this.mensaje=data
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
          title: 'Exito',
          text: "Se ha registrado el depósito de la cuota al Inversionista",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then( ()=>{
          this.activeModal.dismiss();
        }
        );
      }
    )
  }
}
