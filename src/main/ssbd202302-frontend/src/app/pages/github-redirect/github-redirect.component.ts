import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {first, Subject, takeUntil} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Constants} from "../../utils/constants";
import {ActivatedRoute} from "@angular/router";
import {AuthenticationService} from "../../services/authentication.service";
import {TranslateService} from "@ngx-translate/core";
import {TokenService} from "../../services/token.service";
import {AlertService} from "@full-fledged/alerts";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {SelectItem} from "../../interfaces/select.item";
import {AccountRegister} from "../../interfaces/account.register";
import {AccountService} from "../../services/account.service";
import {HttpErrorResponse} from "@angular/common/http";
import {AccountType} from "../../enums/account.type";
import { TimeZone } from 'src/app/utils/time.zone';

@Component({
  selector: 'app-github-redirect',
  templateUrl: './github-redirect.component.html',
  styleUrls: ['./github-redirect.component.sass'],
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
export class GithubRedirectComponent implements OnInit, OnDestroy {
  destroy = new Subject<boolean>();
  loading = true;
  hide = true;
  languages: SelectItem[] = [];
  checked = false;
  email: string;
  locale: string;

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
    private dialogService: DialogService,
    private accountService: AccountService
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const code = params['code'] as string;
      if (code) {
        this.authenticationService
          .handleGithubRedirect(code, this.translate.getBrowserLang() as string)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: (response) => {
              if (response.status == 200) {
                this.tokenService.saveToken(response.body.token);
                this.tokenService.saveAccountType(AccountType.GITHUB);
                this.translate
                  .get('login.success')
                  .pipe(takeUntil(this.destroy))
                  .subscribe(msg => {
                    this.alertService.success(msg);
                    void this.navigationService.redirectToMainPage();
                  });
              } else if (response.status == 202) {
                this.email = response.body.email;
                this.locale = this.translate.getBrowserLang() as string;
                this.finishRegistrationForm.setValue({
                  login: response.body.login,
                  firstName: this.finishRegistrationForm.value['firstName'],
                  lastName: this.finishRegistrationForm.value['lastName'],
                  country: this.finishRegistrationForm.value['country'],
                  city: this.finishRegistrationForm.value['city'],
                  street: this.finishRegistrationForm.value['street'],
                  streetNumber: this.finishRegistrationForm.value['streetNumber'],
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
                  void this.navigationService.redirectToLoginPage();
                });
            },
          });
      } else {
        void this.navigationService.redirectToNotFoundPage();
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
    const githubAccountRegister: AccountRegister = {
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
      timeZone: TimeZone.EUROPE_WARSAW.value,
    };
    this.authenticationService
      .registerGithubAccount(githubAccountRegister)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (token) => {
          this.loading = false
          this.tokenService.saveToken(token);
          this.translate.get('login.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              void this.navigationService.redirectToMainPage();
            })
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
        }
      });
  }

  onConfirm(): void {
    if (this.finishRegistrationForm.valid) {
      this.translate
        .get('dialog.github.register.message')
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
