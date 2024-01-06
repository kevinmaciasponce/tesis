import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-frequent-questions',
  templateUrl: './frequent-questions.component.html',
  styleUrls: ['./frequent-questions.component.css']
})
export class FrequentQuestionsComponent implements OnInit {
  p1!:boolean;
  p2!:boolean;
  p3!:boolean;
  p4!:boolean;
  p5!:boolean;
  p6!:boolean;
  p7!:boolean;
  p8!:boolean;
  p9!:boolean;
  p10!:boolean;
  p11!:boolean;
  p12!:boolean;
  p13!:boolean;
  p14!:boolean;
  p15!:boolean;
  p16!:boolean;
  p17!:boolean;
  p18!:boolean;
  p19!:boolean;
  p20!:boolean;
  p21!:boolean;

  constructor() { 
   
  }

  ngOnInit(): void {
  }

  toggleState(dato:boolean){
    this.cerrarventanas();
    return !dato;
  }
  agendarReunion(){
    window.open("https://calendly.com/yalina-mora-wealthmanager/30min?month=2022-07", "_blank");
  }

  
  
cerrarventanas(){
  this.p1 = false;
  this.p2 = false;
  this.p3 = false;
  this.p4 = false;
  this.p5 = false;
  this.p6 = false;
  this.p7 = false;
  this.p8 = false;
  this.p9 = false;
  this.p10 = false;
  this.p11 = false;
  this.p12 = false;
  this.p13 = false;
  this.p14 = false;
  this.p15 = false;
  this.p16 = false;
  this.p17 = false;
  this.p18 = false;
  this.p19 = false;
  this.p20 = false;
  this.p21 = false;
}
  
}
