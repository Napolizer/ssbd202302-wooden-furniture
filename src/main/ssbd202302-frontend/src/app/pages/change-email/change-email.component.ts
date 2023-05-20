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
import { Subject, combineLatest, first, map, takeUntil } from 'rxjs';
import { Email } from 'src/app/interfaces/email';
import { AccountService } from 'src/app/services/account.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { CustomValidators } from 'src/app/utils/custom.validators';

@Component({
  selector: 'app-change-email',
  templateUrl: './change-email.component.html',
  styleUrls: ['./change-email.component.sass'],
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
export class ChangeEmailComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  id: string;
  version: string;
  currentEmail: string;
  changeEmailForm: FormGroup;

  constructor(
    private accountService: AccountService,
    private translate: TranslateService,
    private authenticationService: AuthenticationService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private alertService: AlertService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.changeEmailForm = new FormGroup({
      currentEmail: new FormControl(''),
      newEmail: new FormControl('', Validators.compose([Validators.email])),
    },
    {
      validators: Validators.compose([
        CustomValidators.EmailsCannotMatch,
        Validators.required,
      ]),
    });
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.id = id;
      this.accountService
        .retrieveAccount(this.id)
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (account) => {
            this.version = account.hash;
            this.changeEmailForm.setValue({
              currentEmail: account.email,
              newEmail: '',
            });
            this.loading = false;
          },
          error: (e) => {
            combineLatest([
              this.translate.get('exception.occurred'),
              this.translate.get(e.error.message || 'exception.unknown'),
            ])
              .pipe(
                first(),
                takeUntil(this.destroy),
                map((data) => ({
                  title: data[0],
                  message: data[1],
                }))
              )
              .subscribe((data) => {
                const ref = this.dialogService.openErrorDialog(
                  data.title,
                  data.message
                );
                ref
                  .afterClosed()
                  .pipe(first(), takeUntil(this.destroy))
                  .subscribe(() => {
                    void this.navigationService.redirectToMainPage();
                  });
              });
          },
        });
    } else {
      this.accountService
        .retrieveOwnAccount(this.authenticationService.getLogin() ?? '')
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (account) => {
            this.id = account.id.toString();
            this.version = account.hash;
            this.changeEmailForm.setValue({
              currentEmail: account.email,
              newEmail: '',
            });
            this.loading = false;
          },
          error: (e) => {
            combineLatest([
              this.translate.get('exception.occurred'),
              this.translate.get(e.error.message || 'exception.unknown'),
            ])
              .pipe(
                first(),
                takeUntil(this.destroy),
                map((data) => ({
                  title: data[0],
                  message: data[1],
                }))
              )
              .subscribe((data) => {
                const ref = this.dialogService.openErrorDialog(
                  data.title,
                  data.message
                );
                ref
                  .afterClosed()
                  .pipe(first(), takeUntil(this.destroy))
                  .subscribe(() => {
                    void this.navigationService.redirectToMainPage();
                  });
              });
          },
        });
    }
    this.changeEmailForm.get('currentEmail')?.disable();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  onBackClicked(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.navigationService.redirectToAccountPage(id);
    } else {
      this.navigationService.redirectToOwnAccountPage();
    }
  }

  changeEmail(): void {
    this.loading = true;
    const email: Email = {
      email: this.changeEmailForm.value['newEmail']!,
    };
    this.accountService
      .changeEmail(this.id, email, this.version)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false;
          const id = this.route.snapshot.paramMap.get('id');
          if (id) {
            this.navigationService.redirectToAccountPageWithState(this.id, {
              changeEmailSuccess: 'change.email.link.sent',
            });
          } else {
            this.navigationService.redirectToOwnAccountPageWithState({
              changeEmailSuccess: 'change.email.link.sent',
            });
          }
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          if (e.status == 409) {
            this.translate
              .get(e.error.message || 'exception.unknown')
              .pipe(takeUntil(this.destroy))
              .subscribe((msg) => {
                this.alertService.danger(msg);
                const id = this.route.snapshot.paramMap.get('id');
                if (id) {
                  this.navigationService.redirectToAccountPage(this.id)
                } else {
                  this.navigationService.redirectToOwnAccountPage();
                }
              });
          } else if (e.status == 403) {
            this.translate
              .get(e.error.message || 'exception.unknown')
              .pipe(takeUntil(this.destroy))
              .subscribe((msg) => {
                this.alertService.danger(msg);
                this.navigationService.redirectToMainPage();
              });
          } else {
            this.navigationService.redirectToNotFoundPage();
          }
        },
      });
  }

  onEmailChangeClicked(): void {
    if (this.changeEmailForm.valid) {
      this.translate
        .get('dialog.change.email.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.changeEmail();
              }
            });
        });
    } else {
      this.changeEmailForm.markAllAsTouched();
    }
  }
}
