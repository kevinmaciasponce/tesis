import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RegisterSelectPageComponent } from '../register-select-page/register-select-page.component';
@Component({
  selector: 'app-register-choice-page',
  templateUrl: './register-choice-page.component.html',
  styleUrls: ['./register-choice-page.component.css']
})
export class RegisterChoicePageComponent implements OnInit {

  constructor(public modalService: NgbModal,private router: Router) { }

  ngOnInit(): void {
  }
  
  set_data(key:string,data:string){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  get_data(key:string){
    return localStorage.getItem(key)?.toString();
}

  
  clickselectpage(pagina:string){
    switch (pagina){
      case "invierte":
        this.set_data('financiamiento',pagina);
        console.log(this.get_data('financiamiento'));
        const modal = this.modalService.open(RegisterSelectPageComponent, { 
          windowClass: 'my-class'
      });
      break;
      case "financiamiento":
        this.set_data('financiamiento',pagina);
        console.log(this.get_data('financiamiento'));
        this.router.navigateByUrl('register/crear-cuenta/juridica');
      break;
    }
    
  
  }

}