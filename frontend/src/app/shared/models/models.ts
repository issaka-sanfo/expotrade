export interface Order {
  id: string;
  symbol: string;
  side: 'BUY' | 'SELL';
  type: 'MARKET' | 'LIMIT' | 'STOP' | 'STOP_LIMIT';
  quantity: number;
  price: number;
  stopLoss: number | null;
  takeProfit: number | null;
  status: string;
  brokerType: 'IBKR' | 'ETORO';
  externalOrderId: string;
  createdAt: string;
}

export interface Position {
  id: string;
  symbol: string;
  quantity: number;
  averageEntryPrice: number;
  currentPrice: number;
  unrealizedPnl: number;
  marketValue: number;
  brokerType: string;
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

export interface Trade {
  id: string;
  orderId: string;
  symbol: string;
  side: string;
  quantity: number;
  price: number;
  commission: number;
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

export interface Strategy {
  id: string;
  name: string;
  type: string;
  symbols: string[];
  brokerType: string;
  status: 'ACTIVE' | 'PAUSED' | 'STOPPED' | 'ERROR';
  maxPositionSize: number;
  stopLossPercent: number;
  takeProfitPercent: number;
  maxDrawdownPercent: number;
  parameters: Record<string, string>;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  username: string;
}
