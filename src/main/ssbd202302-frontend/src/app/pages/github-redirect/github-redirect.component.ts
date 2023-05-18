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
import {LocalStorageService} from "../../services/local-storage.service";
import {AccountRegister} from "../../interfaces/account.register";

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
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const code = params['code'] as string;
      if (code) {
        this.authenticationService
          .handleGithubRedirect(code)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: (response) => {
              if (response.status == 200) {
                this.tokenService.saveToken(response.body.token);
                this.translate
                  .get('login.success')
                  .pipe(takeUntil(this.destroy))
                  .subscribe(msg => {
                    this.alertService.success(msg);
                    this.navigationService.redirectToMainPage();
                  });
              } else if (response.status == 202) {
                console.log(response);
                this.email = response.body.email;
                this.locale = this.translate.currentLang;
                console.log(this.locale);
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
                this.translate
                  .get([
                    'register.label.language.pl',
                    'register.label.language.en',
                  ])
                  .pipe(takeUntil(this.destroy))
                  .subscribe((msg) => {
                    this.languages.push({
                      value: 'pl',
                      viewValue: msg['register.label.language.pl'] as string,
                    });
                    this.languages.push({
                      value: 'en',
                      viewValue: msg['register.label.language.en'] as string,
                    });
                  });
                this.loading = false;
              }
              console.log(this.finishRegistrationForm);
            },
            error: () => {
              void this.navigationService.redirectToLoginPage();
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
    const accountRegister: AccountRegister = {
      login: this.finishRegistrationForm.value['login']!,
      email: this.email,
      password: 'empty',
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
    };
    console.log(accountRegister);
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
