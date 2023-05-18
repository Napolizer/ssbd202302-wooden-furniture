import { Component } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {DomSanitizer, Title} from "@angular/platform-browser";
import { MatIconRegistry } from '@angular/material/icon';

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
    private domSanitizer: DomSanitizer
  ) {
    this.matIconRegistry.addSvgIcon('google-logo',
      this.domSanitizer.bypassSecurityTrustResourceUrl('assets/images/google-logo.svg'))
    translate.setDefaultLang('en');
    translate.use(translate.getBrowserLang() || 'en');

    translate.onLangChange.subscribe(() => {
      translate.get('page.title').subscribe((res: string) => {
        this.titleService.setTitle(res);
      });
    });
  }
}
