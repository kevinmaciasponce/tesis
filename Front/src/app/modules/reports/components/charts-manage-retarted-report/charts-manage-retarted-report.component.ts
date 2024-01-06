import { Component, OnInit } from '@angular/core';
import { ChartType} from 'chart.js';
import {  Label } from 'ng2-charts';
import { User } from 'src/app/models/user';
import { EmpresasInterface } from 'src/app/shared/models/consulta_Inversiones/empresas.interface';
import { ConsultaInversionesService } from 'src/app/shared/service/consulta-inversiones.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { MonthsService } from 'src/app/shared/services/months.service';
import { ReportsService } from 'src/app/shared/services/reports.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-charts-manage-retarted-report',
  templateUrl: './charts-manage-retarted-report.component.html',
  styleUrls: ['./charts-manage-retarted-report.component.css']
})
export class ChartsManageRetartedReportComponent implements OnInit {
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
  dataReport:dataReport[]=[];
  proyectos:any[]=[];
  constructor(private localstorage: StorageService,
    private dataApiInvestments: ConsultaInversionesService,
    private reportApi:ReportsService,
    private monthservice:MonthsService,) {
      this.user = this.localstorage.getCurrentSession();
      this.cargando=false; 
    }

  ngOnInit(): void {
    this.getCatalogo();
  }

  changeGender(e:any) {
    console.log(e.target.value);
    this.anio=e.target.value;
  }
  filtrarEmpresa(data:any):any{
    console.log(data);
    return this.empresas.filter((r:any) =>  r.codProyecto === data).map(function(r:any)  {return  r.nombreEmpresa});
  }
  consultar(){
    this.dataReport.length=0
    this.proyectos.length=0;
    this.lineChartLabels.length=0;
    this.lineChartData.length=0;
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
    let d:any;
    let labels:any = new Set();
    console.log(this.form)
    this.reportApi.reportRetardedState(this.form,this.user).subscribe((data:any)=>{
      console.log(data);
      this.proyectos=data.reportes;
       for(d of Object.values(data.reportes)){
        console.log(d);
        if(d.listaCuotas){
          for(let t of d.listaCuotas){
            labels.add(t.mes);
            let aux:dataReport={
              mes: '',
              dato: 0,
              empresa:''
            };
            aux.dato=t.totalMes;
            aux.mes= t.mes;
            aux.empresa=this.filtrarEmpresa(d.codProyecto).toString();
            this.dataReport.push(aux);
          }
        }
       }
       console.log(labels);
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
      let aux:any[]=[];
      this.cargando=false;
      for(let i of labels){
        this.lineChartLabels.push(i);
        let auxi:any={
          mes: '',
          dato: 0
        };
        auxi.mes = i;
        auxi.dato=0;
        aux.push(auxi);
      }
      let index=0;
      console.log(aux);
      console.log(this.proyectos);
      for(let i of this.proyectos){
        let lineaux:dataset={
          label: "",
          data: [],
          backgroundColor:'',
          borderColor:'',
          cuota:[]
        };
        for(let j of labels){
          lineaux.data.push(0);
          if(i.listaCuotas){
            for(let li of i.listaCuotas ){
              if(li.mes===j.toString()){
                lineaux.data.pop();
                lineaux.data.push(li.totalMes);
                lineaux.cuota.push(li.reporteFechaProyecto[0].numCuota);
              }
            }
          }
        }
        lineaux.backgroundColor=this.backgroundColor[index];
        lineaux.borderColor=this.borderColor[index];
        index++;
        lineaux.label=this.filtrarEmpresa(i.codProyect);
        this.lineChartData.push(lineaux);
      }
      console.log(this.lineChartData);
    })
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
  public lineChartData: dataset[] = [
    { data: [], 
      label: '',
      backgroundColor:'',
      borderColor:'',
      cuota:[]
    }
  ];

  public borderColor:any[]=[
    '#1225aa',
    '#ff5f00',
    '#e9a723',
    '#1225aa',
    '#ff5f00',
    '#e9a723',
  ]
  public backgroundColor:any[]=[
    '#1225aa00',
    '#ff5f0000',
    '#e9a72300',
    '#1225aa00',
    '#ff5f0000',
    '#e9a72300',
  ]
  public lineChartLabels: Label[] =[];
    
  


  public lineChartOptions = {
    responsive: true,
      tooltips: {
        callbacks: {
          label: function (tooltipItem:any, data:any) {
            const datasetLabel = data.datasets[tooltipItem.datasetIndex].label || '';
            return  datasetLabel + ': ' + tooltipItem.yLabel+ 'Cuota: '+ data.datasets[tooltipItem.datasetIndex].cuota[tooltipItem.index];
          }
        }
      }
}
 
  public lineChartLegend = true;
  lineChartType:ChartType  = 'line';
  public lineChartPlugins = [
  ];

}

export interface dataset {
  label:string,
  data:number[],
  backgroundColor: string,
  borderColor:string,
  cuota:string[],
}


export interface dataReport {
  mes:string,
  dato:number,
  empresa:string,
}