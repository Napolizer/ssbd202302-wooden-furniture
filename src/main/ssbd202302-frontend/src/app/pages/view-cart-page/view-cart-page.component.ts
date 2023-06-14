import {Component, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Product} from "../../interfaces/product";
import {combineLatest, map, pipe, Subject, takeUntil, timer} from "rxjs";
import {CartService} from "../../services/cart.service";
import {OrderedProduct} from "../../interfaces/ordered.product";
import {NavigationService} from "../../services/navigation.service";
import {ProductService} from "../../services/product.service";
import {TranslateService} from "@ngx-translate/core";
import {AlertService} from "@full-fledged/alerts";

@Component({
  selector: 'view-cart-page',
  templateUrl: './view-cart-page.component.html',
  styleUrls: ['./view-cart-page.component.sass'],
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
export class ViewCartPageComponent implements OnInit, OnDestroy {
  orderedProducts: OrderedProduct[];
  products: Product[];
  destroy = new Subject<boolean>();
  loading = true;
  totalPrice: number = 0.0;

  constructor(
    private cartService: CartService,
    private navigationService: NavigationService,
    private productService: ProductService,
    private translate: TranslateService,
    private alertService: AlertService
  ) {

  }

    ngOnInit(): void {
    // const sleep = (ms: number | undefined) => new Promise(r => setTimeout(r, ms));
    // await sleep(1000);
    if (!this.cartService.isDone) {
      this.cartService.isDoneObservable.subscribe(() => {
        this.setUpOrderedProducts();
      })
    } else {
      this.setUpOrderedProducts();
    }

  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  shouldDisplaySpinner(): boolean {
    return this.loading;
  }

  redirectToSingleProductPage(productId: string) {
    void this.navigationService.redirectToSingleProductPage(productId);
  }

  setUpOrderedProducts() {
    combineLatest([this.cartService.getCart(), timer(1000)])
      .pipe(takeUntil(this.destroy), map(obj => obj[0]))
      .subscribe(orderedProducts => {
        this.orderedProducts = orderedProducts;
        this.orderedProducts.forEach(orderedProduct => {
          this.productService.retrieveProduct(orderedProduct.product.id.toString())
            .pipe(takeUntil(this.destroy))
            .subscribe(product => {
              this.products.push(product);
              orderedProduct.product = product;
            })
        })
        this.updateTotalPrice();
        this.loading = false;
      });
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

  decreaseAmount(orderedProduct: OrderedProduct) {
    this.orderedProducts.forEach(product => {
      if (orderedProduct.product.id === product.product.id) {
        if (product.amount === 1) {
          this.translate.get("cart.min.amount.error.message")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
            })
          return
        } else {
          product.amount -= 1;
          this.updateTotalPrice();
          return;
        }
      }
    })
    this.cartService.setCart(this.orderedProducts);
  }

  increaseAmount(orderedProduct: OrderedProduct) {
    this.orderedProducts.forEach(product => {
      if (orderedProduct.product.id === product.product.id) {
        if (product.amount + 1 > product.product.amount) {
          this.translate.get("cart.max.amount.error.message")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
            })
          return;
        } else {
          product.amount += 1;
          this.updateTotalPrice();
          return;
        }
      }
    })
    this.cartService.setCart(this.orderedProducts);
  }

  validateChangedAmount(orderedProduct: OrderedProduct) {
    if (orderedProduct.amount < 1) {
      orderedProduct.amount = 1;
      this.translate.get("cart.min.amount.error.message")
        .pipe(takeUntil(this.destroy))
        .subscribe(msg => {
          this.alertService.danger(msg);
        })
    }
    if (orderedProduct.amount > orderedProduct.product.amount) {
      orderedProduct.amount = orderedProduct.product.amount;
      this.translate.get("cart.max.amount.error.message")
        .pipe(takeUntil(this.destroy))
        .subscribe(msg => {
          this.alertService.danger(msg);
        })
    }
  }

  updateTotalPrice() {
    this.totalPrice = 0.0;
    this.orderedProducts.forEach(orderedProduct => {
      this.totalPrice += orderedProduct.product.price * orderedProduct.amount;
    })
  }

  getListAnimationState(): string {
    return this.loading ? 'unloaded' : 'loaded';
  }

  getTotalAmount(): number {
    return this.cartService.getTotalAmountOfProducts();
  }


}
