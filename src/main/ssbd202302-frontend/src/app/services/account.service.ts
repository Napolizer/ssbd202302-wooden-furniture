import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from '../interfaces/account';
import { environment } from '../../environments/environment';
import { TokenService } from './token.service';
import { AccountRegister } from '../interfaces/account.register';
import { ResetPassword } from '../interfaces/reset.password';
import {EditAccount} from "../interfaces/edit.account";
import { Email } from '../interfaces/email';
import {Accesslevel} from "../interfaces/accesslevel";
import {ChangePassword} from "../interfaces/change.password";
import { AccountCreate } from '../interfaces/account.create';

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

  public create(account: AccountCreate): Observable<Account> {
    console.log(account);
    return this.httpClient.post<Account>(
      `${environment.apiBaseUrl}/account/create`,
      account,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
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

  public changeEmail(id : string, email: Email): Observable<HttpResponse<any>> {
    return this.httpClient.put(
      `${environment.apiBaseUrl}/account/change-email/${id}`,
      email,
      { observe: 'response',
        headers: { Authorization: `Bearer ${this.tokenService.getToken()}`}
      }
    )
  }

  public confirmEmailChange(token: string): Observable<HttpResponse<any>> {
    return this.httpClient.patch(
      `${environment.apiBaseUrl}/account/change-email?token=${token}`,
      null,
      { observe: 'response',
        headers: { Authorization: `Bearer ${this.tokenService.getToken()}`}
      }
    )
  }

  public forgotPassword(email: Email): Observable<HttpResponse<any>> {
    return this.httpClient.post(
      `${environment.apiBaseUrl}/account/forgot-password`,
      email,
      { observe: 'response' }
    );
  }

  public validatePasswordResetToken(token: string): Observable<HttpResponse<any>> {
    return this.httpClient.get(
      `${environment.apiBaseUrl}/account/reset-password?token=${token}`,
      { observe: 'response' }
    );
  }

  public validateChangePasswordToken(token: string): Observable<HttpResponse<any>> {
    return this.httpClient.get(
      `${environment.apiBaseUrl}/account/change-password/confirm?token=${token}`,
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

  public changePassword(password: ChangePassword): Observable<HttpResponse<any>> {
    return this.httpClient.put(
      `${environment.apiBaseUrl}/account/self/changePassword`,
      password,
      {
        observe: 'response',
        headers: {
          Authorization: `Bearer ${ this.tokenService.getToken() }`
        },
      }
    )
  }

  public changeUserPassword(login: string): Observable<HttpResponse<any>> {
    return this.httpClient.put(
      `${environment.apiBaseUrl}/account/login/${login}/changePasswordAsAdmin`,
      null,
      {
        observe: 'response',
        headers: {
          Authorization: `Bearer ${ this.tokenService.getToken() }`
        },
      }
    )
  }

  public editOwnAccount(login: string, account: EditAccount): Observable<EditAccount> {
    return this.httpClient.put<EditAccount>(
      `${environment.apiBaseUrl}/account/login/` + login + `/editOwnAccount`,
      account
    );
  }

  public editUserAccount(login: string, account: EditAccount): Observable<EditAccount> {
    return this.httpClient.put<EditAccount>(
      `${environment.apiBaseUrl}/account/login/` + login + `/editAccountAsAdmin`,
      account,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public addAccountRole(id: string, accountRole: string): Observable<Account> {
    return this.httpClient.put<Account>(
      `${environment.apiBaseUrl}/account/id/` + id + `/accessLevel/` + accountRole,
      null,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    );
  }

  public removeAccountRole(id: string, accountRole: string): Observable<Account> {
    return this.httpClient.delete<Account>(
      `${environment.apiBaseUrl}/account/id/` + id + `/accessLevel/` + accountRole,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    );
  }

public changeAccountRole(id: string, accessLevel: Accesslevel): Observable<Account> {
    return this.httpClient.put<Account>(
      `${environment.apiBaseUrl}/account/id/` + id + `/accessLevel/change`,
        accessLevel,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    );
  }

  public blockAccount(id: string): Observable<Account> {
    return this.httpClient.patch<Account>(
      `${environment.apiBaseUrl}/account/block/` + id,
      null,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    )
  }

  public activateAccount(id: string): Observable<Account> {
    return this.httpClient.patch<Account>(
      `${environment.apiBaseUrl}/account/activate/` + id,
      null,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    )
  }
}
