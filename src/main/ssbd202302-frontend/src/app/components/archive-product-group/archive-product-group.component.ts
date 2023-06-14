import { Component, OnInit } from '@angular/core';
import {combineLatest, first, map, Observable, startWith, Subject, takeUntil, tap} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ProductService} from "../../services/product.service";
import {CustomValidators} from "../../utils/custom.validators";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {NavigationService} from "../../services/navigation.service";
import {MatDialogRef} from "@angular/material/dialog";
import {ArchiveProductGroup} from "../../interfaces/archive.product.group";
import {ProductGroup} from "../../interfaces/product.group";
import {AlertService} from "@full-fledged/alerts";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-archive-product-group',
  templateUrl: './archive-product-group.component.html',
  styleUrls: ['./archive-product-group.component.sass']
})
export class ArchiveProductGroupComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  archiveProductGroupForm: FormGroup;
  productGroupNames: Map<string, number> = new Map();
  allOptions: any[];
  filteredOptions: Observable<any[]>;
  productGroupId: number;
  productGroupToArchive: ArchiveProductGroup = {
    hash: ''
  };

  constructor(
    private productService: ProductService,
    private translate: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private dialogRef: MatDialogRef<ArchiveProductGroupComponent>,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.archiveProductGroupForm = new FormGroup(
      {
        productGroupName: new FormControl('')
      },
      {
        validators: Validators.compose([Validators.required])
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
              productGroup.id
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
          this.filteredOptions = this.archiveProductGroupForm
            .get('productGroupName')
            ?.valueChanges.pipe(
              startWith(''),
              map((value) => this._filter(value || ''))
            ) as Observable<any[]>;
          this.archiveProductGroupForm
            .get('productGroupName')
            ?.setValidators(
              Validators.compose([
                Validators.required,
                CustomValidators.ProductGroupMustExist(this.productGroupNames),
              ])
            );
          this.archiveProductGroupForm.get('productGroupName')?.updateValueAndValidity();
        },
        error: (e) => this.handleRetrieveDataError(e),
      });
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onConfirm(): void {
    if (this.archiveProductGroupForm.valid) {
      this.translate
        .get('dialog.archive.product.group')
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
                    next: (productGroup) => this.productGroupToArchive.hash = productGroup.hash,
                    error: (e) => this.handleRetrieveDataError(e)
                  })
                this.productService.archiveProductGroup(this.productGroupId.toString(), this.productGroupToArchive)
                  .pipe(first(), takeUntil(this.destroy))
                  .subscribe({
                    next: (archivedProductGroup) => this.handleArchiveSuccess(archivedProductGroup),
                    error: (e) => this.handleArchiveError(e)
                  });
              }
            });
        });
    } else {
      this.archiveProductGroupForm.markAllAsTouched();
    }
  }

  onSelectNameChange(): void {
    if (this.archiveProductGroupForm.get('productGroupName')?.valid) {
      this.productGroupId = this.productGroupNames.get(
        this.archiveProductGroupForm.get('productGroupName')?.value
      ) as number;
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

  handleArchiveSuccess(archivedProductGroup: ProductGroup): void {
    this.translate
      .get('archive.product.group.success' || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.alertService.success(msg);
      });
    this.dialogRef.close('success');
  }

  handleArchiveError(e: HttpErrorResponse) {
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
