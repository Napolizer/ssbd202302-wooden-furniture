import { Injectable } from '@angular/core';
import {LocalStorageService} from './local-storage.service';
import {environment} from '../../environments/environment';
import jwtDecode from "jwt-decode";
import {TokenData} from "../interfaces/token.data";
import { AccountType } from '../enums/account.type';
import {Constants} from "../utils/constants";

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private timeoutId: any;

  constructor(
      private localStorageService: LocalStorageService
  ) { }


  public logout(): void {
    this.localStorageService.remove(environment.tokenKey);
    this.localStorageService.remove(environment.refreshTokenKey);
    this.localStorageService.remove(environment.accountTypeKey);
    this.localStorageService.remove(environment.currentRoleKey);
    this.localStorageService.remove(environment.timeoutKey);
    this.clearTimeout()

  }

  public saveToken(token: string): void {
    this.localStorageService.set(environment.tokenKey, token);
  }

  public saveRefreshToken(refreshToken: string): void {
    this.localStorageService.set(environment.refreshTokenKey, refreshToken);
  }

  public saveAccountType(accountType: AccountType): void {
    this.localStorageService.set(environment.accountTypeKey, accountType);
  }

  public saveTimeout(timeout: number): void {
    this.localStorageService.set(environment.timeoutKey, (Date.now() + timeout).toString());
  }

  public getToken(): string | null {
    return this.localStorageService.get(environment.tokenKey);
  }

  public getRefreshToken(): string | null {
    return this.localStorageService.get(environment.refreshTokenKey);
  }

  public getAccountType(): string | null {
    return this.localStorageService.get(environment.accountTypeKey);
  }

  public getTimeout(): string | null {
    return this.localStorageService.get(environment.timeoutKey);
  }

  public getExpirationTime(): number | null {
    return this.getTokenData()?.exp ?? null;
  }

  public isTokenExpired(): boolean {
    const expirationTime = this.getExpirationTime();
    if (expirationTime === null) {
      return true;
    }
    return expirationTime < Date.now() / 1000;
  }

  public getRefreshTokenTime(): number | null {
    const expirationTime = this.getExpirationTime();
    if (expirationTime !== null) {
        return expirationTime * 1000 - Date.now() - Constants.REFRESH_TOKEN_TIME;
      }
    return null;
  }

  public getTokenData(): TokenData | null {
    const token = this.getToken();
    if (token === null) {
      return token;
    }

    try {
      const decodedToken: any = jwtDecode(token);
      if (!decodedToken.sub || !decodedToken.roles) {
        return null;
      }

      return {
        sub: decodedToken.sub,
        roles: decodedToken.roles,
        exp: decodedToken.exp
      }
    } catch (e) {
      return null;
    }
  }

  public setTimeout(callback: () => void, delay: number) {
    this.timeoutId = setTimeout(callback, delay);
  }

  public clearTimeout() {
    clearTimeout(this.timeoutId);
  }
}
