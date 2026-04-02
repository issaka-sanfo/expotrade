# ExpoTrade - Automated Trading Bot Platform

A production-grade automated trading platform integrating Interactive Brokers (IBKR) and eToro with real-time monitoring, strategy management, and multi-broker support.

## Architecture

- **Backend**: Java 21 + Spring Boot 3 (Hexagonal/Clean Architecture)
- **Frontend**: Angular 17 + NgRx + Angular Material
- **Database**: PostgreSQL
- **Cache**: Redis
- **Messaging**: Apache Kafka
- **Monitoring**: Prometheus + Grafana

### Project Structure

```
epotrade/
├── backend/                    # Java Spring Boot backend
│   └── src/main/java/com/epotrade/
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
- Generates BUY on golden cross (short MA crosses above long MA)
- Generates SELL on death cross (short MA crosses below long MA)
- Parameters: `shortPeriod` (default: 10), `longPeriod` (default: 50)

### RSI (Relative Strength Index)
- Generates BUY when RSI < oversold threshold
- Generates SELL when RSI > overbought threshold
- Parameters: `period` (14), `oversold` (30), `overbought` (70)

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
| `DB_USERNAME` | epotrade | PostgreSQL username |
| `DB_PASSWORD` | epotrade | PostgreSQL password |
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
First Steps After Launch

  1. Register a user:
  curl -X POST
  http://localhost:8080/api/v1/auth/register \        
    -H "Content-Type: application/json" \
    -d '{"username":"trader1","email":"trader1@example
  .com","password":"password123"}'

  2. Login to get a JWT token:
  curl -X POST http://localhost:8080/api/v1/auth/login
   \
    -H "Content-Type: application/json" \
    -d
  '{"username":"trader1","password":"password123"}'   

  3. Use the token in subsequent requests (or log in  
  via the frontend at http://localhost:4200/login).   
  4. Create a strategy and place orders from the      
  Trading Panel or Strategy Manager pages.