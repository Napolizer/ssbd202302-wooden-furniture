import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {AccountService} from "../../services/account.service";
import {Account} from "../../interfaces/account";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {NavigationService} from "../../services/navigation.service";
import {NavigationEnd, Router} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BreadcrumbsService } from 'src/app/services/breadcrumbs.service';
import {FullName} from "../../interfaces/fullName";
import {map, Subject, takeUntil, tap} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state('loaded', style({
        opacity: 1,
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "20px",
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
export class AdminPageComponent implements OnInit, OnDestroy {
  accounts: Account[] = [];
  loading = true;
  breadcrumbsData: string[] = [];
  fullName: string = '';
  fullNames: string[] = [];
  displayedColumns = ['login', 'email', 'firstName', 'lastName', 'roles', 'state', 'show', 'delete'];
  dataSource = new MatTableDataSource<Account>(this.accounts);
  destroy = new Subject<boolean>();

  @ViewChild(MatPaginator)
  paginator: MatPaginator;

  constructor(
    private accountService: AccountService,
    private navigationService: NavigationService,
    private breadcrumbsService: BreadcrumbsService,
    private translate: TranslateService,
    private router: Router,
  ) {}

  ngOnInit(): void {
        this.router.events
          .pipe(takeUntil(this.destroy))
          .subscribe((val) => {
            if (val instanceof NavigationEnd) {
              this.loading = true;
            }
          });
    this.accountService.retrieveAllAccounts()
      .pipe(tap(() => this.loading = true), takeUntil(this.destroy))
      .subscribe(accounts => {
        this.accounts = accounts;
        this.loading = false;
        this.dataSource = new MatTableDataSource<Account>(this.accounts);
        this.dataSource.paginator = this.paginator;
      });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
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
      this.accountService.retrieveAllAccounts()
        .subscribe(accounts => {
          this.accounts = accounts;
          this.loading = false
        })
    } else {
      this.accountService.findAccountsByFullName(this.fullName)
        .subscribe(accounts => {
          this.accounts = accounts;
          this.loading = false;
        });
    }
  }

  onResetClicked(): void {
    this.loading = true;
    this.accountService.retrieveAllAccounts()
      .subscribe(accounts => {
        this.accounts = accounts;
        this.loading = false;
      });
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

  shouldDisplayTable(): boolean {
    return this.accounts.length > 0;
  }

  getRoles(account: Account): string {
    return account.roles.map(role => this.translate.instant(`role.${role.toLowerCase()}`)).join(', ') ?? '-';
  }
}
