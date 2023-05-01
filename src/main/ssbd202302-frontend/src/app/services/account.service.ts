import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import { AccountRegister } from '../model/AccountRegister';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(
    private httpClient: HttpClient
  ) {}

  register(account : AccountRegister): Observable<HttpResponse<any>> {
    return this.httpClient.post(`${environment.apiBaseUrl}/account/register`,
     account, {observe: 'response'});
  }
}
