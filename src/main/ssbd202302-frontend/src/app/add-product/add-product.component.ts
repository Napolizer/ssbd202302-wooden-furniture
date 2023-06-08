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
  filter,
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
  loadingImage = true;
  productGroupNames: Map<string, number> = new Map();
  allOptions: any[];
  filteredOptions: Observable<any[]>;
  woodTypes: SelectItem[] = [];
  colors: SelectItem[] = [];

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
      .subscribe((productGroups) => {
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
        if (stringValue.length > 6) {
          this.addProductForm
            .get('price')
            ?.setValue(parseFloat(stringValue.slice(0, 6)));
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

  onConfirm(): void {}
}
