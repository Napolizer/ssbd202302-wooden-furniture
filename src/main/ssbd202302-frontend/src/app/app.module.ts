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
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ErrorDialogComponent } from './components/error-dialog/error-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatCheckboxModule} from '@angular/material/checkbox';
import { NotFoundPageComponent } from './pages/not-found-page/not-found-page.component';
import { ForbiddenPageComponent } from './pages/forbidden-page/forbidden-page.component';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { UserAccountPageComponent } from './pages/user-account-page/user-account-page.component';
import {DatePipe} from "@angular/common";
import { NgxDropzoneModule } from 'ngx-dropzone';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { AddAccountRoleComponent } from './pages/add-account-role-page/add-account-role.component';
import { RemoveAccountRolePageComponent } from './pages/remove-account-role-page/remove-account-role-page.component';
import { ConfirmEmailChangeComponent } from './pages/confirm-email-change/confirm-email-change.component';
import { ConfirmAccountComponent } from './pages/confirm-account/confirm-account.component';
import { ChangeAccountRolePageComponent } from './pages/change-account-role-page/change-account-role-page.component';
import { DigitOnlyDirective } from './utils/digit.only.directive';
import { ChangeOwnPasswordComponent } from './pages/change-own-password/change-own-password.component';
import { CreateAccountComponent } from './pages/create-account/create-account.component';
import { ChangePasswordConfirmComponent } from './pages/change-password-confirm/change-password-confirm.component';
import { GithubRedirectComponent } from './pages/github-redirect/github-redirect.component';
import { GoogleRedirectComponent } from './pages/google-redirect/google-redirect.component';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import { ThemeSwitcherComponentComponent } from './components/theme-switcher-component/theme-switcher-component.component';
import {MatTableModule} from "@angular/material/table";
import { AdminActionsMenuComponent } from './components/admin-actions-menu/admin-actions-menu.component';
import {MatRippleModule} from "@angular/material/core";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import { AddRoleComponent } from './components/add-role/add-role.component';
import { ChangeRoleComponent } from './components/change-role/change-role.component';
import { RemoveRoleComponent } from './components/remove-role/remove-role.component';
import { EditAccountComponent } from './components/edit-account/edit-account.component';
import { ChangeEmailComponent } from './components/change-email/change-email.component';
import { AddProductGroupComponent } from './components/add-product-group/add-product-group.component';
import { EmployeePageComponent } from './pages/employee-page/employee-page.component';
import { ProductPageComponent } from './pages/product-page/product-page.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { ClientPageComponent } from './pages/client-page/client-page.component';
import { ClientOrdersPageComponent } from './pages/client-orders-page/client-orders-page.component';
import { AddProductComponent } from './add-product/add-product.component';
import { SingleProductPageComponent } from './pages/single-product-page/single-product-page.component';
import { EditProductComponent } from './components/edit-product/edit-product.component';
import { ClientRatesPageComponent } from './pages/client-rates-page/client-rates-page.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {MatBadgeModule} from "@angular/material/badge";
import { ViewCartPageComponent } from './pages/view-cart-page/view-cart-page.component';
import { EmployeeActionsMenuComponent } from './components/employee-actions-menu/employee-actions-menu.component';
import { ChangeOrderStateComponent } from './components/change-order-state/change-order-state.component';

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
    ConfirmAccountComponent,
    ErrorDialogComponent,
    ConfirmationDialogComponent,
    NotFoundPageComponent,
    ForbiddenPageComponent,
    ResetPasswordComponent,
    UserAccountPageComponent,
    ForgotPasswordComponent,
    AddAccountRoleComponent,
    RemoveAccountRolePageComponent,
    ConfirmEmailChangeComponent,
    ChangeEmailComponent,
    ChangeAccountRolePageComponent,
    ChangeOwnPasswordComponent,
    DigitOnlyDirective,
    CreateAccountComponent,
    GithubRedirectComponent,
    CreateAccountComponent,
    ChangePasswordConfirmComponent,
    GoogleRedirectComponent,
    ThemeSwitcherComponentComponent,
    AdminActionsMenuComponent,
    AddRoleComponent,
    ChangeRoleComponent,
    RemoveRoleComponent,
    EditAccountComponent,
    AddProductGroupComponent,
    EmployeePageComponent,
    AddProductComponent,
    ProductPageComponent,
    ClientPageComponent,
    ClientOrdersPageComponent,
    SingleProductPageComponent,
    EditProductComponent,
    ViewCartPageComponent,
    EmployeeActionsMenuComponent,
    ChangeOrderStateComponent,
    ClientRatesPageComponent,
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
    NgxDropzoneModule,
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
    MatCheckboxModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    MatProgressSpinnerModule,
    MatDialogModule,
    MatAutocompleteModule,
    MatTableModule,
    MatRippleModule,
    MatPaginatorModule,
    MatSortModule,
    MatGridListModule,
    NgbModule,
    MatBadgeModule
  ],
  providers: [
    DatePipe,
    ThemeSwitcherComponentComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
    return new TranslateHttpLoader(http);
}
