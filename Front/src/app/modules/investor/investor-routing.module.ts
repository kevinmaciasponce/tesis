import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { CompleteComponent } from './page/new_investment/complete/complete.component';
import { ConfirmationpageComponent } from './page/my-investments/confirmationpage/confirmationpage.component';
import { HomeComponent } from './page/home/home.component';
import { ProcesspageComponent } from './page/my-investments/processpage/processpage.component';
import { LoadComponent } from './page/new_investment/load/load.component';
import { InvestmentComponent } from './page/new_investment/investment/investment.component';
import { IndicateComponent } from './page/new_investment/indicate/indicate.component';
import { IntransitComponent } from './page/my-investments/intransit/intransit.component';
import { ValidityComponent } from './page/my-investments/validity/validity.component';
import { ReportComponent } from './page/my-investments/report/report.component';
import { ChartsInvestorDepositComponent } from '../reports/components/charts-investor-deposit/charts-investor-deposit.component';
import { RegisterGuard } from 'src/app/shared/guards/register.guard';


const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'indicate',
    component: IndicateComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'complete',
    component: CompleteComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'load',
    component: LoadComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'investement',
    component: InvestmentComponent,
    canActivate: [LoginGuard]
  },
 
  {
    path: 'inprocess',
    component: ProcesspageComponent,
    canActivate: [LoginGuard]
  },
  
   {
    path: 'inconfirmation',
    component: ConfirmationpageComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'intransit',
    component: IntransitComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'validity',
    component:  ValidityComponent,
    canActivate: [LoginGuard]
  }
  ,
  {
    path: 'report',
    component:  ReportComponent,
    canActivate: [LoginGuard]
  } ,
  {
    path: 'prueba',
    component:  ChartsInvestorDepositComponent,
    canActivate: [LoginGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InvestorRoutingModule { }
