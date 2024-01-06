import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatStepperModule} from '@angular/material/stepper';
import { BeneficiaryRoutingModule } from './beneficiary-routing.module';
import { HomeComponent } from './pages/home/home.component';
import { HomeModule } from '../../home/home.module';
import { RegisterSponsorComponent } from './pages/my-sponsor/register-sponsor/register-sponsor.component';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import { NgSelectModule } from '@ng-select/ng-select';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import {MatTooltipModule} from '@angular/material/tooltip';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { ValueModalComponent } from './pages/components/value-modal/value-modal.component';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { InfoModalComponent } from './pages/components/info-modal/info-modal.component';
import { ConsultBeneficiaryComponent } from './pages/my-beneficiary/consult-beneficiary/consult-beneficiary.component';
import { TorneosModalComponent } from './pages/components/torneos-modal/torneos-modal.component';
import { EditBeneficiaryComponent } from './pages/my-beneficiary/edit-beneficiary/edit-beneficiary.component';
import { EditSponsorComponent } from './pages/my-beneficiary/edit-sponsor/edit-sponsor.component';
import { RewardModalComponent } from './pages/components/reward-modal/reward-modal.component';



@NgModule({
  declarations: [
    HomeComponent,
    RegisterSponsorComponent,
    ValueModalComponent,
    InfoModalComponent,
    ConsultBeneficiaryComponent,
    TorneosModalComponent,
    EditBeneficiaryComponent,
    EditSponsorComponent,
    RewardModalComponent,
  ],
  imports: [
    CommonModule,
    BeneficiaryRoutingModule,
    HomeModule,
    MatStepperModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    NgSelectModule,
    ComponentsModule,
    FormsModule,
    MatTooltipModule,
    CurrencyMaskModule,
    MatCheckboxModule,
    MatSelectModule,
    MatDialogModule,
    MatSnackBarModule
  ]
})
export class BeneficiaryModule { }
