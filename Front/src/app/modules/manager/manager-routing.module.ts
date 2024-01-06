import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './page/home/home.component';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { IntransitComponent } from './page/investments-review/intransit/intransit.component';
import { ValidityComponent } from './page/investments-review/validity/validity.component';
import { RetartedStateReportComponent } from '../reports/components/retarted-state-report/retarted-state-report.component';
import { ReportComponent } from './page/investments-review/report/report.component';
import { MenuComponentComponent } from 'src/app/shared/components/menu-component/menu-component.component';
import { Chart } from 'chart.js';
import { ChartsManageRetartedReportComponent } from '../reports/components/charts-manage-retarted-report/charts-manage-retarted-report.component';
import { ChartsInvestorDepositComponent } from '../reports/components/charts-investor-deposit/charts-investor-deposit.component';
const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'intransit',
    component: IntransitComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'valid',
    component: ValidityComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'report',
    component: ReportComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'prueba',
    component: ChartsManageRetartedReportComponent,
    canActivate: [LoginGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManagerRoutingModule { }
