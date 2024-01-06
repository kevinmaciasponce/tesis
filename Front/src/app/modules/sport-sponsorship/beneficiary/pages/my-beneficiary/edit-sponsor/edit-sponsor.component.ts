import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatStepper, StepperOrientation } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-sponsor',
  templateUrl: './edit-sponsor.component.html',
  styleUrls: ['./edit-sponsor.component.css']
})
export class EditSponsorComponent implements OnInit {
  @ViewChild('stepper') stepper: MatStepper | undefined;
  nacionalidad:any;
  nacionalidades:PaisesInterface[] =[];
  cargando:any;
  user:User;
  cedula:any;
  nAuspicio:any;
  Beneficiary:any= {};
  porcenatjeprueba:any=0;
  urlphoto:any
  categorias:any[]=[];
  categoria:any;
  disciplinas:any[]=[];
  disciplina:any;
  stepperOrientation: Observable<StepperOrientation>;
  constructor(private _formBuilder: FormBuilder,
    private router: Router,
    breakpointObserver: BreakpointObserver,
    private dataApiClient: BeneficiaryService,
    private storage:StorageService,
    ){ 
      this.user=this.storage.getCurrentSession();
      this.inicializarTorneos();
      this.stepperOrientation = breakpointObserver
      .observe('(min-width: 800px)')
      .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
      this.getCatalogos();
      this.cedula=this.get_data('cedula');
      this.nAuspicio=this.get_data('nAuspicio');
      this.consultarBeneficiario();
      if(this.nAuspicio){
        this.dbauspicio['id'].setValue(this.nAuspicio); 
        this.consultaBeneficiarioAuspicio();
      }else{
        this.dbauspicio['id'].setValue(null);
      }
      this.ObtenerAuspicio();
    }

  ngOnInit(): void {
    
  }
  inicializarTorneos(){
    this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0},fecha:''});
    this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0},fecha:''});
    this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0 },fecha:''});
  }
  //Auspicios Deportivos
thirdFormGroup = this._formBuilder.group({
  presupuesto: [0, Validators.required],
  id1:['', Validators.required],
  categoria1:['ORO', Validators.required],
  porcentaje1:['> 35%', Validators.required],
  detalle1:['', Validators.required],
  id2:['', Validators.required],
  categoria2:['PLATA', Validators.required],
  porcentaje2:['Entre 16% y 34%', Validators.required],
  detalle2:['', Validators.required],
  id3:['', Validators.required],
  categoria3:['BRONCE', Validators.required],
  porcentaje3:['Menor 15%', Validators.required],
  detalle3:['', Validators.required],
  id:[null, Validators.required]
});
get dbauspicio() { return this.thirdFormGroup.controls }
get presupuesto() { 
  
  if(this.thirdFormGroup.get('presupuesto')?.value){
    return this.thirdFormGroup.get('presupuesto')?.value;
  }
  return 0;
}
torneos:torneos[] = [];
addtorneos() { 
  if(this.torneos.length>=10){
    console.log("No se puede agregar mas torneos")
    return;
  }
  this.torneos.push({nombreTorneo: '',pais:{idNacionalidad:0},fecha:''});
  console.log(this.torneos);
}
tipoPersona:any;
guardarAuspicio(){
  this.cargando=true;
  console.log(this.ObtenerAuspicio());
  this.dataApiClient.AgregrarAuspicio(this.ObtenerAuspicio(),this.user).subscribe(
    (data:any)=>{
      console.log(data);
    },(error:Error)=>{
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: error.message,
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
    },()=>{
      this.dataApiClient.consultaBeneficiarioAuspicio(this.cedula,'BO',this.user).subscribe(
        (data:any)=>{
          console.log(data);
          this.nAuspicio=data[0].numeroAuspicio;
          this.dbauspicio['presupuesto'].setValue(data[0].montoSolicitado);
        },(error:Error)=>{
          this.cargando=false;
        },()=>{
          this.agregarRecompensas();
        }
      );
    }
  )
}
siguienteForm(){
  this.guardarAuspicio();
}
agregarRecompensas(){
  console.log(this.getRecompensas());
  this.dataApiClient.AgregrarRecompensas(this.getRecompensas(),this.user).subscribe(
    (data:any)=>{
      console.log(data);
    },(error:Error)=>{
      console.log(error);
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
        title: 'Éxito!',
        text: "Se han guardado los datos correctamente",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      });
      this.stepper!.linear=false;
      this.stepper!.next();
      this.stepper!.linear=true;
    }
  )
}
getCatalogos(): void {
  this.dataApiClient.consultaPaises().subscribe(
    (data: Array<PaisesInterface>) => {
      this.nacionalidades = data;
    });
    console.log(this.user);
    this.dataApiClient.consultaDisciplina(this.user).subscribe(
      (data: Array<any>) => {
        this.disciplinas = data;
      });

    this.dataApiClient.consultaCategoria(this.user).subscribe(
      (data: Array<any>) => {
        this.categorias = data;
      });
}
RegresarVentana(){
  this.router.navigate(["/agent/show"]);
}
consultaBeneficiarioAuspicio(){
  this.cargando=true;
  this.dataApiClient.consultaBeneficiarioAuspicio(this.cedula,'BO',this.user).subscribe(
    (data:any)=>{
      console.log(data);
      this.dbauspicio['presupuesto'].setValue(data[0].montoSolicitado);
    },(error:Error)=>{
      this.cargando=false;
      console.log(error);
    },()=>{
      // consultaRecompensas y torneo
      this.cargando=true;
      let datos:any;
      this.dataApiClient.consultaTorneos(this.nAuspicio,this.user).subscribe(
        (data:any)=>{
          console.log(data)
          datos=data
        },(error:Error)=>{
          this.cargando=false;
          console.log(error)
        },()=>{
          if(datos.length!=0){
            this.torneos.length=0;
          }
          for(let dato of datos){
            this.torneos.push({nombreTorneo: dato.nombreTorneo,pais:{idNacionalidad: this.getIdPais(dato.pais)
            },fecha:dato.fecha});   
          }
        }
      )
      this.cargando=true;
      this.dataApiClient.consultaRecompensas(this.nAuspicio,this.user).subscribe(
        (data:any)=>{
          console.log(data)
          for(let dato of data){
            switch(dato.categoria){
              case "ORO":
                this.dbauspicio['categoria1'].setValue(dato.categoria);
                this.dbauspicio['porcentaje1'].setValue(dato.porcentaje);
                this.dbauspicio['detalle1'].setValue(dato.detalle);
                this.dbauspicio['id1'].setValue(dato.id);
                break;
                case "PLATA":
                  this.dbauspicio['categoria2'].setValue(dato.categoria);
                  this.dbauspicio['porcentaje2'].setValue(dato.porcentaje);
                  this.dbauspicio['detalle2'].setValue(dato.detalle);
                  this.dbauspicio['id2'].setValue(dato.id);
                break;
                case "BRONCE":
                  this.dbauspicio['categoria3'].setValue(dato.categoria);
                  this.dbauspicio['porcentaje3'].setValue(dato.porcentaje);
                  this.dbauspicio['detalle3'].setValue(dato.detalle);
                  this.dbauspicio['id3'].setValue(dato.id);
                break;
            }
          }
        },(error:Error)=>{
          this.cargando=false;
        },()=>{
          this.cargando=false;
        }
      )
    }
  )
}
agregarTorneos(){
  this.cargando=true;
  this.dataApiClient.AgregrarTorneos(this.getTorneos(),this.user).subscribe(
    (data:any)=>{
      console.log(data);
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
        title: 'Éxito!',
        text: "Se han guardado los datos correctamente",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      });
      this.stepper!.linear=false;
      this.stepper!.next();
      this.stepper!.linear=true;
    }
  )
}


consultarBeneficiario(){
  this.dataApiClient.consultaBeneficiariosxRepresentante(this.cedula,this.user).subscribe(
    (data:any)=>{
      console.log(data);  
      this.urlphoto=data[0].beneficiario.ruta1;
      this.Beneficiary.persona=data[0].persona;
      this.Beneficiary.beneficiario=data[0].beneficiario;
      console.log(this.Beneficiary.persona.nombres);  
    },(error:Error)=>{
      console.log(error.message)
    },()=>{
      this.consultarBeneficiarioTitulos();
    }
  )
}
words2:any[]=[];
consultarBeneficiarioTitulos(){
  this.cargando=true;
  this.dataApiClient.consultaBeneficiarioTitulos(this.cedula,this.user).subscribe(
    (data:any)=>{
      console.log(data);
      if(data.length!=0){
        this.words2.length=0;
      }
      for(let dato of data){
        this.words2.push({nacional: dato.rankingNacional,
          internacional:dato.rankingInternacional,otros:dato.otros, 
          nombreCompetencia:dato.nombreCompetencia,anio:dato.anioTitulo,idTitulo:dato.id});
      }
      console.log(this.words2);
    },(error:Error)=>{
      console.log(error);
      this.cargando=false;
    },()=>{
     this.cargando=false;
    }
  )
}



getCategoria(data:any){
  return this.categorias.filter((r:any) =>  r.id === data).map(function(r:any)  {return  r.nombre});
}

getDisciplina(data:any){
  return this.disciplinas.filter((elemento:any) =>  elemento.id === data).map(function(r:any)  {return  r.nombre});
}

getIdPais(data:any):number{
  return this.nacionalidades.filter((r:any) =>  r.pais === data).map(function(r:any)  {return  r.idNacionalidad})[0];
}


get_data(key:string){
  try{
    return localStorage.getItem(key);
  }catch(e){
    return e;
  }
}
ObtenerAuspicio(){
  let data= {
  "idBene":  this.cedula as string,
  "presupuestoSolicitudo": this.dbauspicio['presupuesto'].value as string,
  'numeroAuspicio':this.dbauspicio['id'].value as string,
  }
  console.log(data);
  return data;
}
regresarForm(){
  this.stepper!.linear=false;
  this.stepper!.previous();
}

getRecompensas(){
  return  {
    'numeroAuspicio':this.nAuspicio as number,
    'identificacion':this.cedula,
    "recompensas":[
    {
      // "id":this.dbauspicio['id1'].value as number,
      "categoria":this.dbauspicio['categoria1'].value as string,
      "porcentaje":this.dbauspicio['porcentaje1'].value as string,
      "detalle":this.dbauspicio['detalle1'].value as string
  },
  {
      // "id":this.dbauspicio['id2'].value as number,
      "categoria":this.dbauspicio['categoria2'].value as string,
      "porcentaje":this.dbauspicio['porcentaje2'].value as string,
      "detalle":this.dbauspicio['detalle2'].value as string
  },
  {
      // "id":this.dbauspicio['id3'].value as number,
      "categoria":this.dbauspicio['categoria3'].value as string,
      "porcentaje":this.dbauspicio['porcentaje3'].value as string,
      "detalle":this.dbauspicio['detalle3'].value as string
  } 
]
}
}


getPais(data:any){
  return this.nacionalidades.filter((r:any) =>  r.iso === data).map(function(r:any)  {return  r.pais});
}
getTorneos(){
  let torneos1:any[]=[];
  console.log(this.torneos);
  for( let word of this.torneos){
    torneos1.push(word);
  }
  console.log(torneos1);
return {
  'numeroAuspicio':this.nAuspicio,
  'identificacion': this.cedula as string,
  "torneos":torneos1
}
}

confirmarDatos(){
let mensaje:any;
this.cargando=true;
console.log(this.nAuspicio);
  this.dataApiClient.ConfirmarAuspicio(this.nAuspicio ,this.user).subscribe(
    (data:any)=>{
      mensaje = data;
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
      console.log(mensaje);
      Swal.fire({
        title: 'Exito!',
        text: "Su auspicio se envió para la revisión",
        icon: 'success',
        confirmButtonText: 'Aceptar'
      }).then(
        ()=>{
          this.cargando=true;
          this.stepper!.linear=false;
          this.stepper!.next();
          this.stepper!.linear=true;
          setTimeout(() => {
            this.cargando=false;
            this.router.navigate(["/agent/show"]);
          }, 1000);
        }
      );
    }
  )
}

consultarTorneosyRecompensas(){
  this.cargando=true;
      this.cargando=true;
      let datos:any;
      this.dataApiClient.consultaTorneos(this.nAuspicio,this.user).subscribe(
        (data:any)=>{
          console.log(data)
          datos=data
        },(error:Error)=>{
          this.cargando=false;
          console.log(error)
        },()=>{
          if(datos.length!=0){
            this.torneos.length=0;
          }
          for(let dato of datos){
            this.torneos.push({nombreTorneo: dato.nombreTorneo,pais:{idNacionalidad: this.getIdPais(dato.pais)
            },fecha:dato.fecha});   
          }
        }
      )
      this.cargando=true;
      this.dataApiClient.consultaRecompensas(this.nAuspicio,this.user).subscribe(
        (data:any)=>{
          console.log(data)
          for(let dato of data){
            switch(dato.categoria){
              case "ORO":
                this.dbauspicio['categoria1'].setValue(dato.categoria);
                this.dbauspicio['porcentaje1'].setValue(dato.porcentaje);
                this.dbauspicio['detalle1'].setValue(dato.detalle);
                this.dbauspicio['id1'].setValue(dato.id);
                break;
                case "PLATA":
                  this.dbauspicio['categoria2'].setValue(dato.categoria);
                  this.dbauspicio['porcentaje2'].setValue(dato.porcentaje);
                  this.dbauspicio['detalle2'].setValue(dato.detalle);
                  this.dbauspicio['id2'].setValue(dato.id);
                break;
                case "BRONCE":
                  this.dbauspicio['categoria3'].setValue(dato.categoria);
                  this.dbauspicio['porcentaje3'].setValue(dato.porcentaje);
                  this.dbauspicio['detalle3'].setValue(dato.detalle);
                  this.dbauspicio['id3'].setValue(dato.id);
                break;
            }
          }
        },(error:Error)=>{
          this.cargando=false;
        },()=>{
          this.cargando=false;
        }
      )
}



}

export interface torneos{
  nombreTorneo:string;
  pais:{idNacionalidad:number| string};
  fecha:string;
}

