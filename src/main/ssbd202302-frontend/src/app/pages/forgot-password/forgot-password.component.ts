import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';
import { Email } from 'src/app/interfaces/email';
import { AccountService } from 'src/app/services/account.service';
import { NavigationService } from 'src/app/services/navigation.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.sass'],
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
export class ForgotPasswordComponent implements OnInit {
  loaded = false;
  loading = false;
  destroy = new Subject<boolean>();
  token: string;
  forgotPasswordForm = new FormGroup({
    email: new FormControl('', Validators.compose([Validators.email])),
  });

  constructor(
    private alertService: AlertService,
    private accountService: AccountService,
    private navigationService: NavigationService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  onConfirm(): void {
    if (this.forgotPasswordForm.valid) {
      this.loading = true;
      const email: Email = {
        email: this.forgotPasswordForm.value['email']!,
      };
      this.accountService
        .forgotPassword(email)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: () => {
            this.loading = false;
            this.navigationService.redirectToLoginPageWithState({
              forgotPasswordSuccess: 'forgot.password.success',
            });
          },
          error: (e: HttpErrorResponse) => {
            this.loading = false;
            this.translate
              .get(e.error.message || 'exception.unknown')
              .pipe(takeUntil(this.destroy))
              .subscribe((msg) => {
                this.alertService.danger(msg);
              });
          },
        });
    } else {
      this.forgotPasswordForm.markAllAsTouched();
    }
  }
}
