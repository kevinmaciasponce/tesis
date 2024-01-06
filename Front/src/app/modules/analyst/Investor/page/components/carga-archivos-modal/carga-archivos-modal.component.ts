import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-carga-archivos-modal',
  templateUrl: './carga-archivos-modal.component.html',
  styleUrls: ['./carga-archivos-modal.component.css']
})
export class CargaArchivosModalComponent implements OnInit {
  user: User | undefined;
  fileInversionsita?: File;
  fileContrato?: File;
  fileModelo?: File;
  cargando:any;
  filePagare?: File;
  fileTabla?: File;
  fileAcuerdo?: File;
  file?: File;
  submitted: boolean = false;
  observacion!:string;
  rm!: any;
  nombreEmpresa: String | undefined;
  nombre:string ="Archivo Excel";
  nombredoc:string[]=["Carga tu documento","Carga tu documento","Carga tu documento",
  "Carga tu documento","Carga tu documento","Carga tu documento"];
  
  registrationForm: FormGroup = new FormGroup({
    observacion: new FormControl(null, [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(100),
    ]),
    fileAnexo: new FormControl(null, [
      Validators.required,
    ]),
    fileContrato: new FormControl(null, [
      Validators.required,
    ]),
    fileModelo: new FormControl(null, [
      Validators.required,
    ]),
    filePagare: new FormControl(null, [
      Validators.required,
    ]),
    fileTabla: new FormControl(null, [
      Validators.required,
    ]),
    fileAcuerdo: new FormControl(null, [
      Validators.required,
    ])
  })
  
 


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


  constructor(private analystService: AnalystService, 
    private localstorage: StorageService,
    private activeModal:NgbActiveModal) {
    this.user = this.localstorage.getCurrentSession();
    this.cargando=false;
   }
  
  ngOnInit(): void {
    this.rm = JSON.parse(this.get_data('cargaDatos'));
    this.nombreEmpresa = this.rm.nomEmpresa;
    console.log(this.rm);
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }

  onFilechange(event: any,doc:number) {
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "application/pdf"){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos pdf",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    if(event.target.files[0].size > 2900000){
      Swal.fire({
        title: 'Error!',
        text: "Error El archivo no debe superar los 3MB",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombredoc[doc] = "Cargar documento";
      return;
    }
    switch(doc){
      case 0:
        this.fileInversionsita = event.target.files[0];
        this.nombredoc[doc] = this.fileInversionsita!.name;
        break;
      case 1:
        this.fileContrato = event.target.files[0];
        this.nombredoc[doc] = this.fileContrato!.name;
        break;
      case 2:
        this.fileModelo = event.target.files[0];
        this.nombredoc[doc] = this.fileModelo!.name;
        break;
      case 3:
        this.filePagare = event.target.files[0];
        this.nombredoc[doc] = this.filePagare!.name;
        break;
      case 4:
        this.fileTabla = event.target.files[0];
        this.nombredoc[doc] = this.fileTabla!.name;
        break;
      case 5:
        this.fileAcuerdo = event.target.files[0];
        this.nombredoc[doc] = this.fileAcuerdo!.name;
        break;
    }
  }

  cleanFiles(){
    this.fileInversionsita=undefined;
    this.fileContrato=undefined;
    this.fileModelo=undefined;
    this.filePagare=undefined;
    this.fileTabla=undefined;
    this.fileAcuerdo=undefined;
  }

  existeDocumento(file:File):boolean{
    if(!file){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe subir un documento",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return false;
    }
    return true;
  }

   cargarArchivos(){
    this.cargando=true;
    this.submitted = true;
    let continuar=true;
    continuar = this.existeDocumento(this.fileInversionsita!)&&this.existeDocumento(this.fileContrato!);
    console.log(continuar);
    continuar = continuar&&this.existeDocumento(this.fileAcuerdo!)&&this.existeDocumento(this.fileModelo!);
    console.log(continuar);
    continuar = continuar&&this.existeDocumento(this.filePagare!)&&this.existeDocumento(this.fileTabla!);
    console.log(continuar);
    console.log(this.rm);
    this.analystService.cargarDatosxSolicitud(this.observacion,this.rm.NumSolicitud,this.fileInversionsita!,
      this.fileContrato!,this.fileModelo!,this.filePagare!,this.fileTabla!,this.fileAcuerdo!,this.user!).subscribe(
      (data:any)=>{
        
      },(error: Error)=>{
        this.cargando=false;
        Swal.fire({
          title: 'Error',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Exitoso!',
          text: "Su documento ha sido cargado",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.activeModal.dismiss();
          }
        )
      }
    )
    this.submitted = false;


   }

}
interface rikuest {
  codProyecto: string;
  nomProyecto: String;
  NumSolicitud: String;
}