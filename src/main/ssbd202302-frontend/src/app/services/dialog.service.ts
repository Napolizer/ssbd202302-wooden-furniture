import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ErrorDialogComponent} from "../components/error-dialog/error-dialog.component";
import { ConfirmationDialogComponent } from '../components/confirmation-dialog/confirmation-dialog.component';
import {Account} from "../interfaces/account";
import {AddRoleComponent} from "../components/add-role/add-role.component";
import {ChangeRoleComponent} from "../components/change-role/change-role.component";
import {RemoveRoleComponent} from "../components/remove-role/remove-role.component";
import {AdminEditAccountComponent} from "../components/admin-edit-account/admin-edit-account.component";
import { ChangeEmailComponent } from '../components/change-email/change-email.component';

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

  openAdminChangeEmailDialog(id: string): MatDialogRef<ChangeEmailComponent> {
    return this.matDialog.open(ChangeEmailComponent, {
      width: '450px',
      height: '380px',
      data: {
        id: id
      }
    });
  }

  openAdminEditAccountDialog(account: Account): MatDialogRef<AdminEditAccountComponent> {
    return this.matDialog.open(AdminEditAccountComponent, {
      data: {account},
      width: '500px',
    });
  }
}
