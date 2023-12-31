import {
  trigger,
  state,
  style,
  transition,
  animate,
} from '@angular/animations';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { first, takeUntil, combineLatest, map, Subject } from 'rxjs';
import { Role } from 'src/app/enums/role';
import { Product } from 'src/app/interfaces/product';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { ProductService } from 'src/app/services/product.service';
import { LocalStorageService } from '../../services/local-storage.service';
import { OrderedProduct } from '../../interfaces/ordered.product';
import { environment } from '../../../environments/environment';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-single-product-page',
  templateUrl: './single-product-page.component.html',
  styleUrls: ['./single-product-page.component.sass'],
  animations: [
    trigger('loadedUnloadedList', [
      state(
        'loaded',
        style({
          opacity: 1,
          backgroundColor: 'rgba(221, 221, 221, 1)',
        })
      ),
      state(
        'unloaded',
        style({
          opacity: 0,
          paddingTop: '80px',
          backgroundColor: 'rgba(0, 0, 0, 0)',
        })
      ),
      transition('loaded => unloaded', [animate('0.5s ease-in')]),
      transition('unloaded => loaded', [animate('0.5s ease-in')]),
    ]),
  ],
})
export class SingleProductPageComponent implements OnInit {
  product: Product;
  productsByProductGroup: Product[] = [];
  productsByCategory: Product[] = [];
  displayedColumns: string[] = [
    'image',
    'name',
    'price',
    'color',
    'amount',
    'rating',
  ];
  productData: string[] = [];
  dataSource = new MatTableDataSource<Product>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  id = '';
  destroy = new Subject<boolean>();
  loading = true;

  constructor(
    private productService: ProductService,
    private activatedRoute: ActivatedRoute,
    private translate: TranslateService,
    private navigationService: NavigationService,
    private dialogService: DialogService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private localStorageService: LocalStorageService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id') || '';
    this.productService
      .retrieveProduct(this.id)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: (product) => {
          this.product = product;
          this.setProductData(product);
          this.productService
            .retrieveProductsByGivenProductGroup(
              this.product.productGroup.id.toString()
            )
            .pipe(first(), takeUntil(this.destroy))
            .subscribe({
              next: (productsByProductGroup) => {
                this.productsByProductGroup = productsByProductGroup;
                this.productService
                  .retrieveProductsByGivenCategory(
                    this.product.productGroup.category.id.toString()
                  )
                  .pipe(first(), takeUntil(this.destroy))
                  .subscribe({
                    next: (productsByCategory) => {
                      this.productsByCategory = productsByCategory;
                      this.dataSource = new MatTableDataSource<Product>(
                        this.productsByCategory
                      );
                      this.dataSource.paginator = this.paginator;
                    },
                  });
              },
            });
          this.loading = false;
        },
      });
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

  handleAddToCartButton(product: Product): void {
    if (this.authenticationService.isCurrentRole(Role.GUEST)) {
      this.translate
        .get('cart.user.not.logged.in.error.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          this.alertService.danger(msg);
          void this.navigationService.redirectToLoginPage();
        });
    } else {
      let orderedProduct: OrderedProduct = {
        amount: 1,
        price: product.price,
        product: product,
      };
      this.cartService.addToCart(orderedProduct);
    }
  }

  handleChooseColorButton(productId: string): void {
    void this.navigationService.redirectToSingleProductPage(productId);
  }

  isUserGuestOrClient(): boolean {
    return (
      this.authenticationService.isCurrentRole(Role.CLIENT) ||
      this.authenticationService.isCurrentRole(Role.GUEST)
    );
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

  isUserEmployee(): boolean {
    return this.authenticationService.isCurrentRole(Role.EMPLOYEE);
  }

  openEditProductDialog(): void {
    this.dialogService
      .openEditProductDialog(this.product)
      .afterClosed()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe((result) => {
        if (result === 'success') {
          void this.navigationService.redirectToSingleProductPage(
            this.product.id.toString()
          );
        }
      });
  }

  archiveProduct(): void {
    this.productService
      .archiveProduct(this.product.id.toString())
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.product.archive = true;
          this.translate
            .get('archive.success')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.success(msg);
              void this.navigationService.redirectToSingleProductPage(
                this.product.id.toString()
              );
            });
        },
        error: (e) => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown'),
          ])
            .pipe(
              first(),
              takeUntil(this.destroy),
              map((data) => ({
                title: data[0],
                message: data[1],
              }))
            )
            .subscribe((data) => {
              this.alertService.danger(`${data.title}: ${data.message}`);
              void this.navigationService.redirectToSingleProductPage(
                this.product.id.toString()
              );
            });
        },
      });
  }
  setProductData(product: Product): void {
    this.productData = [
      product.productGroup.name,
      product.price.toString() + ',-',
      product.color,
      product.productGroup.category.name,
      product.furnitureDimensions.width.toString() +
        ' ' +
        product.furnitureDimensions.height.toString() +
        ' ' +
        product.furnitureDimensions.height.toString() +
        ' [m]',
      product.amount.toString(),
      product.productGroup.averageRating.toString(),
    ];
  }

  redirectToProductEditionHistoryPage(): void {
    void this.navigationService.redirectToProductEditionHistoryPage(this.product.id);
  }

  onResetClicked(): void {
    this.loading=true;
    this.id = this.activatedRoute.snapshot.paramMap.get('id') || '';
    this.productService
      .retrieveProduct(this.id).subscribe(product => {
        this.product = product;
        this.loading = false;
      })
  }
}
