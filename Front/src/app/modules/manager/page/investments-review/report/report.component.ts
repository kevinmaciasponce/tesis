import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['../../../../../shared/components/styleSheets/reportCards.css']
})
export class ReportComponent implements OnInit {

  OpenReport:any='nn';

  constructor() {
    this.OpenReport='nn'
   }

  ngOnInit(): void {
  }



}
