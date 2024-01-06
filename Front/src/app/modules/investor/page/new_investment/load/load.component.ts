import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { SolicitudInterface } from 'src/app/shared/models/solicitud.interface';
import { datos_ProcesoInterface } from 'src/app/shared/models/tabla_amortizacion/datos_proceso.interaface';
import { AmortizacionService } from 'src/app/shared/service/amortizacion.service';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-load',
  templateUrl: './load.component.html',
  styleUrls: ['./load.component.css']
})
export class LoadComponent implements OnInit {
  users!: User;
  filefrontal?: File;
  fileposterior?: File;
  solicitudNueva!: boolean;
  datos!:any;
  cargando!:boolean;
  tabla_amortizacion!:datos_ProcesoInterface;
  rutaposterior!:any;
  rutafrontal!:any;
  numeroSolicitud!: any;
  nombrefrontal:string ="Ver Cédula";
  nombreposterior:string ="Ver Cédula";
  nombredocfrontal:string="Carga tu documento"
  nombredocposterior:string="Carga tu documento"
  constructor(
    private investorservice : InvestorService, 
    private localstorage: StorageService,
    private storage:StorageService,
    private router: Router,
    ) { 
      this.users=this.storage.getCurrentSession();
      // this.set_data('nombredoc',this.nombredoc);
     this.existeRuta();
     this.cargando=false;
    }

 
  ngOnInit(): void {
    
  }

  existeRuta(){ 
    this.investorservice.consultaCedulaInversionista(this.users!).subscribe(
      (data:any)=>{
        console.log(this.users!);
        console.log(data);
        this.rutafrontal=data.ruta;
        this.rutaposterior=data.ruta_post;
        this.nombrefrontal="cédula";
        this.solicitudNueva=true;
        console.log(this.rutafrontal);
        
    },(error: Error)=>{
      this.solicitudNueva = false;
      console.log(error);
      return ;
    },()=>{
      console.log(this.solicitudNueva);
    })
  }

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
    }else if(tipe==2){
      this.fileposterior = event.target.files[0];
      this.nombredocposterior = this.fileposterior!.name;
      console.log(this.nombredocposterior);
    }
    
  }

  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }
  upload(){
    const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger' 
      },
      buttonsStyling: false
    })
    swalWithBootstrapButtons.fire({
      title: '¿Está seguro que desea subir el documento?: '+this.nombredocfrontal+" - "+ this.nombredocposterior,
      text: "La opcion no se puede desahacer",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Confirmar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        if(this.updatedata()){
          swalWithBootstrapButtons.fire(
            'Enviado',
            'Su documento ha sido enviado',
            'success'
          )
        }
      } else if (
        result.dismiss === Swal.DismissReason.cancel
      ) {
        
        this.cargando=false;
        swalWithBootstrapButtons.fire(
          'Cancelado',
          'Puede cambiar de documento',
          'error'
        )
      }
    })
  }
  
  create() {
    const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
      },
      buttonsStyling: false
    })
    swalWithBootstrapButtons.fire({

      title: '¿Está seguro que desea subir el documento?: '+this.nombredocfrontal+" - "+ this.nombredocposterior,
      text: "La opción no se puede desahacer",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Confirmar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        if(this.senddata()){
          swalWithBootstrapButtons.fire(
            'Enviado',
            'Su documento ha sido enviado',
            'success'
          )
        }
      } else if (
        result.dismiss === Swal.DismissReason.cancel
      ) {
        this.cargando=false;
        swalWithBootstrapButtons.fire(
          'Cancelado',
          'Puede cambiar de documento',
          'error'
        )
      }
    })
  }
 senddata(){
    let estado:boolean=false;
    this.cargando=true;
    this.users = this.localstorage.getCurrentSession();
      let datos:any;
      let respuesta={
        nombre:"",
        ruta:""
      }
      datos={
        identificacion:this.users.user.identificacion,
       numeroSolicitud:null,
        usuario: this.users.user.usuarioInterno
      }
      console.log(datos);
      if (this.filefrontal&&this.fileposterior) {
        this.investorservice.GuardarIdentificaion(datos,this.filefrontal,this.fileposterior,this.users).subscribe(  
          (data: any) => {
            console.log(data);
            respuesta = data;
            this.rutafrontal=respuesta.ruta;
        },(error:Error)=>{
          this.cargando=false;
          console.log(error);
          if(error.message=="Forbidden"){
            this.router.navigate(["register/iniciar_sesion"]);
          }
        },()=>{
          this.cargando=false;
          Swal.fire({
            title: 'Exitoso!',
            text: "Su documento ha sido enviado",
            icon: 'success',
            confirmButtonText: 'Aceptar'
          });
          this.cargando=false;
          estado=true;
          this.router.navigate(["investor/investement"]);
        })
      }
    
    return estado;
  }
  updatedata(){
    let estado:boolean=false;
    this.cargando=true;
    this.users = this.localstorage.getCurrentSession();
      let datos:any;
      let respuesta={
        nombre:"",
        ruta:""
      }
      datos={
        identificacion:this.users.user.identificacion,
        numeroSolicitud: null,
        usuario: this.users.user.usuarioInterno
      }
      console.log(datos);
      if (this.filefrontal&&this.fileposterior) {
        this.investorservice.ActualizarIdentificaion(datos,this.filefrontal,this.fileposterior,this.users).
        subscribe(  
          (data: any) => { 
            this.cargando=false;
            console.log(data);
            respuesta = data;
            this.rutafrontal=respuesta.ruta;
            Swal.fire({
              title: 'Exitoso!',
              text: "Su documento ha sido actualizado",
              icon: 'success',
              confirmButtonText: 'Aceptar'
            });
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
            this.router.navigate(["investor/investement"]);
          })
      }else{
        this.cargando=false;
        Swal.fire({
          title: 'Error!',
          text: "Error Debe subir un documento",
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }
      this.cargando=false;
      estado=true;
   
    return estado;
  }

}
