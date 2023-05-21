import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {FormControl, FormGroup} from "@angular/forms";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {EditAccount} from "../../interfaces/edit.account";
import {AccountService} from "../../services/account.service";
import {TranslateService} from "@ngx-translate/core";
import {AuthenticationService} from "../../services/authentication.service";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {AlertService} from "@full-fledged/alerts";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-edit-user-account-page',
  templateUrl: './edit-user-account-page.component.html',
  styleUrls: ['./edit-user-account-page.component.sass'],
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
export class EditUserAccountPageComponent implements OnInit {
  editAccountForm = new FormGroup({
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    country: new FormControl(''),
    city: new FormControl(''),
    street: new FormControl(''),
    streetNumber: new FormControl(0),
    postalCode: new FormControl('')
  });
  destroy = new Subject<boolean>();
  loading = true;
  editableAccount : EditAccount = {
    firstName: '',
    lastName: '',
    country: '',
    city: '',
    street: '',
    postalCode: '',
    streetNumber: 0,
    hash: ''
  }
  login = ''
  id = ''

  constructor(
    private activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private translate: TranslateService,
    private authenticationService: AuthenticationService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id') || ''
    this.accountService.retrieveAccount(this.id)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: account => {
          this.editableAccount.firstName = account.firstName;
          this.editableAccount.lastName = account.lastName;
          this.editableAccount.country = account.address.country;
          this.editableAccount.city = account.address.city;
          this.editableAccount.postalCode = account.address.postalCode;
          this.editableAccount.street = account.address.street;
          this.editableAccount.streetNumber = account.address.streetNumber;
          this.editableAccount.hash = account.hash;
          this.editAccountForm.setValue({
            firstName: account.firstName,
            lastName: account.lastName,
            country: account.address.country,
            city: account.address.city,
            street: account.address.street,
            streetNumber: account.address.streetNumber,
            postalCode: account.address.postalCode
          });
          this.login = account.login;
          this.loading = false
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              const ref = this.dialogService.openErrorDialog(data.title, data.message);
              ref.afterClosed()
                .pipe(first(), takeUntil(this.destroy))
                .subscribe(() => {
                  void this.navigationService.redirectToMainPage();
                });
            });
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

  onSaveClicked() {
    this.loading = true
    this.editableAccount.firstName = this.editAccountForm.value.firstName!;
    this.editableAccount.lastName = this.editAccountForm.value.lastName!;
    this.editableAccount.country = this.editAccountForm.value.country!;
    this.editableAccount.city = this.editAccountForm.value.city!;
    this.editableAccount.postalCode = this.editAccountForm.value.postalCode!;
    this.editableAccount.street = this.editAccountForm.value.street!;
    this.editableAccount.streetNumber = this.editAccountForm.value.streetNumber!
    this.accountService.editUserAccount(this.login, this.editableAccount)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: editedAccount => {
          this.editableAccount = editedAccount;
          this.translate.get('edit.success')
            .pipe(takeUntil(this.destroy))
            .subscribe({
              next: msg => {
                this.alertService.success(msg)
                void this.navigationService.redirectToAccountPage(this.id)
              },
              error: e => {
                const title = this.translate.instant(e[0]);
                const message = this.translate.instant(e[1]);
                this.dialogService.openErrorDialog(title, message);
              }
            });
        },
        error: e => {
          const title = this.translate.instant('exception.occurred');
          const message = this.translate.instant(e.error.message || 'exception.unknown');
          const ref = this.dialogService.openErrorDialog(title, message);
          ref.afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe(() => {
              void this.navigationService.redirectToAccountPage(this.id);
            });
        }
      });
    this.loading = false
  }

  onResetClicked() {
    this.loading = true;
    this.editAccountForm.setValue({
      firstName: this.editableAccount.firstName!,
      lastName: this.editableAccount.lastName!,
      country: this.editableAccount.country!,
      city: this.editableAccount.city!,
      street: this.editableAccount.street!,
      streetNumber: this.editableAccount.streetNumber!,
      postalCode: this.editableAccount.postalCode!
    });
    this.loading = false;
  }

  onBackClicked(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id') || '';
    void this.navigationService.redirectToAccountPage(id);
  }
  
}
