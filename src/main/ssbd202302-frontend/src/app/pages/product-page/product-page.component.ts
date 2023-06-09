import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router, NavigationEnd } from '@angular/router';
import { tap, takeUntil, Subject } from 'rxjs';
import { Product } from 'src/app/interfaces/product';
import { NavigationService } from 'src/app/services/navigation.service';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-product-page',
  templateUrl: './product-page.component.html',
  styleUrls: ['./product-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state('loaded', style({
        opacity: 1,
      })),
      state('unloaded', style({
        opacity: 0,
        paddingTop: "20px",
      })),
      transition('loaded => unloaded', [
        animate('0.5s ease-in')
      ]),
      transition('unloaded => loaded', [
        animate('0.5s ease-in')
      ])
    ]),
  ]
})
export class ProductPageComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  destroy = new Subject<boolean>();
  loading = true;
  dataSource = new MatTableDataSource<Product>(this.products);
  sortedProducts: any[] = []; // Array to store the sorted products
  isAscending: boolean = true;
  pageSize = 8; // Number of products per page
  pageSizeOptions: number[] = [4, 8, 12];
  currentPage = 1; // Current page number
  pagedProducts: Product[] = []; // Paged products to display

  constructor(private productService: ProductService, private router: Router, private navigationService: NavigationService) {}

  ngOnInit(): void {
    this.router.events.pipe(takeUntil(this.destroy)).subscribe((val) => {
      if (val instanceof NavigationEnd) {
        this.loading = true;
      }
    });

    this.productService
      .retrieveAllProducts()
      .pipe(tap(() => (this.loading = true)), takeUntil(this.destroy))
      .subscribe((products) => {
        this.products = products;
        this.loading = false;
        console.log(this.products);
        this.updatePagedProducts();
      });
  }

  onPageChange(event: any): void {
    this.pageSize = event.pageSize;
    this.currentPage = event.pageIndex + 1;
    this.updatePagedProducts();
  }

  updatePagedProducts(): void {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedProducts = this.products.slice(startIndex, endIndex);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  sortByPrice(sortOrder: string) {
    switch (sortOrder) {
      case 'default':
        this.sortedProducts = this.products.slice(); // Copy the original products array
        break;
      case 'ascending':
        this.sortedProducts = this.products.slice().sort((a, b) => a.price - b.price);
        this.isAscending = true;
        break;
      case 'descending':
        this.sortedProducts = this.products.slice().sort((a, b) => b.price - a.price);
        this.isAscending = false;
        break;
    }
  }

  shouldDisplaySpinner(): boolean {
    return this.loading;
  }

  getStarArray(averageRating: number): string[] {
    const starArray = [];
    const fullStars = Math.floor(averageRating);
    const halfStars = Math.ceil(averageRating - fullStars);
    const emptyStars = 5 - fullStars - halfStars;

    for (let i = 0; i < fullStars; i++) {
      starArray.push('star');
    }

    for (let i = 0; i < halfStars; i++) {
      starArray.push('star_half');
    }

    for (let i = 0; i < emptyStars; i++) {
      starArray.push('star_border');
    }

    return starArray;
  }

  redirectToSingleProductPage(id: string): void {
    void this.navigationService.redirectToSingleProductPage(id);
  }
}
