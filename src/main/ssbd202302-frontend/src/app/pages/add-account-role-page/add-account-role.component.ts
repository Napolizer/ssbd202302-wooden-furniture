import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {environment} from "../../../environments/environment";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {Account} from "../../interfaces/account";
import {ActivatedRoute} from "@angular/router";
import {AccountService} from "../../services/account.service";
import {AlertService} from "@full-fledged/alerts";
import {AuthenticationService} from "../../services/authentication.service";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {AccountRegister} from "../../interfaces/account.register";
import {Role} from "../../enums/role";

@Component({
  selector: 'account-group-add',
  templateUrl: './add-account-role.component.html',
  styleUrls: ['./add-account-role.component.sass'],
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
export class AddAccountRoleComponent implements OnInit {
  destroy = new Subject<boolean>();
  account: Account;

  roles: string[] = [Role.ADMINISTRATOR,
    Role.EMPLOYEE,
    Role.SALES_REP,
    Role.CLIENT]
  id = '';
  loading = true;

  constructor(
    private activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService
  ) {
  }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id') || '';
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
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  confirmAdd(accountRole: string): void {
    this.translate
      .get('dialog.add.role.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.addAccountRoleToAccount(accountRole);
            }
          });
      });
  }

  addAccountRoleToAccount(accountRole: string): void {
    this.accountService.addAccountRole(this.id, accountRole)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.translate.get('account.addrole.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              this.navigationService.redirectToAccountPage(this.id);
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

  onBackClicked(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id') || '';
    void this.navigationService.redirectToAccountPage(id);
  }
}
