import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private httpClient: HttpClient
  ) {}

  login(login: string, password: string): Promise<Object> {
    return lastValueFrom(this.httpClient.post(`${environment.apiBaseUrl}/account/login`, {
      login: login,
      password: password
    })).then((response: any) => response.token);
  }
}
