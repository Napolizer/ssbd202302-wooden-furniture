import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Product} from "../../interfaces/product";
import {first, Subject, takeUntil} from "rxjs";
import {CartService} from "../../services/cart.service";
import {OrderedProduct} from "../../interfaces/ordered.product";
import {NavigationService} from "../../services/navigation.service";
import {ProductService} from "../../services/product.service";
import {TranslateService} from "@ngx-translate/core";
import {AlertService} from "@full-fledged/alerts";
import {CreateOrderDto} from "../../interfaces/create.order.dto";
import {ShippingDataDto} from "../../interfaces/shipping.data.dto";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Constants} from "../../utils/constants";
import {DialogService} from "../../services/dialog.service";
import {OrderService} from "../../services/order.service";
import {HttpErrorResponse} from "@angular/common/http";
import {OrderProductDto} from "../../interfaces/order.product.dto";

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
  orderedProductsDto: OrderProductDto[] = [];
  products: Product[];
  destroy = new Subject<boolean>();
  loading = true;
  totalPrice: number = 0.0;
  createOrderDto: CreateOrderDto;
  shippingDataDto: ShippingDataDto;
  checked = false;
  shippingDataForm: FormGroup;

  constructor(
    private cartService: CartService,
    private navigationService: NavigationService,
    private productService: ProductService,
    private translate: TranslateService,
    private alertService: AlertService,
    private dialogService: DialogService,
    private orderService: OrderService
  ) {

  }

  async ngOnInit(): Promise<void> {
    const sleep = (ms: number | undefined) => new Promise(r => setTimeout(r, ms));
    await sleep(1000);
    this.cartService.getProductsFromLocalStorage();
    this.orderedProducts = this.cartService.getCart();
    this.orderedProducts.forEach(orderedProduct => {
      this.productService.retrieveProduct(orderedProduct.product.id.toString())
        .pipe(takeUntil(this.destroy))
        .subscribe(product => {
          this.products.push(product);
          orderedProduct.product = product;
          if (orderedProduct.amount > product.amount) {
            orderedProduct.amount = product.amount;
          }
        })
    })
    this.cartService.setCart(this.orderedProducts);
    this.updateTotalPrice();
    this.loading = false;

    this.shippingDataForm = new FormGroup(
      {
        recipientFirstName: new FormControl(''),
        recipientLastName: new FormControl(''),
        country: new FormControl(''),
        city: new FormControl(''),
        street: new FormControl(''),
        streetNumber: new FormControl(''),
        postalCode: new FormControl(''),
      }
    )
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
    this.loading = true;
    this.productService.retrieveProduct(orderedProduct.product.id.toString())
      .pipe(takeUntil(this.destroy))
      .subscribe(product => {
        orderedProduct.product = product;
        if (orderedProduct.amount === 1 || orderedProduct.amount - 1 > product.amount) {
          this.translate.get("cart.min.amount.error.message")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
              orderedProduct.amount = 1;
              this.loading = false;
            })
        } else {
          orderedProduct.amount -= 1;
          this.updateTotalPrice();
          this.cartService.setCart(this.orderedProducts);
          this.loading = false;
        }
      })
  }

  increaseAmount(orderedProduct: OrderedProduct) {
    this.loading = true;
    this.productService.retrieveProduct(orderedProduct.product.id.toString())
      .pipe(takeUntil(this.destroy))
      .subscribe(product => {
        orderedProduct.product = product;
        if (orderedProduct.amount + 1 > product.amount || orderedProduct.amount > product.amount) {
          this.translate.get("cart.max.amount.error.message")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
              orderedProduct.amount = product.amount;
              this.loading = false;
            })
        } else {
          orderedProduct.amount += 1;
          this.updateTotalPrice();
          this.cartService.setCart(this.orderedProducts);
          this.loading = false;
        }
      })
  }

  validateChangedAmount(orderedProduct: OrderedProduct) {
    this.loading = true
    this.productService.retrieveProduct(orderedProduct.product.id.toString())
      .pipe(takeUntil(this.destroy))
      .subscribe(product => {
        if (orderedProduct.amount < 1) {
          orderedProduct.amount = 1;
          this.translate.get("cart.min.amount.error.message")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
              this.loading = false
            })
        }
        if (orderedProduct.amount > product.amount) {
          orderedProduct.amount = product.amount;
          this.translate.get("cart.max.amount.error.message")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
              this.loading = false
            })
        }
      })
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

  onCheck() {
    if (this.checked) {
      this.shippingDataForm
        .get('recipientFirstName')
        ?.setValidators(
          [Validators.pattern(Constants.CAPITALIZED_PATTERN),
          Validators.required,
          ]);
      this.shippingDataForm
        .get('recipientLastName')
        ?.setValidators(
          [Validators.pattern(Constants.CAPITALIZED_PATTERN),
            Validators.required,
          ]);
      this.shippingDataForm
        .get('country')
        ?.setValidators(
          [Validators.pattern(Constants.CAPITALIZED_PATTERN),
            Validators.required,
          ]);
      this.shippingDataForm
        .get('city')
        ?.setValidators(
          [Validators.pattern(Constants.CAPITALIZED_PATTERN),
            Validators.required,
          ]);
      this.shippingDataForm
        .get('street')
        ?.setValidators(
          [Validators.pattern(Constants.CAPITALIZED_PATTERN),
            Validators.required,
          ]);
      this.shippingDataForm
        .get('streetNumber')
        ?.setValidators(
          [Validators.min(1),
            Validators.max(9999),
            Validators.required,
          ]);
      this.shippingDataForm
        .get('postalCode')
        ?.setValidators(
          [Validators.pattern(Constants.POSTAL_CODE_PATTERN),
            Validators.required,
          ]);
    } else {
      this.shippingDataForm.reset();
      this.shippingDataForm.clearValidators();
    }
  }

  createOrder(): void {
    this.loading = true;
    if (this.checked) {
      this.shippingDataDto = {
        recipientFirstName: this.shippingDataForm.value['recipientFirstName'],
        recipientLastName: this.shippingDataForm.value['recipientLastName'],
        recipientAddress: {
          country: this.shippingDataForm.value['country'],
          city: this.shippingDataForm.value['city'],
          street: this.shippingDataForm.value['street'],
          streetNumber: this.shippingDataForm.value['streetNumber'],
          postalCode: this.shippingDataForm.value['postalCode'],
        }
      }
    }
    this.orderedProducts.forEach(orderedProduct => {
      let orderedProductDto: OrderProductDto = {
        amount: orderedProduct.amount,
        productId: orderedProduct.product.id
      }
      this.orderedProductsDto.push(orderedProductDto);
    })
    this.createOrderDto = {
      products: this.orderedProductsDto,
      shippingData: this.shippingDataDto
    }
    this.orderService.createOrder(this.createOrderDto)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false
          this.translate.get("order.create.success")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
            })
          this.cartService.clearCart();
          void this.navigationService.redirectToClientPage();
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          const message = e.error.message as string;
          this.translate
            .get(message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.danger(msg);
              this.orderedProductsDto = []
            })
        }
      })
  }

  onConfirm(): void {
    if (this.shippingDataForm.valid) {
      this.translate
        .get('dialog.create.order.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.createOrder();
              }
            });
        });
    } else {
      this.shippingDataForm.markAllAsTouched();
    }
  }

}
