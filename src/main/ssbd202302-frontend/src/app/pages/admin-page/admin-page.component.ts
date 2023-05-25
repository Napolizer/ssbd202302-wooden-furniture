import { Component, OnInit, OnDestroy } from '@angular/core';
import {AccountService} from "../../services/account.service";
import {Account} from "../../interfaces/account";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {NavigationService} from "../../services/navigation.service";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BreadcrumbsService } from 'src/app/services/breadcrumbs.service';
import {FullName} from "../../interfaces/fullName";
import {concatMap, map} from "rxjs";

import { AccountSearchSettings } from 'src/app/interfaces/account.search.settings';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { SortBy } from 'src/app/interfaces/sort.by';
import { Form, FormControl } from '@angular/forms';
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
  placeholderSort: string;
  sortBy: string;
  orderBy:string;
  inputValue: 23;
  counter: number = 1;
  maxPage: number;
  allAccountsSize: number;
  sortControl: FormControl;
  orderControl: FormControl;
  accountsByPhraseSize: number;
  maxAccountsSize: number;

  accountSearchSettings: AccountSearchSettings = {
    searchPage: 1,
    displayedAccounts: 10,
    searchKeyword: "",
    sortBy: "LOGIN",
    sortAscending: true,
  };

  constructor(
    private accountService: AccountService,
    private navigationService: NavigationService
  ) {
  }

  ngOnInit(): void {
    this.accountService.retrieveOwnSearchSettings()
    .subscribe(searchSettings => {
      this.accountSearchSettings = searchSettings;
      this.initSearchSettings();
    });

    if(this.fullName==='') {
    this.accountService.retrieveAllAccounts()
    .subscribe(allAccountsList => {
      this.maxAccountsSize = allAccountsList.length;
  
      // this.accountService.findAccountsByFullName(this.accountSearchSettings.searchKeyword)
      // .subscribe(accountList => {
      //   this.accountsByPhraseSize = accountList.length
          this.accountService.findAccountsByFullNameWithPagination(this.accountSearchSettings)
            .subscribe(accounts => {
              this.allAccountsSize = accounts.length;
              this.allAccounts = accounts;
              this.accounts = this.allAccounts;
              this.maxPage = Math.ceil(this.maxAccountsSize/this.accountSearchSettings.displayedAccounts);
              this.loading = false;
            });
          });
        }
  else {
      this.accountService.findAccountsByFullName(this.accountSearchSettings.searchKeyword)
      .subscribe(accountList => {
        this.accountsByPhraseSize = accountList.length
          this.accountService.findAccountsByFullNameWithPagination(this.accountSearchSettings)
            .subscribe(accounts => {
              this.allAccountsSize = accounts.length;
              this.allAccounts = accounts;
              this.accounts = this.allAccounts;
              this.maxPage = Math.ceil(this.accountsByPhraseSize/this.accountSearchSettings.displayedAccounts);
              this.loading = false;
            });
    });
  }
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
      this.accountSearchSettings.searchKeyword=this.fullName;
      this.accountSearchSettings.searchPage = 1;
      this.counter = 1;
      if(this.fullName==='') {
        this.accountsByPhraseSize = this.maxAccountsSize;
        this.maxPage = Math.ceil(this.accountsByPhraseSize/this.accountSearchSettings.displayedAccounts)
      this.accountService.findAccountsByFullNameWithPagination(this.accountSearchSettings)
        .subscribe(accounts => {
          this.accounts = accounts;
          this.loading = false;
        });
      }
      else {
        this.accountService.findAccountsByFullName(this.accountSearchSettings.searchKeyword)
      .subscribe(accountList => {
        console.log(accountList)
        this.accountsByPhraseSize = accountList.length
        this.maxPage = Math.ceil(this.accountsByPhraseSize/this.accountSearchSettings.displayedAccounts)
      this.accountService.findAccountsByFullNameWithPagination(this.accountSearchSettings)
        .subscribe(accounts => {
          this.accounts = accounts;
          this.loading = false;
        });
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


  initSearchSettings(): void {
    this.sortControl = new FormControl(this.accountSearchSettings.sortBy)
    this.usersPerPage = this.accountSearchSettings.displayedAccounts.toString();
    this.counter = this.accountSearchSettings.searchPage;
    this.fullName = this.accountSearchSettings.searchKeyword;
    if(this.accountSearchSettings.sortAscending===true) {
      this.orderBy='asc'
      this.orderControl = new FormControl('asc')
    }
    else if(this.accountSearchSettings.sortAscending===false) {
      this.orderBy='dec'
      this.orderControl = new FormControl('dec')
      }
  }

  changeUsersPerPage(selectedValue: string){
    this.accountSearchSettings.displayedAccounts=parseInt(selectedValue)  
    this.accountService.findAccountsByFullName(this.fullName)
      .subscribe(accountList => {
        this.accountsByPhraseSize = accountList.length
        this.maxPage = Math.ceil(this.accountsByPhraseSize/this.accountSearchSettings.displayedAccounts)
      });
    this.requestAccountsByFullNameWithPagination()
  }

  changeSortBy(selectedValue: string){
    this.accountSearchSettings.sortBy = selectedValue
    this.requestAccountsByFullNameWithPagination()
  }

  changeOrderBy(selectedValue: string) {
    if(selectedValue==='asc') {
      this.accountSearchSettings.sortAscending = true;
    }
    else if (selectedValue==='dec') {
      this.accountSearchSettings.sortAscending = false;
    }
    this.requestAccountsByFullNameWithPagination()
  }

  requestAccountsByFullNameWithPagination() {
    this.loading = true;
    this.accountService.findAccountsByFullNameWithPagination(this.accountSearchSettings)
        .subscribe(accounts => {
          this.accounts = accounts;
          this.loading = false;
        });
  }

  increment() {
    if (this.counter < this.maxPage) {
      this.counter++;
      this.accountSearchSettings.searchPage=this.counter;
      this.requestAccountsByFullNameWithPagination()
    }
  }

  decrement() {
    if (this.counter > 1) { // Minimum value is 1
      this.counter--;
      this.accountSearchSettings.searchPage=this.counter;
      this.requestAccountsByFullNameWithPagination()
    }
  }
}
