import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-confirm-page',
  template: '',
})
export class ConfirmPageComponent implements OnInit, OnDestroy {
  destroy = new Subject<boolean>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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
              this.router.navigate(['/login'], {
                state: {
                  confirmAccountSuccess: 'account.confirmation.success',
                },
              });
            },
            error: (e: HttpErrorResponse) => {
              if (e.status == 410) {
                const message = e.error.message as string;
                this.router.navigate(['/login'], {
                  state: { confirmAccountError: message },
                });
              } else {
                this.router.navigate(['/not-found']);
              }
            },
          });
      } else {
        this.router.navigate(['/not-found']);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }
}
