import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductGroupCreate } from '../interfaces/product.group.create';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TokenService } from './token.service';
import { ProductGroup } from '../interfaces/product.group';
import { Product } from '../interfaces/product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) {}

  public addProductGroup(
    productGroup: ProductGroupCreate
  ): Observable<HttpResponse<any>> {
    return this.httpClient.post(
      `${environment.apiBaseUrl}/product/group`,
      productGroup,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
        observe: 'response',
      }
    );
  }

  public retrieveAllProducts(): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${environment.apiBaseUrl}/product`);
  }

  public retrieveAllProductGroupNames(): Observable<ProductGroup[]> {
    return this.httpClient.get<ProductGroup[]>(
      `${environment.apiBaseUrl}/product/group`
    );
  }
}
