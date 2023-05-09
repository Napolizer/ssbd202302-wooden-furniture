import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatStepperModule} from '@angular/material/stepper';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {AlertModule} from '@full-fledged/alerts';
import { RegisterPageComponent } from './pages/register-page/register-page.component';
import {MatListModule} from "@angular/material/list";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatMenuModule} from "@angular/material/menu";
import { ToolbarComponent } from './components/toolbar/toolbar.component';
import { AccountPageComponent } from './pages/account-page/account-page.component';
import {MatCardModule} from "@angular/material/card";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import { SpinnerComponent } from './components/spinner/spinner.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { ConfirmPageComponent } from './pages/confirm-page/confirm-page.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ErrorDialogComponent } from './components/error-dialog/error-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import { NotFoundPageComponent } from './pages/not-found-page/not-found-page.component';
import { ForbiddenPageComponent } from './pages/forbidden-page/forbidden-page.component';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { UserAccountPageComponent } from './pages/user-account-page/user-account-page.component';
import {DatePipe} from "@angular/common";
import { EditOwnAccountComponent } from './pages/edit-own-account/edit-own-account.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    HomePageComponent,
    RegisterPageComponent,
    HomePageComponent,
    ToolbarComponent,
    AccountPageComponent,
    SpinnerComponent,
    AdminPageComponent,
    ConfirmPageComponent,
    ErrorDialogComponent,
    ConfirmationDialogComponent,
    NotFoundPageComponent,
    ForbiddenPageComponent,
    ConfirmPageComponent,
    ResetPasswordComponent,
    UserAccountPageComponent,
    EditOwnAccountComponent,
    ForgotPasswordComponent
  ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatInputModule,
        MatSlideToggleModule,
        MatFormFieldModule,
        MatStepperModule,
        MatIconModule,
        MatButtonModule,
        MatSelectModule,
        FormsModule,
        AlertModule.forRoot({maxMessages: 8, timeout: 5000, positionX: 'right', positionY: 'top'}),
        ReactiveFormsModule,
        MatListModule,
        MatSidenavModule,
        MatExpansionModule,
        MatToolbarModule,
        MatTooltipModule,
        MatMenuModule,
        MatCardModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        MatProgressSpinnerModule,
        MatDialogModule
    ],
  providers: [
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
    return new TranslateHttpLoader(http);
}
