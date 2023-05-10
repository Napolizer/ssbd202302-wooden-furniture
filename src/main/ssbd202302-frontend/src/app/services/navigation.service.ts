import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(
    private router: Router
  ) { }

  public redirectToMainPage(): Promise<boolean> {
    return this.router.navigate(['/']);
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

  public redirectToAddAccountGroupsPage(id: string): Promise<boolean> {
    return this.router.navigate(['/account-group-add/' + id]);
  }

  public isCurrentlyOnLoginPage(): boolean {
    return this.router.url === '/login';
  }

  redirectToRemoveAccountGroupsPage(id: string): Promise<boolean> {
    return this.router.navigate(['/account-group-remove/' + id])
  }
}
