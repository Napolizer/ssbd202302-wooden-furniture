import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";
import {TranslateService} from "@ngx-translate/core";
import {AuthenticationService} from "../../services/authentication.service";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {FormControl, FormGroup} from "@angular/forms";
import {EditOwnAccount} from "../../interfaces/edit.own.account";
import {AlertService} from "@full-fledged/alerts";

@Component({
  selector: 'app-edit-own-account',
  templateUrl: './edit-own-account.component.html',
  styleUrls: ['./edit-own-account.component.sass'],
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
export class EditOwnAccountComponent implements OnInit, OnDestroy {
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
  id: string = "";
  editableAccount : EditOwnAccount = {
    firstName: '',
    lastName: '',
    country: '',
    city: '',
    street: '',
    postalCode: '',
    streetNumber: 0,
  }


  constructor(
    private accountService: AccountService,
    private translate: TranslateService,
    private authenticationService: AuthenticationService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.accountService.retrieveOwnAccount(this.authenticationService.getLogin() ?? '')
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: account => {
          this.id = account.id.toString();
          this.editableAccount.firstName = account.firstName;
          this.editableAccount.lastName = account.lastName;
          this.editableAccount.country = account.address.country;
          this.editableAccount.city = account.address.city;
          this.editableAccount.postalCode = account.address.postalCode;
          this.editableAccount.street = account.address.street;
          this.editableAccount.streetNumber = account.address.streetNumber;
          this.editAccountForm.setValue({
            firstName: account.firstName,
            lastName: account.lastName,
            country: account.address.country,
            city: account.address.city,
            street: account.address.street,
            streetNumber: account.address.streetNumber,
            postalCode: account.address.postalCode
          });
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
    this.accountService.editOwnAccount(this.authenticationService.getLogin()!, this.editableAccount)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe(editedAccount => {
        this.editableAccount = editedAccount;
        this.translate.get('edit.success')
          .pipe(takeUntil(this.destroy))
          .subscribe(msg => {
            this.alertService.success(msg)
            this.navigationService.redirectToAccountPage(this.id)
          });
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
}
