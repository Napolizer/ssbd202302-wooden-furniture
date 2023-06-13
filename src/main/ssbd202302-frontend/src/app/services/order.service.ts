import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {TokenService} from "./token.service";
import {OrderDetailsDto} from "../interfaces/order.details.dto";

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) { }

  public getOrders(): Observable<OrderDetailsDto[]> {
    return this.httpClient.get<OrderDetailsDto[]>(
      `${environment.apiBaseUrl}/order`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public changeOrderState(order: OrderDetailsDto, newState: string): Observable<OrderDetailsDto> {
    return this.httpClient.put<OrderDetailsDto>(
      `${environment.apiBaseUrl}/order/state/${order.id}`,
      {
        state: newState.toUpperCase(),
        hash: order.hash
      },
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }
}
