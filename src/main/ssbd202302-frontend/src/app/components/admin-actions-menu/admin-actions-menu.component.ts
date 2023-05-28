import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Account} from "../../interfaces/account";
import {AccountService} from "../../services/account.service";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {AlertService} from "@full-fledged/alerts";
import {DialogService} from "../../services/dialog.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-admin-actions-menu',
  templateUrl: './admin-actions-menu.component.html',
  styleUrls: ['./admin-actions-menu.component.sass']
})
export class AdminActionsMenuComponent implements OnInit, OnDestroy {
  @Input()
  account: Account;

  @Output()
  loadingChanged = new EventEmitter<boolean>();

  destroy = new Subject<boolean>();

  constructor(
    private accountService: AccountService,
    private alertService: AlertService,
    private dialogService: DialogService,
    private translate: TranslateService,
  ) { }


  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.complete();
  }

  blockAccount(account: Account): void {
    this.loadingChanged.emit(true);
    this.accountService.blockAccount(account.id.toString())
      .pipe(first(), takeUntil(this.destroy))
      .subscribe( {
        next: () => {
          this.account.accountState = "BLOCKED";
          this.translate.get('block.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg)
              this.loadingChanged.emit(false);
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
              this.loadingChanged.emit(false);
            });
        }
      });
  }

  unblockAccount(account: Account): void {
    this.loadingChanged.emit(true);
    this.accountService.activateAccount(account.id.toString())
      .pipe(first(), takeUntil(this.destroy))
      .subscribe( {
        next: () => {
          this.account.accountState = "ACTIVE";
          this.translate.get('activate.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg)
              this.loadingChanged.emit(false);
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
              this.loadingChanged.emit(false);
            });
        }
      });
  }

  confirmBlock(account: Account): void {
    this.translate
      .get('dialog.block.account.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.blockAccount(account);
            }
          });
      });
  }

  confirmUnblock(account: Account): void {
    this.translate
      .get('dialog.unblock.account.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.unblockAccount(account);
            }
          });
      });
  }

  openAddRoleDialog(account: Account): void {
    this.dialogService.openAddRoleDialog(account);
  }

  openChangeRoleDialog(account: Account): void {
    this.dialogService.openChangeRoleDialog(account);
  }

  openRemoveRoleDialog(account: Account): void {
    this.dialogService.openRemoveRoleDialog(account);
  }

  openAdminChangeEmailDialog(account: Account): void {
    this.dialogService.openAdminChangeEmailDialog(account);
  }

  openAdminEditAccountDialog(account: Account): void {
    this.dialogService.openAdminEditAccountDialog(account);
  }

  canAddRole(account: Account): boolean {
    return account.roles.length < 2;
  }

  canChangeRole(account: Account): boolean {
    return account.roles.length < 2;
  }

  canRemoveRole(account: Account): boolean {
    return account.roles.length > 1;
  }

  confirmChangePassword(account: Account): void {
    this.translate
      .get('dialog.change.password.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.changePassword(account);
            }
          });
      });
  }

  changePassword(account: Account): void {
    this.loadingChanged.emit(true);
    this.accountService.changeUserPassword(account.login)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loadingChanged.emit(false);
          this.translate
            .get('change.password.user.send')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.success(msg);
            })
        },
        error: (e: HttpErrorResponse) => {
          this.loadingChanged.emit(false);
          this.translate
            .get('exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
            });
        }
      })
  }
}
