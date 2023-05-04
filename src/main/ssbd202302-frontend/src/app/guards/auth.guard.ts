import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import {AlertService} from "@full-fledged/alerts";
import {AuthenticationService} from "../services/authentication.service";
import {TokenService} from "../services/token.service";
import {NavigationService} from "../services/navigation.service";
import {Group} from "../enums/group";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private navigationService: NavigationService,
    private tokenService: TokenService
  ) {}

  private displayAuthorizationWarning(): void {
    this.alertService.warning('You are not authorized to access this page');
  }

  private displayAuthenticationWarning(): void {
    this.alertService.warning('You are not authenticated');
  }

  private displayTokenExpiredWarning(): void {
    this.alertService.warning('Your token has expired');
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (route.data['groups']) {
      console.log('login')
      console.log(this.authenticationService.getLogin())
      console.log('groups')
      console.log(this.authenticationService.getGroups())
      console.log('token')
      console.log(this.tokenService.getTokenData())
      if (this.authenticationService.getLogin() === null) {
        this.displayAuthenticationWarning();
        void this.navigationService.redirectToLoginPage();
        return false;
      }
      if (this.tokenService.isTokenExpired()) {
        this.displayTokenExpiredWarning();
        void this.navigationService.redirectToLoginPage();
        return false;
      }
      for (const group of route.data['groups']) {
        if (this.authenticationService.isUserInGroup(group)) {
          return true;
        }
      }
      this.displayAuthorizationWarning();
      void this.navigationService.redirectToMainPage();
      return false;
    }
    return true;
  }

}
