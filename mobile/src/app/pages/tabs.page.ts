import { Component } from '@angular/core';
import { IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { homeOutline, statsChartOutline, cartOutline, swapHorizontalOutline, settingsOutline } from 'ionicons/icons';

@Component({
  selector: 'app-tabs',
  standalone: true,
  imports: [IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel],
  template: `
    <ion-tabs>
      <ion-tab-bar slot="bottom">
        <ion-tab-button tab="dashboard">
          <ion-icon name="home-outline"></ion-icon>
          <ion-label>Dashboard</ion-label>
        </ion-tab-button>
        <ion-tab-button tab="market-data">
          <ion-icon name="stats-chart-outline"></ion-icon>
          <ion-label>Market</ion-label>
        </ion-tab-button>
        <ion-tab-button tab="orders">
          <ion-icon name="cart-outline"></ion-icon>
          <ion-label>Orders</ion-label>
        </ion-tab-button>
        <ion-tab-button tab="trades">
          <ion-icon name="swap-horizontal-outline"></ion-icon>
          <ion-label>Trades</ion-label>
        </ion-tab-button>
        <ion-tab-button tab="strategies">
          <ion-icon name="settings-outline"></ion-icon>
          <ion-label>Strategies</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  `
})
export class TabsPage {
  constructor() {
    addIcons({ homeOutline, statsChartOutline, cartOutline, swapHorizontalOutline, settingsOutline });
  }
}
