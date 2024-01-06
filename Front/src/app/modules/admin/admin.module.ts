import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatPaginatorModule} from '@angular/material/paginator';
import { AdminRoutingModule } from './admin-routing.module';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SponsorComponent } from './pages/sponsor/sponsor.component';
import { AdminComponent } from './pages/admin/admin.component';
import { AcountComponent } from './pages/acount/acount.component';
import { InvestorComponent } from './pages/investor/investor.component';
import {MatTabsModule} from '@angular/material/tabs';
import {MatInputModule} from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSortModule } from '@angular/material/sort';
import { TablesaddComponent } from './components/tablesadd/tablesadd.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { CreateaccountComponent } from './components/createaccount/createaccount.component';
import { AssingroleComponent } from './components/assingrole/assingrole.component';
import { PromoterComponent } from './pages/promoter/promoter.component';
import { AcountModalComponent } from './components/acount-modal/acount-modal.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { RoleModalComponent } from './components/role-modal/role-modal.component';
import {MatButtonModule} from '@angular/material/button';
import { NgSelectModule } from '@ng-select/ng-select';
import { ParametersModalComponent } from './components/parameters-modal/parameters-modal.component';

@NgModule({
  declarations: [
    DashboardComponent,
    SponsorComponent,
    AdminComponent,
    AcountComponent,
    InvestorComponent,
    TablesaddComponent,
    CreateaccountComponent,
    AssingroleComponent,
    PromoterComponent,
    AcountModalComponent,
    RoleModalComponent,
    ParametersModalComponent
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    MatSidenavModule,
    MatTooltipModule,
    MatTabsModule,
    MatPaginatorModule,
    MatInputModule,
    MatTableModule,
    MatFormFieldModule,
    MatSortModule,
    FormsModule,
    ComponentsModule,
    MatCardModule,
    MatDividerModule,
    MatProgressBarModule,
    MatCheckboxModule,
    MatIconModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatButtonModule,
    NgSelectModule
  ]
})
export class AdminModule { }
