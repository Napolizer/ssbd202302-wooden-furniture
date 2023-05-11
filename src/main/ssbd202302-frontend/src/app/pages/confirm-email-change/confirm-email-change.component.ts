import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AccountService } from '../../services/account.service';
import { NavigationService } from '../../services/navigation.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-confirm-email-page',
  template: '',
})
export class ConfirmEmailChangeComponent implements OnInit {
  destroy = new Subject<boolean>();

  constructor(
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const token = params['token'];
      if (token) {
        this.accountService
          .confirmEmailChange(token)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: () => {
              this.navigationService.redirectToOwnAccountPageWithState({
                changeEmailSuccess: 'change.email.success',
              });
            },
            error: (e: HttpErrorResponse) => {
              if (e.status == 410) {
                const message = e.error.message as string;
                this.navigationService.redirectToOwnAccountPageWithState({
                  changeEmailError: message,
                });
              } else {
                this.navigationService.redirectToNotFoundPage();
              }
            },
          });
      } else {
        this.navigationService.redirectToNotFoundPage();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }
}
