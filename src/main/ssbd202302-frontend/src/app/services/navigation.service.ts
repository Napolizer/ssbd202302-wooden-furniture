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

  public redirectToOwnAccountPage(): Promise<boolean> {
    return this.router.navigate(['/self']);
  }

  public redirectToOwnAccountPageWithState(state: any): Promise<boolean> {
    return this.router.navigate(['/self'], {state: state});
  }

  public redirectToChangeOwnEmailPage(): Promise<boolean> {
    return this.router.navigate(['/change-email']);
  }

  public redirectToChangeUserEmailPage(id: string): Promise<boolean> {
    return this.router.navigate(['/change-email/' + id]);
  }

  public redirectToChangeOwnPasswordPage(): Promise<boolean> {
    return this.router.navigate(['/change-password']);
  }

  public redirectToAccountPage(id: string): Promise<boolean> {
    return this.router.navigate(['account/' + id])
  }

  public redirectToAccountPageWithState(id: string, state: any): Promise<boolean> {
    return this.router.navigate(['account/' + id], {state: state});
  }

  public redirectToLoginPage(): Promise<boolean> {
    return this.router.navigate(['/login']);
  }

  public redirectToRegisterPage() {
    return this.router.navigate(['/register']);
  }

  public redirectToCreateAccountPage() {
    return this.router.navigate(['/create-account']);
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

  public redirectToEditOwnAccountPage(): Promise<boolean> {
    return this.router.navigate(['/edit-own-account']);
  }

  public redirectToEditUserAccountPage(id: string): Promise<boolean> {
    return this.router.navigate(['account/' + id + '/edit'])
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
}
