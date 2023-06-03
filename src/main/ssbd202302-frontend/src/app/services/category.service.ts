import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Category } from '../interfaces/category';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  constructor(private httpClient: HttpClient) {}

  public retrieveAllCategories(): Observable<Category[]> {
    return this.httpClient.get<Category[]>(`${environment.apiBaseUrl}/category`);
  }
}
