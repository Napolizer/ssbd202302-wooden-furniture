import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Role} from "../../enums/role";
import {Account} from "../../interfaces/account";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {AlertService} from "@full-fledged/alerts";
import {NavigationService} from "../../services/navigation.service";
import {AccountService} from "../../services/account.service";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";

@Component({
  selector: 'app-remove-role',
  templateUrl: './remove-role.component.html',
  styleUrls: ['./remove-role.component.sass']
})
export class RemoveRoleComponent implements OnInit, OnDestroy {
  chosenRole: string;
  loading = false;
  private destroy = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<RemoveRoleComponent>,
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

  getPossibleRemoveRoles(): string[] {
    return this.getAccount().roles.map(role => this.translate.instant(`role.${role}`));
  }

  confirmRemove(accountRole: string): void {
    accountRole = this.getAccount().roles.find(role => this.translate.instant(`role.${role}`) === accountRole) ?? '';
    this.translate
      .get('dialog.remove.role.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.removeAccountRoleFromAccount(accountRole);
            }
          });
      });
  }

  removeAccountRoleFromAccount(accountGroup: string): void {
    const id = this.data.account.id.toString();
    this.loading = true;
    this.accountService.removeAccountRole(id, accountGroup)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: modifiedClient => {
          this.getAccount().roles = modifiedClient.roles;
          this.translate.get('account.removerole.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              this.dialogRef.close('action');
              this.loading = false;
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
              this.dialogRef.close('action');
              this.loading = false;
            });
        }
      });
  }

  getAccountRoles(): string {
    return this.getAccount().roles.map(role => this.translate.instant(`role.${role.toLowerCase()}`)).join(', ') ?? '-';
  }
}
