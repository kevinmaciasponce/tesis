import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RegisterRoutingModule } from './register-routing.module';
import { CrearCuentaComponent } from './pages/crear-cuenta/crear-cuenta.component';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FormComponent } from './pages/form/form.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { NaturalComponent } from './pages/components/natural/natural.component';
import { JuridicoComponent } from './pages/components/juridico/juridico.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { IniciarSesionComponent } from './pages/iniciar-sesion/iniciar-sesion.component';


@NgModule({
  declarations: [
    CrearCuentaComponent,
    FormComponent,
    NaturalComponent,
    JuridicoComponent,
    IniciarSesionComponent
  ],
  imports: [
    CommonModule,
    RegisterRoutingModule,
    ComponentsModule,
    ReactiveFormsModule,
    NgSelectModule,
    MatTabsModule,
    MatIconModule
  ]
})
export class RegisterModule { }
