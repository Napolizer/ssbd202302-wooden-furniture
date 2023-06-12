import { DOCUMENT } from '@angular/common';
import {AfterViewInit, Component, ElementRef, Inject, OnInit, Renderer2, ViewChild} from '@angular/core';
import { Mode } from 'src/app/interfaces/mode';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-theme-switcher-component',
  templateUrl: './theme-switcher-component.component.html',
  styleUrls: ['./theme-switcher-component.component.sass']
})
export class ThemeSwitcherComponentComponent implements OnInit, AfterViewInit {
  isDarkThemeActive: boolean;
  changeMode: Mode = {
    mode: 'LIGHT'
  }

  @ViewChild('darkModeSwitch', { read: ElementRef }) element: ElementRef | undefined;

  constructor(@Inject(DOCUMENT) private document: Document,
              private accountService: AccountService,
              private renderer: Renderer2
  )
  {}

  ngAfterViewInit(): void {
    this.setIcon();
  }

  setIcon(): void {
    if (this.element) {
      const targetSpan: HTMLElement = this.element.nativeElement.querySelector('.mat-slide-toggle-thumb');
      while (targetSpan.firstChild) {
        targetSpan.firstChild.remove();
      }
      const elem = this.renderer.createElement('mat-icon');
      const icon = this.isChecked() ? 'dark_mode' : 'light_mode';
      elem.setAttribute('class', 'mat-icon notranslate material-icons mat-icon-no-color light-mode-switch-icon');
      elem.style.fontSize = '20px';
      elem.textContent = icon
      targetSpan.appendChild(elem);
    }
  }

  onThemeClick(theme: string): void {
    this.changeMode.mode = theme;
    switch(theme) {
      case 'DARK':
      this.document.body.classList.add('dark-mode');
      this.changeMode.mode = 'DARK';
      this.accountService.changeMode(this.changeMode)
      .subscribe();
        break;
      case 'LIGHT':
      this.document.body.classList.remove('dark-mode');
      this.changeMode.mode='LIGHT';
      this.accountService.changeMode(this.changeMode)
      .subscribe();
        break;
      default:
        break;
    }
    this.setIcon();
  }


  loginInit(): void {
    this.accountService.retrieveOwnMode()
    .subscribe(mode => {
      this.changeMode.mode = mode.mode;
      switch(this.changeMode.mode) {
        case 'DARK':
          this.isDarkThemeActive=true;
          this.document.body.classList.add('dark-mode');
          break;
        case 'LIGHT':
          this.isDarkThemeActive=false;
          this.document.body.classList.remove('dark-mode');
          break;
        default:
          break;
      }
    })
  }

  ngOnInit(): void {
    this.accountService.retrieveOwnMode()
    .subscribe(mode => {
      this.changeMode.mode = mode.mode;
      switch(this.changeMode.mode) {
        case 'DARK':
          this.isDarkThemeActive=false;
          this.document.body.classList.add('dark-mode');
          break;
        case 'LIGHT':
          this.isDarkThemeActive=true;
          this.document.body.classList.remove('dark-mode');
          break;
        default:
          break;
      }
    })
  }

  isChecked(): boolean {
    return document.body.classList.contains('dark-mode');
  }
}
