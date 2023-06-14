import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AlertService } from '@full-fledged/alerts';
import { TranslateService } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-generate-report',
  templateUrl: './generate-report.component.html',
  styleUrls: ['./generate-report.component.sass'],
})
export class GenerateReportComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  generateReportForm: FormGroup;

  constructor(
    private translate: TranslateService,
    private alertService: AlertService,
    private orderService: OrderService,
    public dialogRef: MatDialogRef<GenerateReportComponent>,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.generateReportForm = new FormGroup(
      {
        startDate: new FormControl<Date | null>(null),
        endDate: new FormControl<Date | null>(null),
      },
      {
        validators: Validators.compose([Validators.required]),
      }
    );
    this.loading = false;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  handleGenerateReportSuccess(response: Blob): void {
    console.log(response);
    this.loading = false;
    const startDate = this.datePipe
      .transform(this.generateReportForm.get('startDate')?.value, 'MM_dd_yyyy')
      ?.toString();
    const endDate = this.datePipe
      .transform(this.generateReportForm.get('endDate')?.value, 'MM_dd_yyyy')
      ?.toString();
    const url = window.URL.createObjectURL(response);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'sales_report_' + startDate + '_to_' + endDate + '.xlsx';
    link.click();
    this.translate
      .get('generate.report.success' || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.alertService.success(msg);
      });
    this.dialogRef.close('success');
  }

  handleGenerateReportError(e: HttpErrorResponse): void {
    this.loading = false;
    const message = e.error.message as string;
    this.translate
      .get(message || 'exception.unknown')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        this.alertService.danger(msg);
      });
  }

  onConfirm(): void {
    if (this.generateReportForm.valid) {
      this.orderService
        .generateSalesReport(
          this.datePipe
            .transform(
              this.generateReportForm.get('startDate')?.value,
              'MM/dd/yyyy'
            )
            ?.toString() as string,
          this.datePipe
            .transform(
              this.generateReportForm.get('endDate')?.value,
              'MM/dd/yyyy'
            )
            ?.toString() as string,
          this.translate.currentLang
        )
        .pipe(takeUntil(this.destroy))
        .subscribe({
          next: (response: Blob) => this.handleGenerateReportSuccess(response),
          error: (e: HttpErrorResponse) => this.handleGenerateReportError(e),
        });
    } else {
      this.generateReportForm.markAllAsTouched();
    }
  }
}
