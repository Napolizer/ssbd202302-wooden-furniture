import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from '../interfaces/account';
import { environment } from '../../environments/environment';
import { TokenService } from './token.service';
import { AccountRegister } from '../interfaces/account.register';
import { ResetPassword } from '../interfaces/reset.password';
import {EditOwnAccount} from "../interfaces/edit.own.account";

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) {}

  public retrieveOwnAccount(login: string): Observable<Account> {
    return this.httpClient.get<Account>(
      `${environment.apiBaseUrl}/account/self`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    );
  }

  public register(account: AccountRegister): Observable<HttpResponse<any>> {
    return this.httpClient.post(
      `${environment.apiBaseUrl}/account/register`,
      account,
      { observe: 'response' }
    );
  }

  public retrieveAllAccounts(): Observable<Account[]> {
    return this.httpClient.get<Account[]>(`${environment.apiBaseUrl}/account`, {
      headers: {
        Authorization: `Bearer ${this.tokenService.getToken()}`
      }
    })
  }

  public retrieveAccount(id: string): Observable<Account> {
    return this.httpClient.get<Account>(
      `${environment.apiBaseUrl}/account/id/` + id,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    )
  }


  public confirm(token: string): Observable<HttpResponse<any>> {
    return this.httpClient.patch(
      `${environment.apiBaseUrl}/account/confirm?token=${token}`,
      null,
      { observe: 'response' }
    );
  }

  public validatePasswordResetToken(token: string): Observable<HttpResponse<any>> {
    return this.httpClient.get(
      `${environment.apiBaseUrl}/account/reset-password?token=${token}`,
      { observe: 'response' }
    );
  }

  public resetPassword(token: string, password: ResetPassword): Observable<HttpResponse<any>> {
    return this.httpClient.put(
      `${environment.apiBaseUrl}/account/reset-password?token=${token}`,
      password,
      { observe: 'response' }
    );
  }

  public editOwnAccount(login: string, account: EditOwnAccount): Observable<EditOwnAccount> {
    console.log("elo")
    return this.httpClient.put<EditOwnAccount>(
      `${environment.apiBaseUrl}/account/login/` + login + `/editOwnAccount`,
      account
    );
  }

  public addAccountGroup(id: string,accountGroup: string): Observable<Account> {
    console.log("eloelo")
    return this.httpClient.put<Account>(
      `${environment.apiBaseUrl}/account/id/` + id + `/accessLevel/` + accountGroup,
      null,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    );
  }

  public removeAccountGroup(id: string, accountGroup: string): Observable<Account> {
    console.log("usuwam")
    return this.httpClient.delete<Account>(
      `${environment.apiBaseUrl}/account/id/` + id + `/accessLevel/` + accountGroup,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    );
  }
}
