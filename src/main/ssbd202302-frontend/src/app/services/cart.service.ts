import {Injectable} from "@angular/core";
import {OrderedProduct} from "../interfaces/orderedProduct";
import {LocalStorageService} from "./local-storage.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private localStorageKey: string;
  private products: OrderedProduct[] = [];

  constructor(
    private localStorageService: LocalStorageService,
    private translate: TranslateService,
    private alertService: AlertService,
  ) {}

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
            this.translate.get("cart.amount.error.message")
                  .subscribe(msg => {
                    this.alertService.danger(msg);
              })
          }
        }
      })
    } else {
      this.products.push(addedProduct);
      this.saveCart();
    }
  }

  clearCart(): void {
    this.products = [];
    this.saveCart();
  }

  clearProducts(): void {
    this.products = [];
  }

  getCart(): OrderedProduct[] {
    return this.products;
  }

  setCart(orderedProducts: OrderedProduct[]) {
    this.products = orderedProducts;
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
