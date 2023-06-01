import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { Account } from '../../interfaces/account';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from '../../services/dialog.service';
import { AlertService } from '@full-fledged/alerts';
import { NavigationService } from '../../services/navigation.service';
import { AccountService } from '../../services/account.service';
import { combineLatest, first, map, Subject, takeUntil } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { EditAccount } from '../../interfaces/edit.account';
import { Constants } from 'src/app/utils/constants';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.sass'],
})
export class EditAccountComponent implements OnInit, OnDestroy {
  loading = true;
  private destroy = new Subject<void>();

  editAccountForm = new FormGroup(
    {
      firstName: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.CAPITALIZED_PATTERN)])
      ),
      lastName: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.CAPITALIZED_PATTERN)])
      ),
      country: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.CAPITALIZED_PATTERN)])
      ),
      city: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.CAPITALIZED_PATTERN)])
      ),
      street: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.CAPITALIZED_PATTERN)])
      ),
      streetNumber: new FormControl(
        '',
        Validators.compose([Validators.min(1), Validators.max(9999)])
      ),
      postalCode: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.POSTAL_CODE_PATTERN)])
      ),
    },
    {
      validators: Validators.compose([Validators.required]),
    }
  );
  editableAccount: EditAccount = {
    firstName: '',
    lastName: '',
    country: '',
    city: '',
    street: '',
    postalCode: '',
    streetNumber: 0,
    hash: '',
  };
  login = '';
  id = '';
  admin : boolean;

  constructor(
    private dialogRef: MatDialogRef<EditAccountComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private translate: TranslateService,
    private dialogService: DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService,
    private accountService: AccountService,
    private authenticationService: AuthenticationService
  ) {
    this.id = data ? data.account.id : undefined;
    this.admin = data ? data.admin : false;
  }

  ngOnInit(): void {
    if (this.admin) {
      this.accountService
        .retrieveAccount(this.id)
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (account) => this.fillFormWithAccount(account),
          error: (e) => this.handleGetAccountError(e),
        });
    } else {
      this.accountService
        .retrieveOwnAccount()
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (account) => this.fillFormWithAccount(account),
          error: (e) => this.handleGetAccountError(e),
        });
    }
  }

  ngOnDestroy() {
    this.destroy.next();
    this.destroy.complete();
  }

  getAccount(): Account {
    return this.data.account;
  }

  fillFormWithAccount(account: Account) {
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
      streetNumber: account.address.streetNumber.toString(),
      postalCode: account.address.postalCode,
    });
    this.login = account.login;
    this.loading = false;
  }

  handleGetAccountError(e: any) {
    combineLatest([
      this.translate.get('exception.occurred'),
      this.translate.get(e.error.message || 'exception.unknown'),
    ])
      .pipe(
        first(),
        takeUntil(this.destroy),
        map((data) => ({
          title: data[0],
          message: data[1],
        }))
      )
      .subscribe((data) => {
        const ref = this.dialogService.openErrorDialog(
          data.title,
          data.message
        );
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe(() => {
            void this.navigationService.redirectToMainPage();
          });
      });
    this.dialogRef.close();
  }

  saveAccount() {
    this.loading = true;
    this.editableAccount.firstName = this.editAccountForm.value.firstName!;
    this.editableAccount.lastName = this.editAccountForm.value.lastName!;
    this.editableAccount.country = this.editAccountForm.value.country!;
    this.editableAccount.city = this.editAccountForm.value.city!;
    this.editableAccount.postalCode = this.editAccountForm.value.postalCode!;
    this.editableAccount.street = this.editAccountForm.value.street!;
    this.editableAccount.streetNumber = parseInt(
      this.editAccountForm.value.streetNumber!
    );
    if (this.admin) {
      this.accountService
        .editUserAccount(this.login, this.editableAccount)
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (editedAccount) => this.handleEditSuccess(editedAccount),
          error: (e) => this.handleEditError(e),
        });
    } else {
      this.accountService
        .editOwnAccount(
          this.authenticationService.getLogin()!,
          this.editableAccount
        )
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (editedAccount) => this.handleEditSuccess(editedAccount),
          error: (e) => this.handleEditError(e),
        });
    }
  }

  handleEditSuccess(editedAccount: EditAccount): void {
    Object.assign(this.getAccount(), editedAccount);
    this.translate
      .get('edit.success' || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (msg) => {
          this.alertService.success(msg);
          this.loading = false;
          this.dialogRef.close('action');
        },
      });
  }

  handleEditError(e: any): void {
    const title = this.translate.instant('exception.occurred');
    const message = this.translate.instant(
      e.error.message || 'exception.unknown'
    );
    const ref = this.dialogService.openErrorDialog(title, message);
    ref
      .afterClosed()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe(() => {
        this.loading = false;
        this.dialogRef.close('error');
      });
  }

  confirmSave(): void {
    if (this.editAccountForm.valid) {
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
    } else {
      this.editAccountForm.markAllAsTouched();
    }
  }

  onResetClicked() {
    this.loading = true;
    this.editAccountForm.setValue({
      firstName: this.editableAccount.firstName!,
      lastName: this.editableAccount.lastName!,
      country: this.editableAccount.country!,
      city: this.editableAccount.city!,
      street: this.editableAccount.street!,
      streetNumber: this.editableAccount.streetNumber!.toString(),
      postalCode: this.editableAccount.postalCode!,
    });
    this.loading = false;
  }

  onBackClicked(): void {
    this.dialogRef.close('action');
  }
}
