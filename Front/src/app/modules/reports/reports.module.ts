import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvestorReportComponent } from './components/investor-report/investor-report.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RetartedStateReportComponent } from './components/retarted-state-report/retarted-state-report.component';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { ChartsManageRetartedReportComponent } from './components/charts-manage-retarted-report/charts-manage-retarted-report.component';
import { ChartsModule } from 'ng2-charts';
import { ChartsInvestorDepositComponent } from './components/charts-investor-deposit/charts-investor-deposit.component';



@NgModule({
  declarations: [
    InvestorReportComponent,
    RetartedStateReportComponent,
    ChartsManageRetartedReportComponent,
    ChartsInvestorDepositComponent,
    
  ],
  imports: [
    CommonModule,
    NgSelectModule,
    ReactiveFormsModule,
    FormsModule,
    ComponentsModule,
    ChartsModule
  ],
  exports: [
    InvestorReportComponent,
    RetartedStateReportComponent,
    ChartsManageRetartedReportComponent,
    ChartsInvestorDepositComponent
  ]
})
export class ReportsModule { }
