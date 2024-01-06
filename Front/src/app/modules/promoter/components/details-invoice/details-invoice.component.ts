import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { PromoterService } from '../../service/promoter.service';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-details-invoice',
  templateUrl: './details-invoice.component.html',
  styleUrls: ['./details-invoice.component.css']
})
export class DetailsInvoiceComponent implements OnInit, OnDestroy {
  user:User;
  idFactura:any;
  factura:any;
  dataSource: MatTableDataSource<any>;
  displayedColumns: string[] = ['id', 'descripcion','cantidad', 'preciou','total'];


  constructor(private promotor:PromoterService,private storage: StorageService) { 
    this.user=this.storage.getCurrentSession();
    this.idFactura=this.get_data('idFactura');
    this.dataSource = new MatTableDataSource();
  }
  
  ngOnDestroy(): void {
    localStorage.removeItem('idFactura');
  }

  ngOnInit(): void {
    this.consultarDetFactura();
  }

  consultarDetFactura(){
    this.promotor.consultarFacturasDocumentos(this.idFactura,this.user).subscribe(
      (data:any)=>{
        console.log(data);
        this.factura=data;
        this.dataSource = new MatTableDataSource(data.detalles);
      }
    )
  }

  captureScreen() {
    let data = document.getElementById('factura');
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
      var position = 3
      pdfData.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight)
      pdfData.save(`Factura Promotor.pdf`);
  });
  }



  get_data(key: any): any {
    return localStorage.getItem(key);
  }
}
