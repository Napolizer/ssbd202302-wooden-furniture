import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Subject, takeUntil} from "rxjs";
import {AccountService} from "../../services/account.service";
import {NavigationService} from "../../services/navigation.service";
import {HttpErrorResponse} from "@angular/common/http";


@Component({
  selector: 'app-confirm-email-page',
  template: ''
})
export class ConfirmEmailPageComponent implements OnInit {
  destroy = new Subject<boolean>();

  constructor(
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private accountService: AccountService
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const id = params['id']
      this.accountService
        .confirmEmail(id)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: () => {
            this.navigationService.redirectToOwnAccountPage()
          },
          error: (e: HttpErrorResponse) => {
            this.navigationService.redirectToNotFoundPage();
          }
        })
    })
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

}
