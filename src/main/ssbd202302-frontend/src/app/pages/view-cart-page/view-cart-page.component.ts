import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Product} from "../../interfaces/product";
import {Subject, takeUntil} from "rxjs";
import {CartService} from "../../services/cart.service";
import {OrderedProduct} from "../../interfaces/orderedProduct";
import {NavigationService} from "../../services/navigation.service";
import {ProductService} from "../../services/product.service";
import {TranslateService} from "@ngx-translate/core";
import {AlertService} from "@full-fledged/alerts";
import {FormControl, Validators} from "@angular/forms";

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

  constructor(
    private cartService: CartService,
    private navigationService: NavigationService,
    private productService: ProductService,
    private translate: TranslateService,
    private alertService: AlertService
  ) {

  }

  ngOnInit(): void {
    this.orderedProducts = this.cartService.getCart();
    console.log(this.orderedProducts)
    this.orderedProducts.forEach(orderedProduct => {
      this.productService.retrieveProduct(orderedProduct.product.id.toString())
        .pipe(takeUntil(this.destroy))
        .subscribe(product => {
          this.products.push(product);
          orderedProduct.product = product;
        })
    })
    this.loading = false;
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

}
