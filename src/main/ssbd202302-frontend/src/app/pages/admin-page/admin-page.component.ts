import { Component, OnInit, OnDestroy } from '@angular/core';
import {AccountService} from "../../services/account.service";
import {Account} from "../../interfaces/account";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {NavigationService} from "../../services/navigation.service";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BreadcrumbsService } from 'src/app/services/breadcrumbs.service';
import {FullName} from "../../interfaces/fullName";
import {map} from "rxjs";

import { AccountSearchSettings } from 'src/app/interfaces/account.search.settings';
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
  accounts: Account[] = [];
  ownAccount: Account;
  loading = true;
  breadcrumbsData: string[] = [];
  fullName: string = '';
  fullNames: string[] = [];
  login: String = '';
  usersPerPage:string;
  sortBy:string;
  orderBy:string;
  inputValue: 23;

  accountSearchSettings: AccountSearchSettings = {
    searchPage: 1,
    displayedAccounts: 10,
    searchKeyword: '',
    sortBy: 'Login',
    sortAscending: true
  };

  constructor(
    private accountService: AccountService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    // this.accountService.retrieveListOfAccountsWithGivenSearchSettings(string: str)
    // .subscribe(accountSearchPreferences => {
    //   this.accountSearchPreferences = accountSearchPreferences;
    // })
    console.log(this.accountSearchSettings)
    this.sortBy = this.accountSearchSettings.sortBy;
    this.initOrderBy();
    this.usersPerPage = this.accountSearchSettings.displayedAccounts.toString();
    this.accountService.retrieveAllAccounts()
      .subscribe(accounts => {
        console.log(accounts)
        this.allAccounts = accounts;
        this.accounts = this.allAccounts;
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

  onSearchClicked(): void {
    this.loading = true;
    if (this.fullName === '') {
      this.accounts = this.allAccounts
      this.loading = false
    } else {
      this.accountService.findAccountsByFullName(this.fullName)
        .subscribe(accounts => {
          this.accounts = accounts;
          this.loading = false;
        });
    }
  }

  autoCompleteFullNames(event: Event) {
    const phrase = (event.target as HTMLInputElement).value;
    this.accountService.autoCompleteFullNames(phrase)
      .pipe(
        map((fullNames: FullName[]) => fullNames.map((fullName: FullName) => fullName.fullName))
      )
      .subscribe((fullNames: string[]) => {
        this.fullNames = fullNames;
      })
  }


  initOrderBy(): void {
    if(this.accountSearchSettings.sortAscending===true) {
      this.orderBy='asc'
    }
    else if(this.accountSearchSettings.sortAscending===false) {
      this.orderBy='dec'
      }
  }

  changeUsersPerPage(selectedValue: string){
    this.accountSearchSettings.displayedAccounts=parseInt(selectedValue)
    console.log(this.accountSearchSettings)
  }

  changeSortBy(selectedValue: string){
    this.accountSearchSettings.sortBy = selectedValue
    console.log('selectedValue = ' + selectedValue)
    console.log(this.accountSearchSettings)
  }

  changeOrderBy(selectedValue: string) {
    if(selectedValue==='asc') {
      this.accountSearchSettings.sortAscending = true;
    }
    else if (selectedValue==='dec') {
      this.accountSearchSettings.sortAscending = false;
    }
    console.log('selectedValue = ' + selectedValue)
    console.log(this.accountSearchSettings)
  }

  incrementValue() {

  }

  decrementValue() {

  }
}
