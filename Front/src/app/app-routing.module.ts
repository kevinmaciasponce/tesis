import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FrequentQuestionsComponent } from './shared/components/statics/frequent-questions/frequent-questions.component';
import { ProyectComponent } from './shared/components/statics/proyect/proyect.component';
import { RegisterGuard } from './shared/guards/register.guard';

const routes: Routes = [
  { path: '',loadChildren: () => import('./modules/home/home.module').then(m => m.HomeModule) },
  { path: 'register', loadChildren: () => import('./modules/register/register.module').then(m => m.RegisterModule) },
  //Modulos con Login
  { path: 'investor', loadChildren: () => import('./modules/investor/investor.module').then(m => m.InvestorModule),canActivate:[RegisterGuard]},
  { path: 'analyst', loadChildren: () => import('./modules/analyst/analyst.module').then(m => m.AnalystModule),canActivate:[RegisterGuard] },
  { path: 'manager', loadChildren: () => import('./modules/manager/manager.module').then(m => m.ManagerModule),canActivate:[RegisterGuard]},
  { path: 'promoter', loadChildren: () => import('./modules/promoter/promoter.module').then(m => m.PromoterModule)},
  { path: 'sport-sponsorship', loadChildren: () => import('./modules/sport-sponsorship/sport-sponsorship.module').then(m => m.SportSponsorshipModule) },
  { path: 'managerOper', loadChildren: () => import('./modules/manager-operative/manager-operative.module').then(m => m.ManagerOperativeModule),canActivate:[RegisterGuard] },
  { path: 'sponsor-analyst',loadChildren: () => import('./modules/sport-sponsorship/sponsor-analyst/sponsor-analyst.module').then(m => m.SponsorAnalystModule),canActivate:[RegisterGuard] },
  { path: 'admin', loadChildren: () => import('./modules/admin/admin.module').then(m => m.AdminModule),canActivate:[RegisterGuard] },
  { path: 'project', loadChildren: () => import('./modules/project/project.module').then(m => m.ProjectModule) },
  { path: 'statics/proyects',component: ProyectComponent},
  { path: 'statics/questions',component: FrequentQuestionsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    anchorScrolling: "enabled",
    scrollPositionRestoration: 'enabled', 
    scrollOffset: [0, 60]// Add options right here
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
