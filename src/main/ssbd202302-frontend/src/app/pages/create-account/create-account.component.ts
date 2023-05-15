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
import { Subject, first, takeUntil } from 'rxjs';
import { AccountCreate } from 'src/app/interfaces/account.create';
import { Accesslevel } from 'src/app/interfaces/accesslevel';
import { SelectItem } from 'src/app/interfaces/select.item';
import { AccountService } from 'src/app/services/account.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { Constants } from 'src/app/utils/constants';
import { CustomValidators } from 'src/app/utils/custom.validators';

@Component({
  selector: 'app-create-account',
  templateUrl: './create-account.component.html',
  styleUrls: ['./create-account.component.sass'],
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
export class CreateAccountComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  createAccountForm: FormGroup;
  hide = true;
  languages: SelectItem[] = [];
  roles: SelectItem[] = [];
  checked = false;

  constructor(
    private accountService: AccountService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.createAccountForm = new FormGroup(
      {
        email: new FormControl('', Validators.compose([Validators.email])),
        login: new FormControl(
          '',
          Validators.compose([
            Validators.minLength(6),
            Validators.maxLength(20),
            Validators.pattern(Constants.LOGIN_PATTERN),
          ])
        ),
        password: new FormControl(
          '',
          Validators.compose([
            Validators.minLength(8),
            Validators.maxLength(32),
            Validators.pattern(Constants.PASSWORD_PATTERN),
          ])
        ),
        confirmPassword: new FormControl('', Validators.compose([])),
        firstName: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.CAPITALIZED_PATTERN),
          ])
        ),
        lastName: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.CAPITALIZED_PATTERN),
          ])
        ),
        country: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.CAPITALIZED_PATTERN),
          ])
        ),
        city: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.CAPITALIZED_PATTERN),
          ])
        ),
        street: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.CAPITALIZED_PATTERN),
          ])
        ),
        streetNumber: new FormControl(
          '',
          Validators.compose([Validators.min(1), Validators.max(9999)])
        ),
        postalCode: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.POSTAL_CODE_PATTERN),
          ])
        ),
        locale: new FormControl(''),
        accessLevel: new FormControl(''),
        nip: new FormControl(''),
        companyName: new FormControl(''),
      },
      {
        validators: Validators.compose([
          CustomValidators.MatchPasswords,
          Validators.required,
        ]),
      }
    );
    this.createAccountForm.get('password')?.valueChanges.subscribe(() => {
      this.createAccountForm.get('confirmPassword')?.updateValueAndValidity();
    });
    this.translate
      .get([
        'role.administrator',
        'role.employee',
        'role.sales_rep',
        'role.client',
      ])
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.roles.push({
          value: 'Administrator',
          viewValue: msg['role.administrator'] as string,
        });
        this.roles.push({
          value: 'Employee',
          viewValue: msg['role.employee'] as string,
        });
        this.roles.push({
          value: 'SalesRep',
          viewValue: msg['role.sales_rep'] as string,
        });
        this.roles.push({
          value: 'Client',
          viewValue: msg['role.client'] as string,
        });
      });
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
    this.loading = false;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  createAccount(): void {
    this.loading = true;
    const accountCreate: AccountCreate = {
      login: this.createAccountForm.value['login']!,
      password: this.createAccountForm.value['password']!,
      email: this.createAccountForm.value['email']!,
      firstName: this.createAccountForm.value['firstName']!,
      lastName: this.createAccountForm.value['lastName']!,
      country: this.createAccountForm.value['country']!,
      city: this.createAccountForm.value['city']!,
      street: this.createAccountForm.value['street']!,
      streetNumber: parseInt(this.createAccountForm.value['streetNumber']!),
      postalCode: this.createAccountForm.value['postalCode']!,
      locale: this.createAccountForm.value['locale']!,
      nip: this.checked ? this.createAccountForm.value['nip']! : null,
      companyName: this.checked
        ? this.createAccountForm.value['companyName']!
        : null,
      accessLevel: {
        name: this.createAccountForm.value['accessLevel']!,
      } as Accesslevel,
    };
    this.accountService
      .create(accountCreate)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (account) => {
          this.loading = false;
          this.navigationService.redirectToAccountPageWithState(
            account.id.toString(),
            {
              createAccountSuccess: 'create.account.success',
            }
          );
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
                  this.createAccountForm
                    .get('login')
                    ?.setErrors({ incorrect: true });
                } else if (message.includes('email')) {
                  this.createAccountForm
                    .get('email')
                    ?.setErrors({ incorrect: true });
                } else if (message.includes('nip')) {
                  this.createAccountForm
                    .get('nip')
                    ?.setErrors({ incorrect: true });
                }
              }
            });
        },
      });
  }

  onConfirm(): void {
    if (this.createAccountForm.valid) {
      this.translate
        .get('dialog.create.account.message')
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
      this.createAccountForm.markAllAsTouched();
    }
  }

  onSelect(): void {
    if (this.createAccountForm.value['accessLevel'] !== 'Client') {
      this.checked = false;
      this.onCheck();
    }
  }

  onCheck(): void {
    if (this.checked) {
      this.createAccountForm
        .get('nip')
        ?.setValidators(
          Validators.compose([Validators.minLength(10), Validators.required])
        );
      this.createAccountForm
        .get('companyName')
        ?.setValidators([
          Validators.pattern(Constants.COMPANY_PATTERN),
          Validators.required,
        ]);
    } else {
      this.createAccountForm.get('nip')?.clearValidators();
      this.createAccountForm.get('companyName')?.clearValidators();
      this.createAccountForm.get('nip')?.updateValueAndValidity();
      this.createAccountForm.get('companyName')?.updateValueAndValidity();
    }
  }
}
