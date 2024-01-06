import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgCircleProgressModule } from 'ng-circle-progress';
import { ProyectComponent } from './proyect/proyect.component';
import { AppRoutingModule } from 'src/app/app-routing.module';


@NgModule({
  declarations: [
    ProyectComponent,
  ],
  imports: [
    CommonModule,
    AppRoutingModule,
    NgCircleProgressModule.forRoot({
      radius: 60,
      space: -10,
      outerStrokeGradient: true,
      outerStrokeWidth: 10,
      outerStrokeColor: "#3176BA",
      outerStrokeGradientStopColor: "#004889",
      innerStrokeColor: "transparent",
      innerStrokeWidth: 10,
      animateTitle: false,
      animationDuration: 1000,
      showUnits: true,
      showBackground: false,
      clockwise: false,
      startFromZero: false,
      showSubtitle: false,
      lazy: false
    }),
  ]
})
export class StaticsModule { }
