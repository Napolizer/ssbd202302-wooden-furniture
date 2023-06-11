import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Product} from "../../interfaces/product";
import {Subject, takeUntil, tap} from "rxjs";
import {MatTableDataSource} from "@angular/material/table";
import {ProductService} from "../../services/product.service";
import {NavigationEnd, Router} from "@angular/router";

@Component({
  selector: 'app-client-rates-page',
  templateUrl: './client-rates-page.component.html',
  styleUrls: ['./client-rates-page.component.sass'],
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
export class ClientRatesPageComponent implements OnInit, OnDestroy {

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

  constructor(private productService: ProductService, private router: Router) {}

  ngOnInit(): void {
    this.router.events.pipe(takeUntil(this.destroy)).subscribe((val) => {
      if (val instanceof NavigationEnd) {
        this.loading = true;
      }
    });

    this.productService
      .retrieveClientProducts()
      .pipe(tap(() => (this.loading = true)), takeUntil(this.destroy))
      .subscribe((products) => {
        this.products = products.map((product) => ({
          ...product,
          image: this.getImageSrc(product.imageUrl),
        }));

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

  getImageSrc(image: string): string {
    return 'data:image/jpeg;base64,' + image;
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

}
