import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-info-modal',
  templateUrl: './info-modal.component.html',
  styleUrls: ['./info-modal.component.css']
})
export class InfoModalComponent implements OnInit {

  constructor(private activeModal:NgbActiveModal) { }

  ngOnInit(): void {
  }

  cerrarModal(){
    this.activeModal.dismiss();
  }

}
