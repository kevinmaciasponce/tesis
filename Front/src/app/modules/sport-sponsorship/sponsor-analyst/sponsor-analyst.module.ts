import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeModule } from '../../home/home.module';
import { SponsorAnalystRoutingModule } from './sponsor-analyst-routing.module';
import { InconfirmationComponent } from './page/sponsors/inconfirmation/inconfirmation.component';
import { HomeAnalystComponent } from './page/home-analyst/home-analyst.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { CurrencyMaskModule } from 'ng2-currency-mask';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { ShowSponsorComponent } from './page/components/show-sponsor/show-sponsor.component';
import { EditSponsorComponent } from './page/components/edit-sponsor/edit-sponsor.component';
import { MatStepperModule } from '@angular/material/stepper';
import { ComponentsModule } from 'src/app/shared/components/components.module';
import { EditBeneficiaryComponent } from './page/components/edit-beneficiary/edit-beneficiary.component';
import { EditValueComponent } from './page/components/edit-value/edit-value.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';


@NgModule({
  declarations: [
    InconfirmationComponent,
    HomeAnalystComponent,
    ShowSponsorComponent,
    EditSponsorComponent,
    EditBeneficiaryComponent,
    EditValueComponent
  ],
  imports: [
    CommonModule,
    HomeModule,
    SponsorAnalystRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    NgSelectModule,
    ComponentsModule,
    MatCardModule,
    CurrencyMaskModule,
    MatButtonModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatSelectModule,
    MatStepperModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule 
  ]
})
export class SponsorAnalystModule { }
