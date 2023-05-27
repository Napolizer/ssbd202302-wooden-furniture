import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Account} from "../../interfaces/account";
import {Role} from "../../enums/role";
import {TranslateService} from "@ngx-translate/core";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {DialogService} from "../../services/dialog.service";
import {AlertService} from "@full-fledged/alerts";
import {NavigationService} from "../../services/navigation.service";
import {AccountService} from "../../services/account.service";

@Component({
  selector: 'app-add-role',
  templateUrl: './add-role.component.html',
  styleUrls: ['./add-role.component.sass']
})
export class AddRoleComponent implements OnInit, OnDestroy {
  chosenRole: string;
  loading = false;
  private destroy = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<AddRoleComponent>,
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

  getAllRoles(): string[] {
    return Object.keys(Role).filter(role => role !== 'GUEST');
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
    const id = this.getAccount().id.toString();
    this.loading = true;
    this.accountService.addAccountRole(id, accountRole)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.translate.get('account.addrole.success')
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

}
