import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {first, Subject, takeUntil} from "rxjs";
import {AccountService} from "../../services/account.service";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {AlertService} from "@full-fledged/alerts";
import {CustomValidators} from "../../utils/custom.validators";
import {ChangePassword} from "../../interfaces/change.password";
import {HttpErrorResponse} from "@angular/common/http";
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-change-own-password',
  templateUrl: './change-own-password.component.html',
  styleUrls: ['./change-own-password.component.sass'],
})
export class ChangeOwnPasswordComponent implements OnInit {

  destroy = new Subject<boolean>();
  hide = true;
  loading = true;
  currentPassword: string;
  newPassword: string;
  changePasswordForm: FormGroup;
  breadcrumbData: string[];
  constructor(
    private accountService: AccountService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService,
    public dialogRef: MatDialogRef<ChangeOwnPasswordComponent>,
  ) { }

  ngOnInit(): void {
    this.changePasswordForm = new FormGroup({
      currentPassword: new FormControl(
        '',
        Validators.compose([
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern('^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$')
        ])),
      newPassword: new FormControl(
        '',
        Validators.compose([
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern('^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$')
        ])),
      confirmPassword: new FormControl('', Validators.compose([]))
    }, {
      validators: Validators.compose( [
        CustomValidators.MatchNewPasswords,
        CustomValidators.PasswordsMatch,
        Validators.required
      ]),
    });
    this.changePasswordForm.get('newPassword')?.valueChanges.subscribe(() => {
      this.changePasswordForm.get('confirmPassword')?.updateValueAndValidity();
    });
    this.changePasswordForm.get('currentPassword')?.valueChanges.subscribe(() => {
      this.changePasswordForm.get('newPassword')?.updateValueAndValidity();
    });
    this.loading = false;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  changePassword(): void {
    this.loading = true;
    const newPassword: ChangePassword = {
      password: this.changePasswordForm.value['newPassword']!,
      currentPassword: this.changePasswordForm.value['currentPassword']!
    };
    this.accountService
      .changePassword(newPassword)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false;
          this.translate
            .get('change.password.success')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) =>{
              this.alertService.success(msg);
              this.dialogRef.close();
            })
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          this.translate
            .get(e.error.message || 'exception.current.password.invalid')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
              this.dialogRef.close();
            });
        }
      })
  }

  onPasswordChangeClicked(): void {
    if (this.changePasswordForm.valid) {
      this.translate
        .get('dialog.change.password.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.changePassword();
              }
            });
        });
    } else {
      this.changePasswordForm.markAllAsTouched();
    }
  }
}
