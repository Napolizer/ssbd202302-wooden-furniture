import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { AlertService } from '@full-fledged/alerts';
import { ActivatedRoute } from '@angular/router';
import { Subject, first, takeUntil } from 'rxjs';
import { AccountService } from 'src/app/services/account.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomValidators } from 'src/app/utils/custom.validators';
import { ResetPassword } from 'src/app/interfaces/reset.password';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { Constants } from 'src/app/utils/constants';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
      state(
        'loaded',
        style({
          opacity: 1,
          backgroundColor: 'rgba(221, 221, 221, 1)',
        })
      ),
      state(
        'unloaded',
        style({
          opacity: 0,
          paddingTop: '80px',
          backgroundColor: 'rgba(0, 0, 0, 0)',
        })
      ),
      transition('loaded => unloaded', [animate('0.5s ease-in')]),
      transition('unloaded => loaded', [animate('0.5s ease-in')]),
    ]),
  ],
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  hide = true;
  loaded = false;
  loading = false;
  destroy = new Subject<boolean>();
  token: string;
  resetPasswordForm = new FormGroup(
    {
      password: new FormControl(
        '',
        Validators.compose([
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern(Constants.PASSWORD_PATTERN),
        ])
      ),
      confirmPassword: new FormControl(''),
    },
    {
      validators: Validators.compose([
        CustomValidators.MatchPasswords,
        Validators.required,
      ]),
    }
  );

  constructor(
    private alertService: AlertService,
    private accountService: AccountService,
    private navigationService: NavigationService,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);

    this.route.queryParams.subscribe((params) => {
      const token = params['token'];
      if (token) {
        this.accountService
          .validatePasswordResetToken(token)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: () => {
              this.token = token;
            },
            error: (e: HttpErrorResponse) => {
              if (e.status == 410) {
                const message = e.error.message as string;
                this.navigationService.redirectToLoginPageWithState({
                  resetPasswordError: message,
                });
              } else {
                this.navigationService.redirectToNotFoundPage();
              }
            },
          });
      } else {
        this.navigationService.redirectToNotFoundPage();
      }
    });

    this.resetPasswordForm.get('password')?.valueChanges.subscribe(() => {
      this.resetPasswordForm.get('confirmPassword')?.updateValueAndValidity();
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  resetPassword(): void {
    this.loading = true;
    const resetPassword: ResetPassword = {
      password: this.resetPasswordForm.value['password']!,
    };
    this.accountService
      .resetPassword(this.token, resetPassword)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false;
          this.navigationService.redirectToLoginPageWithState({
            resetPasswordSuccess: 'reset.password.success',
          });
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          if (e.status == 409) {
            this.translate
              .get(e.error.message || 'exception.unknown')
              .pipe(takeUntil(this.destroy))
              .subscribe((msg) => {
                this.alertService.danger(msg);
              });
          } else if (e.status == 410) {
            const message = e.error.message as string;
            this.navigationService.redirectToLoginPageWithState({
              resetPasswordError: message,
            });
          } else {
            this.navigationService.redirectToNotFoundPage();
          }
        },
      });
  }

  onConfirm(): void {
    if (this.resetPasswordForm.valid) {
      this.translate
        .get('dialog.confirmation.password.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.resetPassword();
              }
            });
        });
    } else {
      this.resetPasswordForm.markAllAsTouched();
    }
  }
}
