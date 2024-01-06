import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { HomeComponent } from './Investor/page/home/home.component';
import { CanceledComponent } from './Investor/page/my-request-menu/canceled/canceled.component';
import { ConfirmationpageComponent } from './Investor/page/my-request-menu/confirmationpage/confirmationpage.component';
import { LiquidatedComponent } from './Investor/page/my-request-menu/liquidated/liquidated.component';
import { SignpageComponent } from './Investor/page/my-request-menu/signpage/signpage.component';
import { TransferpageComponent } from './Investor/page/my-request-menu/transferpage/transferpage.component';
import { ValidpageComponent } from './Investor/page/my-request-menu/validpage/validpage.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'inconfirmation',
    component: ConfirmationpageComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'aproveTransfer',
    component: TransferpageComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'transfer',
    component: SignpageComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'validity',
    component: ValidpageComponent,
    canActivate: [LoginGuard]
  },
  
  {
    path: 'canceled',
    component: CanceledComponent,
    canActivate: [LoginGuard]
  },
  
  {
    path: 'liquidated',
    component: LiquidatedComponent,
    canActivate: [LoginGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AnalystRoutingModule { }
