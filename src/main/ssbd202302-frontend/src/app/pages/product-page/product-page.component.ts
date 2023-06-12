import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort, MatSortable } from '@angular/material/sort';
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
  sortedProducts: any[] = [];
  isAscending: boolean = true;
  pageSize = 8;
  pageSizeOptions: number[] = [4, 8, 12];
  currentPage = 1;
  pagedProducts: Product[] = [];

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

  getProductColor(color: string): string {
    switch (color) {
      case 'RED':
        return 'product.color.red';
      case 'BLACK':
        return 'product.color.black';
      case 'GREEN':
        return 'product.color.green';
      case 'BLUE':
        return 'product.color.blue';
      case 'BROWN':
        return 'product.color.brown';
      case 'WHITE':
        return 'product.color.white';
      default:
        return '';
    }
  }

  redirectToSingleProductPage(id: string): void {
    void this.navigationService.redirectToSingleProductPage(id);
  }
}
