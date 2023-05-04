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

  public redirectToAccountPage(): Promise<boolean> {
    return this.router.navigate(['/account']);
  }

  public redirectToLoginPage(): Promise<boolean> {
    return this.router.navigate(['/login']);
  }

  public isCurrentlyOnLoginPage(): boolean {
    return this.router.url === '/login';
  }
}
