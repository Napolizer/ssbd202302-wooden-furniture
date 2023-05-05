import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { AlertService } from '@full-fledged/alerts';
import { Subject, first, takeUntil } from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AccountService } from 'src/app/services/account.service';
import { CustomValidators } from 'src/app/utils/custom.validators';
import { AccountRegister } from 'src/app/interfaces/account.register';
import { MatStepper } from '@angular/material/stepper';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from 'src/app/services/dialog.service';

interface Language {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.sass'],
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
export class RegisterPageComponent implements OnInit, OnDestroy {
  @ViewChild('stepper') private myStepper: MatStepper;
  accountForm: FormGroup;
  personForm: FormGroup;
  hide = true;
  loaded = false;
  loading = false;
  destroy = new Subject<boolean>();
  languages: Language[] = [];

  constructor(
    private alertService: AlertService,
    private accountService: AccountService,
    private translate: TranslateService,
    private dialogService: DialogService
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);

    this.accountForm = new FormGroup(
      {
        email: new FormControl('', Validators.compose([Validators.email])),
        login: new FormControl(
          '',
          Validators.compose([
            Validators.minLength(6),
            Validators.maxLength(20),
            Validators.pattern('^[a-zA-Z][a-zA-Z0-9]*$'),
          ])
        ),
        password: new FormControl(
          '',
          Validators.compose([
            Validators.minLength(8),
            Validators.maxLength(32),
            Validators.pattern('^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$'),
          ])
        ),
        confirmPassword: new FormControl('', Validators.compose([])),
      },
      {
        validators: Validators.compose([
          CustomValidators.MatchPasswords,
          Validators.required,
        ]),
      }
    );

    this.accountForm.get('password')?.valueChanges.subscribe(() => {
      this.accountForm.get('confirmPassword')?.updateValueAndValidity();
    });

    this.personForm = new FormGroup(
      {
        firstName: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        lastName: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        country: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        city: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        street: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        streetNumber: new FormControl(
          '',
          Validators.compose([Validators.min(1), Validators.max(9999)])
        ),
        postalCode: new FormControl(
          '',
          Validators.compose([Validators.pattern('[0-9]{2}-[0-9]{3}')])
        ),
        locale: new FormControl(''),
      },
      {
        validators: Validators.compose([Validators.required]),
      }
    );

    this.translate
      .get(['register.label.language.pl', 'register.label.language.en'])
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
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  createAccount(): void {
    this.loading = true;
    const accountRegister: AccountRegister = {
      login: this.accountForm.value['login']!,
      password: this.accountForm.value['password']!,
      email: this.accountForm.value['email']!,
      firstName: this.personForm.value['firstName']!,
      lastName: this.personForm.value['lastName']!,
      country: this.personForm.value['country']!,
      city: this.personForm.value['city']!,
      street: this.personForm.value['street']!,
      streetNumber: parseInt(this.personForm.value['streetNumber']!),
      postalCode: this.personForm.value['postalCode']!,
      locale: this.personForm.value['locale']!,
    };
    this.accountService
      .register(accountRegister)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false;
          this.myStepper.next();
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          const message = e.error.message as string;
          this.translate
            .get(message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
              if (message) {
                if (message.includes('login')) {
                  this.accountForm.get('login')?.setErrors({ incorrect: true });
                } else if (message.includes('email')) {
                  this.accountForm.get('email')?.setErrors({ incorrect: true });
                }
              }
              this.myStepper.previous();
            });
        },
      });
  }

  onConfirm(): void {
    if (this.personForm.valid) {
      this.translate
        .get('dialog.confirmation.register.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.createAccount();
              }
            });
        });
    } else {
      this.accountForm.markAllAsTouched();
    }
  }
}
