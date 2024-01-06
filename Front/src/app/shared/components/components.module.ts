import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MenuComponentComponent } from './menu-component/menu-component.component';
import {ExtraOptions, RouterModule} from '@angular/router';
import { LoadingComponent } from './loading/loading.component';
import { MatMenuModule } from '@angular/material/menu';

const routerOptions: ExtraOptions = {
  anchorScrolling: "enabled",
  scrollPositionRestoration: 'enabled'
}

@NgModule({
  declarations: [
    MenuComponentComponent,
    LoadingComponent,
  ],
  imports: [
    CommonModule,RouterModule, MatMenuModule
  ], 
  exports: [
    MenuComponentComponent,
    LoadingComponent,
    
  ]
})
export class ComponentsModule { }
