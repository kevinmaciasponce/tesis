import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeSportRoutingModule } from './home-sport-routing.module';
import { HomeComponent } from './pages/home/home.component';
import { HomeModule } from '../../home/home.module';
import { SponsorsComponent } from './pages/shared/sponsors/sponsors.component';
import { HomeBannerComponent } from './pages/components/home-banner/home-banner.component';
import { MoreDetailsComponent } from './pages/shared/more-details/more-details.component';


@NgModule({
  declarations: [
    HomeComponent,
    SponsorsComponent,
    HomeBannerComponent,
    MoreDetailsComponent
  ],
  imports: [
    CommonModule,
    HomeSportRoutingModule,
    HomeModule
  ]
})
export class HomeSportModule { }
