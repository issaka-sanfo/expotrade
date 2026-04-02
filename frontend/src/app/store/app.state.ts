import { Portfolio, Order, Strategy, MarketData } from '../shared/models/models';

export interface AppState {
  portfolio: PortfolioState;
  orders: OrderState;
  strategies: StrategyState;
  marketData: MarketDataState;
}

export interface PortfolioState {
  portfolio: Portfolio | null;
  loading: boolean;
  error: string | null;
}

export interface OrderState {
  orders: Order[];
  loading: boolean;
  error: string | null;
}

export interface StrategyState {
  strategies: Strategy[];
  loading: boolean;
  error: string | null;
}

export interface MarketDataState {
  data: Record<string, MarketData>;
}
