import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-concilation-modal',
  templateUrl: './concilation-modal.component.html',
  styleUrls: ['./concilation-modal.component.css']
})
export class ConcilationModalComponent implements OnInit {
  users!: User;
  file?: File;
  datos!:any;
  observacion!:string;
  conciliacion:conciliacionrecepterInterface;
  nombre:string ="Archivo Excel";
  nombredoc:string="Carga tu documento"
  constructor( private localstorage: StorageService,
    private analystService: AnalystService,
    public activeModal: NgbActiveModal
    ) {
    this.conciliacion = JSON.parse(this.get_data('conciliacion'));
    this.observacion="";
   }
  ngOnInit(): void {
  }

  get_data(key:string):any{
    return localStorage.getItem(key)?.toString();
  }
  registrationForm: FormGroup = new FormGroup({
    observacion: new FormControl(null, [
      Validators.required,
    ])
  }
  );
  
  get db() { return this.registrationForm.controls }

 /* enviarconciliacion(){
    this.users = this.localstorage.getCurrentSession();
    let conciliaciones:any 
    conciliaciones = {
      usuario : this.users.user.usuarioInterno,
      numeroConciliacion : this.conciliacion.numeroConciliacion ,
      observacion : this.db['observacion'].value as string,
    }
    if (this.file) {
    this.analystService.GuardarConciliacion(conciliaciones,this.file,this.users).subscribe(
      (data: any) => {
        Swal.fire({
          title: 'Perfecto',
          text: data.mensaje,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(()=>{
          window.location.reload();
          this.activeModal.dismiss();
        });
      })
    }
  }*/

  onFilechange(event: any) {
    console.log(event.target.files[0]);
    if(event.target.files[0].type != "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" &&  event.target.files[0].type != "application/vnd.ms-excel"){
      Swal.fire({
        title: 'Error!',
        text: "Error Debe ingresar solo archivos excel",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombredoc = "Cargar documento";
      return;
    }
    if(event.target.files[0].size > 2000000){
      Swal.fire({
        title: 'Error!',
        text: "Error El archivo no debe superar los 2MB",
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      this.nombre = "Cargar documento";
      return;
    }
    this.file = event.target.files[0];
    this.nombredoc = this.file!.name;
    console.log(this.nombredoc);
  }


  
}

interface conciliacionrecepterInterface{
  numeroConciliacion: number,
  totalConciliado: string
}

