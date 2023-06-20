import {Injectable} from "@angular/core";
import {OrderedProduct} from "../interfaces/ordered.product";
import {LocalStorageService} from "./local-storage.service";
import {AlertService} from "@full-fledged/alerts";
import {TranslateService} from "@ngx-translate/core";
import {ProductService} from "./product.service";
import {AccountService} from "./account.service";

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
    private productService: ProductService,
    private accountService: AccountService

  ) {
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
          this.productService.retrieveProduct(product.product.id.toString())
            .subscribe(chosenProduct => {
              product.product = chosenProduct;
              if (product.amount < chosenProduct.amount) {
                product.amount += 1;
                this.saveCart();
                this.translate.get("cart.add.product.success")
                  .subscribe(msg => {
                    this.alertService.success(msg);
                  })
              } else {
                this.translate.get("cart.max.amount.error.message")
                  .subscribe(msg => {
                    this.alertService.danger(msg);
                  })
              }
            })
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

  getCart(): OrderedProduct[] {
    return this.products;
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

  getProductsFromLocalStorage() {
    this.accountService.retrieveOwnAccount()
      .subscribe(account => {
        this.setLocalStorageKey(account.login + "-cart-products");
        if (this.localStorageService.get(this.getLocalStorageKey()) !== null) {
          let orderedProducts: OrderedProduct[] = JSON.parse(this.localStorageService.get(this.localStorageKey)!);
          orderedProducts.forEach(orderedProduct => {
            this.productService.retrieveProduct(orderedProduct.product.id.toString())
              .subscribe(product => {
                if (orderedProduct.product !== product) {
                  orderedProduct.product = product;
                  orderedProduct.price = product.price;
                  if (orderedProduct.product.amount === 0) {
                    this.removeProduct(orderedProduct);
                  }
                  if (orderedProduct.amount > product.amount) {
                    orderedProduct.amount = product.amount;
                  }
                }
              })
          })
          this.setCart(orderedProducts);
        }
      });
  }

  removeProduct(orderedProduct: OrderedProduct) {
    if (this.isProductInCart(orderedProduct)) {
      this.products.forEach((product, index) => {
        if (orderedProduct.product.id === product.product.id) {
          this.products.splice(index, 1);
        }
      })
    }
    this.saveCart();
  }

  isAnyProductArchive(): boolean {
    let isAnyProductArchive: boolean = false;
    this.products.forEach(orderedProduct => {
      if (orderedProduct.product.productGroup.archive) {
        isAnyProductArchive = true;
        return;
      } else {
        if (orderedProduct.product.archive) {
          isAnyProductArchive = true;
          return;
        }
      }
    })
    return isAnyProductArchive;
  }
}
