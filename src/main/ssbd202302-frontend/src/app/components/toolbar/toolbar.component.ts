import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from "../../services/navigation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {first, Subject, takeUntil} from "rxjs";
import { Group } from 'src/app/enums/group';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.sass']
})
export class ToolbarComponent implements OnInit, OnDestroy {
  destroy = new Subject<boolean>();

  constructor(
    private alertService: AlertService,
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
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
    return this.authenticationService.isUserInGroup(Group.ADMINISTRATOR);
  }

  isCurrentlyOnLoginPage(): boolean {
    return this.navigationService.isCurrentlyOnLoginPage();
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
}
