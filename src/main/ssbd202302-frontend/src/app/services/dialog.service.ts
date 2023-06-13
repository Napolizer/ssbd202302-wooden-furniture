import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ErrorDialogComponent} from "../components/error-dialog/error-dialog.component";
import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import {Account} from "../interfaces/account";
import {AddRoleComponent} from "../components/add-role/add-role.component";
import {ChangeRoleComponent} from "../components/change-role/change-role.component";
import {RemoveRoleComponent} from "../components/remove-role/remove-role.component";
import { EditAccountComponent } from '../components/edit-account/edit-account.component';
import { ChangeEmailComponent } from '../components/change-email/change-email.component';
import { ChangeOwnPasswordComponent } from '../pages/change-own-password/change-own-password.component';
import { CreateAccountComponent } from '../pages/create-account/create-account.component';
import { AddProductGroupComponent } from '../components/add-product-group/add-product-group.component';
import { AddProductComponent } from '../add-product/add-product.component';
import { EditProductComponent } from '../components/edit-product/edit-product.component';
import { Product } from '../interfaces/product';
import {OrderDetailsDto} from "../interfaces/order.details.dto";
import {ChangeOrderStateComponent} from "../components/change-order-state/change-order-state.component";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(
    private matDialog: MatDialog
  ) { }

  openErrorDialog(title: string, message: string): MatDialogRef<ErrorDialogComponent> {
    return this.matDialog.open(ErrorDialogComponent,{
      data: {
        title: title,
        message: message
      }
    });
  }

  openConfirmationDialog(message: string, color : string): MatDialogRef<ConfirmationDialogComponent> {
    return this.matDialog.open(ConfirmationDialogComponent,{
      data: {
        message: message,
        color: color
      }
    });
  }

  openAddRoleDialog(account: Account): MatDialogRef<AddRoleComponent> {
    return this.matDialog.open(AddRoleComponent, {
      data: {account}
    });
  }

  openChangeRoleDialog(account: Account): MatDialogRef<ChangeRoleComponent> {
    return this.matDialog.open(ChangeRoleComponent, {
      data: {account}
    });
  }

  openRemoveRoleDialog(account: Account): MatDialogRef<RemoveRoleComponent> {
    return this.matDialog.open(RemoveRoleComponent, {
      data: {account}
    });
  }

  openChangeEmailDialog(id: string | undefined): MatDialogRef<ChangeEmailComponent> {
    return this.matDialog.open(ChangeEmailComponent, {
      disableClose: true,
      width: '450px',
      height: '380px',
      data: {
        id: id
      }
    });
  }

  openCreateAccountDialog(): MatDialogRef<CreateAccountComponent> {
    return this.matDialog.open(CreateAccountComponent, {
      disableClose: true,
      width: '1100px',
      height: '750px',
    });
  }

  openChangePasswordDialog(): MatDialogRef<ChangeOwnPasswordComponent> {
    return this.matDialog.open(ChangeOwnPasswordComponent, {
      disableClose: true,
      width: '450px',
      height: '520px',
    });
  }

  openEditAccountDialog(account: Account, admin: boolean): MatDialogRef<EditAccountComponent> {
    return this.matDialog.open(EditAccountComponent, {
      disableClose: true,
      width: '600px',
      height: '600px',
      data: { account, admin },
    });
  }

  openAddProductGroupDialog(): MatDialogRef<AddProductGroupComponent> {
    return this.matDialog.open(AddProductGroupComponent, {
      disableClose: true,
      width: '800px',
      height: '500px',
    });
  }

  openAddProductDialog(): MatDialogRef<AddProductComponent> {
    return this.matDialog.open(AddProductComponent, {
      disableClose: true,
      width: '1100px',
      height: '755px',
      autoFocus: false,
    });
  }

  openEditProductDialog(product: Product): MatDialogRef<EditProductComponent> {
    return this.matDialog.open(EditProductComponent, {
      disableClose: true,
      width: '800px',
      height: '500px',
      autoFocus: false,
      data: { product }
    });
  }

  openChangeOrderStateDialog(order: OrderDetailsDto): MatDialogRef<ChangeOrderStateComponent> {
    return this.matDialog.open(ChangeOrderStateComponent, {
      data: {order}
    });
  }
}
