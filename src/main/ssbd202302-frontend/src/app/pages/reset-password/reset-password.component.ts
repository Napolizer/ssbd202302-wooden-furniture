import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AlertService} from '@full-fledged/alerts';
import {ActivatedRoute, Router} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import { AccountService } from 'src/app/services/account.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomValidators } from 'src/app/utils/custom.validators';
import { ResetPassword } from 'src/app/interfaces/reset.password';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.sass'],
  animations: [
    trigger('loadedUnloadedForm', [
      state('loaded', style({
        opacity: 1,
        backgroundColor: "rgba(221, 221, 221, 1)"
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "80px",
        backgroundColor: "rgba(0, 0, 0, 0)"
      })),
      transition('loaded => unloaded', [
        animate('0.5s ease-in')
      ]),
      transition('unloaded => loaded', [
        animate('0.5s ease-in')
      ])
    ]),
  ]
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  hide = true;
  loaded = false;
  destroy = new Subject<boolean>();
  token : string;
  resetPasswordForm = new FormGroup(
    {
      password: new FormControl(
        '',
          Validators.compose([
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern('^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$'),
        ])
      ),
      confirmPassword: new FormControl(
        '',
        Validators.compose([Validators.required])
      ),

    },
    { validators: Validators.compose([CustomValidators.MatchPasswords]) }
  );


  constructor(
    private alertService: AlertService,
    private accountService: AccountService,
    private router: Router,
    private route: ActivatedRoute,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);

    this.route.queryParams.subscribe((params) => {
      const token = params["token"];
      if(token) {
        this.accountService.validatePasswordResetToken(token)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: () => {
            this.token = token
          },
          error: (e: HttpErrorResponse) => {
            const message = e.error.message as string;
            this.router.navigate(['/login'], { state: {resetPasswordError: message} });
          }
        })
      } else {
        this.router.navigate(['/'])
        // change to 404 page
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  onResetPassword(): void {
    if (this.resetPasswordForm.valid) {
      const resetPassword: ResetPassword = {
        password: this.resetPasswordForm.value['password']!,
      };
      this.accountService.resetPassword(this.token, resetPassword)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: () => {
            this.router.navigate(['/login'], { state: {resetPasswordSuccess: 'reset.password.success'} });
          },
          error: (e: HttpErrorResponse) => {
            if(e.status == 409) {
              this.translate.get(e.error.message || 'exception.unknown')
                .pipe(takeUntil(this.destroy))
                .subscribe(msg => {
                  this.alertService.danger(msg);
                });
            } else {
              const message = e.error.message as string;
              this.router.navigate(['/login'], { state: {resetPasswordError: message} });
            }
          }
        })
    } else {
      this.resetPasswordForm.markAllAsTouched()
    }
  }
}
