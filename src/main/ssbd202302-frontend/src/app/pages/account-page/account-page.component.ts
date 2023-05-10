import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {Account} from "../../interfaces/account";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {DatePipe} from "@angular/common";
import { Location } from '@angular/common';

@Component({
  selector: 'app-account-page',
  templateUrl: './account-page.component.html',
  styleUrls: ['./account-page.component.sass'],
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
export class AccountPageComponent implements OnInit, OnDestroy {
  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true})
  });
  destroy = new Subject<boolean>();
  account: Account;
  loading = true;

  constructor(
    private accountService: AccountService,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.accountService.retrieveOwnAccount(this.authenticationService.getLogin() ?? '')
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

  getAccountState(): string {
    switch(this.account.accountState.toUpperCase()) {
      case 'BLOCKED': return 'account.state.blocked';
      case 'INACTIVE': return 'account.state.inactive';
      case 'NOT_VERIFIED': return 'account.state.notVerified';
      default: return 'account.state.active';
    }
  }

  getGroups(): string {
    return this.account.groups.map(group => `group.${group.toLowerCase()}`).join(', ') ?? '-';
  }

  onEditClicked(): void {
    this.navigationService.redirectToEditOwnAccountPage();
  }
}
