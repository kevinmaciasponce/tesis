import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { SponsorAnalystService } from 'src/app/modules/sport-sponsorship/service/sponsor-analyst.service';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { StorageService } from 'src/app/shared/service/storage.service';


@Component({
  selector: 'app-inconfirmation',
  templateUrl: './inconfirmation.component.html',
  styleUrls: ['./inconfirmation.component.css']
})
export class InconfirmationComponent implements OnInit {
  isOpenConsulta=false;
  disciplina:any;
  disciplinas:any[]=[];
  user:User;
  contadorAuspicios:any=0;
  Beneficiarys:any;
  Beneficiary:any={};
  porcenatjeprueba:any=0;
  urlphoto:any
  categorias:any[]=[];
  categoria:any;
  nacionalidad:any;
  nacionalidades:PaisesInterface[] =[];
  showCard:any=false;
  constructor(
    private _formBuilder: FormBuilder,
    private dataApiClient: BeneficiaryService,
    private sponsorApi:SponsorAnalystService,
    private router: Router,
    private storage:StorageService) { 
      this.user = this.storage.getCurrentSession();
      this. getCatalogos();
      this.getSponsors();
    }

  ngOnInit(): void {
  }
  get db() { return this.registrationForm.controls }
  registrationForm = this._formBuilder.group({
    numeroAuspicio: ['',],
    nomApe: ['',],
    identificacion: ['', ],
  });

  toggleState(dato:boolean){
    return !dato;
  }

  clickObtenerDatos(){
    this.showCard=false;
    console.log(this.obtenerDatos());
    this.getSponsors();

  }

  obtenerDatos(){
    let cuenta =  {
      "id": this.registrationForm.get('numeroAuspicio')?.value,
      "nomApe":this.registrationForm.get('nomApe')?.value,
      "identificacion":this.registrationForm.get('identificacion')?.value,
      "disciplina":'',
      "estado":"PC"
    }
    if(this.disciplina!=null){
      cuenta = {
        ...cuenta,
        "disciplina":this.disciplina
      }
    }
    return cuenta;
  }

  words2:any[]=[];
consultarBeneficiarioTitulos(cedula:any){
  this.dataApiClient.consultaBeneficiarioTitulos(cedula,this.user).subscribe(
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
    },()=>{

    }
  )
}

  getSponsors(){
    this.sponsorApi.consultaAuspiciosPorConfirmar(this.obtenerDatos()).subscribe(
      (data:any)=>{
        console.log(data);
        this.Beneficiarys=data;
        this.contadorAuspicios=data.length;
        console.log(this.Beneficiarys);
      },(error:Error)=>{

      },()=>{

      }
    )
  }


  getPais(data:any){
    return this.nacionalidades.filter((r:any) =>  r.iso === data).map(function(r:any)  {return  r.pais});
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
  
  editarAuspicios(){
    this.set_data('auspCed',this.Beneficiary.persona.identificacion);
    this.set_data('auspNum',this.Beneficiary.persona.identificacion);
    this.router.navigate(["/sponsor-analyst/edit"]);
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  ObtenerData(data:any){
    console.log(data);
    
    let cuenta =  {
      "id": data as string,
      "nomApe":'',
      "identificacion":'',
      "disciplina":'',
      "estado":"PC"
    }
    this.sponsorApi.consultaAuspiciosPorConfirmar(cuenta).subscribe(
      (data:any)=>{
        console.log(data);
        this.Beneficiary=data[0];
        this.showCard=true;
        console.log(this.Beneficiary);
      },(error:Error)=>{

      },()=>{
        this.consultarBeneficiarioTitulos(this.Beneficiary.persona.identificacion);
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
}
