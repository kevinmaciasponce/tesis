import { Component, OnDestroy, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { historialInversionesInterface } from '../../models/historialInversiones.interface';
import { Tabla_amortizacionInterface } from 'src/app/shared/models/tabla_amortizacion/tabla_amortizacion.interface';
import { ConsultaInversionesService } from '../../service/consulta-inversiones.service';
import { StorageService } from '../../service/storage.service';
import Swal from 'sweetalert2';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { AmortizacionService } from '../../service/amortizacion.service';
@Component({
  selector: 'app-depreciation-tables',
  templateUrl: './depreciation-tables.component.html',
  styleUrls: ['./depreciation-tables.component.css']
})
export class DepreciationTablesComponent implements OnInit,OnDestroy {
  objectValues = Object.values;
  values:any;
  cuenta:any;
  nns!:any ;
  codProyecto!:string;
  tipoTabla!:any;
  user: User | undefined;
  history:Array<historialInversionesInterface>=[];
  amortizacion! :any;
  constructor(private dataApiInvestments: ConsultaInversionesService,
    private dataApiAmortizacion: AmortizacionService,
    private router: Router,
    private localstorage: StorageService,public activeModal: NgbActiveModal) { 
    this.user = this.localstorage.getCurrentSession();
    this.consultar();
  }

  
  ngOnDestroy(): void {
    localStorage.removeItem('numSolicitud');
    localStorage.removeItem('codProyecto');
    localStorage.removeItem('tipoTabla');
  }

  ngOnInit(): void {
  }

  get_data(key:string):any{
    return localStorage.getItem(key)?.toString();
  }

  obtenerurlactual():string{
    var url = this.router.url;
    var arrayDeCadenas = url.split("/");
   return arrayDeCadenas[arrayDeCadenas.length-1];
  }
  
  consultar(): void {
    this.nns= JSON.parse(this.get_data('cuenta'));
    this.cuenta=this.nns.idTipoTabla;
    let dataAmort:any;
    dataAmort={
      numSol:this.nns.numeroSolicitud,
      tipo:this.nns.idTipoTabla,
      codProyecto:null
    }
      this.dataApiAmortizacion.consultaAmortizacion(dataAmort, this.user!).subscribe(
      (data: any) => {
        this.amortizacion=data; 
        console.log({data});
       this.values= Object.values(data.amortizacion.detallesTblAmortizacion);
      
      },(error:Error)=>{
        console.log(error);
        Swal.fire({
          title: 'Error!',
          text: 'No tiene generada una tabla de amortización',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
        this.activeModal.dismiss();
      });
  }

}
