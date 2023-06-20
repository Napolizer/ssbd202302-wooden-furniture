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
import {AddAccountRoleComponent} from "./pages/add-account-role-page/add-account-role.component";
import {RemoveAccountRolePageComponent} from "./pages/remove-account-role-page/remove-account-role-page.component";
import {ForgotPasswordComponent} from './pages/forgot-password/forgot-password.component';
import {ChangeAccountRolePageComponent} from "./pages/change-account-role-page/change-account-role-page.component";
import {ConfirmEmailChangeComponent} from './pages/confirm-email-change/confirm-email-change.component';
import {ConfirmAccountComponent} from './pages/confirm-account/confirm-account.component';
import {ChangePasswordConfirmComponent} from "./pages/change-password-confirm/change-password-confirm.component";
import {GoogleRedirectComponent} from './pages/google-redirect/google-redirect.component';
import {AccountType} from './enums/account.type';
import {GithubRedirectComponent} from "./pages/github-redirect/github-redirect.component";
import {EmployeePageComponent} from './pages/employee-page/employee-page.component';
import {ProductPageComponent} from './pages/product-page/product-page.component';
import {SingleProductPageComponent} from './pages/single-product-page/single-product-page.component';
import {ClientPageComponent} from "./pages/client-page/client-page.component";
import {ClientOrdersPageComponent} from "./pages/client-orders-page/client-orders-page.component";
import {ClientRatesPageComponent} from "./pages/client-rates-page/client-rates-page.component";
import {ViewCartPageComponent} from "./pages/view-cart-page/view-cart-page.component";
import { DoneOrdersPageComponent } from './pages/done-orders-page/done-orders-page.component';
import {OrderPageComponent} from "./pages/order-page/order-page.component";
import {ClientOrderPageComponent} from "./pages/client-order-page/client-order-page.component";
import {OrderStatsPageComponent} from "./pages/order-stats-page/order-stats-page.component";
import {ServerErrorPageComponent} from "./pages/server-error-page/server-error-page.component";
import {UnauthorizedPageComponent} from "./pages/unauthorized-page/unauthorized-page.component";
import {
  ProductEditionHistoryPageComponent
} from "./pages/product-edition-history-page/product-edition-history-page.component";

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
    path: 'admin',
    component: AdminPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.ADMINISTRATOR],
      breadcrumbs: ['Home']
    }
  },
  {
    path: 'employee',
    component: EmployeePageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.EMPLOYEE],
    }
  },
  {
    path: 'client',
    component: ClientPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT],
    }
  },
  {
    path: 'client/orders',
    component: ClientOrdersPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT],
    }
  },
  {
    path: 'client/orders/rates',
    component: ClientRatesPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT],
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
    path: 'change-email/confirm',
    component: ConfirmEmailChangeComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT, Role.ADMINISTRATOR, Role.EMPLOYEE, Role.SALES_REP],
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
    path: 'server-error',
    component: ServerErrorPageComponent
  },
  {
    path: 'unauthorized',
    component: UnauthorizedPageComponent
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
  {
    path:'products',
    component:ProductPageComponent
  },
  {
    path:'product/:id',
    component:SingleProductPageComponent
  },
  {
    path:'cart',
    component:ViewCartPageComponent,
    canActivate:[AuthGuard],
    data: {
      roles: [Role.CLIENT]
    }
  },
  {
    path:'orders/done',
    component:DoneOrdersPageComponent,
    canActivate:[AuthGuard],
    data: {
      roles: [Role.SALES_REP]
    }
  },
  {
    path:'orders/stats/:from/:to',
    component:OrderStatsPageComponent,
    canActivate:[AuthGuard],
    data: {
      roles: [Role.SALES_REP]
    }
  },
  {
    path: 'employee/orders/:id',
    component: OrderPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.EMPLOYEE]
    }
  },
  {
    path: 'client/orders/:id',
    component: ClientOrderPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.CLIENT]
    }
  },
  {
    path: 'product/:id/history',
    component: ProductEditionHistoryPageComponent,
    canActivate: [AuthGuard],
    data: {
      roles: [Role.EMPLOYEE]
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
