import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { MoreDetailsComponent } from './pages/shared/more-details/more-details.component';
import { SponsorsComponent } from './pages/shared/sponsors/sponsors.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'sponsor',
    component: SponsorsComponent
  },
  {
    path: 'sponsor/:id',
    component: MoreDetailsComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeSportRoutingModule { }
