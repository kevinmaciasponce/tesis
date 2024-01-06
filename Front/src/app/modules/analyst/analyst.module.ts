import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalystRoutingModule } from './analyst-routing.module';
import { HomeComponent } from './Investor/page/home/home.component';
import { MenuConteinerComponent } from './Investor/page/components/menu-conteiner/menu-conteiner.component';
import { UserMenuComponent } from './Investor/page/components/user-menu/user-menu.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import {MatTooltipModule} from '@angular/material/tooltip';
import { ConfirmationpageComponent } from './Investor/page/my-request-menu/confirmationpage/confirmationpage.component';
import { TransferpageComponent } from './Investor/page/my-request-menu/transferpage/transferpage.component';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import { AnalystService } from 'src/app/shared/service/analyst.service';
import { NgSelectModule } from '@ng-select/ng-select';
import { ConcilationModalComponent } from './Investor/page/components/concilation-modal/concilation-modal.component';
import { UsersDetailsModalComponent } from './Investor/page/components/users-details-modal/users-details-modal.component';
import { TransferDetailsModalComponent } from './Investor/page/components/transfer-details-modal/transfer-details-modal.component';
import { CargaArchivosModalComponent } from './Investor/page/components/carga-archivos-modal/carga-archivos-modal.component';
import { SignpageComponent } from './Investor/page/my-request-menu/signpage/signpage.component';
import { LoadfilesModalComponent } from './Investor/page/components/loadfiles-modal/loadfiles-modal.component';
import { DeprecationDateModalComponent } from './Investor/page/components/deprecation-date-modal/deprecation-date-modal.component';
import { TransferFundsModalComponent } from './Investor/page/components/transfer-funds-modal/transfer-funds-modal.component';
import { ValidpageComponent } from './Investor/page/my-request-menu/validpage/validpage.component';
import { OutstandingComponent } from './Investor/page/my-request-menu/outstanding/outstanding.component';
import { RegisterpaymentsComponent } from './Investor/page/components/registerpayments/registerpayments.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ProyectsmodalsComponent } from './Investor/page/components/proyectsmodals/proyectsmodals.component';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { LiquidatedComponent } from './Investor/page/my-request-menu/liquidated/liquidated.component';
import { CanceledComponent } from './Investor/page/my-request-menu/canceled/canceled.component';


@NgModule({
  declarations: [
    HomeComponent,
    MenuConteinerComponent,
    UserMenuComponent,
    ConfirmationpageComponent,
    TransferpageComponent,
    ConcilationModalComponent,
    UsersDetailsModalComponent,
    TransferDetailsModalComponent,
    CargaArchivosModalComponent,
    SignpageComponent,
    LoadfilesModalComponent,
    DeprecationDateModalComponent,
    TransferFundsModalComponent,
    ValidpageComponent,
    OutstandingComponent,
    RegisterpaymentsComponent,
    ProyectsmodalsComponent,
    LiquidatedComponent,
    CanceledComponent
  ],
  imports: [
    CommonModule,
    AnalystRoutingModule,
    MatTabsModule,
    NgSelectModule,
    FormsModule,
    CurrencyMaskModule,
    ReactiveFormsModule,
    MatTooltipModule,
    ComponentsModule,
    MatAutocompleteModule,
    NgbModule
  ],
  providers: [
    AnalystService,
    
  ]
})
export class AnalystModule { }
