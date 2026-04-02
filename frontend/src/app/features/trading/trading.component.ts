import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { ApiService } from '../../core/services/api.service';
import { Order } from '../../shared/models/models';

@Component({
  selector: 'app-trading',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatTableModule, MatChipsModule],
  template: `
    <h2>Trading Panel</h2>
    <div class="card-grid">
      <mat-card>
        <mat-card-header><mat-card-title>Place Order</mat-card-title></mat-card-header>
        <mat-card-content>
          <mat-form-field style="width: 100%">
            <mat-label>Symbol</mat-label>
            <input matInput [(ngModel)]="orderForm.symbol" placeholder="AAPL">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Side</mat-label>
            <mat-select [(ngModel)]="orderForm.side">
              <mat-option value="BUY">Buy</mat-option>
              <mat-option value="SELL">Sell</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Type</mat-label>
            <mat-select [(ngModel)]="orderForm.type">
              <mat-option value="MARKET">Market</mat-option>
              <mat-option value="LIMIT">Limit</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Quantity</mat-label>
            <input matInput type="number" [(ngModel)]="orderForm.quantity">
          </mat-form-field>
          <mat-form-field style="width: 100%" *ngIf="orderForm.type === 'LIMIT'">
            <mat-label>Price</mat-label>
            <input matInput type="number" [(ngModel)]="orderForm.price">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Broker</mat-label>
            <mat-select [(ngModel)]="orderForm.brokerType">
              <mat-option value="IBKR">Interactive Brokers</mat-option>
              <mat-option value="ETORO">eToro</mat-option>
            </mat-select>
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Stop Loss</mat-label>
            <input matInput type="number" [(ngModel)]="orderForm.stopLoss">
          </mat-form-field>
          <mat-form-field style="width: 100%">
            <mat-label>Take Profit</mat-label>
            <input matInput type="number" [(ngModel)]="orderForm.takeProfit">
          </mat-form-field>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="placeOrder()">Place Order</button>
        </mat-card-actions>
      </mat-card>

      <mat-card>
        <mat-card-header><mat-card-title>Recent Orders</mat-card-title></mat-card-header>
        <mat-card-content>
          <table mat-table [dataSource]="orders" style="width: 100%">
            <ng-container matColumnDef="symbol">
              <th mat-header-cell *matHeaderCellDef>Symbol</th>
              <td mat-cell *matCellDef="let o">{{ o.symbol }}</td>
            </ng-container>
            <ng-container matColumnDef="side">
              <th mat-header-cell *matHeaderCellDef>Side</th>
              <td mat-cell *matCellDef="let o">{{ o.side }}</td>
            </ng-container>
            <ng-container matColumnDef="quantity">
              <th mat-header-cell *matHeaderCellDef>Qty</th>
              <td mat-cell *matCellDef="let o">{{ o.quantity }}</td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef>Status</th>
              <td mat-cell *matCellDef="let o">
                <mat-chip-option [color]="o.status === 'FILLED' ? 'primary' : 'warn'" selected>
                  {{ o.status }}
                </mat-chip-option>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="orderColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: orderColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class TradingComponent implements OnInit {
  orders: Order[] = [];
  orderColumns = ['symbol', 'side', 'quantity', 'status'];
  orderForm = {
    symbol: '', side: 'BUY', type: 'MARKET', quantity: 1,
    price: 0, brokerType: 'IBKR', stopLoss: null as number | null, takeProfit: null as number | null
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getOrders().subscribe(orders => this.orders = orders);
  }

  placeOrder(): void {
    this.api.placeOrder(this.orderForm as any).subscribe(order => {
      this.orders = [order, ...this.orders];
    });
  }
}
