import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Account} from "../../interfaces/account";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {AlertService} from "@full-fledged/alerts";
import {NavigationService} from "../../services/navigation.service";
import {AccountService} from "../../services/account.service";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {EditAccount} from "../../interfaces/edit.account";

@Component({
  selector: 'app-admin-edit-account',
  templateUrl: './admin-edit-account.component.html',
  styleUrls: ['./admin-edit-account.component.sass']
})
export class AdminEditAccountComponent implements OnInit, OnDestroy {
  loading = true;
  private destroy = new Subject<void>();

  editAccountForm = new FormGroup({
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    country: new FormControl(''),
    city: new FormControl(''),
    street: new FormControl(''),
    streetNumber: new FormControl(0),
    postalCode: new FormControl('')
  });
  editableAccount : EditAccount = {
    firstName: '',
    lastName: '',
    country: '',
    city: '',
    street: '',
    postalCode: '',
    streetNumber: 0,
    hash: ''
  }
  login = ''
  id = ''

  constructor(
    private dialogRef: MatDialogRef<AdminEditAccountComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private translate: TranslateService,
    private dialogService: DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.id = this.getAccount().id.toString();
    this.accountService.retrieveAccount(this.id)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: account => {
          this.editableAccount.firstName = account.firstName;
          this.editableAccount.lastName = account.lastName;
          this.editableAccount.country = account.address.country;
          this.editableAccount.city = account.address.city;
          this.editableAccount.postalCode = account.address.postalCode;
          this.editableAccount.street = account.address.street;
          this.editableAccount.streetNumber = account.address.streetNumber;
          this.editableAccount.hash = account.hash;
          this.editAccountForm.setValue({
            firstName: account.firstName,
            lastName: account.lastName,
            country: account.address.country,
            city: account.address.city,
            street: account.address.street,
            streetNumber: account.address.streetNumber,
            postalCode: account.address.postalCode
          });
          this.login = account.login;
          this.loading = false
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

  saveAccount() {
    this.loading = true
    this.editableAccount.firstName = this.editAccountForm.value.firstName!;
    this.editableAccount.lastName = this.editAccountForm.value.lastName!;
    this.editableAccount.country = this.editAccountForm.value.country!;
    this.editableAccount.city = this.editAccountForm.value.city!;
    this.editableAccount.postalCode = this.editAccountForm.value.postalCode!;
    this.editableAccount.street = this.editAccountForm.value.street!;
    this.editableAccount.streetNumber = this.editAccountForm.value.streetNumber!
    this.accountService.editUserAccount(this.login, this.editableAccount)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: editedAccount => {
          this.editableAccount = editedAccount;
          for(const k in editedAccount) {
            this.getAccount()[k] = editedAccount[k];
          }
          this.translate.get('edit.success')
            .pipe(takeUntil(this.destroy))
            .subscribe({
              next: msg => {
                this.alertService.success(msg)
                this.loading = false;
                this.dialogRef.close('action');
              },
              error: e => {
                const title = this.translate.instant(e[0]);
                const message = this.translate.instant(e[1]);
                this.loading = false;
                this.dialogRef.close('action');
                this.dialogService.openErrorDialog(title, message);
              }
            });
        },
        error: e => {
          const title = this.translate.instant('exception.occurred');
          const message = this.translate.instant(e.error.message || 'exception.unknown');
          const ref = this.dialogService.openErrorDialog(title, message);
          ref.afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe(() => {
              this.loading = false;
              this.dialogRef.close('action');
            });
        }
      });
    this.loading = false
  }

  confirmSave(): void {
    this.translate
      .get('dialog.admin.edit.account.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.saveAccount();
            }
          });
      });
  }

  onResetClicked() {
    this.loading = true;
    this.editAccountForm.setValue({
      firstName: this.editableAccount.firstName!,
      lastName: this.editableAccount.lastName!,
      country: this.editableAccount.country!,
      city: this.editableAccount.city!,
      street: this.editableAccount.street!,
      streetNumber: this.editableAccount.streetNumber!,
      postalCode: this.editableAccount.postalCode!
    });
    this.loading = false;
  }

  onBackClicked(): void {
    this.dialogRef.close('action');
  }
}
