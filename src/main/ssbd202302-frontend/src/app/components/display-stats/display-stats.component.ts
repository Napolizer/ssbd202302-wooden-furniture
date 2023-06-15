import { Component, OnInit } from '@angular/core';
import {Subject} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {NavigationService} from "../../services/navigation.service";
import {DatePipe} from "@angular/common";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-display-stats',
  templateUrl: './display-stats.component.html',
  styleUrls: ['./display-stats.component.sass']
})
export class DisplayStatsComponent implements OnInit {
  destroy = new Subject<boolean>();
  loading = true;
  displayStatsForm: FormGroup;

  constructor(
    private navigationService: NavigationService,
    private datePipe: DatePipe,
    private dialogRef: MatDialogRef<DisplayStatsComponent>
  ) { }

  ngOnInit(): void {
    this.displayStatsForm = new FormGroup(
      {
        startDate: new FormControl<Date | null>(null),
        endDate: new FormControl<Date | null>(null),
      },
      {
        validators: Validators.compose([Validators.required])
      }
    );
    this.loading = false;
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onConfirm(): void {
    if (this.displayStatsForm.valid) {
      this.navigationService.redirectToStatsPage(
        this.datePipe
          .transform(
            this.displayStatsForm.get('startDate')?.value,
            'YYYY-MM-dd'
          )?.toString() as string,
        this.datePipe
          .transform(this.displayStatsForm.get('endDate')?.value,
            'YYYY-MM-dd'
          )?.toString() as string
      );
      this.dialogRef.close('success');
    }
  }
}

