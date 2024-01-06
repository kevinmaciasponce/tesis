import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { HomeComponent } from './page/home/home.component';
import { ReviewpageComponent } from './page/investments-review/reviewpage/reviewpage.component';
import { ValidityComponent } from './page/investments-review/validity/validity.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'valid',
    component: ValidityComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'intransit',
    component: ReviewpageComponent,
    canActivate: [LoginGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManagerOperativeRoutingModule { }
