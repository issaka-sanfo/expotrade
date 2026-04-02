import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { ApiService } from '../../core/services/api.service';
import { Strategy } from '../../shared/models/models';

@Component({
  selector: 'app-strategy',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatSlideToggleModule, MatListModule, MatChipsModule],
  template: `
    <h2>Strategy Manager</h2>
    <div class="card-grid">
      <mat-card>
        <mat-card-header><mat-card-title>Create Strategy</mat-card-title></mat-card-header>
        <mat-card-content>
          <mat-form-field style="width: 100%">
            <mat-label>Name</mat-label>
            <input matInput [(ngModel)]="form.name">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Type</mat-label>
            <mat-select [(ngModel)]="form.type">
              <mat-option value="MOVING_AVERAGE">Moving Average Crossover</mat-option>
              <mat-option value="RSI">RSI</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Symbols (comma separated)</mat-label>
            <input matInput [(ngModel)]="symbolsInput">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Broker</mat-label>
            <mat-select [(ngModel)]="form.brokerType">
              <mat-option value="IBKR">IBKR</mat-option>
              <mat-option value="ETORO">eToro</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field style="width: 48%">
            <mat-label>Stop Loss %</mat-label>
            <input matInput type="number" [(ngModel)]="form.stopLossPercent">
          </mat-form-field>
          <mat-form-field style="width: 48%; margin-left: 4%">
            <mat-label>Take Profit %</mat-label>
            <input matInput type="number" [(ngModel)]="form.takeProfitPercent">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Max Position Size</mat-label>
            <input matInput type="number" [(ngModel)]="form.maxPositionSize">
          </mat-form-field>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="createStrategy()">Create Strategy</button>
        </mat-card-actions>
      </mat-card>

      <mat-card>
        <mat-card-header><mat-card-title>Active Strategies</mat-card-title></mat-card-header>
        <mat-card-content>
          <mat-list>
            <mat-list-item *ngFor="let s of strategies">
              <div style="display: flex; align-items: center; width: 100%; justify-content: space-between;">
                <div>
                  <strong>{{ s.name }}</strong> ({{ s.type }})
                  <br>
                  <small>{{ s.symbols.join(', ') }} | {{ s.brokerType }}</small>
                </div>
                <mat-slide-toggle
                  [checked]="s.status === 'ACTIVE'"
                  (change)="toggleStrategy(s)">
                  {{ s.status }}
                </mat-slide-toggle>
              </div>
            </mat-list-item>
          </mat-list>
          <p *ngIf="strategies.length === 0">No strategies configured.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class StrategyComponent implements OnInit {
  strategies: Strategy[] = [];
  symbolsInput = '';
  form = {
    name: '', type: 'MOVING_AVERAGE', brokerType: 'IBKR',
    stopLossPercent: 2, takeProfitPercent: 5, maxPositionSize: 10000,
    maxDrawdownPercent: 10, parameters: {}
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getStrategies().subscribe(s => this.strategies = s);
  }

  createStrategy(): void {
    const strategy = { ...this.form, symbols: this.symbolsInput.split(',').map(s => s.trim()) };
    this.api.createStrategy(strategy as any).subscribe(s => {
      this.strategies = [...this.strategies, s];
    });
  }

  toggleStrategy(strategy: Strategy): void {
    const action = strategy.status === 'ACTIVE'
      ? this.api.disableStrategy(strategy.id)
      : this.api.enableStrategy(strategy.id);

    action.subscribe(updated => {
      this.strategies = this.strategies.map(s => s.id === updated.id ? updated : s);
    });
  }
}
