import {Component, Inject, OnInit} from '@angular/core';
import {Account} from "../../interfaces/account";
import {Role} from "../../enums/role";
import {NavigationService} from "../../services/navigation.service";
import {AlertService} from "@full-fledged/alerts";
import {DialogService} from "../../services/dialog.service";
import {TranslateService} from "@ngx-translate/core";
import {AccountService} from "../../services/account.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {Accesslevel} from "../../interfaces/accesslevel";

@Component({
  selector: 'app-change-role',
  templateUrl: './change-role.component.html',
  styleUrls: ['./change-role.component.sass']
})
export class ChangeRoleComponent implements OnInit {
  chosenRole: string;
  loading = false;
  private destroy = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<ChangeRoleComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private translate: TranslateService,
    private dialogService: DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {}

  ngOnDestroy() {
    this.destroy.next();
    this.destroy.complete();
  }

  getAccount(): Account {
    return this.data.account;
  }

  onCancelClick(): void {
    this.dialogRef.close('cancel');
  }

  getPossibleChangeRoles(): string[] {
    return Object.keys(Role).filter(role => role !== 'GUEST' && role !== this.getAccount().roles[0].toUpperCase());
  }

  changeRoleName(role: string): string {
    if (role == 'SALES_REP') {
      return 'SalesRep'
    }
    return role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
  }

  confirmChange(accountRole: string): void {
    this.translate
      .get('dialog.change.role.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.changeAccountRole(accountRole);
            }
          });
      });
  }

  changeAccountRole(accountRole: string): void {
    this.loading = true;
    const accessLevel: Accesslevel = {
      name: this.changeRoleName(accountRole)
    }
    const id = this.getAccount().id.toString();
    this.accountService.changeAccountRole(id, accessLevel)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: changedAccount => {
          this.getAccount().roles = changedAccount.roles;
          this.translate.get('account.changerole.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              this.loading = false;
              this.dialogRef.close('cancel');
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
              this.loading = false;
              this.dialogRef.close('cancel');
            });
        }
      });
  }

  getAccountRoles(): string {
    return this.getAccount().roles.map(role => this.translate.instant(`role.${role.toLowerCase()}`)).join(', ') ?? '-';
  }
}
