import { Component, OnInit } from '@angular/core';
import { DeviceDetectorService } from 'ngx-device-detector';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {
  isOpenContact:boolean = false;
  p1:boolean = false;
  p2:boolean = false;
  p3:boolean = false;
  constructor(private deviceDetectorService: DeviceDetectorService,) {
    this.isOpenContact= true;
  }
  ngOnInit(): void {
  }


  


}
