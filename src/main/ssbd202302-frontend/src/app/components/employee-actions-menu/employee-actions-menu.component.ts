import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {OrderDetailsDto} from "../../interfaces/order.details.dto";
import {Subject, takeUntil} from "rxjs";
import { OrderService } from 'src/app/services/order.service';
import {AlertService} from "@full-fledged/alerts";
import {DialogService} from "../../services/dialog.service";
import {TranslateService} from "@ngx-translate/core";
import {NavigationService} from "../../services/navigation.service";
import {OrderState} from "../../enums/order.state";

@Component({
  selector: 'app-employee-actions-menu',
  templateUrl: './employee-actions-menu.component.html',
  styleUrls: ['./employee-actions-menu.component.sass']
})
export class EmployeeActionsMenuComponent implements OnInit {
  @Input()
  order: OrderDetailsDto;

  @Output()
  loadingChanged = new EventEmitter<boolean>();

  destroy = new Subject<boolean>();

  constructor(
    private orderService: OrderService,
    private alertService: AlertService,
    private dialogService: DialogService,
    private translate: TranslateService,
    private navigationService: NavigationService
  ) { }


  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.complete();
  }

  openChangeRoleDialog(order: OrderDetailsDto): void {
    this.dialogService.openChangeOrderStateDialog(order);
  }

  openCancelOrderDialog(order: OrderDetailsDto): void {
    this.dialogService.openConfirmationDialog(this.translate.instant('order.cancel.confirmation'), 'warn')
      .afterClosed()
      .pipe(takeUntil(this.destroy))
      .subscribe(result => {
        if (result === 'action') {
          this.loadingChanged.emit(true);
          this.orderService.cancelOrderAsEmployee(order)
            .pipe(takeUntil(this.destroy))
            .subscribe(updatedOrder => {
              this.alertService.success(this.translate.instant('order.cancel.success'));
              this.order.hash = updatedOrder.hash;
              this.order.orderState = updatedOrder.orderState;
          }, e => {
              this.alertService.danger(this.translate.instant(e.error.message));
          }).add(() => this.loadingChanged.emit(false));
        }
    });
  }

  canEmployeeCancelOrder(order: OrderDetailsDto): boolean {
    return order.orderState === OrderState.CREATED || order.orderState === OrderState.COMPLETED;
  }
}
