import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { HttpClientModule } from '@angular/common/http';
import es from '@angular/common/locales/es';
import { RegisterSelectPageComponent } from './modules/register/pages/register-select-page/register-select-page.component';
import { ValidaEmailService } from './services/valida-email.service';
import { ConfigService } from './config.service';
import { NgSelectModule} from '@ng-select/ng-select';
import { HomePageComponent } from './modules/home/pages/home-page/home-page.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './modules/home/pages/footer/footer.component';
import { CookieService } from 'ngx-cookie-service';
import { StorageService } from './shared/service/storage.service';
import { StaticsModule } from './shared/components/statics/statics.module';
import { ContactoComponent } from './modules/home/pages/footer/components/contacto/contacto.component';
import { DepreciationTablesComponent } from './shared/components/depreciation-tables/depreciation-tables.component';
import { ActivationCuentaComponent } from './shared/components/activation-cuenta/activation-cuenta.component';
import { FrequentQuestionsComponent } from './shared/components/statics/frequent-questions/frequent-questions.component';
import { ChartsModule } from 'ng2-charts';
import { ComponentsModule } from './shared/components/components.module';
import { SportSponsorshipModule } from './modules/sport-sponsorship/sport-sponsorship.module';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';

registerLocaleData(es);
export function configServiceFactory(config: ConfigService) {
  return () => config.load();
}
@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    ContactoComponent,
    FooterComponent,
    DepreciationTablesComponent,
    ActivationCuentaComponent,
    FrequentQuestionsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    ReactiveFormsModule,
    MatToolbarModule,
    FormsModule,
    FlexLayoutModule,
    HttpClientModule,
    CommonModule,
    NgSelectModule,
    StaticsModule,
    ChartsModule,
    ComponentsModule,
    SportSponsorshipModule,
    MatDialogModule,
    MatSelectModule
  ],
  providers: [
    ConfigService,
    DatePipe,
    ValidaEmailService,
    CookieService,
    StorageService
  ],
  bootstrap: [AppComponent],
  entryComponents: [RegisterSelectPageComponent,HomePageComponent]
})
export class AppModule { }
