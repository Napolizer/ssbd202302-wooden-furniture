import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ErrorDialogComponent} from "../components/error-dialog/error-dialog.component";

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
}
