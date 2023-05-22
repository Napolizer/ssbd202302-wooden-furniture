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
import { ActivatedRoute } from '@angular/router';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { Subject, first, takeUntil } from 'rxjs';
import { AccountType } from 'src/app/enums/account.type';
import { AccountGoogleRegister } from 'src/app/interfaces/google.register';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { TokenService } from 'src/app/services/token.service';
import { Constants } from 'src/app/utils/constants';
import { TimeZone } from 'src/app/utils/time.zone';

@Component({
  selector: 'app-google-redirect',
  templateUrl: './google-redirect.component.html',
  styleUrls: ['./google-redirect.component.sass'],
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
export class GoogleRedirectComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  hide = true;
  checked = false;
  email: string;
  locale: string;
  idToken: string;
  finishRegistrationForm: FormGroup = new FormGroup(
    {
      login: new FormControl(
        '',
        Validators.compose([
          Validators.minLength(6),
          Validators.maxLength(20),
          Validators.pattern(Constants.LOGIN_PATTERN),
        ])
      ),
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
      nip: new FormControl(''),
      companyName: new FormControl(''),
    },
    {
      validators: Validators.compose([Validators.required]),
    }
  );

  constructor(
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService,
    private tokenService: TokenService,
    private translate: TranslateService,
    private alertService: AlertService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.route.queryParams.subscribe((params) => {
      const state = params['state'] as string;
      const code = params['code'] as string;
      if (code && state) {
        this.authenticationService
          .handleGoogleRedirect(code, state, this.translate.getBrowserLang() as string)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: (response) => {
              if (response.status == 200) {
                this.tokenService.saveToken(response.body.token);
                this.tokenService.saveAccountType(AccountType.GOOGLE);
                this.translate
                  .get('login.success')
                  .pipe(takeUntil(this.destroy))
                  .subscribe((msg) => {
                    this.alertService.success(msg);
                    this.navigationService.redirectToMainPage();
                  });
              } else if (response.status == 202) {
                this.idToken = response.body.idToken;
                this.email = response.body.email;
                this.locale = response.body.locale;
                this.idToken = response.body.idToken;
                this.finishRegistrationForm.setValue({
                  login: response.body.login,
                  firstName: response.body.firstName
                    ? response.body.firstName
                    : '',
                  lastName: response.body.lastName
                    ? response.body.lastName
                    : '',
                  country: this.finishRegistrationForm.value['country'],
                  city: this.finishRegistrationForm.value['city'],
                  street: this.finishRegistrationForm.value['street'],
                  streetNumber:
                    this.finishRegistrationForm.value['streetNumber'],
                  postalCode: this.finishRegistrationForm.value['postalCode'],
                  nip: this.finishRegistrationForm.value['nip'],
                  companyName: this.finishRegistrationForm.value['companyName'],
                });
                this.loading = false;
              }
            },
            error: (e: HttpErrorResponse) => {
              this.loading = false;
              const message = e.error.message as string;
              this.translate
                .get(message || 'exception.unknown')
                .pipe(takeUntil(this.destroy))
                .subscribe((msg) => {
                  this.alertService.danger(msg);
                  this.navigationService.redirectToLoginPage();
                });
            },
          });
      } else {
        this.navigationService.redirectToNotFoundPage();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  finishRegistration(): void {
    this.loading = true;
    const accountGoogleRegister: AccountGoogleRegister = {
      login: this.finishRegistrationForm.value['login']!,
      email: this.email,
      password: 'EmptyPassword!',
      firstName: this.finishRegistrationForm.value['firstName']!,
      lastName: this.finishRegistrationForm.value['lastName']!,
      country: this.finishRegistrationForm.value['country']!,
      city: this.finishRegistrationForm.value['city']!,
      street: this.finishRegistrationForm.value['street']!,
      streetNumber: parseInt(
        this.finishRegistrationForm.value['streetNumber']!
      ),
      postalCode: this.finishRegistrationForm.value['postalCode']!,
      locale: this.locale,
      nip: this.checked ? this.finishRegistrationForm.value['nip']! : null,
      companyName: this.checked
        ? this.finishRegistrationForm.value['companyName']!
        : null,
      idToken: this.idToken,
      timeZone: TimeZone.EUROPE_WARSAW.value,
    };

    this.authenticationService
      .registerGoogleAccount(accountGoogleRegister)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (token) => {
          this.loading = false;
            this.tokenService.saveToken(token);
            this.tokenService.saveAccountType(AccountType.GOOGLE);
            this.translate.get('login.success')
              .pipe(takeUntil(this.destroy))
              .subscribe(msg => {
                this.alertService.success(msg);
                this.navigationService.redirectToMainPage();
              });
        },
        error: (e: HttpErrorResponse) => {
          console.log(e);
          this.loading = false;
          const message = e.error.message as string;
          this.translate
            .get(message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
              if (message) {
                if (message.includes('login')) {
                  this.finishRegistrationForm
                    .get('login')
                    ?.setErrors({ incorrect: true });
                } else if (message.includes('nip')) {
                  this.finishRegistrationForm
                    .get('nip')
                    ?.setErrors({ incorrect: true });
                }
              }
            });
        },
      });
  }

  onConfirm(): void {
    if (this.finishRegistrationForm.valid) {
      this.translate
        .get('dialog.google.register.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.finishRegistration();
              }
            });
        });
    } else {
      this.finishRegistrationForm.markAllAsTouched();
    }
  }

  onCheck(): void {
    if (this.checked) {
      this.finishRegistrationForm
        .get('nip')
        ?.setValidators(
          Validators.compose([Validators.minLength(10), Validators.required])
        );
      this.finishRegistrationForm
        .get('companyName')
        ?.setValidators([
          Validators.pattern(Constants.COMPANY_PATTERN),
          Validators.required,
        ]);
    } else {
      this.finishRegistrationForm.get('nip')?.clearValidators();
      this.finishRegistrationForm.get('companyName')?.clearValidators();
      this.finishRegistrationForm.get('nip')?.updateValueAndValidity();
      this.finishRegistrationForm.get('companyName')?.updateValueAndValidity();
    }
  }
}
