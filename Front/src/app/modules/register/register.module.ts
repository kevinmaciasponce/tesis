import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterSelectPageComponent } from './pages/register-select-page/register-select-page.component';
import { RegisterRoutingModule } from './register-routing.module';
import { RegisterNaturalPageComponent } from './pages/register-natural-page/register-natural-page.component';
import { RegisterJuridicaPageComponent } from './pages/register-juridica-page/register-juridica-page.component';
import { RegisterChoicePageComponent } from './pages/register-choice-page/register-choice-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RegisterCrearCuentaPageComponent } from './pages/register-crear-cuenta-page/register-crear-cuenta-page.component';
import { RegisterIniciarSesionPageComponent } from './pages/register-iniciar-sesion-page/register-iniciar-sesion-page.component';
import { NgSelectModule} from '@ng-select/ng-select';
import { RegisterForgotPasswordComponent } from './pages/register-forgot-password/register-forgot-password.component';
import { RegisterSwitchPasswordComponent } from './pages/register-switch-password/register-switch-password.component';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { ModulesComponent } from './pages/modules/modules.component';
import { MatCardModule } from '@angular/material/card';

@NgModule({
  declarations: [
    RegisterSelectPageComponent,
    RegisterNaturalPageComponent,
    RegisterJuridicaPageComponent,
    RegisterChoicePageComponent,
    RegisterCrearCuentaPageComponent,
    RegisterIniciarSesionPageComponent,
    RegisterForgotPasswordComponent,
    RegisterSwitchPasswordComponent,
    ModulesComponent
  ],
  imports: [
    RegisterRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    NgSelectModule,
    MatCardModule,
    ComponentsModule
  ]
})
export class RegisterModule { }
