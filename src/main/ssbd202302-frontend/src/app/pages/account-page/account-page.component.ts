import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";
import {first, Subject, takeUntil} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {Account} from "../../interfaces/Account";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-account-page',
  templateUrl: './account-page.component.html',
  styleUrls: ['./account-page.component.sass'],
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
export class AccountPageComponent implements OnInit, OnDestroy {
  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true})
  });
  destroy = new Subject<boolean>();
  account: Partial<Account> = {
    accountState: '',
    email: '',
    groups: [],
    locale: '',
    login: ''
  };
  loading = true;

  constructor(
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.accountService.retrieveOwnAccount('Administrator')
      .pipe(first(), takeUntil(this.destroy))
      .subscribe(account => {
        this.account = account;
        this.loading = false;
      });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  getFormAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }
}
