import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {TokenService} from "./token.service";
import {OrderDetailsDto} from "../interfaces/order.details.dto";
import {OrderDto} from "../interfaces/order.dto";
import {CreateOrderDto} from "../interfaces/create.order.dto";
import {ClientOrder} from "../interfaces/client.order";
import {OrderWithProductsDto} from "../interfaces/order.with.products.dto";
import {OrderStats} from "../interfaces/order.stats";

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

  public createOrder(createOrderDto: CreateOrderDto): Observable<HttpResponse<OrderDto>> {
    return this.httpClient.post<OrderDto>(
      `${environment.apiBaseUrl}/order/create`,
      createOrderDto,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
        observe: 'response'
      }
    )
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

  public cancelOrderAsEmployee(order: OrderDetailsDto): Observable<OrderDetailsDto> {
    return this.httpClient.put<OrderDetailsDto>(
      `${environment.apiBaseUrl}/order/employee/cancel`,
      {
        id: order.id,
        hash: order.hash
      },
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public observeOrder(order: ClientOrder): Observable<OrderDetailsDto> {
    return this.httpClient.put<OrderDetailsDto>(
      `${environment.apiBaseUrl}/order/observe`,
      {
        id: order.id,
        hash: order.hash
      },
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public cancelOrder(order: ClientOrder): Observable<OrderDetailsDto> {
    return this.httpClient.put<OrderDetailsDto>(
      `${environment.apiBaseUrl}/order/cancel`,
      {
        id: order.id,
        hash: order.hash
      },
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public generateSalesReport(startDate: string, endDate: string, locale: string): Observable<any> {
    return this.httpClient.get<Blob>(
      `${environment.apiBaseUrl}/order/report?startDate=${startDate}&endDate=${endDate}`,
      {
        responseType: 'blob' as 'json',
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
          'Accept-Language': locale
        },

      }
    );
  }

  public getOrderStats(startDate: string, endDate: string): Observable<OrderStats[]> {
    return this.httpClient.get<OrderStats[]>(
      `${environment.apiBaseUrl}/order/statistics?startDate=${startDate}&endDate=${endDate}`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    );
  }

  public getDoneOrders(): Observable<OrderDetailsDto[]> {
    return this.httpClient.get<OrderDetailsDto[]>(
      `${environment.apiBaseUrl}/order/done`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public getOrderAsEmployee(orderId: number): Observable<OrderWithProductsDto> {
    return this.httpClient.get<OrderWithProductsDto>(
      `${environment.apiBaseUrl}/order/id/${orderId}`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public getOrderAsClient(orderId: number): Observable<OrderWithProductsDto> {
    return this.httpClient.get<OrderWithProductsDto>(
      `${environment.apiBaseUrl}/order/id/${orderId}/client`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public getDoneFilterOrders(minPrice: number, maxPrice: number, amount: number): Observable<OrderDetailsDto[]> {
    return this.httpClient.get<OrderDetailsDto[]>(
      `${environment.apiBaseUrl}/order/filters?minPrice=${minPrice}&maxPrice=${maxPrice}&amount=${amount}`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }


}
