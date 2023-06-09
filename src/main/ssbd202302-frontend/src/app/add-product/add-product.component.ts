import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from '../services/dialog.service';
import { NavigationService } from '../services/navigation.service';
import { ProductService } from '../services/product.service';
import { AlertService } from '@full-fledged/alerts';
import {
  Observable,
  Subject,
  combineLatest,
  first,
  map,
  startWith,
  takeUntil,
  tap,
} from 'rxjs';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { WoodType } from 'src/app/utils/wood.type';
import { CustomValidators } from '../utils/custom.validators';
import { SelectItem } from '../interfaces/select.item';
import { Color } from '../utils/color';
import { ProductCreate } from '../interfaces/product.create';
import { HttpErrorResponse } from '@angular/common/http';
import { Product } from '../interfaces/product';

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.sass'],
})
export class AddProductComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  addProductForm: FormGroup;
  displayedImage: string;
  productGroupNames: Map<string, number> = new Map();
  allOptions: any[];
  filteredOptions: Observable<any[]>;
  woodTypes: SelectItem[] = [];
  colors: SelectItem[] = [];
  imageUpload = true;
  allSelected = false;
  image: File;
  imageProductId: number;

  constructor(
    private dialogRef: MatDialogRef<AddProductComponent>,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private productService: ProductService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.addProductForm = new FormGroup(
      {
        productGroupName: new FormControl(''),
        woodType: new FormControl(''),
        color: new FormControl(''),
        furnitureWidth: new FormControl(''),
        furnitureHeight: new FormControl(''),
        furnitureDepth: new FormControl(''),
        weight: new FormControl(''),
        packageWidth: new FormControl(''),
        packageHeight: new FormControl(''),
        packageDepth: new FormControl(''),
        weightInPackage: new FormControl(''),
        price: new FormControl(''),
        quantity: new FormControl(''),
      },
      {
        validators: Validators.compose([Validators.required]),
      }
    );
    this.productService
      .retrieveAllProductGroupNames()
      .pipe(
        tap(() => (this.loading = true)),
        takeUntil(this.destroy)
      )
      .subscribe({
        next: (productGroups) => {
          this.productGroupNames = new Map(
            productGroups.map((productGroup) => [
              productGroup.name,
              productGroup.id,
            ])
          );
          this.loading = false;
          this.allOptions = Array.from(
            productGroups.map(
              (productGroup) =>
                ({
                  name: productGroup.name,
                  categoryName: productGroup.category.name,
                } as any)
            )
          );
          this.filteredOptions = this.addProductForm
            .get('productGroupName')
            ?.valueChanges.pipe(
              startWith(''),
              map((value) => this._filter(value || ''))
            ) as Observable<any[]>;
          this.addProductForm
            .get('productGroupName')
            ?.setValidators(
              Validators.compose([
                Validators.required,
                CustomValidators.ProductGroupMustExist(this.productGroupNames),
              ])
            );
          this.addProductForm.get('productGroupName')?.updateValueAndValidity();
        },
        error: (e) => this.handleRetrieveDataError(e),
      });
    for (const key of Object.keys(WoodType)) {
      const woodType: WoodType = WoodType[key as keyof typeof WoodType];
      this.translate
        .get(woodType.displayName || 'exception.unknown')
        .pipe(takeUntil(this.destroy))
        .subscribe((displayName) => {
          this.woodTypes.push({
            value: woodType.value,
            viewValue: displayName,
          });
        });
    }

    for (const key of Object.keys(Color)) {
      const color: Color = Color[key as keyof typeof Color];
      this.translate
        .get(color.displayName || 'exception.unknown')
        .pipe(takeUntil(this.destroy))
        .subscribe((displayName) => {
          this.colors.push({
            value: color.value,
            viewValue: displayName,
          });
        });
    }
    this.addProductForm
      .get('weight')
      ?.valueChanges.subscribe((value: number) => {
        const stringValue = value ? value.toString() : '';
        if (stringValue.length > 4) {
          this.addProductForm
            .get('weight')
            ?.setValue(parseFloat(stringValue.slice(0, 4)));
        }
      });

    this.addProductForm
      .get('weightInPackage')
      ?.valueChanges.subscribe((value: number) => {
        const stringValue = value ? value.toString() : '';
        if (stringValue.length > 4) {
          this.addProductForm
            .get('weightInPackage')
            ?.setValue(parseFloat(stringValue.slice(0, 4)));
        }
      });

    this.addProductForm
      .get('price')
      ?.valueChanges.subscribe((value: number) => {
        const stringValue = value ? value.toString() : '';
        if (stringValue.length > 8) {
          this.addProductForm
            .get('price')
            ?.setValue(parseFloat(stringValue.slice(0, 8)));
        }
      });
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.allOptions
      .filter(
        (option) =>
          option.name.toLowerCase().includes(filterValue) ||
          option.categoryName.toLowerCase().includes(filterValue)
      )
      .sort((a, b) => a.categoryName.localeCompare(b.categoryName));
  }

  createProduct(): void {
    this.loading = true;
    const productCreate: ProductCreate = {
      amount: this.addProductForm.value['quantity']!,
      color: this.addProductForm.value['color']!,
      furnitureWidth: parseInt(this.addProductForm.value['furnitureWidth']!),
      furnitureHeight: this.addProductForm.value['furnitureHeight']!,
      furnitureDepth: this.addProductForm.value['furnitureDepth']!,
      packageWidth: this.addProductForm.value['packageWidth']!,
      packageHeight: this.addProductForm.value['packageHeight']!,
      packageDepth: this.addProductForm.value['packageDepth']!,
      price: this.addProductForm.value['price']!,
      weight: this.addProductForm.value['weight']!,
      weightInPackage: this.addProductForm.value['weightInPackage']!,
      woodType: this.addProductForm.value['woodType']!,
      productGroupId: this.productGroupNames.get(
        this.addProductForm.value['productGroupName']!
      ) as number,
    };

    if (this.image) {
      this.productService
        .createProductWithNewImage(this.image, productCreate)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: (product) =>
            this.handleCreateProductSuccess(product.body as Product),
          error: (e: HttpErrorResponse) => this.handleCreateProductError(e),
        });
    } else {
      const productCreateWithImage = {
        ...productCreate,
        imageProductId: this.imageProductId,
      };
      this.productService
        .createProductWithExistingImage(productCreateWithImage)
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: (product) =>
            this.handleCreateProductSuccess(product.body as Product),
          error: (e: HttpErrorResponse) => this.handleCreateProductError(e),
        });
    }
  }

  handleCreateProductSuccess(product: Product) {
    this.loading = false;
    this.translate
      .get('add.product.success' || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.alertService.success(msg);
      });
    this.dialogRef.close('success');
  }

  handleRetrieveDataError(e: any) {
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

  handleCreateProductError(e: HttpErrorResponse) {
    this.loading = false;
    const message = e.error.message as string;
    this.translate
      .get(message || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.alertService.danger(msg);
        if (message) {
          if (message.includes('dimensions')) {
            this.addProductForm
              .get('furnitureWidth')
              ?.setErrors({ incorrect: true });
            this.addProductForm
              .get('furnitureHeight')
              ?.setErrors({ incorrect: true });
            this.addProductForm
              .get('furnitureDepth')
              ?.setErrors({ incorrect: true });
            this.addProductForm.get('weight')?.setErrors({ incorrect: true });
          }
        }
      });
  }

  onConfirm(): void {
    if (this.addProductForm.valid) {
      if (this.imageUpload && !this.image) {
        this.translate
          .get('exception.image.required' || 'exception.unknown')
          .pipe(takeUntil(this.destroy))
          .subscribe((msg) => {
            this.alertService.danger(msg);
          });
      } else {
        this.translate
          .get('dialog.add.product.group.message')
          .pipe(takeUntil(this.destroy))
          .subscribe((msg) => {
            const ref = this.dialogService.openConfirmationDialog(
              msg,
              'primary'
            );
            ref
              .afterClosed()
              .pipe(first(), takeUntil(this.destroy))
              .subscribe((result) => {
                if (result === 'action') {
                  this.createProduct();
                }
              });
          });
      }
    } else {
      this.addProductForm.markAllAsTouched();
    }
  }

  onFileAdd(event: any): void {
    if (event.rejectedFiles.length) {
      this.translate
        .get('exception.invalid.image.file.format' || 'exception.unknown')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          this.alertService.danger(msg);
        });
    } else {
      this.image = event.addedFiles[0];
    }
  }

  onFileRemove(): void {
    this.image = null as any;
  }

  onSelectChange(): void {
    if (
      this.addProductForm.get('productGroupName')?.valid &&
      this.addProductForm.get('color')?.valid &&
      this.addProductForm.get('woodType')?.valid
    ) {
      const productGroupId = this.productGroupNames.get(
        this.addProductForm.get('productGroupName')?.value
      ) as number;
      const color = this.addProductForm.get('color')?.value;
      const woodType = this.addProductForm.get('woodType')?.value;
      this.productService
        .retrieveAllProductWithOptions(productGroupId, color, woodType)
        .pipe(takeUntil(this.destroy))
        .subscribe((products) => {
          this.displayedImage = products.at(0)?.imageUrl as string;
          this.imageProductId = this.displayedImage
            ? (products.at(0)?.id as number)
            : 0;
          this.imageUpload = this.displayedImage ? false : true;
          this.allSelected = true;
        });
    } else {
      this.allSelected = false;
      this.imageUpload = true;
      this.onFileRemove();
    }
  }

  handleMissingImage(): void {
    this.imageUpload = true;
  }
}
