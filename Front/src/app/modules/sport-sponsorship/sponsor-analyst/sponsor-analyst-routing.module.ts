import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { RegisterGuard } from 'src/app/shared/guards/register.guard';
import { EditBeneficiaryComponent } from './page/components/edit-beneficiary/edit-beneficiary.component';
import { EditSponsorComponent } from './page/components/edit-sponsor/edit-sponsor.component';
import { ShowSponsorComponent } from './page/components/show-sponsor/show-sponsor.component';
import { HomeAnalystComponent } from './page/home-analyst/home-analyst.component';

const routes: Routes = [
  {
    path:'',
    redirectTo:'/home',
    pathMatch: 'full',
  },
  {
    path:'home',
    component:HomeAnalystComponent,
    canActivate: [LoginGuard]
  },
  {
    path:'edit',
    component:ShowSponsorComponent,
    canActivate: [LoginGuard]
  },
  {
    path:'edit-sponsor',
    component:EditSponsorComponent,
    canActivate: [LoginGuard]
  },
  {
    path:'edit-beneficiary',
    component:EditBeneficiaryComponent,
    canActivate: [LoginGuard]
  },
  { path: '**', component: HomeAnalystComponent ,
  canActivate: [LoginGuard]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SponsorAnalystRoutingModule { }
