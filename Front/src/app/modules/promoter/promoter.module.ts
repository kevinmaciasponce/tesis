import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PromoterRoutingModule } from './promoter-routing.module';
import { HomeComponent } from './pages/home/home.component';
import {MatCardModule} from '@angular/material/card';
import { RegisterProyectComponent } from './pages/register-proyect/register-proyect.component';
import {MatStepperModule} from '@angular/material/stepper';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import { NgSelectModule } from '@ng-select/ng-select';
import {MatTableModule} from '@angular/material/table';
import {MatTooltipModule} from '@angular/material/tooltip';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import { RegisterInvoiceComponent } from './pages/register-invoice/register-invoice.component';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { DetailsInvoiceComponent } from './components/details-invoice/details-invoice.component';
import { RegisterPaidInvoiceComponent } from './components/register-paid-invoice/register-paid-invoice.component';
import { HelpComponent } from './components/help/help.component';
import {DragDropModule} from '@angular/cdk/drag-drop';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [
    HomeComponent,
    RegisterProyectComponent,
    RegisterInvoiceComponent,
    DetailsInvoiceComponent,
    RegisterPaidInvoiceComponent,
    HelpComponent
  ],
  imports: [
    CommonModule,
    PromoterRoutingModule,
    MatCardModule,
    MatStepperModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    NgSelectModule,
    MatTableModule,
    MatTooltipModule,
    ComponentsModule,
    CurrencyMaskModule,
    MatPaginatorModule,
    MatSortModule,
    DragDropModule,
    NgbModule
  ]
})
export class PromoterModule { }
