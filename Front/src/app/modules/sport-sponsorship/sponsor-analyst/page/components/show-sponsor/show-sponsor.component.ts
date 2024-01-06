import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { RewardModalComponent } from 'src/app/modules/sport-sponsorship/beneficiary/pages/components/reward-modal/reward-modal.component';
import { TorneosModalComponent } from 'src/app/modules/sport-sponsorship/beneficiary/pages/components/torneos-modal/torneos-modal.component';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { SponsorAnalystService } from 'src/app/modules/sport-sponsorship/service/sponsor-analyst.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';
import { EditValueComponent } from '../edit-value/edit-value.component';


@Component({
  selector: 'app-show-sponsor',
  templateUrl: './show-sponsor.component.html',
  styleUrls: ['./show-sponsor.component.css']
})
export class ShowSponsorComponent implements OnInit {
  identificacion:any;
  user:User;
  nacionalidad:any;
  nacionalidades:any;
  Beneficiary:any
  auspicios:any[]=[];
  numeroAuspicio:any;
  urlphoto:any;
  urlphoto2:any;
  cargando: any =false;
  existeValoracion:any;
  addValoracion:any;
  constructor(private storage:StorageService,
    public modalService: NgbModal,
    private router: Router,
    private sponsorService: SponsorAnalystService,
    private dataApiClient : BeneficiaryService) {
    this.identificacion= this.get_data('auspCed');
    this.numeroAuspicio= this.get_data('auspNum');
    this.user= this.storage.getCurrentSession();
    this.consultarBeneficiario();
   }

  ngOnInit(): void {
    
  }
  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }
  // consultaAuspiciosPorConfirmar

  getConsulta(){
    return {
        "id": '',
        "nomApe":'',
        "identificacion":this.identificacion,
        "disciplina":'',
        "estado":"PC"
      }
  }
  consultarBeneficiario(){
    this.cargando=true;
    console.log(this.getConsulta());
    this.sponsorService.consultaAuspiciosPorConfirmar(this.getConsulta()).subscribe(
      (data:any)=>{
        console.log(data);  
        this.urlphoto=data[0].beneficiario.ruta1;
        this.urlphoto2= data[0].beneficiario.ruta2;
        this.Beneficiary=data[0];
      },(error:Error)=>{
        this.cargando=false;
        console.log(error.message)
      },()=>{
        this.consultarAuspicio();
      }
    )
  }
  //Arreglo de Auspicios
  consultarAuspicio(){
    this.cargando=true;
    this.dataApiClient.consultaBeneficiarioAuspicio(this.identificacion,'PC',this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        this.Beneficiary.auspicios=data;
      },(error:Error)=>{
        this.cargando=false;
        console.log(error.message)
      },()=>{
        this.consultarValoracion();
        this.consultarTitulos();
        for(let auspicio of  this.Beneficiary.auspicios){
          this.consultarRecompensas(auspicio);
          this.consultarTorneos(auspicio);
        }
        this.existeData();
        
      });

  }
  imprimirnumero(auspicio:any){
    console.log(auspicio)
  }
  irEditarBeneficiario(cedula:any){
    this.set_data('cedula',cedula);
    console.log(cedula);
    this.router.navigate(["sponsor-analyst/edit-beneficiary"]);
  }

  
  editarAupisicios(auspicio:any,cedula:any){
    this.set_data('nAuspicio',auspicio);
    this.set_data('cedula',cedula);
    this.router.navigate(["sponsor-analyst/edit-sponsor"]);
  }

  editarValoracion(cedula:any){
    this.set_data('ced',cedula);
    const modal = this.modalService.open(EditValueComponent, { 
      windowClass: 'my-class',
  });
  }
  AddValoracion(){
    var date1 = new Date();
    var date2 = new Date(this.Beneficiary.valoracion.fechaCaducidad);
    this.Beneficiary.valoracion.fechaCaducidad
    if(date1>date2){
      console.log("Se puede añadir valoracion");
      this.addValoracion=true;
    }else{
      console.log("No se puede añadir valoracion");
      this.addValoracion=false;
    }
  }
  existeData(){
    this.dataApiClient.validaValoracion(this.Beneficiary.persona.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.existeValoracion=data.mensaje;
      },(error :Error)=>{
      },()=>{
      }
    )
  }
  regresar(){
    this.router.navigate(["/sponsor-analyst/home"]);
  }
  verTorneos(auspicio:any){
    this.set_data('auspicio',auspicio);
    console.log(auspicio);
    const modal = this.modalService.open(TorneosModalComponent, { 
      windowClass: 'my-class',
  });
  }
  verRecompensas(auspicio:any){
    this.set_data('auspicio',auspicio);
    console.log(auspicio);
    const modal = this.modalService.open(RewardModalComponent, { 
      windowClass: 'my-class',
  });
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }
  consultarTitulos(){
    this.cargando=true;
    this.dataApiClient.consultaBeneficiarioTitulos(this.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        this.Beneficiary.titulos=data;
      },(error:Error)=>{
        this.cargando=false;
        console.log(error.message)
      },()=>{
        this.cargando=false;
      });
  }
  consultarRecompensas(auspicio:any,){
    this.cargando=true;
    this.dataApiClient.consultaRecompensas(auspicio.numeroAuspicio,this.user).subscribe(
      (data:any)=>{
        this.cargando=false;
        console.log(data); 
        auspicio.recompensas=data;
      },(error:Error)=>{
        this.cargando=false;
        console.log(error.message)
      },()=>{
      });
  }

  consultarTorneos(auspicio:any){
    this.dataApiClient.consultaTorneos(auspicio.numeroAuspicio,this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        auspicio.torneos=data;
      },(error:Error)=>{
        console.log(error.message)
      },()=>{
        console.log(this.Beneficiary);
      });
  }

  consultarValoracion(){
    this.cargando=true;
    this.dataApiClient.consultarValoracion(this.identificacion,this.user).subscribe(
      (data:any)=>{
        this.Beneficiary.valoracion=data;
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        console.log(error.message)
      },()=>{
        this.cargando=false;
        this.AddValoracion();
      }
    )
  }

  getAuspicios(){
    return {
      numAus:this.Beneficiary.auspicios[0].numeroAuspicio,
      comentario:this.observacion
    }
  }
  aprobarAuspicio(){
    this.cargando=true;
    console.log(this.getAuspicios());
    this.sponsorService.confirmarAuspicios(this.getAuspicios(),this.user).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Exito!',
          text: "Su asupicio ha cambiado a estado Vigente",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.consultarAuspicio();
          }
        )
      }
    )
  }
  observacion:any;
  cancelarAuspicio(){
    Swal.fire({
      title: 'Se anulará el auspicio',
      text: "Ingrese el motivo de la anulación",
      icon: 'warning',
      input: 'text',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Si',
      cancelButtonText: 'No',
      preConfirm: (data) => {
        if(data==""){Swal.showValidationMessage(
          `Motivo requerido:`
        )
        }else{
        this.cargando=true;
        this.observacion=data;
        console.log(this.getAuspicios());
        this.sponsorService.anularAuspicios(this.getAuspicios(),this.user).subscribe(
          (data:any)=>{
            console.log(data);
          },(error:Error)=>{
            this.cargando=false;
            console.log(error);
          },()=>{
            this.cargando=false;
            Swal.fire({
              title: 'Exito!',
              text: "Su asupicio ha cambiado a estado Anulado",
              icon: 'success',
              confirmButtonText: 'Aceptar'
            }).then(
              ()=>{
                this.consultarAuspicio();
              }
            )
          }
        )
      }
      }
    })
    
  }

  pendienteAuspicio(){
    this.cargando=true;
    this.observacion='Aprobado por '+this.user.user.identificacion;
    console.log(this.getAuspicios());
    this.sponsorService.pendienteAuspicios(this.getAuspicios(),this.user).subscribe(
      (data:any)=>{
        console.log(data);
      },(error:Error)=>{
        this.cargando=false;
        console.log(error);
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Exito!',
          text: "Su asupicio ha cambiado a estado Proximamente",
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(
          ()=>{
            this.consultarAuspicio();
          }
        )
      }
    )
  }

}
