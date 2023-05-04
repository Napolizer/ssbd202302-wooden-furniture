import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginPageComponent} from "./pages/login-page/login-page.component";
import {HomePageComponent} from "./pages/home-page/home-page.component";
import { RegisterPageComponent } from './pages/register-page/register-page.component';
import {AccountPageComponent} from "./pages/account-page/account-page.component";
import {AdminPageComponent} from "./pages/admin-page/admin-page.component";
import { ConfirmPageComponent } from './pages/confirm-page/confirm-page.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomePageComponent
  },
  {
    path: 'login',
    component: LoginPageComponent
  },
  {
    path: 'account',
    component: AccountPageComponent
  },
  {
    path: 'admin',
    component: AdminPageComponent
  },
  {
    path: 'register',
    component: RegisterPageComponent
  },
  {
    path: 'confirm',
    component: ConfirmPageComponent
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
