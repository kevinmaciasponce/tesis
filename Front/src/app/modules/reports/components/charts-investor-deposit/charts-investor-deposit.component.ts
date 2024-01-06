import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ChartType} from 'chart.js';
import {  Label } from 'ng2-charts';
import { User } from 'src/app/models/user';
import { InvestorService } from 'src/app/shared/service/investor.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import { MonthsService } from 'src/app/shared/services/months.service';
import { ReportsService } from 'src/app/shared/services/reports.service';
import Swal from 'sweetalert2';
import { persona } from '../investor-report/investor-report.component';
import DataLabelsPlugin from 'chartjs-plugin-datalabels';
import { DatePipe } from '@angular/common'
import { literal } from '@angular/compiler/src/output/output_ast';
@Component({
  selector: 'app-charts-investor-deposit',
  templateUrl: './charts-investor-deposit.component.html',
  styleUrls: ['./charts-investor-deposit.component.css']
})
export class ChartsInvestorDepositComponent implements OnInit,AfterViewInit  {
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
  charts:any[]=[];
  chartSelected:any[]=[];
  esInversionista!:any;
  persona!:persona[];
  personaselected!:persona[];
  constructor(private localstorage: StorageService,
    private reportApi:ReportsService,
    private monthservice:MonthsService,
    private investorApi: InvestorService,
    public datepipe: DatePipe) 
    {
      this.user = this.localstorage.getCurrentSession();
      this.cargando=false;
      this.esInversionista=this.get_data('reportePago');
    }
  ngAfterViewInit(): void {
    this.consultar();
  }

  ngOnInit(): void {
   
    this.getCatalogo();
    this.consultar();
    
  }
  get_data(key:string){
    return localStorage.getItem(key)?.toString();
  }
  changeGender(e:any) {
    console.log(e.target.value);
    this.anio=e.target.value;
  }
  filtrarEmpresa(data:any):any{
    console.log(data);
    return this.empresas.filter((r:any) =>  r.codProyecto === data).map(function(r:any)  {return  r.nombreEmpresa});
  }

  

  limpiarelementos(){
    this.dataReport.length=0
    this.proyectos.length=0;
    this.lineChartLabels.length=0;
    this.lineChartData.length=0;
  }
  

  obtenerdatos(){
    if(this.selectmonth){
      if(this.selectmonth.toString()==='todos'){
        for(let i=1;i<13;i++){
          if(i==1){
            this.selectmonth=[i];
          }else{
            this.selectmonth=[...this.selectmonth ,
              i];
          }
        }
      }console.log(this.selectmonth.toString());

      this.form={
        meses:this.selectmonth.toString(),
        anio:'',
      }
    }else{
      this.form={
        meses:'',
        anio:'',
      }
    }

    if(this.anio){
      this.form={
        ...this.form,
        anio:this.anio
        
      }
    }else{
      this.form={
        ...this.form,
        anio:''
      }
    }


    this.form={
      identificacion:this.user.user.identificacion,
      ...this.form
    }
  }

  consultar(){
    this.limpiarelementos();
    this.cargando=true;
    this.obtenerdatos();
    this.cargando=true;
    let d:any;
    let labels:any = new Set();
    console.log(this.form)
    console.log(this.user);
    this.reportApi.chartInvestorMonths(this.form,this.user).subscribe((data:any)=>{
      console.log(data);
      this.proyectos=data.reportes;
       for(d of Object.values(data.reportes)){
        console.log(d);
        if(d.reportePorFechas){
          for(let t of d.reportePorFechas){


            labels.add(t.mes);
            let aux:dataReport={
              mes: '',
              dato: 0,
              empresa:''
            };


            aux.dato=t.totalMes;
            aux.mes= t.mes;
            this.dataReport.push(aux);
          }
        }
       }
       console.log(this.dataReport);
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
      });
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
      let lineChartReport:dataset[]=[];
      let index=0;
      console.log(aux);
      console.log(this.proyectos);
      for(let i of this.proyectos){
        let lineaux:dataset={
          label: "",
          data: [],
          backgroundColor:'',
          datalabels: {
            anchor: 'end',
            align: 'end'
          }
        };
        let aux=0;
        for(let j of labels){
          lineaux.data.push(0);
          if(i.reportePorFechas){
            for(let li of i.reportePorFechas){

              if(li.mes===j.toString()){
                lineaux.data.pop();
                aux=li.totalMes;
                // aux=li.totalMes+aux;
                lineaux.data.push(aux).toFixed(2);
              }
            }
          }
        }
        lineaux.backgroundColor=this.backgroundColor[index];
        index++;
        lineaux.label=i.nombre;
        this.lineChartData.push(lineaux);
      }
      console.log(this.lineChartData);
    })
  }

  getCatalogo(){
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
  public lineChartData: dataset[] = [
    { data: [], 
      label: '',
      backgroundColor:'',
      datalabels: {
        anchor: 'end',
        align: 'end'
      }
    }
  ];
  public backgroundColor:any[]=[
    '#1225aa9c',
    '#ff5f009c',
    '#e9a7239c',
    'rgba(148,159,177,0.2)',
    'rgba(77,83,96,0.2)',
    'rgba(255,0,0,0.3)'
  ]
  public lineChartLabels: Label[] =[];
    
  public lineChartOptions = {
    responsive: true,
    tooltips: {
      callbacks: {
        label: function (tooltipItem:any, data:any) {
          const datasetLabel = data.datasets[tooltipItem.datasetIndex].label || '';
          return  'Total al recibir:' + tooltipItem.yLabel.toLocaleString("en-US", {
            style: "currency",
            currency: "USD"
          });;
        }
      }
    },
    plugins: {
      datalabels: {
        color:'black',
        formatter: function(value:any) {
          return value.toLocaleString("en-US", {
            style: "currency",
            currency: "USD"
          });;
        },
        font: {
          size: 10,
        },
        padding: 6,
      },
  },
  scales: {
    yAxes: [
        {
          ticks: {
                callback: function(label:any, index:any, labels:any) {
                    return label.toLocaleString("en-US", {
                      style: "currency",
                      currency: "USD"
                    });;
                }
            },
            scaleLabel: {
                display: true,
                labelString: 'Total a cobrar'
            }
        }
    ]
  }
  };
     
  public lineChartLegend = true;
  lineChartType:ChartType  = 'bar';
  public lineChartPlugins = [
     //DataLabelsPlugin,
  ]
}

export interface dataset {
  label:string,
  data:number[],
  backgroundColor: string,
  datalabels: {
    anchor: "center" | "end" | "start",
    align: "center" | "end" | "start"
  }
}


export interface dataReport {
  mes:string,
  dato:number,
  empresa:string,
}
