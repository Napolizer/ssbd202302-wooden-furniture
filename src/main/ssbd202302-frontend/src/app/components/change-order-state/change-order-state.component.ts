import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../services/dialog.service";
import {AlertService} from "@full-fledged/alerts";
import {NavigationService} from "../../services/navigation.service";
import { OrderService } from 'src/app/services/order.service';
import {OrderDetailsDto} from "../../interfaces/order.details.dto";
import {OrderState} from "../../enums/order.state";

@Component({
  selector: 'app-change-order-state',
  templateUrl: './change-order-state.component.html',
  styleUrls: ['./change-order-state.component.sass']
})
export class ChangeOrderStateComponent implements OnInit {
  chosenOrderState: string;
  loading = false;
  private destroy = new Subject<void>();

  constructor(
    private dialogRef: MatDialogRef<ChangeOrderStateComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private translate: TranslateService,
    private dialogService: DialogService,
    private alertService: AlertService,
    private navigationService: NavigationService,
    private orderService: OrderService,
  ) {}

  ngOnInit(): void {}

  ngOnDestroy() {
    this.destroy.next();
    this.destroy.complete();
  }

  getOrder(): OrderDetailsDto {
    return this.data.order;
  }

  onCancelClick(): void {
    this.dialogRef.close('cancel');
  }

  getPossibleOrderStates(): string[] {
    return Object.keys(OrderState)
      .filter(key => key !== OrderState.CANCELLED)
      .map(state => state.toLowerCase());
  }

  confirmChange(accountRole: string): void {
    this.translate
      .get('dialog.change.order.state.message')
      .pipe(takeUntil(this.destroy))
      .subscribe((msg) => {
        const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref
          .afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe((result) => {
            if (result == 'action') {
              this.changeOrderState(accountRole);
            }
          });
      });
  }

  changeOrderState(orderState: string): void {
    this.loading = true;
    this.orderService.changeOrderState(this.getOrder(), orderState)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: changedOrder => {
          this.data.order.hash = changedOrder.hash;
          this.data.order.orderState = changedOrder.orderState;
          this.translate.get('order.change.state.success')
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              this.loading = false;
              this.dialogRef.close('cancel');
            });
        },
        error: e => {
          combineLatest([
            this.translate.get('exception.occurred'),
            this.translate.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              this.alertService.danger(`${data.title}: ${data.message}`);
              this.loading = false;
              this.dialogRef.close('cancel');
            });
        }
      });
  }
}
