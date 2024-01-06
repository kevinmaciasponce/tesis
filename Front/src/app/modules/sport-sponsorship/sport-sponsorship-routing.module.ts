import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: 'register',loadChildren: () => import('./register/register.module').then(m => m.RegisterModule) },
  { path: '',loadChildren: () => import('./home-sport/home-sport.module').then(m => m.HomeSportModule) },
  { path: 'agent',loadChildren: () => import('./beneficiary/beneficiary.module').then(m => m.BeneficiaryModule) },
  //{ path: 'sponsor-analyst/home',loadChildren: () => import('./sponsor-analyst/sponsor-analyst.module').then(m => m.SponsorAnalystModule) },
  
]

@NgModule({
  imports: [RouterModule.forChild(routes),

],
  exports: [RouterModule]
})

export class SportSponsorshipRoutingModule { }
