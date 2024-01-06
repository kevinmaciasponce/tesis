import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SportSponsorshipRoutingModule } from './sport-sponsorship-routing.module';
import { RegisterModule } from '../register/register.module';
import { HomeModule } from '../home/home.module';
import { HomeSportModule } from './home-sport/home-sport.module';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SportSponsorshipRoutingModule,
    RegisterModule,
    HomeModule,
    HomeSportModule
  ]
})
export class SportSponsorshipModule { }
