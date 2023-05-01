import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Account} from "../interfaces/account";
import {environment} from "../../environments/environment";
import {TokenService} from "./token.service";
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import { AccountRegister } from '../model/AccountRegister';

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
    private httpClient: HttpClient
  ) {}

  register(account : AccountRegister): Observable<HttpResponse<any>> {
    return this.httpClient.post(`${environment.apiBaseUrl}/account/register`,
     account, {observe: 'response'});
  }
}
