import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { BeneficiaryService } from '../../../service/beneficiary.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
    user:User;
    public beneficiarys:any[]=[]
  constructor(private router:Router,
    private dataApliClient:BeneficiaryService,
    private storage:StorageService,
    ) {
      
      this.user=this.storage.getCurrentSession();
     }

  ngOnInit(): void {
    this.consultarBeneficiario();
  }
  Editar(identificacion:any){
    this.set_data('ced',identificacion);
    this.router.navigate(["/agent/complete"]);
  }
  verBeneficiario(identificacion:any){
    this.set_data('ced',identificacion);
    this.router.navigate(["/agent/show"]);
  }
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }
  

  gotoRoute(){
    this.router.navigate(["/agent/complete"]);
  }

  gotoOwnbeneficiary(){
    
    this.router.navigate(["/agent/complete"]);
  }
  data:any;
  consultarBeneficiario(){
    this.dataApliClient.consultaBeneficiariosxRepresentante('',this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.beneficiarys = data;
      },(error:Error)=>{

      },()=>{

      }
    )
  }
}
