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
import {DatePipe, Location } from "@angular/common";
import { TokenService } from 'src/app/services/token.service';
import { AccountType } from 'src/app/enums/account.type';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import {ChangeEmailComponent} from "../../components/change-email/change-email.component";

@Component({
  selector: 'app-account-page',
  templateUrl: './account-page.component.html',
  styleUrls: ['./account-page.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
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
export class AccountPageComponent implements OnInit, OnDestroy {
  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true})
  });
  destroy = new Subject<boolean>();
  account: Account;
  loading = true;
  breadcrumbData: string[];
  constructor(
    private accountService: AccountService,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private datePipe: DatePipe,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private location: Location,
    private tokenService: TokenService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.accountService.retrieveOwnAccount()
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

  isUserNormalType(): boolean {
    return this.tokenService.getAccountType() === AccountType.NORMAL;
  }

  getRoles(): string {
    return this.account.roles.map(role => this.translate.instant(`role.${role.toLowerCase()}`)).join(', ') ?? '-';
  }

  onBackClicked(): void {
    void this.navigationService.redirectToMainPage();
  }

  openChangeEmailDialog(): void {
    this.dialog.open(ChangeEmailComponent, {
      width: '450px',
      height: '400px',
    } as MatDialogConfig<any>);
  }

  openEditAccountDialog(): void {
    this.dialogService.openEditAccountDialog(this.account, false);
  }

  openChangePasswordDialog(): void {
    this.dialogService.openChangePasswordDialog();
  }

  getCardStyling(): any {
    return {
      'background-color': document.body.classList.contains('dark-mode') ? '#424242' : '#fafafa'
    };
  }
}
