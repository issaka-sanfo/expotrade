export interface Order {
  id: string;
  symbol: string;
  side: 'BUY' | 'SELL';
  type: 'MARKET' | 'LIMIT' | 'STOP' | 'STOP_LIMIT';
  quantity: number;
  price: number;
  stopPrice?: number;
  takeProfitPrice?: number;
  stopLossPrice?: number;
  status: 'PENDING' | 'SUBMITTED' | 'PARTIALLY_FILLED' | 'FILLED' | 'CANCELLED' | 'REJECTED' | 'EXPIRED';
  brokerType: 'IBKR' | 'ETORO';
  externalOrderId?: string;
  strategyId?: string;
  userId: string;
  createdAt: string;
  updatedAt?: string;
}

export interface Trade {
  id: string;
  orderId: string;
  symbol: string;
  side: 'BUY' | 'SELL';
  quantity: number;
  price: number;
  commission: number;
  userId: string;
  executedAt: string;
}

export interface MarketData {
  symbol: string;
  bid: number;
  ask: number;
  last: number;
  volume: number;
  high: number;
  low: number;
  open: number;
  close: number;
  timestamp: string;
}

export interface StrategyConfig {
  id: string;
  name: string;
  type: string;
  symbols: string[];
  brokerType: 'IBKR' | 'ETORO';
  status: 'ACTIVE' | 'PAUSED' | 'STOPPED' | 'ERROR';
  maxPositionSize: number;
  stopLossPercent: number;
  takeProfitPercent: number;
  maxDrawdownPercent: number;
  parameters: Record<string, string>;
  userId: string;
}

export interface Portfolio {
  totalValue: number;
  cashBalance: number;
  unrealizedPnl: number;
  realizedPnl: number;
  dayPnl: number;
  maxDrawdown: number;
  positions: Position[];
}

export interface Position {
  id: string;
  symbol: string;
  quantity: number;
  averageEntryPrice: number;
  currentPrice: number;
  unrealizedPnl: number;
  realizedPnl: number;
  brokerType: 'IBKR' | 'ETORO';
  userId: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  username: string;
}

export interface WsEnvelope<T = unknown> {
  topic: string;
  eventType: string;
  timestamp: string;
  data: T;
}
