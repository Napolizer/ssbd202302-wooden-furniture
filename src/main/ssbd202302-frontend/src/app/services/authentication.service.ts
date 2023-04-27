import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {first, map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private httpClient: HttpClient
  ) {}

  login(login: string, password: string): Observable<string> {
    return this.httpClient.post(`${environment.apiBaseUrl}/account/login`, {
      login: login,
      password: password
    }).pipe(first(), map((response: any) => response.token));
  }
}
