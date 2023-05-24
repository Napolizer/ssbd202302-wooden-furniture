import { Component, OnInit, OnDestroy } from '@angular/core';
import {AccountService} from "../../services/account.service";
import {Account} from "../../interfaces/account";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {NavigationService} from "../../services/navigation.service";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BreadcrumbsService } from 'src/app/services/breadcrumbs.service';
import { AccountSearchPreferences } from 'src/app/interfaces/account.search.preferences';
import { AuthenticationService } from 'src/app/services/authentication.service';
@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
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
export class AdminPageComponent implements OnInit {
  allAccounts: Account[] = [];
  ownAccount: Account;
  loading = true;
  breadcrumbsData: string[] = [];
  login: String = '';
  usersPerPage:string;
  sortBy:string;
  orderBy:string;
  inputValue: 23;

  accountSearchPreferences: AccountSearchPreferences = {
    page: 0,
    recordsPerPage: 3,
    sortBy: 'login',
    orderBy: true
  };

  constructor(
    private accountService: AccountService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this.breadcrumbsData = this.breadcrumbsService.getAdminBreadcrumb();
    // this.accountService.retrieveOwnAccountSearchPreferences(0)
    // .subscribe(accountSearchPreferences => {
    //   this.accountSearchPreferences = accountSearchPreferences;
    // })
    console.log(this.accountSearchPreferences)
    this.sortBy = this.accountSearchPreferences.sortBy;
    this.initOrderBy();
    this.usersPerPage = this.accountSearchPreferences.recordsPerPage.toString();
    this.accountService.retrieveAllAccounts()
      .subscribe(accounts => {
        console.log(accounts)
        this.allAccounts = accounts;
        this.loading = false;
      });

  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  redirectToAccountPage(id: string): void {
    void this.navigationService.redirectToAccountPage(id);
  }

  onBackClicked(): void {
    void this.navigationService.redirectToMainPage();
  }


  initOrderBy(): void {
    if(this.accountSearchPreferences.orderBy===true) {
      this.orderBy='asc'
    }
    else if(this.accountSearchPreferences.orderBy===false) {
      this.orderBy='dec'
      }
  }

  changeUsersPerPage(selectedValue: string){
    this.accountSearchPreferences.recordsPerPage=parseInt(selectedValue)
    console.log(this.accountSearchPreferences)
  }

  changeSortBy(selectedValue: string){
    this.accountSearchPreferences.sortBy = selectedValue
    console.log('selectedValue = ' + selectedValue)
    console.log(this.accountSearchPreferences)
  }

  changeOrderBy(selectedValue: string) {
    if(selectedValue==='asc') {
      this.accountSearchPreferences.orderBy = true;
    }
    else if (selectedValue==='dec') {
      this.accountSearchPreferences.orderBy = false;
    }
    console.log('selectedValue = ' + selectedValue)
    console.log(this.accountSearchPreferences)
  }

  incrementValue() {

  }

  decrementValue() {

  }
}
