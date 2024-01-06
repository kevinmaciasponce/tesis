import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { HttpClientModule } from '@angular/common/http';
import { HomeRoutingModule } from './home-routing.module';
import { AboutUsComponent } from './pages/about-us/about-us.component';
import { ChoiseOportunityComponent } from './pages/choise-oportunity/choise-oportunity.component';
import { HowToInvestComponent } from './pages/how-to-invest/how-to-invest.component';
import { SimulatorComponent } from './pages/simulator/simulator.component';
import { ProfitsComponent } from './pages/profits/profits.component';
import { HomeComponent } from './pages/home/home.component';
import { HowWeDidComponent } from './pages/how-we-did/how-we-did.component';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SportSponsorComponent } from './pages/sport-sponsor/sport-sponsor.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { TutorialComponent } from './pages/tutorial/tutorial.component';
@NgModule({
  declarations: [
    HomePageComponent,
    AboutUsComponent,
    HomeComponent,
    HowToInvestComponent,
    ProfitsComponent,
    HowWeDidComponent,
    ChoiseOportunityComponent,
    SimulatorComponent,
    SportSponsorComponent,
    TutorialComponent,
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    HttpClientModule,
    CurrencyMaskModule,
    FormsModule,
    ReactiveFormsModule,
    MatTooltipModule,
    MatSnackBarModule
  ],exports:[
    SportSponsorComponent
  ]
})
export class HomeModule { }
