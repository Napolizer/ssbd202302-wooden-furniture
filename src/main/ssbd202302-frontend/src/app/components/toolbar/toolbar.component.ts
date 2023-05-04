import { Component, OnInit } from '@angular/core';
import {NavigationService} from "../../services/navigation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {AlertService} from "@full-fledged/alerts";

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.sass']
})
export class ToolbarComponent implements OnInit {

  constructor(
    private alertService: AlertService,
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
  }

  redirectToMainPage(): void {
    void this.navigationService.redirectToMainPage();
  }

  redirectToAdminPage(): void {
    void this.navigationService.redirectToAdminPage();
  }

  redirectToAccountPage(): void {
    void this.navigationService.redirectToAccountPage();
  }

  redirectToLoginPage(): void {
    void this.navigationService.redirectToLoginPage();
  }

  isUserLoggedIn(): boolean {
    return this.authenticationService.isUserLoggedIn();
  }

  isCurrentlyOnLoginPage(): boolean {
    return this.navigationService.isCurrentlyOnLoginPage();
  }

  logout(): void {
    this.authenticationService.logout();
    this.alertService.success('You have been logged out');
    this.redirectToMainPage();
  }
}
