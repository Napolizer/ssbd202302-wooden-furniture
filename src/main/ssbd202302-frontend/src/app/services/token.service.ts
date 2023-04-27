import { Injectable } from '@angular/core';
import {LocalStorageService} from './local-storage.service';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  constructor(
      private localStorageService: LocalStorageService
  ) { }

  public logout(): void {
    this.localStorageService.remove(environment.tokenKey);
  }

  public saveToken(token: string): void {
    this.localStorageService.set(environment.tokenKey, token);
  }

  public getToken(): string | null {
    return this.localStorageService.get(environment.tokenKey);
  }
}
