import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Subject, takeUntil, tap} from "rxjs";
import {MatTableDataSource} from "@angular/material/table";
import {ProductService} from "../../services/product.service";
import {NavigationEnd, Router} from "@angular/router";
import {OrderProductWithRate} from "../../interfaces/orderProductWithRate";
import {RateService} from "../../services/rate.service";
import {Rate} from "../../interfaces/rate";
import {HttpErrorResponse} from "@angular/common/http";
import {TranslateService} from "@ngx-translate/core";
import {AlertService} from "@full-fledged/alerts";
import { EventEmitter } from '@angular/core';

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

  orderProducts: OrderProductWithRate[] = [];
  destroy = new Subject<boolean>();
  loading = true;
  dataSource = new MatTableDataSource<OrderProductWithRate>(this.orderProducts);
  sortedProducts: any[] = []; // Array to store the sorted products
  isAscending: boolean = true;
  pageSize = 4; // Number of products per page
  pageSizeOptions: number[] = [4, 8, 12];
  starRating = 2;
  currentPage = 1; // Current page number
  pagedProducts: OrderProductWithRate[] = []; // Paged products to display
// Declare an event emitter
  refreshToolbar: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    private productService: ProductService,
    private rateService: RateService,
    private router: Router,
    private translate: TranslateService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.router.events.pipe(takeUntil(this.destroy)).subscribe((val) => {
      if (val instanceof NavigationEnd) {
        this.loading = true;

        this.refreshToolbar.emit();
      }
    });
    this.loadClientProducts();
  }

  loadClientProducts(): void {
    this.productService
      .retrieveClientProducts()
      .pipe(tap(() => (this.loading = true)), takeUntil(this.destroy))
      .subscribe((products) => {
        this.orderProducts = products.map((orderProduct) => ({
          ...orderProduct,
          image: this.getImageSrc(orderProduct.product.imageUrl),
        }));

        this.loading = false;
        console.log(this.orderProducts);
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
    this.pagedProducts = this.orderProducts.slice(startIndex, endIndex);
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

  onRateChange(orderProduct: OrderProductWithRate): void {
    if (orderProduct.oldRate === 0) {
      this.createRate(orderProduct);
    } else {
      this.updateRate(orderProduct);
    }
  }

  createRate(orderProduct: OrderProductWithRate): void {
    const rate: Rate = {
      rate: orderProduct.rate,
      productId: orderProduct.product.productGroup.id
    }
    this.rateService.createRate(rate)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (rate: Rate) => {
          this.loading = false;
          this.loadClientProducts();
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          orderProduct.rate = orderProduct.oldRate;
          this.translate
            .get(e.error.message || 'exception.moz.rate.notfound')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
            });
        },
      });
  }

  updateRate(orderProduct: OrderProductWithRate): void {
    orderProduct.oldRate = orderProduct.rate;

    const rate: Rate = {
      rate: orderProduct.rate,
      productId: orderProduct.product.productGroup.id
    }
    this.rateService.changeRate(rate)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (rate: Rate) => {
          this.loading = false;
          this.loadClientProducts();
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          orderProduct.rate = orderProduct.oldRate;
          this.translate
            .get(e.error.message || 'exception.moz.rate.notfound')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
            });
        },
      });
  }

  removeRating(orderProduct: OrderProductWithRate): void {
    orderProduct.rate = 0;
    this.rateService.removeRate(orderProduct.product.productGroup.id)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (rate: Rate) => {
          this.loading = false;
          this.loadClientProducts();
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          orderProduct.rate = orderProduct.oldRate;
          this.translate
            .get(e.error.message || 'exception.moz.rate.notfound')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
            });
        },
      });
  }

  onResetClicked() {
    this.loadClientProducts();
  }
}
