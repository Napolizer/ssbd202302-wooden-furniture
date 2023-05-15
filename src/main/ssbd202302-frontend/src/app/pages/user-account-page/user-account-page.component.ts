import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {FormControl, FormGroup} from "@angular/forms";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {AccountService} from "../../services/account.service";
import {AlertService} from "@full-fledged/alerts";
import {AuthenticationService} from "../../services/authentication.service";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {ActivatedRoute} from "@angular/router";
import {Account} from "../../interfaces/account";
import {DatePipe} from "@angular/common";
import { Location } from '@angular/common';

@Component({
  selector: 'app-user-account-page',
  templateUrl: './user-account-page.component.html',
  styleUrls: ['./user-account-page.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
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
export class UserAccountPageComponent implements OnInit {
  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true})
  });
  destroy = new Subject<boolean>();
  account: Account;
  loading = true;
  id = ''

  constructor(
    private activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private datePipe: DatePipe,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id') || ''
    this.accountService.retrieveAccount(this.id)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: account => {
          this.account = account;
          this.loading = false;
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              const ref = this.dialogService.openErrorDialog(data.title, data.message);
              ref.afterClosed()
                .pipe(first(), takeUntil(this.destroy))
                .subscribe(() => {
                  void this.navigationService.redirectToMainPage();
                });
            });
        }
      });

      const state = this.location.getState() as any;
      const keys = Object.keys(state);

      if (keys.length == 2) {
        const code = state[keys[0]];
        const isError = code.startsWith('exception');
        this.translate
          .get(code)
          .pipe(takeUntil(this.destroy))
          .subscribe((msg) => {
            if (isError) {
              this.alertService.danger(msg);
            } else {
              this.alertService.success(msg);
            }
          });
      }
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  formatDate(date: Date | undefined): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd HH:mm:ss') ?? '-';
  }
  redirectToAddAccountRolesPage(): void{
    void this.navigationService.redirectToAddAccountRolesPage(this.id);
  }

  redirectToRemoveAccountRolesPage(): void{
    void this.navigationService.redirectToRemoveAccountRolesPage(this.id);
  }

  getAccountState(): string {
    switch(this.account.accountState.toUpperCase()) {
      case 'BLOCKED': return 'account.state.blocked';
      case 'INACTIVE': return 'account.state.inactive';
      case 'NOT_VERIFIED': return 'account.state.notVerified';
      default: return 'account.state.active';
    }
  }

  getRoles(): string {
    return this.account.roles.map(role => this.translate.instant(`role.${role.toLowerCase()}`)).join(', ') ?? '-';
  }

  onEditClicked(): void {
    void this.navigationService.redirectToEditUserAccountPage(this.id);
  }

  onChangeEmailClicked(): void {
    void this.navigationService.redirectToChangeUserEmailPage(this.id);
  }

  redirectToChangeAccountRolesPage(): void {
    void this.navigationService.redirectToChangeAccountGroupPage(this.id);
  }

  isBlocked(): boolean {
    return this.account.accountState == "ACTIVE";
  }

  onBlockClicked(): void {
    this.accountService.blockAccount(this.id)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe( {
        next: () => {
        this.account.accountState = "BLOCKED";
        this.translate.get('block.success')
          .pipe(takeUntil(this.destroy))
          .subscribe(msg => {
            this.alertService.success(msg)
          });
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              this.alertService.danger(`${data.title}: ${data.message}`);
            });
        }
      });
  }

  onActivateClicked(): void {
    this.accountService.activateAccount(this.id)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe( {
        next: () => {
          this.account.accountState = "ACTIVE";
          this.translate.get('activate.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg)
            });
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              this.alertService.danger(`${data.title}: ${data.message}`);
            });
        }
      });
  }
}
