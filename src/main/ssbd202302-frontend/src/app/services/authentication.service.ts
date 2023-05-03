import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {first, map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TokenService} from "./token.service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) {}

  public login(login: string, password: string): Observable<string> {
    return this.httpClient.post(`${environment.apiBaseUrl}/account/login`, {
      login: login,
      password: password
    }).pipe(first(), map((response: any) => response.token));
  }

  public logout(): void {
    this.tokenService.logout();
  }

  public isUserLoggedIn(): boolean {
    return !this.tokenService.isTokenExpired();
  }

  public getLogin(): string | null {
    const tokenData = this.tokenService.getTokenData();
    return tokenData?.sub ?? null;
  }

  public getGroups(): string[] {
    const tokenData = this.tokenService.getTokenData();
    return tokenData?.groups ?? [];
  }
}
