import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { detalleCobroInterface } from 'src/app/shared/models/detallecobro.interface';
import { solicitud_tablaInterface } from 'src/app/shared/models/solicitud_tablaInterface';
import { Tabla_amortizacionInterface } from 'src/app/shared/models/tabla_amortizacion/tabla_amortizacion.interface';
import { AmortizacionService } from 'src/app/shared/service/amortizacion.service';
import { StorageService } from 'src/app/shared/service/storage.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-indicate',
  templateUrl: './indicate.component.html',
  styleUrls: ['./indicate.component.css']
})
export class IndicateComponent implements OnInit {
  objectValues = Object.values;
  values:any;
  fechaActual:any;
  monto:string="0";
  project:any;
  cedula:string="";
  user:User | undefined;
  cargando!:boolean;
  detalles!: Array<detalleCobroInterface>;
  datos_tabla!: Tabla_amortizacionInterface;
  aux!:detalleCobroInterface;
  tipo_cuentas:any[]=[];
  tipo_cuenta:any;
  idEmpresa:any;
  constructor(private storage:StorageService,
    private amortizacion:AmortizacionService,
    private router: Router
    ) { 
    this.project=this.get_data('proyecto');
    this.idEmpresa=this.get_data('id');
    this.user=this.storage.getCurrentSession();
    this.cedula=this.user?.user.identificacion!;
    console.log(this.idEmpresa);
    this.calcular();
    this.cargando=false;
    const now = new Date();
    this.fechaActual=now.toLocaleDateString();
  }

  ngOnInit(): void {
    this.cedula=this.user?.user.identificacion!;
    this.tipo_cuentas.push(
      {'valor':'$ 45,000.00', 'monto' : 45000},
      {'valor':'$ 90,000.00', 'monto' : 90000},
      {'valor':'$ 135,000.00', 'monto' :135000 },
      {'valor':'$ 270,000.00', 'monto' :270000 },
    );
  }
  imprimirValor(){
    console.log(this.tipo_cuenta);
    this.monto=this.tipo_cuenta;
    this.generartabla();
  }
  calcular(){
    this.cargando=true;
    let datos:solicitud_tablaInterface;
    datos={
      identificacion:this.cedula,
      codigoProyecto: this.project,
      inversion: Number(this.monto)
    }
    this.amortizacion.ConsultaTabla(datos).subscribe(
      (data: Tabla_amortizacionInterface) => {
        this.datos_tabla = data;
      },(error : Error)=>{
        this.cargando=false;
      },()=>{
        this.cargando=false;
      });
  }
  
  generartabla(){
    if(Number(this.monto)<500){
      Swal.fire('Error!',"No se puede ingresar valores menores a $500", 'error');
      return;
    }

    let datos:solicitud_tablaInterface;
    datos={
      identificacion:this.cedula,
      codigoProyecto: this.project,
      inversion: Number(this.monto)
    }
    console.log(datos);
    this.amortizacion.ConsultaTabla(datos).subscribe(
      (data: Tabla_amortizacionInterface) => {
        this.datos_tabla = data;
        console.log(data);
        this.values= Object.values(this.datos_tabla.detallesTblAmortizacion
          );
      },(error:Error)=>{
        Swal.fire({
          title: 'Error',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        })
      });
  }

  guardar(){
    if(Number(this.monto)==0){
      Swal.fire('Error!',"Debe ingresar valores para poder continuar", 'error');
      return;
    }
    if(Number(this.monto)<500){
      Swal.fire('Error!',"No se puede ingresar valores menores a $500", 'error');
      return;
    }
    this.cargando=true;
    let datos:any;
    datos={
      identificacion:this.cedula,
      codigoProyecto: this.project,
      inversion: Number(this.monto)
    }
    console.log(datos);
    this.amortizacion.GuardarTabla(datos,this.user!).subscribe(
      (data: any) => {
        this.set_data('numero_solicitud',data.numeroSolicitud);
        console.log(data);
      },(error: Error)=>{
        this.cargando=false;
        console.log(error.name);
        Swal.fire({
          title: 'Error!',
          text: error.message,
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
        this.router.navigateByUrl('investor');
      },()=>{
        this.cargando=false;
        Swal.fire({
          title: 'Éxito!',
          text: 'Usted ha realizado una Solicitud de Inversión por el valor de $'+ this.monto +' ',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        }).then(()=>{
          this.router.navigateByUrl('investor/complete');
        });
      });
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
      this.router.navigate(["/investor/indicate"]);
    } catch(e){
      console.log(e);
    }
  }
  get_data(key:string){
    try{
      return localStorage.getItem(key);
    }catch(e){
      return e;
    }
  }

  keyPressNumbers(event: { which: any; keyCode: any; preventDefault: () => void; }) {
    var charCode = (event.which) ? event.which : event.keyCode;
    // Only Numbers 0-9
    if ((charCode < 48 || charCode > 57)) {
      event.preventDefault();
      return false;
    } else {
      return true;
    }
  }
  
}
