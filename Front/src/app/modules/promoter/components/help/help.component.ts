import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styles:[
  ],
  styleUrls: ['./help.component.css',],
 
})
export class HelpComponent implements OnInit {
  toggle=false;
  toggle2=false;
  toggleDatos=false;
  constructor(private router:Router,private modalService: NgbModal) { 

  }

  openSm(content:any) {
		this.modalService.open(content,  { size: 'xl',centered: true });
	}
  ngOnInit(): void {
  }

 
}
