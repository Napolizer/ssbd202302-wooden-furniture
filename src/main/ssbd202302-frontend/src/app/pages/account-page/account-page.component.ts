import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";
import {first, Subject, takeUntil} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {Account} from "../../interfaces/account";

@Component({
  selector: 'app-account-page',
  templateUrl: './account-page.component.html',
  styleUrls: ['./account-page.component.sass']
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

  constructor(
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.accountService.retrieveOwnAccount('Administrator')
      .pipe(first(), takeUntil(this.destroy))
      .subscribe(account => {
        this.account = account;
      });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }
}
