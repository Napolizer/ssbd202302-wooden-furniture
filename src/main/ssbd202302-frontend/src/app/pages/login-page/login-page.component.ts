import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AuthenticationService} from '../../services/authentication.service';
import {AlertService} from '@full-fledged/alerts';
import {TokenService} from '../../services/token.service';
import {combineLatest, first, map, Subject, takeUntil} from 'rxjs';
import {FormControl, FormGroup} from '@angular/forms';
import {TranslateService} from "@ngx-translate/core";
import { Location } from '@angular/common';
import { NavigationService } from 'src/app/services/navigation.service';
import { AccountType } from 'src/app/enums/account.type';
import {LocalStorageService} from "../../services/local-storage.service";
import {environment} from "../../../environments/environment";
import {DialogService} from "../../services/dialog.service";
import {AccountService} from "../../services/account.service";
import {RefreshTokenService} from "../../services/refresh-token.service";
import {CartService} from "../../services/cart.service";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
      state('loaded', style({
        opacity: 1,
        backgroundColor: "rgba(221, 221, 221, 1)"
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "80px",
        backgroundColor: "rgba(0, 0, 0, 0)"
      })),
      transition('loaded => unloaded', [
        animate('0.5s ease-in')
      ]),
      transition('unloaded => loaded', [
        animate('0.5s ease-in')
      ])
    ]),
  ]
})
export class LoginPageComponent implements OnInit, OnDestroy {
  hide = true;
  loaded = false;
  loginForm = new FormGroup({
    login: new FormControl(''),
    password: new FormControl('')
  });
  destroy = new Subject<boolean>();

  constructor(
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private navigationService: NavigationService,
    private tokenService: TokenService,
    private translate: TranslateService,
    private location: Location,
    private localStorageService: LocalStorageService,
    private refreshTokenService: RefreshTokenService,
    private accountService: AccountService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);

    const state = this.location.getState() as any;
    const keys = Object.keys(state);

    if (keys.length == 2) {
      const code = state[keys[0]];
      const isError = code.startsWith('exception');
      this.translate
        .get(code)
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          if (isError) {
            this.alertService.danger(msg);
          } else {
            this.alertService.success(msg);
          }
        });
    }
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  onLoginClicked(): void {
    this.authenticationService.login(this.loginForm.value['login'] ?? '', this.loginForm.value['password'] ?? '',
      this.translate.getBrowserLang() as string)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: tokens => {
          this.tokenService.saveToken(tokens.token);
          this.tokenService.saveRefreshToken(tokens.refresh_token);
          this.tokenService.saveAccountType(AccountType.NORMAL);
          this.tokenService.saveTimeout(this.tokenService.getRefreshTokenTime()!);
          this.localStorageService.set(environment.currentRoleKey, this.tokenService.getTokenData()?.roles[0] ?? '')
          this.authenticationService.showWarningIfSessionExpired();

          this.translate.get('login.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              this.navigationService.redirectToMainPage();
            });
          this.tokenService.setTimeout(() => {
            this.refreshTokenService.generateNewToken();
          }, this.tokenService.getRefreshTokenTime()!);
          this.accountService.retrieveOwnAccount()
            .subscribe(account => {
              this.localStorageService.set(environment.localeKey, account.locale);
              this.cartService.clearProducts();
              this.cartService.setLocalStorageKey(account.login + "-cart-products");
              this.cartService.getProductsFromLocalStorage();
            });
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              this.alertService.warning(`${data.title}: ${data.message}`);
            });
        }
      })
  }

  onLoginWithGoogle() : void {
    this.authenticationService.getGoogleOauthLink()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: (url) => {
          this.localStorageService.set(environment.currentRoleKey, 'client')
          window.location.href = url;
        },
        error: () => {
          this.navigationService.redirectToNotFoundPage();
        }
      })
  }

  onLoginWithGithubClicked(): void {
    this.authenticationService.getGithubOauthLink()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: (url) => {
          this.localStorageService.set(environment.currentRoleKey, 'client')
          window.location.href = url;
        },
        error: () => {
          void this.navigationService.redirectToNotFoundPage();
        }
      })
  }
}
