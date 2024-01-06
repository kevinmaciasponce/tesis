import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CrearCuentaComponent } from './pages/crear-cuenta/crear-cuenta.component';
import { FormComponent } from './pages/form/form.component';
import { IniciarSesionComponent } from './pages/iniciar-sesion/iniciar-sesion.component';

const routes: Routes = [
  {
    path: '',
    component: CrearCuentaComponent
  },
  {
    path: 'form',
    component: FormComponent
  },
  {
    path: 'iniciar_sesion',
    component: IniciarSesionComponent
  }
  ,
  {
    path: 'beneficiary',
    component: IniciarSesionComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RegisterRoutingModule { }
