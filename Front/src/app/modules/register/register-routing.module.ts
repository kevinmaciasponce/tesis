import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterChoicePageComponent } from './pages/register-choice-page/register-choice-page.component';
import { RegisterCrearCuentaPageComponent } from './pages/register-crear-cuenta-page/register-crear-cuenta-page.component';
import { RegisterIniciarSesionPageComponent } from './pages/register-iniciar-sesion-page/register-iniciar-sesion-page.component';
import { RegisterJuridicaPageComponent } from './pages/register-juridica-page/register-juridica-page.component';
import { RegisterNaturalPageComponent } from './pages/register-natural-page/register-natural-page.component';
import { RegisterSelectPageComponent } from './pages/register-select-page/register-select-page.component';
import { RegisterForgotPasswordComponent } from './pages/register-forgot-password/register-forgot-password.component';
import { RegisterSwitchPasswordComponent } from './pages/register-switch-password/register-switch-password.component';
import { ModulesComponent } from './pages/modules/modules.component';
import { LoginGuard } from 'src/app/shared/guards/login.guard';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'choice',
    pathMatch: 'full'
  },
  {
    path: 'choice',
    component: RegisterChoicePageComponent
  },
  {
    path: 'select',
    component: RegisterSelectPageComponent
  },
  {
    path: 'natural',
    component: RegisterNaturalPageComponent,
    canActivate: []
  },
  {
    path: 'juridica',
    component: RegisterJuridicaPageComponent,
    canActivate: []
  },
  {
    path: 'crear-cuenta/natural',
    component: RegisterCrearCuentaPageComponent,
  },
  {
    path: 'crear-cuenta/juridica',
    component: RegisterCrearCuentaPageComponent,
  },
  {
    path: 'iniciar_sesion',
    component: RegisterIniciarSesionPageComponent
  },
  {
    path: 'forgot_password',
    component: RegisterForgotPasswordComponent
  }
  ,
  {
    path: 'switch_password/:token',
    component: RegisterSwitchPasswordComponent
  }
  ,
  {
    path: 'modules-access',
    component: ModulesComponent,
    canActivate: [LoginGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RegisterRoutingModule { }
