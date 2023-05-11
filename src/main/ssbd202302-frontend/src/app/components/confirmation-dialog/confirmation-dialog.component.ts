import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.sass'],
})
export class ConfirmationDialogComponent implements OnInit {
  constructor(
    private dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any
  ) {}

  ngOnInit(): void {}

  getMessage(): string {
    return this.data.message;
  }

  getColor(): string {
    return this.data.color;
  }

  onActionClick(): void {
    this.dialogRef.close('action');
  }

  onCancelClick(): void {
    this.dialogRef.close('cancel');
  }
}
