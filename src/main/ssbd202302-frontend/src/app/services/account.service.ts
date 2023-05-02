import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Account} from "../interfaces/account";
import {environment} from "../../environments/environment";
import {TokenService} from "./token.service";

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) {

  }

  public retrieveOwnAccount(login: string): Observable<Account> {
    return this.httpClient.get<Account>(`${environment.apiBaseUrl}/account/login/${login}`, {
      headers: {
        Authorization: `Bearer ${this.tokenService.getToken()}`
      }
    });
  }

  public retrieveAllAccounts(): Observable<Account[]> {
    return this.httpClient.get<Account[]>(`${environment.apiBaseUrl}/account`, {
      headers: {
        Authorization: `Bearer ${this.tokenService.getToken()}`
      }
    })
  }
}
