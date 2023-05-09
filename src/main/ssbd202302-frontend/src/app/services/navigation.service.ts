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

  public redirectToEditOwnAccountPage(): Promise<boolean> {
    return this.router.navigate(['/edit-own-account']);
  }

  public isCurrentlyOnLoginPage(): boolean {
    return this.router.url === '/login';
  }
}
