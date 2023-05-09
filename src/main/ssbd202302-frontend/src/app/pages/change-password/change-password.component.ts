import { Component, OnInit } from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {AuthenticationService} from "../../services/authentication.service";
import {AccountService} from "../../services/account.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {Account} from "../../interfaces/account";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.sass']
})
export class ChangePasswordComponent implements OnInit {
  get oldPassword() {
    return this.changePasswordForm.controls['oldPassword'];
  }

  get newPassword() {
    return this.changePasswordForm.controls['newPassword'];
  }
  get newPasswordLength() {
    let newPassword = this.changePasswordForm.getRawValue().newPassword;
    if(newPassword !== null) {
      return newPassword.length;
    }
    return 0;
  }
  incorrectPassword: boolean = false;
  loading = true;
  destroy = new Subject<boolean>();
  account: Partial<Account> = {
    accountState: '',
    email: '',
    groups: [],
    locale: '',
    login: ''
  };
  constructor(
    // private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private accountService: AccountService,
    private alertService: AlertService,
    private authenticationService: AuthenticationService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService
  ) { }


  changePasswordForm = this.fb.group({
    oldPassword: ['', [
      Validators.required
    ]],
    newPassword: ['', [
      Validators.required,
      Validators.minLength(8)
    ]]
  });

  ngOnInit(): void {
    this.accountService.retrieveOwnAccount(this.authenticationService.getLogin() ?? '')
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: account => {
          this.account = account;
          this.loading = false;
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

  onSubmit() {
    // const credentials = {
    //   login: this.authService.getLogin(),
    //   password: this.changePasswordForm.getRawValue().oldPassword,
    //   newPassword: this.changePasswordForm.getRawValue().newPassword
    // } as CredentialsChangePassword;
    //
    // this.authService.changePassword(credentials).subscribe(result => {
    //   if(result.status == 200) {
    //     this.authService.logout();
    //     this.router.navigate(['/login']);
    //   }
    // }, error => {
    //   this.incorrectPassword = true;
    // })
  }
}
