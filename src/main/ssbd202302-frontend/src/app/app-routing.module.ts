import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginPageComponent} from "./pages/login-page/login-page.component";
import {HomePageComponent} from "./pages/home-page/home-page.component";
import {RegisterPageComponent} from './pages/register-page/register-page.component';
import {AccountPageComponent} from "./pages/account-page/account-page.component";
import {AdminPageComponent} from "./pages/admin-page/admin-page.component";
import {Role} from "./enums/role";
import {AuthGuard} from "./guards/auth.guard";
import {NotFoundPageComponent} from "./pages/not-found-page/not-found-page.component";
import {ForbiddenPageComponent} from "./pages/forbidden-page/forbidden-page.component";
import {ResetPasswordComponent} from './pages/reset-password/reset-password.component';
import {UserAccountPageComponent} from "./pages/user-account-page/user-account-page.component";
import {EditOwnAccountComponent} from "./pages/edit-own-account/edit-own-account.component";
import {EditUserAccountPageComponent} from "./pages/edit-user-account-page/edit-user-account-page.component";
import {AddAccountRoleComponent} from "./pages/add-account-role-page/add-account-role.component";
import {RemoveAccountRolePageComponent} from "./pages/remove-account-role-page/remove-account-role-page.component";
import {ForgotPasswordComponent} from './pages/forgot-password/forgot-password.component';
import {ChangeAccountRolePageComponent} from "./pages/change-account-role-page/change-account-role-page.component";
import {ConfirmEmailChangeComponent} from './pages/confirm-email-change/confirm-email-change.component';
import {ConfirmAccountComponent} from './pages/confirm-account/confirm-account.component';
import {ChangeEmailComponent} from './pages/change-email/change-email.component';
import {ChangeOwnPasswordComponent} from "./pages/change-own-password/change-own-password.component";
import {CreateAccountComponent} from './pages/create-account/create-account.component';
import {ChangePasswordConfirmComponent} from "./pages/change-password-confirm/change-password-confirm.component";
import { GoogleRedirectComponent } from './pages/google-redirect/google-redirect.component';
import { AccountType } from './enums/account.type';
import {GithubRedirectComponent} from "./pages/github-redirect/github-redirect.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomePageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST, Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP]
    }
  },
  {
    path: 'login',
    component: LoginPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'github',
    component: GithubRedirectComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'self',
    component: AccountPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP]
    }
  },
  {
    path: 'edit-own-account',
    component: EditOwnAccountComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP]
    }
  },
  {
    path: 'account/:id/edit',
    component: EditUserAccountPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR]
    }
  },
  {
    path: 'admin',
    component: AdminPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR],
      breadcrumbs: ['Home']
    }
  },
  {
    path: 'register',
    component: RegisterPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'forbidden',
    component: ForbiddenPageComponent
  },
  {
    path: 'confirm',
    component: ConfirmAccountComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'google',
    component: GoogleRedirectComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'change-email',
    component: ChangeEmailComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP],
      exclude: [AccountType.GOOGLE, AccountType.GITHUB]
    }
  },
  {
    path: 'change-password',
    component: ChangeOwnPasswordComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP],
      exclude: [AccountType.GOOGLE, AccountType.GITHUB]
    }
  },
  {
    path: 'create-account',
    component: CreateAccountComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR]
    }
  },
  {
    path: 'change-email/confirm',
    component: ConfirmEmailChangeComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP],
      exclude: [AccountType.GOOGLE, AccountType.GITHUB]
    }
  },
  {
    path: 'change-email/:id',
    component: ChangeEmailComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR],
      exclude: [AccountType.GOOGLE, AccountType.GITHUB]
    }
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'change-password/confirm',
    component: ChangePasswordConfirmComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.GUEST]
    }
  },
  {
    path: 'account/:id',
    component: UserAccountPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR],
      breadcrumbs: ['Home','Admin Panelsad']
    }
  },
  {
    path: 'not-found',
    component: NotFoundPageComponent
  },
  {
    path:'account-role-add/:id',
    component: AddAccountRoleComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR]
    }
  },
  {
    path:'account-role-remove/:id',
    component:RemoveAccountRolePageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR]
    }
  },
  {
    path:'account-role-change/:id',
    component:ChangeAccountRolePageComponent,
    canActivate:[AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR]
    }
  },
  // IMPORTANT: this route must be the last one
  {
    path: '**',
    redirectTo: '/not-found'
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    onSameUrlNavigation: 'reload'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
