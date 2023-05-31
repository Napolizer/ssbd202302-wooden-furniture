import {Injectable} from "@angular/core";
import {first} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "./dialog.service";
import {TokenService} from "./token.service";
import {AccountService} from "./account.service";
import {AuthenticationService} from "./authentication.service";
import {NavigationService} from "./navigation.service";
import {AlertService} from "@full-fledged/alerts";

@Injectable({
  providedIn: 'root'
})
export class RefreshTokenService {
  constructor(
    private translate: TranslateService,
    private dialogService: DialogService,
    private tokenService: TokenService,
    private accountService: AccountService,
    private authenticationService: AuthenticationService,
    private navigationService: NavigationService,
    private alertService: AlertService
  ) { }

  public generateNewToken(): void {
    this.translate
      .get('dialog.refresh.token')
      .pipe()
      .subscribe(msg => {
        const ref = this.dialogService.openConfirmationDialog(msg, "primary");
        ref
          .afterClosed()
          .pipe(first())
          .subscribe(result => {
            if (result === 'action') {
              if (!this.tokenService.isTokenExpired()) {
                this.accountService.generateTokenFromRefresh(this.tokenService.getRefreshToken()!)
                  .pipe(first())
                  .subscribe(token => {
                    this.tokenService.saveToken(token);
                    this.tokenService.saveTimeout(this.tokenService.getRefreshTokenTime()!);
                    this.tokenService.setTimeout(() => {
                      this.generateNewToken();
                    }, this.tokenService.getRefreshTokenTime()!)
                  })
              } else {
                this.displayTokenExpiredWarning();
                this.authenticationService.logout();
                void this.navigationService.redirectToLoginPage();
              }
            }
          })
      })
  }

  private displayTokenExpiredWarning(): void {
    this.alertService.warning(this.translate.instant('auth.token.expired'));
  }
}
