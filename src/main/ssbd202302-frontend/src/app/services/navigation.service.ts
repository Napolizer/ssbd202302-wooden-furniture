import { Injectable } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(
    private router: Router,
  ) { }

  public redirectToMainPage(): Promise<boolean> {
    return this.router.navigate(['/']);
  }

  public redirectToCurrentPage(): Promise<boolean> {
    return this.router.navigateByUrl(this.router.url);
  }

  public redirectToAdminPage(): Promise<boolean> {
    return this.router.navigate(['/admin']);
  }

  public redirectToEmployeePage(): Promise<boolean> {
    return this.router.navigate(['/employee']);
  }

  public redirectToClientPage(): Promise<boolean> {
    return this.router.navigate(['/client']);
  }

  public redirectToOwnAccountPage(): Promise<boolean> {
    return this.router.navigate(['/self']);
  }

  public redirectToOwnAccountPageWithState(state: any): Promise<boolean> {
    return this.router.navigate(['/self'], {state: state});
  }

  public redirectToAccountPage(id: string): Promise<boolean> {
    return this.router.navigate(['account/' + id])
  }

  public redirectToLoginPage(): Promise<boolean> {
    return this.router.navigate(['/login']);
  }

  public redirectToRegisterPage() {
    return this.router.navigate(['/register']);
  }

  public redirectToLoginPageWithState(state: any): Promise<boolean> {
    return this.router.navigate(['/login'], {state: state});
  }

  public redirectToNotFoundPage(): Promise<boolean> {
    return this.router.navigate(['/not-found']);
  }

  public redirectToForbiddenPage(): Promise<boolean> {
    return this.router.navigate(['/forbidden']);
  }

  public redirectToAddAccountRolesPage(id: string): Promise<boolean> {
    return this.router.navigate(['/account-role-add/' + id]);
  }

  public isCurrentlyOnLoginPage(): boolean {
    return this.router.url === '/login';
  }

  redirectToRemoveAccountRolesPage(id: string): Promise<boolean> {
    return this.router.navigate(['/account-role-remove/' + id])
  }

  redirectToChangeAccountGroupPage(id: string): Promise<boolean> {
    return this.router.navigate(['/account-role-change/' + id])
  }

  public redirectToClientOrdersPage(): Promise<boolean> {
    return this.router.navigate(['/client/orders']);
  }

  redirectToSingleProductPage(id: string): Promise<boolean> {
    return this.router.navigate(['/product/' + id])
  }

  redirectToProductsPage(): Promise<boolean> {
    return this.router.navigate(['/products'])
  }

  public redirectToRatePage(): Promise<boolean> {
    return this.router.navigate(['/client/orders/rates']);
  }
}
