import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActivationCuentaComponent } from 'src/app/shared/components/activation-cuenta/activation-cuenta.component';

import { ContactoComponent } from './pages/footer/components/contacto/contacto.component';

import { HomePageComponent } from './pages/home-page/home-page.component';
import { TutorialComponent } from './pages/tutorial/tutorial.component';

const routes: Routes = [
  {
    path: '',
    component: HomePageComponent
  },
  {
    path: 'contact',
    component: ContactoComponent,
  },
  {
    path: 'activacion/:token',
    component: ActivationCuentaComponent,
  },
  {
    path: 'tutorial',
    component: TutorialComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
