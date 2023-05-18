import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {first, map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TokenService} from "./token.service";
import {Role} from "../enums/role";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) {}

  public login(login: string, password: string, locale : string): Observable<string> {
    return this.httpClient.post(`${environment.apiBaseUrl}/account/login`, {
      login: login,
      password: password
    },
    {
    headers: {
      'Accept-Language': locale
      }
    }).pipe(first(), map((response: any) => response.token));
  }

  public getGithubOauthLink(): Observable<string> {
    return this.httpClient
      .get(`${environment.apiBaseUrl}/account/github/login`)
      .pipe(
        first(),
        map((response: any) => response.url)
      );
  }

  public handleGithubRedirect(code: string): Observable<any> {
    const params = new HttpParams().set('code', code);
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/account/github/redirect`,
        params.toString(),
        {
          headers: { 'Content-Type': 'application/x-www-form-urlencoded'},
          observe: 'response',
        }
      )
      .pipe(first());
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

  public getRoles(): Role[] {
    const tokenData = this.tokenService.getTokenData();
    let roles = [];
    for (let role of tokenData?.roles || []) {
      roles.push(role as Role);
    }
    if (roles.length === 0) {
      roles.push(Role.GUEST);
    }
    return roles;
  }

  public isUserInRole(role: Role): boolean {
    return this.getRoles()?.includes(role);
  }
}
