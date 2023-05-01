import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { AlertService } from '@full-fledged/alerts';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import {
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { AccountService } from 'src/app/services/account.service';
import { CustomValidators } from 'src/app/utils/custom.validators';
import { AccountRegister } from 'src/app/model/AccountRegister';
import { MatStepper } from '@angular/material/stepper';
import { HttpErrorResponse} from '@angular/common/http';

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
  @ViewChild('stepper') private myStepper: MatStepper
  accountForm : FormGroup
  personForm : FormGroup
  hide = true;
  loaded = false;
  editable = true;
  destroy = new Subject<boolean>();
  languages: Language[] = [
    {value: 'pl', viewValue: 'Polish'},
    {value: 'us', viewValue: 'English'},
  ];
  
  constructor(
    private alertService: AlertService,
    private accountService: AccountService,
    private router: Router
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
            this.editable = false
          },
          error: (e: HttpErrorResponse) => {
            if (e.status === 409) {
              if(e.error.message === 'exception.mok.account.login.already.exists') {
                this.alertService.danger('Account with given login already exists')
                this.accountForm.get('login')?.setErrors({'incorrect': true});
              } else if(e.error.message === 'exception.mok.account.email.already.exists') {
                this.alertService.danger('Account with given email already exists')
                this.accountForm.get('email')?.setErrors({'incorrect': true});
              }
              this.myStepper.previous()
            } else {
              this.alertService.danger('Unknown exception has occurred');
              this.myStepper.reset()
            }
          }
        })
    } else {
      this.accountForm.markAllAsTouched()
    }

  }
}
