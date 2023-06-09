import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TokenService} from "./token.service";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {ClientOrder} from "../interfaces/clientOrder";

@Injectable({
  providedIn: 'root'
})
export class ClientOrderService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) { }

  public getAllClientOrders(): Observable<ClientOrder[]> {
    return this.httpClient.get<ClientOrder[]>(
      `${environment.apiBaseUrl}/order/customer`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    );
  }
}
