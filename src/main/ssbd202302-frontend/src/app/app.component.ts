import {Component} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {DomSanitizer, Title} from "@angular/platform-browser";
import { MatIconRegistry } from '@angular/material/icon';
import {TokenService} from "./services/token.service";
import {RefreshTokenService} from "./services/refresh-token.service";
import {LocalStorageService} from "./services/local-storage.service";
import {environment} from "../environments/environment";
import {AuthenticationService} from "./services/authentication.service";
import {AccountService} from "./services/account.service";
import {CartService} from "./services/cart.service";
import {Subject, takeUntil} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent {

  constructor(
    private translate: TranslateService,
    private titleService: Title,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private tokenService: TokenService,
    private refreshTokenService: RefreshTokenService,
    private localStorageService: LocalStorageService,
    private authenticationService: AuthenticationService,
    private accountService: AccountService,
    private cartService: CartService
  ) {

    if (authenticationService.isUserLoggedIn()) {
      translate.use(this.localStorageService.get(environment.localeKey)!);
      this.cartService.getProductsFromLocalStorage();
    } else {
      translate.use(this.translate.getBrowserLang() as string);
    }

    this.matIconRegistry.addSvgIcon('google-logo',
      this.domSanitizer.bypassSecurityTrustResourceUrl('assets/images/google-logo.svg'))
    translate.setDefaultLang('en');

    translate.onLangChange.subscribe(() => {
      translate.get('page.title').subscribe((res: string) => {
        this.titleService.setTitle(res);
      });
    });
    if (Number(this.tokenService.getTimeout()) > Date.now()) {
      setTimeout(() => {
        this.refreshTokenService.generateNewToken();
      }, Number(this.tokenService.getTimeout()) - Date.now())
    } else {
      this.localStorageService.remove(environment.timeoutKey);
    }

    if (this.authenticationService.isUserLoggedIn()) {
      this.authenticationService.showWarningIfSessionExpired();
    }
  }
}
