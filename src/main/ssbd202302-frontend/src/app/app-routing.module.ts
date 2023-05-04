import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginPageComponent} from "./pages/login-page/login-page.component";
import {HomePageComponent} from "./pages/home-page/home-page.component";
import { RegisterPageComponent } from './pages/register-page/register-page.component';
import {AccountPageComponent} from "./pages/account-page/account-page.component";
import {AdminPageComponent} from "./pages/admin-page/admin-page.component";
import {Group} from "./enums/group";
import {AuthGuard} from "./guards/auth.guard";
import {NotFoundPageComponent} from "./pages/not-found-page/not-found-page.component";
import { ConfirmPageComponent } from './pages/confirm-page/confirm-page.component';

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
    component: AccountPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.USERS, Group.ADMINISTRATORS, Group.EMPLOYEES, Group.SALES_REPS]
    }
  },
  {
    path: 'admin',
    component: AdminPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.ADMINISTRATORS]
    }
  },
  {
    path: 'register',
    component: RegisterPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.GUESTS]
    }
  },
  {
    path: 'confirm',
    component: ConfirmPageComponent
  },
  {
    path: 'not-found',
    component: NotFoundPageComponent
  },
  // IMPORTANT: this route must be the last one
  {
    path: '**',
    redirectTo: '/not-found'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
