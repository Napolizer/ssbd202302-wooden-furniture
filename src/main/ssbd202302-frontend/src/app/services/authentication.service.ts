import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {first, map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TokenService} from "./token.service";
import {Group} from "../enums/group";

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

  public getGroups(): Group[] {
    const tokenData = this.tokenService.getTokenData();
    let groups = [];
    for (let group of tokenData?.groups || []) {
      groups.push(group as Group);
    }
    if (groups.length === 0) {
      groups.push(Group.GUEST);
    }
    return groups;
  }

  public isUserInGroup(group: Group): boolean {
    return this.getGroups()?.includes(group);
  }
}
