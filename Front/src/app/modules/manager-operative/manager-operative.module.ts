import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ManagerOperativeRoutingModule } from './manager-operative-routing.module';
import { MenuManagerOperativeComponent } from './page/components/menu-manager-operative/menu-manager-operative.component';
import { ValidityComponent } from './page/investments-review/validity/validity.component';
import { HomeComponent } from './page/home/home.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReviewpageComponent } from './page/investments-review/reviewpage/reviewpage.component';


@NgModule({
  declarations: [
    MenuManagerOperativeComponent,
    ValidityComponent,
    HomeComponent,
    ReviewpageComponent
  ],
  imports: [
    CommonModule,
    ManagerOperativeRoutingModule,
    MatTooltipModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class ManagerOperativeModule { }
