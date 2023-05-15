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

  public currentGroup: Group;
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

  isUserLoggedIn(): boolean {
    return this.authenticationService.isUserLoggedIn();
  }

  isCurrentlyOnLoginPage(): boolean {
    return this.navigationService.isCurrentlyOnLoginPage();
  }

  switchGroup(group: string): void {
    switch(group) {
      case 'ADMINISTRATOR':
        this.currentGroup = Group.ADMINISTRATOR;
        console.log(this.currentGroup);
        break;
      case 'EMPLOYEE':
        this.currentGroup = Group.EMPLOYEE;
        console.log(this.currentGroup);
        break;
      case 'SALES_REP':
        this.currentGroup = Group.SALES_REP;
        console.log(this.currentGroup);
        break;
      case 'CLIENT':
        this.currentGroup = Group.CLIENT;
        console.log(this.currentGroup);
        break;
      case 'GUEST':
        this.currentGroup = Group.GUEST;
        console.log(this.currentGroup);
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
      return this.authenticationService.isUserInGroup(Group.ADMINISTRATOR);
      break;
    case 'EMPLOYEE':
      return this.authenticationService.isUserInGroup(Group.EMPLOYEE);
      break;
    case 'SALES_REP':
      return this.authenticationService.isUserInGroup(Group.SALES_REP);
      break;
    case 'CLIENT':
      return this.authenticationService.isUserInGroup(Group.CLIENT);
      break;
    case 'GUEST':
      return this.authenticationService.isUserInGroup(Group.GUEST);
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
