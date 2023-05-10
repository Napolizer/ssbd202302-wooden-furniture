import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginPageComponent} from "./pages/login-page/login-page.component";
import {HomePageComponent} from "./pages/home-page/home-page.component";
import {RegisterPageComponent} from './pages/register-page/register-page.component';
import {AccountPageComponent} from "./pages/account-page/account-page.component";
import {AdminPageComponent} from "./pages/admin-page/admin-page.component";
import {Group} from "./enums/group";
import {AuthGuard} from "./guards/auth.guard";
import {NotFoundPageComponent} from "./pages/not-found-page/not-found-page.component";
import {ForbiddenPageComponent} from "./pages/forbidden-page/forbidden-page.component";
import {ConfirmPageComponent} from './pages/confirm-page/confirm-page.component';
import {ResetPasswordComponent} from './pages/reset-password/reset-password.component';
import {UserAccountPageComponent} from "./pages/user-account-page/user-account-page.component";
import {EditOwnAccountComponent} from "./pages/edit-own-account/edit-own-account.component";
import {AddAccountGroupComponent} from "./pages/add-account-group-page/add-account-group.component";
import {RemoveAccountGroupPageComponent} from "./pages/remove-account-group-page/remove-account-group-page.component";
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import {ConfirmEmailPageComponent} from "./pages/confirm-email-page/confirm-email-page.component";

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
    component: LoginPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.GUEST]
    }
  },
  {
    path: 'self',
    component: AccountPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.CLIENT, Group.ADMINISTRATOR, Group.EMPLOYEE, Group.SALES_REP]
    }
  },
  {
    path: 'edit-own-account',
    component: EditOwnAccountComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.CLIENT, Group.ADMINISTRATOR, Group.EMPLOYEE, Group.SALES_REP]
    }
  },
  {
    path: 'admin',
    component: AdminPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.ADMINISTRATOR]
    }
  },
  {
    path: 'register',
    component: RegisterPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.GUEST]
    }
  },
  {
    path: 'forbidden',
    component: ForbiddenPageComponent
  },
  {
    path: 'confirm',
    component: ConfirmPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.GUEST]
    }
  },
  {
    path: 'confirm-email',
    component: ConfirmEmailPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.CLIENT, Group.ADMINISTRATOR, Group.EMPLOYEE, Group.SALES_REP]
    }
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.GUEST]
    }
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.GUEST]
    }
  },
  {
    path: 'account/:id',
    component: UserAccountPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.ADMINISTRATOR]
    }
  },
  {
    path: 'not-found',
    component: NotFoundPageComponent
  },
  {
    path:'account-group-add/:id',
    component: AddAccountGroupComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.ADMINISTRATOR]
    }
  },
  {
    path:'account-group-remove/:id',
    component:RemoveAccountGroupPageComponent,
    canActivate: [AuthGuard],
    data: {
      groups: [Group.ADMINISTRATOR]
    }
  },
  // IMPORTANT: this route must be the last one
  {
    path: '**',
    redirectTo: '/not-found'
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
