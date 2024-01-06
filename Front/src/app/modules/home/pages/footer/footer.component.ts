import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FrequentQuestionsComponent } from 'src/app/shared/components/statics/frequent-questions/frequent-questions.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  p1!:boolean;
  p2!:boolean;
  p3!:boolean;
  p4!:boolean;
  p5!:boolean;
  constructor(private router: Router,public modalService: NgbModal,) { }

  ngOnInit(): void {

  // var screenWidth = screen.width;
  // var screenHeight = screen.height;

  // // Get the browser window size
  // var windowWidth = window.innerWidth;
  // var windowHeight = window.innerHeight;

  // console.log(screenWidth+' * '+ screenHeight)
  // console.log(windowWidth+' * '+ windowHeight)
  }
  preguntasFrecuentes(){
    const modal = this.modalService.open(FrequentQuestionsComponent,{ 
      windowClass: 'my-class'
  });
  }
  toggleState(dato:boolean){
    this.cerrarventanas();
    return !dato;
  }

  goToRoute(route:any){
    this.router.navigate([route]);
   
  }
  mensajeConstruccion(){
    Swal.fire({
      title: 'Multiplo',
      text: 'Pagina en construcción',
      icon: 'warning',
      confirmButtonText: 'Aceptar'
    })
  }
  cerrarventanas(){
    this.p1 = false;
    this.p2 = false;
    this.p3 = false;
    this.p4 = false;
    this.p5 = false;
  }
}
