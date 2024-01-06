import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from 'src/app/shared/service/storage.service';
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BeneficiaryService } from 'src/app/modules/sport-sponsorship/service/beneficiary.service';
import { PaisesInterface } from 'src/app/shared/models/paises-interface';
import { environment } from 'src/environments/environment';
@Component({
  selector: 'app-sport-sponsor',
  templateUrl: './sport-sponsor.component.html',
  styleUrls: ['./sport-sponsor.component.css']
})
export class SportSponsorComponent implements OnInit {
  porcenatjeprueba:any
  porcenatjeprueba2:any
  projects:any;
  user:any;
  deportist:any
  nacionalidades:any
  pais:any;
  constructor( private localstorage: StorageService,   
    private clipboard: Clipboard,
    private dataApiClient: BeneficiaryService, 
    private _snackBar: MatSnackBar,
    private router:Router,) { 
    this.porcenatjeprueba= 0;
    this.porcenatjeprueba2=0;
    this.user = this.localstorage.getCurrentSession();
    this.getCatalogos();
  }

  ngOnInit(): void {
  }

  convert(percent:any){
    if(percent[0]==','){
      return 0;
    }
     let num;

    try {
      num = Number(this.obtenernumero(percent));
    } catch (error) {
      return 0;
    } 
    return num;
  }
  obtenernumero(numero:any):string{
    var arrayDeCadenas = numero.split(",");
   return arrayDeCadenas[0];
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  QuieroAuspiciatGoto(){
    switch(this.user?.user.tipoCliente){
      case "BENEFICIARIO":
        this.router.navigate(["/beneficiary/complete"]);
        break;
      default:
      this.router.navigate(["/beneficiary/register"]);
      break;
    }
  }

  masDetalles(idauspicio:any){
    // this.set_data('beneficiario',nombre)
    this.router.navigate(["/sport-sponsorship/sponsor/"+idauspicio]);
  }

  consultarAuspiciosVigentes(){
    let cuenta ={
          "nomApe":'',
          "identificacion":'',
          "disciplina":''
    }
    this.dataApiClient.consultaAuspiciosVigentes(cuenta).subscribe(
      (data:any)=>{
        console.log(data);
        for (let deportistas of data){
          this.dataApiClient.consultaTitulosPublico(deportistas.persona.identificacion).subscribe(
            (data:any)=>{
              deportistas.torneos=data;
            }
          )
        }
        this.deportist=data;
      },null,()=>{
      }
    );
  }

  getPais(data:any):number{
    return this.nacionalidades.filter((r:any) =>  r.iso === data).map(function(r:any)  {return  r.pais})[0];
  }
  getCatalogos(): void {
    this.dataApiClient.consultaPaises().subscribe(
      (data: Array<PaisesInterface>) => {
        this.nacionalidades = data;
      },null,()=>{
        this.consultarAuspiciosVigentes();
      });
  }

  copyToClipboard(numeroAuspicio:any): void {
    // Se copia el texto del input al portapapeles
    // let contacto = "https://multiplolendersqa.azurewebsites.net/sport-sponsorship/sponsor/"+numeroAuspicio;
    let contacto = "https://multiplolenders.com/sport-sponsorship/sponsor/"+numeroAuspicio;
    // let contacto = "http://localhost:8080/sport-sponsorship/sponsor/"+numeroAuspicio;
    this.clipboard.copy(contacto);
    this._snackBar.open('¡Enlace copiado al portapapeles!', '', {
      duration: 2000,
      panelClass: 'snackbar'
    });
  }

// item.identificacion == '0930071261' || 
// item.identificacion == '0950414573' || 
// item.identificacion == '0993322954001' || 
// item.identificacion == '0920788767' || 
// item.identificacion == '1207826486' 

}
