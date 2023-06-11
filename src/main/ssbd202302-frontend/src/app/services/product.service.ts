import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductGroupCreate } from '../interfaces/product.group.create';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TokenService } from './token.service';
import { ProductGroup } from '../interfaces/product.group';
import { Product } from '../interfaces/product';
import { ProductCreate } from '../interfaces/product.create';
import { ProductCreateWithImage } from '../interfaces/product.create with.image';

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

  public retrieveAllProductWithOptions(productGroupId: number, color: string, woodType: string): Observable<Product[]> {
    return this.httpClient.get<Product[]>(
      `${environment.apiBaseUrl}/product/search?productGroupId=${productGroupId}&color=${color}&woodType=${woodType}`
    );
  }

  public createProductWithNewImage(image: File, product: ProductCreate): Observable<HttpResponse<Product>> {
    const formData: FormData = new FormData();
    formData.append('image', image);
    formData.append('product', JSON.stringify(product));
    return this.httpClient.post<Product>(
      `${environment.apiBaseUrl}/product/new-image`,
      formData,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
        observe: 'response'
      }
    );

  }

  public createProductWithExistingImage(product: ProductCreateWithImage): Observable<HttpResponse<Product>> {
    return this.httpClient.post<Product>(
      `${environment.apiBaseUrl}/product/existing-image`,
      product,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
        observe: 'response'
      }
    );
  }


  public retrieveProduct(id: string): Observable<Product> {
    return this.httpClient.get<Product>(`${environment.apiBaseUrl}/product/id/` + id);
  }

  public retrieveProductsByGivenProductGroup(productGroupId: string): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${environment.apiBaseUrl}/product/group/id/` + productGroupId);
  }

  public retrieveProductsByGivenCategory(productCategoryId: string): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${environment.apiBaseUrl}/product/category/id/` + productCategoryId);
  }
}
