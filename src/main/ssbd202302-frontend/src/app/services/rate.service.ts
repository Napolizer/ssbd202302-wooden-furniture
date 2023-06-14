import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {TokenService} from "./token.service";
import {Rate} from "../interfaces/rate";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class RateService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) { }

  public createRate(rate: Rate): Observable<Rate> {
    return this.httpClient.post<Rate>(
      `${environment.apiBaseUrl}/rate`,
      rate,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    )
  }

  public changeRate(rate: Rate): Observable<Rate> {
    return this.httpClient.put<Rate>(
      `${environment.apiBaseUrl}/rate`,
      rate,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    )
  }
}
