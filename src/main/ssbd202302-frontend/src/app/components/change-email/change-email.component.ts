import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { Subject, combineLatest, first, map, takeUntil } from 'rxjs';
import { Email } from 'src/app/interfaces/email';
import { AccountService } from 'src/app/services/account.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { CustomValidators } from 'src/app/utils/custom.validators';

@Component({
  selector: 'app-change-email',
  templateUrl: './change-email.component.html',
  styleUrls: ['./change-email.component.sass'],
})
export class ChangeEmailComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  id: string;
  version: string;
  currentEmail: string;
  changeEmailForm: FormGroup;
  breadcrumbData: string[];

  constructor(
    private accountService: AccountService,
    private translate: TranslateService,
    private authenticationService: AuthenticationService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService,
    public dialogRef: MatDialogRef<ChangeEmailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.id = data ? data.id : undefined;
  }

  ngOnInit(): void {
    this.changeEmailForm = new FormGroup({
      currentEmail: new FormControl(''),
      newEmail: new FormControl('', Validators.compose([Validators.email])),
    },
    {
      validators: Validators.compose([
        CustomValidators.EmailsCannotMatch,
        Validators.required,
      ]),
    });
    if (this.id) {
      this.id = this.id as string;
      this.accountService
        .retrieveAccount(this.id)
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (account) => {
            this.version = account.hash;
            this.changeEmailForm.setValue({
              currentEmail: account.email,
              newEmail: '',
            });
            this.loading = false;
          },
          error: (e) => {
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
                    this.navigationService.redirectToMainPage();
                    this.dialogRef.close();
                  });
              });
          },
        });
    } else {
      this.accountService
        .retrieveOwnAccount(this.authenticationService.getLogin() ?? '')
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (account) => {
            this.id = account.id.toString();
            this.version = account.hash;
            this.changeEmailForm.setValue({
              currentEmail: account.email,
              newEmail: '',
            });
            this.loading = false;
          },
          error: (e) => {
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
                    this.navigationService.redirectToMainPage();
                    this.dialogRef.close();
                  });
              });
          },
        });
    }
    this.changeEmailForm.get('currentEmail')?.disable();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  changeEmail(): void {
    this.loading = true;
    const email: Email = {
      email: this.changeEmailForm.value['newEmail']!,
    };
    this.accountService
      .changeEmail(this.id, email, this.version)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.translate
          .get('change.email.link.sent' || 'exception.unknown')
          .pipe(takeUntil(this.destroy))
          .subscribe((msg) => {
            this.alertService.success(msg);
            this.dialogRef.close();
          });
        },
        error: (e: HttpErrorResponse) => {
          this.translate
          .get(e.error.message || 'exception.unknown')
          .pipe(takeUntil(this.destroy))
          .subscribe((msg) => {
            this.alertService.danger(msg);
            this.dialogRef.close();
          });
        },
      });
  }

  onEmailChangeClicked(): void {
    if (this.changeEmailForm.valid) {
      this.translate
        .get('dialog.change.email.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.changeEmail();
              }
            });
        });
    } else {
      this.changeEmailForm.markAllAsTouched();
    }
  }
}
