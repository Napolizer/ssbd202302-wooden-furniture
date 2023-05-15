import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from "../../services/navigation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {first, Subject, takeUntil} from "rxjs";
import { Role } from 'src/app/enums/role';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.sass']
})
export class ToolbarComponent implements OnInit, OnDestroy {
  destroy = new Subject<boolean>();

  public currentRole: Role;
  constructor(
    private alertService: AlertService,
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService,
    private translate: TranslateService
  ) {
  }

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
    return this.authenticationService.isUserInRole(Role.ADMINISTRATOR);
  }

  isCurrentlyOnLoginPage(): boolean {
    return this.navigationService.isCurrentlyOnLoginPage();
  }

  switchRole(role: string): void {
    switch(role) {
      case 'ADMINISTRATOR':
        this.currentRole = Role.ADMINISTRATOR;
        console.log(this.currentRole);
        break;
      case 'EMPLOYEE':
        this.currentRole = Role.EMPLOYEE;
        console.log(this.currentRole);
        break;
      case 'SALES_REP':
        this.currentRole = Role.SALES_REP;
        console.log(this.currentRole);
        break;
      case 'CLIENT':
        this.currentRole = Role.CLIENT;
        console.log(this.currentRole);
        break;
      case 'GUEST':
        this.currentRole = Role.GUEST;
        console.log(this.currentRole);
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
      break;
    case 'EMPLOYEE':
      return this.authenticationService.isUserInRole(Role.EMPLOYEE);
      break;
    case 'SALES_REP':
      return this.authenticationService.isUserInRole(Role.SALES_REP);
      break;
    case 'CLIENT':
      return this.authenticationService.isUserInRole(Role.CLIENT);
      break;
    case 'GUEST':
      return this.authenticationService.isUserInRole(Role.GUEST);
      break;
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
}
