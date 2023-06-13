import {Injectable} from "@angular/core";
import {OrderedProduct} from "../interfaces/ordered.product";
import {LocalStorageService} from "./local-storage.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {Observable, of, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private localStorageKey: string;
  private products: OrderedProduct[] = [];
  public isDoneObservable: Subject<boolean>;
  public isDone: boolean = false;

  constructor(
    private localStorageService: LocalStorageService,
    private translate: TranslateService,
    private alertService: AlertService,

  ) {
    this.isDoneObservable = new Subject();
  }

  setLocalStorageKey(localStorageKey: string) {
    this.localStorageKey = localStorageKey;
  }

  getLocalStorageKey(): string {
    return this.localStorageKey;
  }

  saveCart() {
    this.localStorageService.set(this.localStorageKey, JSON.stringify(this.products));
  }

  addToCart(addedProduct: OrderedProduct) {
    if (this.isProductInCart(addedProduct)) {
      this.products.forEach(product => {
        if (product.product.id == addedProduct.product.id) {
          if (product.amount < addedProduct.product.amount) {
            product.amount += 1;
            this.saveCart();
          } else {
            this.translate.get("cart.max.amount.error.message")
                  .subscribe(msg => {
                    this.alertService.danger(msg);
              })
          }
        }
      })
    } else {
      this.products.push(addedProduct);
      this.saveCart();
      this.translate.get("cart.add.product.success")
        .subscribe(msg => {
          this.alertService.success(msg);
        })
    }
  }

  clearCart(): void {
    this.localStorageService.remove(this.localStorageKey);
    this.products = [];
    this.saveCart();
  }

  clearProducts(): void {
    this.products = [];
  }

  getCart(): Observable<OrderedProduct[]> {
    return of(this.products);
  }

  setCart(orderedProducts: OrderedProduct[]) {
    this.products = orderedProducts;
    this.saveCart();
  }

  isProductInCart(checkedProduct: OrderedProduct): boolean {
    return this.products.some(product => {
      return product.product.id === checkedProduct.product.id;
    })
  }

  getTotalAmountOfProducts(): number {
    let amount: number = 0;
    this.products.forEach(product => {
      amount += product.amount;
    })
    return amount;
  }
}
