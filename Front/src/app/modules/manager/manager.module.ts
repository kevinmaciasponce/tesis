import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ManagerRoutingModule } from './manager-routing.module';
import { HomeComponent } from './page/home/home.component';
import { MenuManagerComponent } from './page/components/menu-manager/menu-manager.component';
import { IntransitComponent } from './page/investments-review/intransit/intransit.component';
import { ModalmasdetallesComponent } from './page/components/modalmasdetalles/modalmasdetalles.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import {MatDialogModule} from '@angular/material/dialog';
import { ValidityComponent } from './page/investments-review/validity/validity.component';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { ReportComponent } from './page/investments-review/report/report.component';
import { ReportsModule } from '../reports/reports.module';

@NgModule({
  declarations: [
    HomeComponent,
    MenuManagerComponent,
    IntransitComponent,
    ModalmasdetallesComponent,
    ValidityComponent,
    ReportComponent,
  ],
  imports: [
    MatDialogModule,
    FormsModule,
    ReactiveFormsModule,
    MatTooltipModule,
    CommonModule,
    ManagerRoutingModule,
    ComponentsModule,
    ReportsModule
  ]
})
export class ManagerModule { }
