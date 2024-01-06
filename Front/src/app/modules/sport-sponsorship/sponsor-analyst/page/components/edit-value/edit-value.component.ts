import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder,Validators } from '@angular/forms';
import { MatStepper } from '@angular/material/stepper';
import { DomSanitizer } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { SponsorAnalystService } from 'src/app/modules/sport-sponsorship/service/sponsor-analyst.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-value',
  templateUrl: './edit-value.component.html',
  styleUrls: ['./edit-value.component.css']
})
export class EditValueComponent implements OnInit {
  @ViewChild('stepper') stepper: MatStepper | undefined;
  filefrontal3?: File;
  datoscompletos:boolean=false;
  cargando:any;
  user:User;
  urlphoto3:any;
  checked:any=false;
  nombredocfrontal3:string="Carga tu documento"
  anio:any=2022;
  id:any;
  submitted:any = false;
  constructor(private sanitizer: DomSanitizer,
    private _formBuilder: FormBuilder,
    private beneficiario: BeneficiaryService,
    private sponsorAnalystService : SponsorAnalystService,
    private storage:StorageService,
    private activeModal:NgbActiveModal
    ) {
      this.user=this.storage.getCurrentSession();
      if(this.get_data('ced')){
        this.consultarValoracion();
      }
      this.cargando=false;
     }
  ngOnDestroy(): void {
    localStorage.removeItem('ced');
  }

  ngOnInit(): void {

  }
  FormGroup = this._formBuilder.group({
    selectyear: ['2022', [Validators.required]],
    cod: ['', [Validators.required]],
    preAprobado1: [0, [Validators.required]],
    valRecibido: [0, [Validators.required]],
    fechaVigencia: ['', [Validators.required]],
    fechaCalificacion: ['', [Validators.required]],
  });

  get anioreg() { return this.FormGroup.get('selectyear')?.value as string; }
  get db() { return this.FormGroup.controls }
  actualizarAnio(){
    this.anio=this.anioreg;
  }

  IsInvalid(control: string): boolean {
    if (this.FormGroup.get(control) != null) {
      let controlForm = this.FormGroup.get(control);
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
  onFilechange(event: any,tipe:number) {
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
    if (tipe==1){
      this.filefrontal3 = event.target.files[0];
      this.nombredocfrontal3 = this.filefrontal3!.name;
      console.log(this.nombredocfrontal3);
      let blob = new Blob([this.filefrontal3!], { type: this.filefrontal3!.type });
      let url = window.URL.createObjectURL(blob);
      this.urlphoto3 = this.sanitizer.bypassSecurityTrustUrl(url);
      console.log(this.urlphoto3);
    }
  }

 onSubmit() {
    this.submitted = true;
    this.cargando=true;
    if (!this.FormGroup.valid) {
      this.cargando=false;
      console.log(this.FormGroup.errors)
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
    this.sponsorAnalystService.editaValoracion(JSON.stringify(this.obtenerDatos()),this.filefrontal3!,this.user).subscribe( 
      (data:any)=>{
        console.log(data)
        this.message=data.mensaje
      },(error:Error)=>{
        console.log(error)
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
            this.activeModal.dismiss();
          }
        )
      }
    )
  }

  guardaValoracionAdd(){
    if(!this.existeDoc()){
      return;
    }
    this.cargando=true;
    console.table(this.obtenerDatos());
    this.beneficiario.guardaValoracion(JSON.stringify(this.obtenerDatos()) ,this.filefrontal3!,this.user).subscribe( 
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
            this.stepper?.next();
            this.activeModal.dismiss();
          }
        )
      }
    )
  }
  regresar(){
    this.activeModal.dismiss();
  }
  consultarValoracion(){
    console.table(this.obtenerDatos());
    this.beneficiario.consultarValoracion(this.get_data('ced')as string,this.user).subscribe( 
      (data:any)=>{
        console.table(data);
        this.db['cod'].setValue(data.calificacion);
        this.db['fechaCalificacion'].setValue(data.fechaCalificacion);
        this.db['fechaVigencia'].setValue(data.fechaCaducidad);
        this.db['preAprobado1'].setValue(data.presupuestoAprobado);
        this.db['valRecibido'].setValue(data.presupuestoRecaudado);
        this.checked=data.bianual;
        this.urlphoto3=data.ruta;
        this.id=data.id;
      },(error:Error)=>{
        console.log(error);
      }
    )
  }
  

  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }
  obtenerDatos(){
    let valoracion:any = {
      "anio":this.anio as string,
      "calificacion":this.db['cod'].value as string,
      "fechaCalificacion":this.db['fechaCalificacion'].value as Date,
      "fechaCaducidad":this.db['fechaVigencia'].value as string,
      "presupuestoAprobado":this.db['preAprobado1'].value as number,
      "presupuestoRecaudado":this.db['valRecibido'].value as number,
      "bianual":this.checked as boolean,
      "id":this.id
    }
    if(this.get_data('ced')){
      valoracion = {
        "idBene":this.get_data('ced')as string,
        ...valoracion,
      }
    }
    return valoracion;
  }

}
