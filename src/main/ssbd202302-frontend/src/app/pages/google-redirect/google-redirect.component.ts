import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { NavigationService } from 'src/app/services/navigation.service';

@Component({
  selector: 'app-google-redirect',
  template: '',
})
export class GoogleRedirectComponent implements OnInit {
  destroy = new Subject<boolean>();

  constructor(
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const state = params['state'] as string;
      const code = params['code'] as string;
      if (code && state) {
        this.authenticationService
          .handleGoogleRedirect(code, state)
          .pipe(takeUntil(this.destroy))
          .subscribe({
            next: (response) => {
              console.log(response);
            },
            error: (e: HttpErrorResponse) => {
              console.log(e);
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
