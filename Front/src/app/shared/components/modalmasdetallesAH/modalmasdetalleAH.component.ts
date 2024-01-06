import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { historialInversionesInterface } from 'src/app/shared/models/historialInversiones.interface';
import { StorageService } from 'src/app/shared/service/storage.service';
import { AmortizacionService } from '../../service/amortizacion.service';

import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
@Component({
  selector: 'app-modalmasdetalles',
  templateUrl: './modalmasdetallesAH.component.html',
  styleUrls: ['./modalmasdetallesAH.component.css']
})

export class ModalmasdetallesAHComponent implements OnInit,OnDestroy {
  objectValues = Object.values;
  amortizacion:any;
  nns:any ;
  user: User | undefined;
  history:Array<historialInversionesInterface>=[];
  datos! :any;
  detalles!:any;
  tipoTabla:any;
  constructor(private dataApiAmortizacion: AmortizacionService,
    private localstorage: StorageService,
    private router:Router) 
    { 
    this.user = this.localstorage.getCurrentSession();
    this.consultar();
    this.tipoTabla=this.get_data('tipoTabla')
  }
  ngOnDestroy(): void {
    localStorage.removeItem('tipoTabla');
  }

  ngOnInit(): void {
  }

  get_data(key:string){
    return localStorage.getItem(key)?.toString();
  }

  consultar(): void {
    let cuenta :any;
    cuenta=JSON.parse(this.get_data('amortizacion')!);
    console.log(cuenta);
    this.user = this.localstorage.getCurrentSession();
    console.log(this.user?.token)
      this.dataApiAmortizacion.consultaAmortizacion(cuenta, this.user!).subscribe(
      (data: any) => {
        console.error(data);
        this.datos=data; 
       // this.amortizacion= Object.values(this.datos.amortizacion);
       // this.detalles= Object.values(this.amortizacion.amortizacion.detallesTblAmortizacion);
       
      },(error:Error)=>{
        console.log(error);
      },()=>{});
  }


  captureScreen() {
    let data = document.getElementById('tble');
    let datas = document.getElementById('tble2');
    let pdfData = new jsPDF('p', 'mm', 'a4');
    const options = {
      scrollX: 0,
      scrollY: -window.scrollY
    };
    
    html2canvas(data!,options).then(canvas => {
        var imgWidth = 200;
        var pageHeight = 225;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        var position = 25;
        pdfData.text("Tabla de Amortización", 5,20);
        pdfData.addImage(contentDataURL, 'PNG', 5, position, imgWidth, imgHeight)
    });
    html2canvas(datas!,options).then(canvas => {
      var imgWidth = 200;
      var pageHeight = 225;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      var heightLeft = imgHeight;
      const contentDataURL = canvas.toDataURL('image/png');
      var position = 95;
      pdfData.addImage(contentDataURL, 'PNG', 5, position, imgWidth, imgHeight)
      pdfData.save(`Tabla Amortizacion.pdf`);
  });
}

  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
  }

}
