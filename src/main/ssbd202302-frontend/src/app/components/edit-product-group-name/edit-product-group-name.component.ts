import { Component, OnInit } from '@angular/core';
import {combineLatest, first, map, Observable, startWith, Subject, takeUntil, tap} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ProductService} from "../../services/product.service";
import {CustomValidators} from "../../utils/custom.validators";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {MatDialogRef} from "@angular/material/dialog";
import {Constants} from "../../utils/constants";
import {EditProductGroup} from "../../interfaces/edit.product.group";
import {ProductGroup} from "../../interfaces/product.group";
import {AlertService} from "@full-fledged/alerts";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-edit-product-group-name',
  templateUrl: './edit-product-group-name.component.html',
  styleUrls: ['./edit-product-group-name.component.sass']
})
export class EditProductGroupNameComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  editProductGroupForm: FormGroup;
  filteredOptions: Observable<any[]>;
  productGroupNames: Map<string, number> = new Map();
  allOptions: any[];
  productGroupId: number;
  productGroupToEdit: EditProductGroup = {
    name: '',
    hash: ''
  }

  constructor(
    private productService: ProductService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private dialogRef: MatDialogRef<EditProductGroupNameComponent>,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.editProductGroupForm = new FormGroup(
      {
        productGroupName: new FormControl(''),
        newName: new FormControl(
          '',
          Validators.compose(
            [Validators.pattern(Constants.CAPITALIZED_PATTERN)])
        )
      },
      {
        validators: Validators.compose([Validators.required])
      }
    );
    this.productService.retrieveAllProductGroupNames()
      .pipe(
        tap(() => (this.loading = true)),
        takeUntil(this.destroy))
      .subscribe({
        next: (productGroups) => {
          this.productGroupNames = new Map(
            productGroups.map((productGroup) => [
              productGroup.name,
              productGroup.id
            ])
          );
          this.loading = false;
          this.allOptions = Array.from(
            productGroups.map(
              (productGroup) => ({
                name: productGroup.name,
                categoryName: productGroup.category.name
              } as any)
            )
          );
          this.filteredOptions = this.editProductGroupForm
            .get('productGroupName')
            ?.valueChanges.pipe(
              startWith(''),
              map((value) => this._filter(value || ''))
            ) as Observable<any[]>;
          this.editProductGroupForm
            .get('productGroupName')
            ?.setValidators(
              Validators.compose([
                Validators.required,
                CustomValidators.ProductGroupMustExist(this.productGroupNames)
              ])
            );
          this.editProductGroupForm.get('productGroupName')?.updateValueAndValidity();
        },
        error: (e) => this.handleRetrieveDataError(e)
      })
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onSelectNameChange(): void {
    if (this.editProductGroupForm.get('productGroupName')?.valid) {
      this.productGroupId = this.productGroupNames.get(
        this.editProductGroupForm.get('productGroupName')?.value
      ) as number;
    }
  }

  onConfirm(): void {
    if(this.editProductGroupForm.valid) {
      this.translate
        .get('dialog.edit.product.group')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.productService.retrieveProductGroup(this.productGroupId.toString())
                  .pipe(first(), takeUntil(this.destroy))
                  .subscribe({
                    next: (productGroup) => {
                      this.productGroupToEdit.name = this.editProductGroupForm.value.newName;
                      this.productGroupToEdit.hash = productGroup.hash;
                      this.productService.editProductGroup(this.productGroupId.toString(), this.productGroupToEdit)
                        .pipe(first(), takeUntil(this.destroy))
                        .subscribe({
                          next: (editedProductGroup) => this.handleEditSuccess(editedProductGroup),
                          error: (e) => this.handleEditError(e)
                        });
                    },
                    error: (e) => this.handleRetrieveDataError(e)
                  });
              }
            });
        });
    } else {
      this.editProductGroupForm.markAllAsTouched();
    }
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

  handleEditSuccess(editedProductGroup: ProductGroup): void {
    this.translate
      .get('edit.product.group.success' || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.alertService.success(msg);
      });
    this.dialogRef.close('success');
  }

  handleEditError(e: HttpErrorResponse) {
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
      });
  }
}
