import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterPaidInvoiceComponent } from './components/register-paid-invoice/register-paid-invoice.component';
import { HomeComponent } from './pages/home/home.component';
import { RegisterInvoiceComponent } from './pages/register-invoice/register-invoice.component';
import { RegisterProyectComponent } from './pages/register-proyect/register-proyect.component';

const routes: Routes = [
  {
    path:'',
    redirectTo:'/home',
    pathMatch: 'full',
  },
  {
    path:'home',
    component:HomeComponent,
  },
  {
    path:'register',
    component:RegisterProyectComponent,
  },
  {
    path:'invoice',
    component:RegisterInvoiceComponent,
  },
  {
    path:'invoice/paid',
    component:RegisterPaidInvoiceComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PromoterRoutingModule { }
