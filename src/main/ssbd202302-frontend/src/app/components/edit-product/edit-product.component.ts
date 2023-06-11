import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { Subject, first, takeUntil, combineLatest, map } from 'rxjs';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { EditProduct } from 'src/app/interfaces/edit.product';
import { ProductService } from 'src/app/services/product.service';
import { Product } from 'src/app/interfaces/product';
import { Constants } from 'src/app/utils/constants';

@Component({
  selector: 'app-edit-product',
  templateUrl: './edit-product.component.html',
  styleUrls: ['./edit-product.component.sass']
})
export class EditProductComponent implements OnInit {
  loading = true;
  private destroy = new Subject<void>();

  editProductForm = new FormGroup(
    {
      price: new FormControl(
        '',
        Validators.compose([Validators.pattern(Constants.PRODUCT_PRICE_PATTERN), Validators.max(9999999)])
      ),
      amount: new FormControl(
        '',
        Validators.compose([Validators.min(0), Validators.max(9999999)])
      ),
    },
    {
      validators: Validators.compose([Validators.required]),
    }
  );
  editableProduct: EditProduct = {
    price: 0,
    amount: 0,
    hash: '',
  };
  id = '';

  constructor(
    private dialogRef: MatDialogRef<EditProductComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private translate: TranslateService,
    private dialogService: DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService,
    private productService: ProductService,
    private authenticationService: AuthenticationService
  ) {
    this.id = data ? data.product.id : undefined;
  }

  ngOnInit(): void {
      this.productService
        .retrieveProduct(this.id)
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (product) => this.fillFormWithProduct(product),
          error: (e) => this.handleGetProductError(e),
        });
  }

  ngOnDestroy() {
    this.destroy.next();
    this.destroy.complete();
  }

  getProduct(): Product {
    return this.data.product;
  }

  fillFormWithProduct(product: Product) {
    this.editableProduct.price = product.price;
    this.editableProduct.amount = product.amount;
    this.editableProduct.hash = product.hash;
    this.editProductForm.setValue({
      price: product.price.toString(),
      amount: product.amount.toString()
    });
    this.loading = false;
  }

  handleGetProductError(e: any) {
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
        const ref = this.dialogService.openErrorDialog(
          data.title,
          data.message
        );
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe(() => {
            void this.navigationService.redirectToMainPage();
          });
      });
    this.dialogRef.close();
  }

  saveProduct() {
    this.loading = true;
    this.editableProduct.price = parseFloat(
      this.editProductForm.value.price!
    );
    this.editableProduct.amount = parseInt(
      this.editProductForm.value.amount!
    );
      this.productService
        .editProduct(this.id, this.editableProduct)
        .pipe(first(), takeUntil(this.destroy))
        .subscribe({
          next: (editedProduct) => this.handleEditSuccess(editedProduct),
          error: (e) => this.handleEditError(e),
        });
    }

  handleEditSuccess(editedProduct: EditProduct): void {
    Object.assign(this.getProduct(), editedProduct);
    this.translate
      .get('edit.product.success' || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (msg) => {
          this.alertService.success(msg);
          this.loading = false;
          this.dialogRef.close('action');
        },
      });
  }

  handleEditError(e: any): void {
    const title = this.translate.instant('exception.occurred');
    const message = this.translate.instant(
      e.error.message || 'exception.unknown'
    );
    const ref = this.dialogService.openErrorDialog(title, message);
    ref
      .afterClosed()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe(() => {
        this.loading = false;
        this.dialogRef.close('error');
        void this.navigationService.redirectToSingleProductPage(this.id)
      });
  }

  confirmSave(): void {
    if (this.editProductForm.valid) {
      this.translate
        .get('dialog.employee.edit.product.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result == 'action') {
                this.saveProduct();
              }
            });
        });
    } else {
      this.editProductForm.markAllAsTouched();
    }
  }

  onResetClicked() {
    this.loading = true;
    this.editProductForm.setValue({
      price: this.editableProduct.price!.toString(),
      amount: this.editableProduct.amount!.toString()
    });
    this.loading = false;
  }

  onBackClicked(): void {
    this.dialogRef.close('action');
  }
}
