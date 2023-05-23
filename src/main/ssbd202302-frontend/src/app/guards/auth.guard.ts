import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import {AlertService} from "@full-fledged/alerts";
import {AuthenticationService} from "../services/authentication.service";
import {TokenService} from "../services/token.service";
import {NavigationService} from "../services/navigation.service";
import { Role } from '../enums/role';
import {TranslateService} from "@ngx-translate/core";
import { Constants } from '../utils/constants';

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
    if (route.data['roles']) {

      if (state.url !== Constants.CHANGE_PASSWORD_URL) {
        if (this.authenticationService.getLogin()) {
          this.authenticationService
          .isUserForcedToChangePassword()
          .subscribe((result) => {
            if (result) {
              this.alertService.danger(this.translate.instant('change.password.force.message'))
              this.navigationService.redirectToChangeOwnPasswordPage();
            }
          });
        }
      }

      if (route.data['roles'].includes(Role.GUEST) && this.authenticationService.isUserInRole(Role.GUEST)) {
        return true;
      }

      if (route.data['exclude']) {
        if (route.data['exclude'].includes(this.tokenService.getAccountType())) {
          this.navigationService.redirectToForbiddenPage();
          return false;
        }
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
      for (const role of route.data['roles']) {
        if (this.authenticationService.isUserInRole(role)) {
          return true;
        }
      }
      void this.navigationService.redirectToForbiddenPage();
      return false;
    }
    return true;
  }

}
