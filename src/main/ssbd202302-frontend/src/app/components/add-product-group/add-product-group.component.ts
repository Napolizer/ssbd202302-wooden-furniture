import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { Subject, combineLatest, first, map, takeUntil } from 'rxjs';
import { Category } from 'src/app/interfaces/category';
import { ProductGroupCreate } from 'src/app/interfaces/product.group.create';
import { SelectCategory } from 'src/app/interfaces/select.category';
import { CategoryService } from 'src/app/services/category.service';
import { DialogService } from 'src/app/services/dialog.service';
import { NavigationService } from 'src/app/services/navigation.service';
import { ProductService } from 'src/app/services/product.service';
import { Constants } from 'src/app/utils/constants';

@Component({
  selector: 'app-add-product-group',
  templateUrl: './add-product-group.component.html',
  styleUrls: ['./add-product-group.component.sass'],
})
export class AddProductGroupComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  addProductGroupForm: FormGroup;
  categories: SelectCategory[] = [];

  constructor(
    private dialogRef: MatDialogRef<AddProductGroupComponent>,
    private categoryService: CategoryService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private productService: ProductService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.addProductGroupForm = new FormGroup(
      {
        name: new FormControl(
          '',
          Validators.compose([
            Validators.pattern(Constants.CAPITALIZED_PATTERN),
          ])
        ),
        category: new FormControl(''),
      },
      {
        validators: Validators.compose([Validators.required]),
      }
    );
    this.categoryService
      .retrieveAllCategories()
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: (categories) => this.mapCategoriesToDisplayedForm(categories),
        error: (e) => this.handleGetCategoriesError(e),
      });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  mapCategoriesToDisplayedForm(categories: Category[]) {
    categories.forEach(({ name, subcategories }) => {
      this.translate
        .get(name || 'exception.unknown')
        .pipe(takeUntil(this.destroy))
        .subscribe((displayName) => {
          const selectCategory: SelectCategory = {
            value: name,
            viewValue: displayName,
            subcategories: [],
          };
          subcategories.forEach(({ name: subcategoryName }) => {
            this.translate
              .get(subcategoryName || 'exception.unknown')
              .pipe(takeUntil(this.destroy))
              .subscribe((displayName) => {
                selectCategory.subcategories.push({
                  value: subcategoryName,
                  viewValue: displayName,
                  subcategories: [],
                } as SelectCategory);
              });
          });
          this.categories.push(selectCategory);
        });
    });
    this.loading = false;
  }

  handleGetCategoriesError(e: any) {
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
            this.loading = false;
            this.navigationService.redirectToMainPage();
          });
      });
    this.dialogRef.close();
  }

  createProductGroup() {
    const productGroupCreate: ProductGroupCreate = {
      name: this.addProductGroupForm.value['name']!,
      categoryName: this.addProductGroupForm.value['category']!,
    };
    this.productService
      .addProductGroup(productGroupCreate)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.loading = false;
          this.translate
            .get('add.product.group.success' || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.success(msg);
            });
          this.dialogRef.close('success');
        },
        error: (e: HttpErrorResponse) => {
          this.loading = false;
          const message = e.error.message as string;
          this.translate
            .get(message || 'exception.unknown')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              this.alertService.danger(msg);
              if (message && message.includes('name')) {
                this.addProductGroupForm
                  .get('name')
                  ?.setErrors({ incorrect: true });
              }
            });
        },
      });
  }

  onConfirm(): void {
    if (this.addProductGroupForm.valid) {
      this.translate
        .get('dialog.add.product.group.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.createProductGroup();
              }
            });
        });
    } else {
      this.addProductGroupForm.markAllAsTouched();
    }
  }
}
