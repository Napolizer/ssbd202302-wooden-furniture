import { Component } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {DomSanitizer, Title} from "@angular/platform-browser";
import { MatIconRegistry } from '@angular/material/icon';
import {TokenService} from "./services/token.service";
import {RefreshTokenService} from "./services/refresh-token.service";
import {LocalStorageService} from "./services/local-storage.service";
import {environment} from "../environments/environment";

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
    private tokenServce: TokenService,
    private refreshTokenService: RefreshTokenService,
    private localStorageService: LocalStorageService
  ) {
    this.matIconRegistry.addSvgIcon('google-logo',
      this.domSanitizer.bypassSecurityTrustResourceUrl('assets/images/google-logo.svg'))
    translate.setDefaultLang('en');

    translate.onLangChange.subscribe(() => {
      translate.get('page.title').subscribe((res: string) => {
        this.titleService.setTitle(res);
      });
    });
    if (Number(this.tokenServce.getTimeout()) > Date.now()) {
      setTimeout(() => {
        this.refreshTokenService.generateNewToken();
      }, Number(this.tokenServce.getTimeout()) - Date.now())
    } else {
      this.localStorageService.remove(environment.timeoutKey);
    }
  }
}
