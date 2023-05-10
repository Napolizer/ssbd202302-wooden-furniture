import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AccountService } from 'src/app/services/account.service';
import { NavigationService } from 'src/app/services/navigation.service';

@Component({
  selector: 'app-confirm-account',
  template: '',
})
export class ConfirmAccountComponent implements OnInit, OnDestroy {
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
          .confirm(token)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: () => {
              this.navigationService.redirectToLoginPageWithState({
                confirmAccountSuccess: 'account.confirmation.success',
              });
            },
            error: (e: HttpErrorResponse) => {
              if (e.status == 410) {
                const message = e.error.message as string;
                this.navigationService.redirectToLoginPageWithState({
                  confirmAccountError: message,
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
