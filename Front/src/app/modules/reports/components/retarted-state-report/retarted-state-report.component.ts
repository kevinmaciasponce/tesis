import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { MonthsService } from 'src/app/shared/services/months.service';
import { ReportsService } from 'src/app/shared/services/reports.service';
import Swal from 'sweetalert2';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
@Component({
  selector: 'app-retarted-state-report',
  templateUrl: './retarted-state-report.component.html',
  styleUrls: ['./retarted-state-report.component.css']
})
export class RetartedStateReportComponent implements OnInit {

  user!:User;
  anio23!:any;
  anio22!:any;
  anio!:any;
  form!:any;
  reporteFecha:any;
  cargando!:any;
  datos!:any;
  cuenta:any;
  investor!:any;
  empresaSelected!:any[];
  empresas!:any[];
  month!:any[];
  selectmonth!:any[];
  constructor( 
    private localstorage: StorageService,
    private dataApiInvestments: ConsultaInversionesService,
    private reportApi:ReportsService,
    private monthservice:MonthsService,
    ) {
    this.user = this.localstorage.getCurrentSession();
    this.cargando=false;
   }

  ngOnInit(): void {
    this.obtenerdata();
    this.getCatalogo();
   
  }

  getCatalogo(){
    this.monthservice.getMonth().subscribe(res=>{
      this.month=res;
    })
    this.dataApiInvestments.ConsultaEmpresas().subscribe(
      (data: Array<EmpresasInterface>) => {
        this.empresas = data;
      });
  }
 
  filtrarEmpresa(data:any):any{
    console.log(data);
    return this.empresas.filter((r:any) =>  r.codProyecto === data).map(function(r:any)  {return  r.nombreEmpresa});
  }


  consultar(){
    
    if(!this.anio){
      Swal.fire({
        title: 'Error!',
        text: 'Debe ingresar un año',
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }

    if(!this.empresaSelected){
      this.datos=[];
      this.reporteFecha=[];
      Swal.fire({
        title: 'Error!',
        text: 'Debe ingresar una empresa',
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    

    if(this.selectmonth){
      this.form={
        meses:this.selectmonth.toString(),
        anio:this.anio,
        codProyect:this.empresaSelected.toString()
      }
    }else{
      this.form={
        meses:'',
        anio:this.anio,
        codProyect:this.empresaSelected.toString()
      }
    }
    this.cargando=true;
    console.log(this.form)
    this.reportApi.reportRetardedState(this.form,this.user).subscribe((data:any)=>{
      console.log(data);
       this.datos= data;
        this.reporteFecha = Object.values(data.reportes);
        this.cargando=false;
      console.log(data);
    },(error:Error)=>{
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: error.message,
        icon: 'error',
        confirmButtonText: 'Aceptar'
      }).finally(()=>{
        this.datos=[];
        this.reporteFecha=[]
      });;
    },()=>{
      this.cargando=false;
    })
  }

  captureScreen() {
    const now = new Date();
    let data = document.getElementById('tble');
    let datas = document.getElementById('tble2');
    let pdfData = new jsPDF('p', 'mm', 'a4');
    const options = {
      scrollX: 0,
      scrollY: -window.scrollY
    };
    
    html2canvas(data!,options).then(canvas => {
        var imgWidth = 100;
        var pageHeight = 225;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        var position = 3;
        
        pdfData.addImage(contentDataURL, 'PNG', 50, position, imgWidth, imgHeight);
    }).finally(()=>{
      html2canvas(datas!,options).then(canvas => {
        var imgWidth = 150;
        var pageHeight = 225;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        var position = 50;
        pdfData.addImage(contentDataURL, 'PNG', 20, position, imgWidth, imgHeight);
        pdfData.save('Reporte Gerente '+now.toLocaleString()+ ' .pdf');
    });
    });
}

  changeGender(e:any) {
    console.log(e.target.value);
    this.anio=e.target.value;
  }

   click(){
    console.log(this.selectmonth);
   }

  obtenerdata(){
    this.cuenta={
      token:this.user.token,
      identificacion:this.user.user.identificacion
    }
  }
}
