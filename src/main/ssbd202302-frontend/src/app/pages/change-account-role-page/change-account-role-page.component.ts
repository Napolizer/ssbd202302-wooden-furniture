import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {FormControl, FormGroup} from "@angular/forms";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {Account} from "../../interfaces/account";
import {Role} from "../../enums/role";
import {ActivatedRoute} from "@angular/router";
import {AccountService} from "../../services/account.service";
import {AlertService} from "@full-fledged/alerts";
import {AuthenticationService} from "../../services/authentication.service";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {Accesslevel} from "../../interfaces/accesslevel";

@Component({
  selector: 'account-group-change',
  templateUrl: './change-account-role-page.component.html',
  styleUrls: ['./change-account-role-page.component.sass'],
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
export class ChangeAccountRolePageComponent implements OnInit {

  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true})
  });
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
      }
      );
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

  changeGroupName(group: string): string {
    if (group == 'SALES_REP') {
      return 'SalesRep'
    }
    return group.charAt(0).toUpperCase() + group.slice(1).toLowerCase();
  }

  changeAccountGroup(accountGroup: string): void {
    const accessLevel: Accesslevel = {
      name: this.changeGroupName(accountGroup)
    }
    this.accountService.changeAccountRole(this.id, accessLevel)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.translate.get('account.changerole.success')
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
}
