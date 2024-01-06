import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { HomeComponent } from './pages/home/home.component';
import { ConsultBeneficiaryComponent } from './pages/my-beneficiary/consult-beneficiary/consult-beneficiary.component';
import { EditBeneficiaryComponent } from './pages/my-beneficiary/edit-beneficiary/edit-beneficiary.component';
import { EditSponsorComponent } from './pages/my-beneficiary/edit-sponsor/edit-sponsor.component';
import { RegisterSponsorComponent } from './pages/my-sponsor/register-sponsor/register-sponsor.component';

const routes: Routes = [
  {
    path:'',
    redirectTo:'home',
    pathMatch: 'full'
  },
  {
    path:'home',
    component:HomeComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'complete',
    component: RegisterSponsorComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'show',
    component: ConsultBeneficiaryComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'editBeneficiary',
    component: EditBeneficiaryComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'editSponsor',
    component: EditSponsorComponent,
    canActivate: [LoginGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BeneficiaryRoutingModule { }
