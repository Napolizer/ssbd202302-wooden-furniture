import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {config, first, Subject, takeUntil} from "rxjs";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {AlertService} from "@full-fledged/alerts";
import {NavigationService} from "../../services/navigation.service";
import {AccountService} from "../../services/account.service";
import {Account} from "../../interfaces/account";
import {Email} from "../../interfaces/email";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CustomValidators} from "../../utils/custom.validators";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-admin-change-email',
  templateUrl: './admin-change-email.component.html',
  styleUrls: ['./admin-change-email.component.sass']
})
export class AdminChangeEmailComponent implements OnInit, OnDestroy {
  chosenRole: string;
  loading = false;
  changeEmailForm: FormGroup;
  private destroy = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<AdminChangeEmailComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private translate: TranslateService,
    private dialogService: DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.changeEmailForm = new FormGroup({
        currentEmail: new FormControl(this.getAccount().email),
        newEmail: new FormControl('', Validators.compose([Validators.email])),
      },
      {
        validators: Validators.compose([
          CustomValidators.EmailsCannotMatch,
          Validators.required,
        ]),
      });
    this.changeEmailForm.get('currentEmail')?.disable();
  }

  ngOnDestroy() {
    this.destroy.next();
    this.destroy.complete();
  }

  getAccount(): Account {
    return this.data.account;
  }

  onCancelClick(): void {
    this.dialogRef.close('cancel');
  }

  changeEmail(): void {
    this.loading = true;
    const id = this.getAccount().id.toString();
    const version = this.getAccount().hash;
    const email: Email = {
      email: this.changeEmailForm.value['newEmail']!,
    };
    this.accountService
      .changeEmail(id, email, version)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.translate
            .get('change.email.link.sent' || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.success(msg);
              this.dialogRef.close();
              this.loading = false;
            });
        },
        error: (e: HttpErrorResponse) => {
          this.translate
            .get(e.error.message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
              this.dialogRef.close();
              this.loading = false;
            });
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
