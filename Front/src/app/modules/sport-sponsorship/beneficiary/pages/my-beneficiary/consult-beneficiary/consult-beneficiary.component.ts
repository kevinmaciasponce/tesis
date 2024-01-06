import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { RewardModalComponent } from '../../components/reward-modal/reward-modal.component';
import { TorneosModalComponent } from '../../components/torneos-modal/torneos-modal.component';
import { ValueModalComponent } from '../../components/value-modal/value-modal.component';

@Component({
  selector: 'app-consult-beneficiary',
  templateUrl: './consult-beneficiary.component.html',
  styleUrls: ['./consult-beneficiary.component.css']
})
export class ConsultBeneficiaryComponent implements OnInit {
  identificacion:any;
  user:User;
  nacionalidad:any;
  nacionalidades:any;
  Beneficiary:any
  auspicios:any[]=[];
  numeroAuspicio:any;
  urlphoto:any;
  urlphoto2:any;
  existeValoracion:any;
  addValoracion:any;
  constructor(private storage:StorageService,
    public modalService: NgbModal,
    private router: Router,
    private dataApiClient : BeneficiaryService) {
    this.identificacion= this.get_data('ced');
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
  

  consultarBeneficiario(){
    this.dataApiClient.consultaBeneficiariosxRepresentante(this.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data);  
        this.urlphoto=data[0].beneficiario.ruta1;
        this.urlphoto2= data[0].beneficiario.ruta2;
        this.Beneficiary=data[0];
      },(error:Error)=>{
        console.log(error.message)
      },()=>{
        this.consultarAuspicio();
      }
    )
  }
  //Arreglo de Auspicios
  consultarAuspicio(){
    this.dataApiClient.consultaBeneficiarioAuspicio(this.identificacion,'',this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        this.Beneficiary.auspicios=data;
       
      },(error:Error)=>{
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
    this.router.navigate(["agent/editBeneficiary"]);
  }

  editarAupisicios(cedula:any,nauspicio:any){
    this.set_data('cedula',cedula);
    this.set_data('nAuspicio',nauspicio);
    console.log(cedula);
    this.router.navigate(["agent/editSponsor"]);
  }
  agregarAupisicios(cedula:any){
    this.set_data('cedula',cedula);
    this.set_data('nAuspicio','');
    console.log(cedula);
    this.router.navigate(["agent/editSponsor"]);
  }

  agregarValoracion(cedula:any){
    this.set_data('cedula',cedula);
    const modal = this.modalService.open(ValueModalComponent, { 
      windowClass: 'my-class',
  });
  }
  AddValoracion(){
    var date1 = new Date();
    var date2 = new Date(this.Beneficiary.valoracion.fechaCaducidad);
    this.Beneficiary.valoracion.fechaCaducidad
    console.log(date1);
    console.log(date2);
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
    this.router.navigate(["/agent/home"]);
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
    this.dataApiClient.consultaBeneficiarioTitulos(this.identificacion,this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        this.Beneficiary.titulos=data;
      },(error:Error)=>{
        console.log(error.message)
      },()=>{
        
      });
  }
  consultarRecompensas(auspicio:any,){
    this.dataApiClient.consultaRecompensas(auspicio.numeroAuspicio,this.user).subscribe(
      (data:any)=>{
        console.log(data); 
        auspicio.recompensas=data;
      },(error:Error)=>{
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
    this.dataApiClient.consultarValoracion(this.identificacion,this.user).subscribe(
      (data:any)=>{
        this.Beneficiary.valoracion=data;
        console.log(data);
      },(error:Error)=>{
        console.log(error.message)
      },()=>{
        this.AddValoracion();
      }
    )
  }
}
