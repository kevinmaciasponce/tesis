import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectFerroPageComponent } from './pages/project-ferro-page/project-ferro-page.component';
import { ProjectSyfPageComponent } from './pages/project-syf-page/project-syf-page.component';



@NgModule({
  declarations: [
    ProjectFerroPageComponent,
    ProjectSyfPageComponent
  ],
  imports: [
    CommonModule
  ]
})
export class ProjectModule { }
