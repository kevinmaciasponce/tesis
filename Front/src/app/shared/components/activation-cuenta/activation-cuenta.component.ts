import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { ConsultasPublicasService } from '../../service/consultas-publicas.service';

@Component({
  selector: 'app-activation-cuenta',
  templateUrl: './activation-cuenta.component.html',
  styleUrls: ['./activation-cuenta.component.css']
})
export class ActivationCuentaComponent implements OnInit {
  token!:string;
  mensaje!:any;
  constructor(private route: ActivatedRoute,public consultas: ConsultasPublicasService,private router:Router) { 
    this.token = this.route.snapshot.paramMap.get("token") as string;
    console.log(this.token);
}

  ngOnInit(): void {
    this.consultaractivacion();
  }

  consultaractivacion(){
    this.consultas.activarcuenta(this.token).subscribe(
      (data:any)=>{
        this.mensaje=data.mensaje;
        console.log(data);
      },(error:Error)=>{
        console.log(error);
      },()=>{
        switch(this.mensaje){
          case 'El enlace no es válido':
            Swal.fire({
              title: 'Error',
              text: this.mensaje,
              icon: 'error',
              confirmButtonText: 'Aceptar'
            }).then(()=>{
              this.router.navigate(["/"]);
            });
          break;
          case 'Su cuenta ha sido verificada exitosamente':
            Swal.fire({
              title: 'Éxito',
              text: this.mensaje,
              icon: 'success',
              confirmButtonText: 'Aceptar'
            }).then(()=>{
              this.router.navigate(["register/iniciar_sesion"]);
            });
          break;
          default:
            Swal.fire({
              title: 'Aviso',
              text: this.mensaje,
              icon: 'warning',
              confirmButtonText: 'Aceptar'
            }).then(()=>{
              this.router.navigate(["register/iniciar_sesion"]);
            });
            break;
        }
        
      }
    )
  }
  

}
