import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import {AlertService} from "@full-fledged/alerts";
import {AuthenticationService} from "../services/authentication.service";
import {TokenService} from "../services/token.service";
import {NavigationService} from "../services/navigation.service";
import { Group } from '../enums/group';
import {TranslateService} from "@ngx-translate/core";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private navigationService: NavigationService,
    private tokenService: TokenService,
    private translate: TranslateService
  ) {}

  private displayAuthenticationWarning(): void {
    this.alertService.warning(this.translate.instant('auth.not.authenticated'));
  }

  private displayTokenExpiredWarning(): void {
    this.alertService.warning(this.translate.instant('auth.token.expired'));
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (route.data['groups']) {
      if (route.data['groups'].includes(Group.GUEST) && this.authenticationService.isUserInGroup(Group.GUEST)) {
        return true;
      }
      if (this.authenticationService.getLogin() === null) {
        this.displayAuthenticationWarning();
        void this.navigationService.redirectToLoginPage();
        return false;
      }
      if (this.tokenService.isTokenExpired()) {
        this.displayTokenExpiredWarning();
        this.authenticationService.logout()
        void this.navigationService.redirectToLoginPage();
        return false;
      }
      for (const group of route.data['groups']) {
        if (this.authenticationService.isUserInGroup(group)) {
          return true;
        }
      }
      void this.navigationService.redirectToForbiddenPage();
      return false;
    }
    return true;
  }

}
