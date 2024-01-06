import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { MonthsService } from 'src/app/shared/services/months.service';
import { ReportsService } from 'src/app/shared/services/reports.service';
import Swal from 'sweetalert2';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-investor-report',
  templateUrl: './investor-report.component.html',
  styleUrls: ['./investor-report.component.css']
})
export class InvestorReportComponent implements OnInit {
  user!:User;
  anio23!:any;
  anio22!:any;
  anio!:any;
  form!:any;
  userReport:any;
  datos!:any;
  cuenta:any;
  investor!:any;
  cargando!:any;
  esInversionista:any;
  month!:any[];
  selectmonth!:any[];

  persona!:persona[];
  personaselected!:persona[];
  constructor( private localstorage: StorageService,private investorApi: InvestorService,private reportApi:ReportsService,private monthservice:MonthsService) {
    this.user = this.localstorage.getCurrentSession();
    this.cargando=false;
    this.esInversionista=this.get_data('reportePago');
    console.log(this.esInversionista);
    
   }

  ngOnInit(): void {
    this.obtenerdata();
    this.obtenerCatalogo();
    
  }

  obtenerCatalogo(){
    
   
    this.monthservice.getMonth().subscribe(res=>{
      this.month=res;
    })

    if(this.esInversionista=='manager'){
      this.reportApi.consultaPersonas(this.user).subscribe(
        (data:any)=>{
          this.persona=data;
        }
      );
    }else{
      this.investorApi.consultaDatosInversionista(this.cuenta).subscribe((data:any)=>{
        console.log(data)
        this.investor=data;
      });
    }
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  get_data(key:string){
    return localStorage.getItem(key)?.toString();
  }

  consultar(){
    this.datos=[];
    this.userReport=[];
    if(!this.anio){
      Swal.fire({
        title: 'Error!',
        text: 'Debe ingresar un año',
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
      return;
    }
    
    this.cargando=true;
    if(this.selectmonth){
      this.form={
        meses:this.selectmonth.toString(),
        anio:this.anio,
       
      }
    }else{
      this.form={
        meses:'',
        anio:this.anio,
        
      }
    }

    if(this.esInversionista=='manager'){
      this.form={
        identificacion:this.personaselected.toString(),
        ...this.form
      }
    }else{
      this.form={
        identificacion:this.user.user.identificacion,
        ...this.form
      }
    }
    console.log(this.form)
    this.reportApi.reportInvestorMonths(this.form,this.user).subscribe((data:any)=>{
      this.datos= data;
      console.log(data);
      this.userReport = Object.values(data.reportes);
      this.cargando=false;
    },(error:Error)=>{
      this.cargando=false;
      Swal.fire({
        title: 'Error!',
        text: error.message,
        icon: 'error',
        confirmButtonText: 'Aceptar'
      }).finally(()=>{
        this.datos=[];
        this.userReport=[]
      });
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
        var imgWidth = 200;
        var pageHeight = 225;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        var position = 3;
        
        pdfData.addImage(contentDataURL, 'PNG', 7, position, imgWidth, imgHeight);
    }).finally(()=>{
      html2canvas(datas!,options).then(canvas => {
        var imgWidth = 200;
        var pageHeight = 225;
        var imgHeight = canvas.height * imgWidth / canvas.width;
        var heightLeft = imgHeight;
        const contentDataURL = canvas.toDataURL('image/png');
        var position = 40;
        pdfData.addImage(contentDataURL, 'PNG', 5, position, imgWidth, imgHeight);
        pdfData.save('Reporte inversionista'+now.toLocaleString()+ ' .pdf');
          });
    });
}

  changeGender(e:any) {
    console.log(e.target.value);
    this.anio=e.target.value;
    console.log(this.personaselected);
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


export interface persona{
  nombres:string,
  identificacion:string
}