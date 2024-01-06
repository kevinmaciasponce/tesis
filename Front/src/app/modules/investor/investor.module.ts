import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvestorRoutingModule } from './investor-routing.module';
import { HomeComponent } from './page/home/home.component';
import { StatusCardsComponent } from './page/components/status-cards/status-cards.component'
import { MenuInvestorComponent } from './page/components/menu-investor/menu-investor.component';
import { AmortizacionService } from 'src/app/shared/service/amortizacion.service';
import { NgSelectModule } from '@ng-select/ng-select';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CompleteComponent } from './page/new_investment/complete/complete.component';
import { ProcesspageComponent } from './page/my-investments/processpage/processpage.component';
import { ConfirmationpageComponent } from './page/my-investments/confirmationpage/confirmationpage.component';
import { IndicateComponent } from './page/new_investment/indicate/indicate.component';
import { LoadComponent } from './page/new_investment/load/load.component';
import { InvestmentComponent } from './page/new_investment/investment/investment.component';
import { PdfinprocessComponent } from './page/pdf/pdfinprocess/pdfinprocess.component';
import { IntransitComponent } from './page/my-investments/intransit/intransit.component';
import { MenuConteinerComponent } from './page/components/menu-conteiner/menu-conteiner.component';
import { ModalmasdetallesAHComponent } from 'src/app/shared/components/modalmasdetallesAH/modalmasdetalleAH.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ValidityComponent } from './page/my-investments/validity/validity.component';
import { ReportsModule } from '../reports/reports.module';
import { ReportComponent } from './page/my-investments/report/report.component';
import { ComponentsModule } from 'src/app/shared/components/components.module';

@NgModule({
  declarations: [
    HomeComponent,
    StatusCardsComponent,
    MenuConteinerComponent,
    ValidityComponent,
    IndicateComponent,
    CompleteComponent,
    LoadComponent,
    InvestmentComponent,
    MenuInvestorComponent,
    ProcesspageComponent,
    ConfirmationpageComponent,
    ModalmasdetallesAHComponent,
    PdfinprocessComponent,
    IntransitComponent,
    ReportComponent
  ],
  imports: [
    MatTabsModule,
    MatTooltipModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    InvestorRoutingModule,
    NgSelectModule,
    CurrencyMaskModule,
    ReportsModule,
    ComponentsModule
  ],
  providers: [
    AmortizacionService
  ]
})
export class InvestorModule { }
