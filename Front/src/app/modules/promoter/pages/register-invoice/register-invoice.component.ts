import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from 'src/app/models/user';
import { StorageService } from 'src/app/shared/service/storage.service';
import { DetailsInvoiceComponent } from '../../components/details-invoice/details-invoice.component';
import { RegisterPaidInvoiceComponent } from '../../components/register-paid-invoice/register-paid-invoice.component';
import { PromoterService } from '../../service/promoter.service';

@Component({
  selector: 'app-register-invoice',
  templateUrl: './register-invoice.component.html',
  styleUrls: ['./register-invoice.component.css']
})
export class RegisterInvoiceComponent implements OnInit,OnDestroy {
  facturas:any[]=[];
  fact!:any;
  dataSource: MatTableDataSource<any>;
  displayedColumns: string[] = ['id', 'nombre', 'estado','descripcion','ver','registrar'];
  user:User;
  tipo:any;
  constructor(private promotor:PromoterService,
    public modalService: NgbModal, private storage:StorageService,
    private router:Router ) {
    this.dataSource = new MatTableDataSource(this.facturas);
    this.user = this.storage.getCurrentSession();
    this.consultarClientes();
    this.tipo = this.get_data('tipo');
    if(this.tipo=='ver'){
      this.displayedColumns= ['id', 'nombre', 'estado','descripcion','ver'];
    }else{
      this.displayedColumns= ['id', 'nombre', 'estado','descripcion','registrar'];
    }
   }
  ngOnDestroy(): void {
    localStorage.removeItem('tipo');
  }

  ngOnInit(): void {
  }

  consultarClientes(){
    this.promotor.consultarFacturasClientes(this.user.user.identificacion,this.user).subscribe(
      (data:any)=>{
        this.facturas=[];
        console.log(data);
        // data = data.filter((element:any)=>{
        //   return element.estado=='P'
        // })
        for( let fac of data){
          let aux:any={
            id: '',
            idCliente: '',
            fechaCreacion:'',
            estado:'',
            totalFactura:0
          };
          console.log(fac);
          aux.id = fac.codFact;
          aux.idCliente = fac.idCliente;
          aux.fechaCreacion = fac.fechaCreacion;
          aux.estado = fac.estado;
          aux.totalFactura = fac.totalFactura;
          this.facturas.push(aux);
        }
       
        this.fact=this.facturas;
        console.log(this.fact);
      },null,()=>{
        this.dataSource = new MatTableDataSource(this.fact);
      }
    );
  }

  returnRazonSocial(){
    return this.user.user.nombres;
    
  }

  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }
  VerDocumento(dato:any){
    this.set_data('idFactura',dato);
    const modal = this.modalService.open(DetailsInvoiceComponent, { 
      windowClass: 'my-class'
    }).result.finally(
          ()=>{
      this.consultarClientes()
    }
    );
  }

  RegistrarDocumento(dato:any){
    this.set_data('idFactura',dato);
    this.router.navigate(["/promoter/invoice/paid"]);
  }

  get_data(key: any): any {
    return localStorage.getItem(key);
  }

  mostrarEstado(estado:any){
    switch(estado){
      case 'P':
        return 'Pendiente';
      case 'C':
        return 'Cobrado';
      case 'G':
        return 'Pagado';
      case 'A':
        return 'Anulado';
      case 'E':
        return 'Generado';
      case 'F':
        return 'Facturado';
      default:
        return ''
      }
  }

}
