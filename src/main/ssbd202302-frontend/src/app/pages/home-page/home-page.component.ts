import { Component, Injector, OnInit } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {AccountService} from "../../services/account.service";
import {AuthenticationService} from "../../services/authentication.service";
import { ThemeSwitcherComponentComponent } from 'src/app/components/theme-switcher-component/theme-switcher-component.component';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.sass']
})
export class HomePageComponent implements OnInit {

  constructor(
    private translate: TranslateService,
    private accountService: AccountService,
    private authenticationService: AuthenticationService,
    private injector: Injector
  ) { }

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.executeThemeSwitcherComponentOnInit();
      this.accountService.retrieveOwnAccount(this.authenticationService.getLogin()!)
        .subscribe(account => {
          this.translate.use(account.locale);
        })
    } else {
      this.translate.use(this.translate.getBrowserLang() || 'en');
    }
  }

  private executeThemeSwitcherComponentOnInit(): void {
    const themeSwitcherComponent = this.injector.get(ThemeSwitcherComponentComponent);
    themeSwitcherComponent.loginInit();
  }

}
