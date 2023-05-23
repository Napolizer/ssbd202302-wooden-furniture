import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {catchError, first, map, Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import {TokenService} from "./token.service";
import {Role} from "../enums/role";
import { AccountGoogleRegister } from '../interfaces/google.register';
import {AccountRegister} from "../interfaces/account.register";
import {LocalStorageService} from "./local-storage.service";
import { ForcePasswordChange } from '../interfaces/force.password.change';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService,
    private localStorageService: LocalStorageService
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

  public registerGoogleAccount(account: AccountGoogleRegister): Observable<string> {
    return this.httpClient.post(
      `${environment.apiBaseUrl}/account/google/register`, account)
        .pipe(first(), map((response: any) => response.token));
  }

  public getGoogleOauthLink(): Observable<string> {
    return this.httpClient
      .get(`${environment.apiBaseUrl}/account/google/login`)
      .pipe(
        first(),
        map((response: any) => response.url)
      );
  }

  public handleGoogleRedirect(code: string, state: string, locale: string): Observable<any> {
    const params = new HttpParams().set('code', code).set('state', state);
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/account/google/redirect`,
        params.toString(),
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept-Language': locale
          },
          observe: 'response',
        }
      )
      .pipe(first());
  }

  public getGithubOauthLink(): Observable<string> {
    return this.httpClient
      .get(`${environment.apiBaseUrl}/account/github/login`)
      .pipe(
        first(),
        map((response: any) => response.url)
      );
  }

  public registerGithubAccount(account: AccountRegister): Observable<string> {
    return this.httpClient.post(
      `${environment.apiBaseUrl}/account/github/register`, account)
      .pipe(first(), map((response: any) => response.token));
  }

  public handleGithubRedirect(code: string, locale: string): Observable<any> {
    const params = new HttpParams().set('code', code);
    return this.httpClient
      .post(
        `${environment.apiBaseUrl}/account/github/redirect`,
        params.toString(),
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept-Language': locale
          },
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

  public isCurrentRole(role: Role): boolean {
    return this.localStorageService.get(environment.currentRoleKey) == role
  }

  private retrieveForcePasswordChange(): Observable<ForcePasswordChange> {
    return this.httpClient.get<ForcePasswordChange>(
      `${environment.apiBaseUrl}/account/self/force-password-change`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    );
  }

  public isUserForcedToChangePassword(): Observable<any> {
    return this.retrieveForcePasswordChange()
      .pipe(
        map((result) => {
          return result.value ?? false;
        }),
        catchError(() => of(false))
      );
  }
}
