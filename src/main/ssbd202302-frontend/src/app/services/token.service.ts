import { Injectable } from '@angular/core';
import {LocalStorageService} from './local-storage.service';
import {environment} from '../../environments/environment';
import jwtDecode from "jwt-decode";
import {TokenData} from "../interfaces/token.data";
import { AccountType } from '../enums/account.type';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  constructor(
      private localStorageService: LocalStorageService
  ) { }


  public logout(): void {
    this.localStorageService.remove(environment.tokenKey);
    this.localStorageService.remove(environment.accountTypeKey);
  }

  public saveToken(token: string): void {
    this.localStorageService.set(environment.tokenKey, token);
  }

  public saveAccountType(accountType: AccountType): void { 
    this.localStorageService.set(environment.accountTypeKey, accountType);
  }

  public getToken(): string | null {
    return this.localStorageService.get(environment.tokenKey);
  }

  public getAccountType(): string | null {
    return this.localStorageService.get(environment.accountTypeKey);
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
}
