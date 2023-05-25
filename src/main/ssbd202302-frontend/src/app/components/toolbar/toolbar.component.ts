import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from "../../services/navigation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {first, Subject, takeUntil} from "rxjs";
import { Role } from 'src/app/enums/role';
import {AccountService} from "../../services/account.service";
import {ChangeLocale} from "../../interfaces/change.locale";
import {DialogService} from "../../services/dialog.service";
import { TokenService } from 'src/app/services/token.service';
import { AccountType } from 'src/app/enums/account.type';
import {Router} from "@angular/router";
import {LocalStorageService} from "../../services/local-storage.service";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.sass']
})
export class ToolbarComponent implements OnInit, OnDestroy {
  destroy = new Subject<boolean>();

  public currentRole: Role;
  private id: number;
  private locale: string;
  private changeLocale: ChangeLocale;
  constructor(
    private alertService: AlertService,
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService,
    private translate: TranslateService,
    private tokenService: TokenService,
    private accountService: AccountService,
    private dialogService: DialogService,
    private router: Router,
    private localStorageService: LocalStorageService
  ) {
  }

  ngOnInit(): void {
    this.router.routeReuseStrategy.shouldReuseRoute = () => { return false; };
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  redirectToMainPage(): void {
    void this.navigationService.redirectToMainPage();
  }

  redirectToAdminPage(): void {
    void this.navigationService.redirectToAdminPage();
  }

  redirectToAccountPage(): void {
    void this.navigationService.redirectToOwnAccountPage();
  }

  redirectToLoginPage(): void {
    void this.navigationService.redirectToLoginPage();
  }

  redirectToRegisterPage(): void {
    void this.navigationService.redirectToRegisterPage();
  }

  redirectToChangeOwnPasswordPage(): void {
    void this.navigationService.redirectToChangeOwnPasswordPage();
  }

  redirectToCreateAccountPage(): void {
    void this.navigationService.redirectToCreateAccountPage();
  }

  isUserLoggedIn(): boolean {
    return this.authenticationService.isUserLoggedIn();
  }

  isUserAdmin(): boolean {
    return this.authenticationService.isCurrentRole(Role.ADMINISTRATOR);
  }

  isCurrentlyOnLoginPage(): boolean {
    return this.navigationService.isCurrentlyOnLoginPage();
  }

  isUserNormalType(): boolean {
    return this.tokenService.getAccountType() === AccountType.NORMAL;
  }

  switchRole(role: string): void {
    switch(role) {
      case 'ADMINISTRATOR':
        this.localStorageService.set(environment.currentRoleKey, Role.ADMINISTRATOR);
        break;
      case 'EMPLOYEE':
        this.localStorageService.set(environment.currentRoleKey, Role.EMPLOYEE);
        break;
      case 'SALES_REP':
        this.localStorageService.set(environment.currentRoleKey, Role.SALES_REP);
        break;
      case 'CLIENT':
        this.localStorageService.set(environment.currentRoleKey, Role.CLIENT);
        break;
      default:
        console.log('default')
        break;
      }
    void this.navigationService.redirectToMainPage();
  }

  isUserInGroup(group: string): boolean {
    switch(group) {
    case 'ADMINISTRATOR':
      return this.authenticationService.isUserInRole(Role.ADMINISTRATOR);
      case 'EMPLOYEE':
      return this.authenticationService.isUserInRole(Role.EMPLOYEE);
      case 'SALES_REP':
      return this.authenticationService.isUserInRole(Role.SALES_REP);
      case 'CLIENT':
      return this.authenticationService.isUserInRole(Role.CLIENT);
      case 'GUEST':
      return this.authenticationService.isUserInRole(Role.GUEST);
      default:
      return false;
    }
  }

  logout(): void {
    this.authenticationService.logout();
    this.translate.get('toolbar.logout.success.message')
      .pipe(first(), takeUntil(this.destroy))
      .subscribe(message => {
        this.alertService.success(message);
        this.redirectToMainPage();
      });
  }

  getCurrentLocale(): string {
    return this.translate.currentLang as string;
  }

  changeLocaleToPolish(): void {
    this.accountService.retrieveOwnAccount(this.authenticationService.getLogin()!)
      .subscribe(account => {
        this.id = account.id
      })
    this.translate.get('dialog.edit.locale')
      .pipe(takeUntil(this.destroy))
      .subscribe(msg => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary')
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe(result => {
            if (result === 'action') {
              this.locale = 'pl';
              this.changeLocale = {
                locale: this.locale
              }
              this.accountService.changeLocale(this.id, this.changeLocale)
                .pipe(first(), takeUntil(this.destroy))
                .subscribe({
                  next: () => {
                    this.translate.use('pl');
                    this.translate.get('change.locale.success')
                      .pipe(takeUntil(this.destroy))
                      .subscribe(msg => {
                        void this.navigationService.redirectToCurrentPage();
                        this.alertService.success(msg);
                      })
                  },
                  error: e => {
                    const title = this.translate.instant('exception.occurred');
                    const message = this.translate.instant(e.error.message || 'exception.unknown');
                    const ref = this.dialogService.openErrorDialog(title, message);
                    ref.afterClosed()
                      .pipe(first(), takeUntil(this.destroy))
                      .subscribe(() => {
                        void this.navigationService.redirectToMainPage();
                      });
                  }
                })
            }
          })
      })
  }

  changeLocaleToEnglish(): void {
    this.accountService.retrieveOwnAccount(this.authenticationService.getLogin()!)
      .subscribe(account => {
        this.id = account.id
      })
    this.translate.get('dialog.edit.locale')
      .pipe(takeUntil(this.destroy))
      .subscribe(msg => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary')
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe(result => {
            if (result === 'action') {
              this.locale = 'en';
              this.changeLocale = {
                locale: this.locale
              }
              this.accountService.changeLocale(this.id, this.changeLocale)
                .pipe(first(), takeUntil(this.destroy))
                .subscribe({
                  next: () => {
                    this.translate.use('en');
                    this.translate.get('change.locale.success')
                      .pipe(takeUntil(this.destroy))
                      .subscribe(msg => {
                        void this.navigationService.redirectToCurrentPage();
                        this.alertService.success(msg);
                      })
                  },
                  error: e => {
                    const title = this.translate.instant('exception.occurred');
                    const message = this.translate.instant(e.error.message || 'exception.unknown');
                    const ref = this.dialogService.openErrorDialog(title, message);
                    ref.afterClosed()
                      .pipe(first(), takeUntil(this.destroy))
                      .subscribe(() => {
                        void this.navigationService.redirectToMainPage();
                      });
                  }
                })
            }
          })
      })
  }

  isCurrentGroup(group: string) : boolean{
    switch(group) {
      case 'ADMINISTRATOR':
        return this.authenticationService.isCurrentRole(Role.ADMINISTRATOR);
      case 'EMPLOYEE':
        return this.authenticationService.isCurrentRole(Role.EMPLOYEE);
      case 'SALES_REP':
        return this.authenticationService.isCurrentRole(Role.SALES_REP);
      case 'CLIENT':
        return this.authenticationService.isCurrentRole(Role.CLIENT);
      default:
        return false;
    }
  }
}
