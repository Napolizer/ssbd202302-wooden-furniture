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
import { EditProduct } from '../interfaces/edit.product';
import {OrderProductWithRate} from "../interfaces/orderProductWithRate";
import {EditProductGroup} from "../interfaces/edit.product.group";

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

  public retrieveClientProducts(): Observable<OrderProductWithRate[]> {
    return this.httpClient.get<OrderProductWithRate[]>(
      `${environment.apiBaseUrl}/product/client`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        },
      }
    );
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

  retrieveProductGroup(id: string): Observable<ProductGroup> {
    return this.httpClient.get<ProductGroup>(
      `${environment.apiBaseUrl}/product/group/id/` + id,
      {headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      });
  }

  public retrieveProduct(id: string): Observable<Product> {
    return this.httpClient.get<Product>(`${environment.apiBaseUrl}/product/id/` + id);
  }

  public retrieveProductsByGivenProductGroup(productGroupId: string): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${environment.apiBaseUrl}/product/group/products/id/` + productGroupId);
  }

  public retrieveProductsByGivenCategory(productCategoryId: string): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${environment.apiBaseUrl}/product/category/id/` + productCategoryId);
  }

  public editProduct(productId: string, editedProduct: EditProduct): Observable<EditProduct> {
    return this.httpClient.put<EditProduct>(
      `${environment.apiBaseUrl}/product/editProduct/id/` + productId,
      editedProduct,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
      }
    );
  }

  public editProductGroup(productGroupId: string, editedProductGroup: EditProductGroup): Observable<ProductGroup> {
    return this.httpClient.put<ProductGroup>(
      `${environment.apiBaseUrl}/product/group/id/` + productGroupId,
      editedProductGroup,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    )
  }

  public archiveProduct(productId: string): Observable<Product> {
    return this.httpClient.patch<Product>(
      `${environment.apiBaseUrl}/product/archive/` + productId,
      null,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    )
  }

  public archiveProductGroup(productGroupId: string): Observable<ProductGroup> {
    return this.httpClient.put<ProductGroup>(
      `${environment.apiBaseUrl}/product/group/archive/id/` + productGroupId,
      null,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`
        }
      }
    )
  }
}
