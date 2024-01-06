import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';


@Component({
  selector: 'app-how-we-did',
  templateUrl: './how-we-did.component.html',
  styleUrls: ['./how-we-did.component.css']
})
export class HowWeDidComponent implements OnInit {
  
  constructor(private router:Router) { }

  ngOnInit(): void {
  }

  
  goto_register(){
    this.router.navigateByUrl('register');
  }
}
