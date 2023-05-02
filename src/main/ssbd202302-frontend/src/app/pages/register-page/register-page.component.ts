import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { AlertService } from '@full-fledged/alerts';
import { Subject, takeUntil } from 'rxjs';
import {
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { AccountService } from 'src/app/services/account.service';
import { CustomValidators } from 'src/app/utils/custom.validators';
import { AccountRegister } from 'src/app/interfaces/AccountRegister';
import { MatStepper } from '@angular/material/stepper';
import { HttpErrorResponse} from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';

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
  accountForm : FormGroup;
  personForm : FormGroup;
  hide = true;
  loaded = false;
  destroy = new Subject<boolean>();
  languages: Language[] = [];

  constructor(
    private alertService: AlertService,
    private accountService: AccountService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    setTimeout(() => {
      this.loaded = true;
    }, 100);

    this.accountForm = new FormGroup(
      {
        email: new FormControl(
          '',
          Validators.compose([Validators.required, Validators.email])
        ),
        login: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.minLength(6),
            Validators.maxLength(20),
            Validators.pattern('^[a-zA-Z][a-zA-Z0-9]*$'),
          ])
        ),
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

    this.personForm = new FormGroup(
      {
        firstName: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        lastName: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        country: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        city: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        street: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.pattern(
              '^[A-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ ]*$'
            ),
          ])
        ),
        streetNumber: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.min(1),
            Validators.max(9999),
          ])
        ),
        postalCode: new FormControl(
          '',
          Validators.compose([
            Validators.required,
            Validators.pattern(
              '[0-9]{2}-[0-9]{3}'
            ),
          ])
        ),
        locale: new FormControl(
          '',
          Validators.compose([
            Validators.required
          ])
        ),
      }
    );
    this.translate.get('register.label.language.pl')
    .pipe(takeUntil(this.destroy))
    .subscribe(msg => {
      this.languages.push({value: 'pl', viewValue: msg as string})
    });

    this.translate.get('register.label.language.en')
    .pipe(takeUntil(this.destroy))
    .subscribe(msg => {
      this.languages.push({ value: 'en', viewValue: msg as string})
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loaded ? 'loaded' : 'unloaded';
  }

  onCreateAccount(): void {
    if (this.personForm.valid) {
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
        locale: this.personForm.value['locale']!
    };
      this.accountService.register(accountRegister)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: () => {
            this.myStepper.next()
          },
          error: (e: HttpErrorResponse) => {
            const message = e.error.message as string;
            this.translate.get(e.error.message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
              if (message.includes('login')) {
                this.accountForm.get('login')?.setErrors({'incorrect': true});
              } else if (message.includes('email')) {
                this.accountForm.get('email')?.setErrors({'incorrect': true});
              }
              this.myStepper.previous()
            });
          }
        })
    } else {
      this.accountForm.markAllAsTouched()
    }
  }
}
