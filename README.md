# ExpoTrade - Automated Trading Bot Platform

A production-grade automated trading platform integrating Interactive Brokers (IBKR) and eToro with real-time monitoring, strategy management, and multi-broker support.

## How the Platform Works

### Architecture Overview

```
[Angular Frontend] --> [Spring Boot API] --> [Broker Adapters (IBKR / eToro)]
        |                     |                          |
    [NgRx Store]    [PostgreSQL / Redis / Kafka]    [Market Data]
                              |
                    [Prometheus --> Grafana]
```

ExpoTrade follows a **hexagonal (clean) architecture**. The domain logic is framework-agnostic, and brokers, databases, and messaging systems are swappable adapters behind port interfaces.

### Core Flow

**1. Authentication**
- Users register and login via `/api/v1/auth` endpoints.
- JWT tokens (1h access, 24h refresh) secure all subsequent requests.
- Passwords are BCrypt-hashed and sessions are stateless.

**2. Trading Strategies (the brain)**
- Users create strategies (Moving Average Crossover or RSI) with parameters: symbols, broker, stop-loss %, take-profit %, max position size.
- The **TradingEngineService** runs a loop **every 5 seconds**:
  1. Fetches all ACTIVE strategies.
  2. Pulls the last 200 market data points from Redis.
  3. Feeds them to the strategy algorithm.
  4. If a **BUY/SELL signal** is generated, it automatically places an order.

**3. Order Execution**
- Orders flow through: `Signal --> RiskManager validation --> BrokerPort --> Broker API`.
- The **RiskManager** checks max position size, max drawdown, and available balance, then calculates stop-loss and take-profit levels.
- Orders are routed to the correct broker adapter (IBKR or eToro) based on strategy config.
- Both brokers are currently **simulated** (paper trading mode).

**4. Market Data**
- Real-time data streams via **WebSocket** (`/ws/market-data`).
- Each tick is cached in **Redis** (latest price + rolling 500-item history).
- Published to the **Kafka** topic `expotrade.market-data` for downstream consumers.

**5. Portfolio**
- Aggregates all positions and enriches them with the latest market prices from Redis.
- Computes: total value, cash balance, unrealized/realized P&L, and day P&L.

### Event-Driven Architecture (Kafka Topics)

| Topic | Events |
|---|---|
| `expotrade.orders` | ORDER_PLACED, ORDER_REJECTED, ORDER_CANCELLED |
| `expotrade.trades` | Trade executions |
| `expotrade.strategies` | STRATEGY_CREATED, STRATEGY_ENABLED, STRATEGY_DISABLED |
| `expotrade.market-data` | Real-time tick updates |

### Frontend (Angular 17 + NgRx)

- **Dashboard**: Portfolio KPIs and positions table with color-coded P&L.
- **Trading**: Manual order form and order history.
- **Strategies**: Create and manage algorithmic strategies with enable/disable toggle.
- **Monitoring**: Embedded Grafana dashboards.

### Infrastructure Stack

| Service | Role |
|---|---|
| **PostgreSQL** | Persistent storage (orders, trades, positions, users) |
| **Redis** | Market data cache (latest ticks + history) |
| **Kafka** | Event streaming between components |
| **Prometheus** | Metrics collection from `/actuator/prometheus` |
| **Grafana** | Real-time monitoring dashboards |

### Key Design Decisions

- **Hexagonal architecture**: domain logic is framework-agnostic; brokers and databases are swappable adapters.
- **Strategy pattern**: new algorithms (Bollinger Bands, MACD, etc.) can be added by implementing `TradingStrategy`.
- **Reactive programming** (Project Reactor): `Mono`/`Flux` for non-blocking, high-throughput data flow.
- **Paper trading mode** (`trading.paper-mode: true`): safe to experiment without real money.

---

## Tech Stack

- **Backend**: Java 21 + Spring Boot 3 (Hexagonal/Clean Architecture)
- **Frontend**: Angular 17 + NgRx + Angular Material
- **Database**: PostgreSQL
- **Cache**: Redis
- **Messaging**: Apache Kafka
- **Monitoring**: Prometheus + Grafana

## Project Structure

```
expotrade/
├── backend/                    # Java Spring Boot backend
│   └── src/main/java/com/expotrade/
│       ├── domain/             # Domain models, ports (interfaces)
│       │   ├── model/          # Order, Position, Trade, MarketData, etc.
│       │   ├── port/in/        # Use cases (PlaceOrder, CancelOrder, etc.)
│       │   ├── port/out/       # Repository & broker port interfaces
│       │   └── service/        # Domain services (RiskManager)
│       ├── application/        # Application services & DTOs
│       ├── infrastructure/     # JPA entities, Redis cache, Kafka publisher
│       ├── adapters/           # Broker implementations, REST controllers, WebSocket
│       └── config/             # Security, JWT, Kafka, WebSocket config
├── frontend/                   # Angular 17 SPA
│   └── src/app/
│       ├── core/               # Services, guards, interceptors
│       ├── features/           # Dashboard, Trading, Strategy, Monitoring
│       ├── shared/             # Models, pipes, shared components
│       └── store/              # NgRx state management
├── monitoring/                 # Prometheus config
├── docker-compose.yml          # Full stack orchestration
└── .github/workflows/ci.yml   # CI/CD pipeline
```

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21+ (for local dev)
- Node.js 20+ (for local dev)

### Run with Docker Compose

```bash
docker-compose up -d
```

Services will be available at:
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Local Development

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm start
```

## First Steps After Launch

1. **Register a user:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"trader1","email":"trader1@example.com","password":"password123"}'
```

2. **Login to get a JWT token:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"trader1","password":"password123"}'
```

3. Use the token in subsequent requests (or log in via the frontend at http://localhost:4200/login).

4. Create a strategy and place orders from the Trading Panel or Strategy Manager pages.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login (returns JWT) |
| GET | `/api/v1/portfolio` | Get portfolio summary |
| POST | `/api/v1/orders` | Place new order |
| DELETE | `/api/v1/orders/{id}` | Cancel order |
| GET | `/api/v1/orders` | List orders |
| GET | `/api/v1/trades` | Trade history |
| POST | `/api/v1/strategies` | Create strategy |
| POST | `/api/v1/strategies/{id}/enable` | Enable strategy |
| POST | `/api/v1/strategies/{id}/disable` | Disable strategy |
| GET | `/api/v1/market-data/{symbol}` | Get latest market data |
| GET | `/api/v1/market-data/stream/{broker}` | SSE market data stream |
| WS | `/ws/market-data` | WebSocket market data |

## Trading Strategies

### Moving Average Crossover
- Generates BUY on golden cross (short MA crosses above long MA).
- Generates SELL on death cross (short MA crosses below long MA).
- Parameters: `shortPeriod` (default: 10), `longPeriod` (default: 50).

### RSI (Relative Strength Index)
- Generates BUY when RSI < oversold threshold.
- Generates SELL when RSI > overbought threshold.
- Parameters: `period` (14), `oversold` (30), `overbought` (70).

## Security

- JWT-based authentication with access/refresh tokens
- BCrypt password hashing
- Role-based access control
- API rate limiting (100 req/min per IP)
- CORS configured for frontend origin

## Configuration

Key environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_USERNAME` | expotrade | PostgreSQL username |
| `DB_PASSWORD` | expotrade | PostgreSQL password |
| `REDIS_HOST` | localhost | Redis host |
| `KAFKA_BOOTSTRAP_SERVERS` | localhost:9092 | Kafka servers |
| `JWT_SECRET` | (default key) | JWT signing secret |

## Testing

```bash
# Backend unit & integration tests
cd backend && ./mvnw test

# Frontend tests
cd frontend && npm test

# E2E tests
cd frontend && npm run e2e
```
