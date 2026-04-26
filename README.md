# ExpoTrade - Automated Trading Bot Platform

A production-grade automated trading platform integrating Interactive Brokers (IBKR) and eToro with real-time monitoring, strategy management, and multi-broker support.

**Live Demo (staging):** http://expotrade-staging-alb-1349821291.eu-west-1.elb.amazonaws.com

- Frontend: http://expotrade-staging-alb-1349821291.eu-west-1.elb.amazonaws.com/
- Backend Health: http://expotrade-staging-alb-1349821291.eu-west-1.elb.amazonaws.com/actuator/health

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
| **Kafka + Zookeeper** | Event streaming between components |
| **Nginx** | Frontend web server and reverse proxy |
| **Docker / Docker Compose** | Containerization and local orchestration |
| **Prometheus** | Metrics collection from `/actuator/prometheus` |
| **Grafana** | Real-time monitoring dashboards |

### Key Design Decisions

- **Hexagonal architecture**: domain logic is framework-agnostic; brokers and databases are swappable adapters.
- **Strategy pattern**: new algorithms (Bollinger Bands, MACD, etc.) can be added by implementing `TradingStrategy`.
- **Reactive programming** (Project Reactor): `Mono`/`Flux` for non-blocking, high-throughput data flow.
- **Paper trading mode** (`trading.paper-mode: true`): safe to experiment without real money.

---

## Tech Stack

### Backend
- **Java 21** + **Spring Boot 3** (Hexagonal/Clean Architecture)
- **Spring Security** — authentication & authorization
- **JJWT** — JWT token generation and validation
- **Spring Data JPA** + **Hibernate** — ORM and database access
- **Spring Data Redis** — Redis cache integration
- **Spring Kafka** — event streaming
- **Project Reactor** — reactive programming (`Mono`/`Flux`)
- **Bucket4j** — API rate limiting
- **SpringDoc OpenAPI** — Swagger UI and API documentation
- **Micrometer** — Prometheus metrics export
- **Maven** — build tool (with Maven Wrapper)

### Frontend
- **Angular 17** + **TypeScript**
- **NgRx** — state management
- **Angular Material** + **Angular CDK** — UI components
- **Chart.js** + **ng2-charts** — data visualization and charts
- **RxJS** — reactive programming

### Database & Messaging
- **PostgreSQL** — persistent storage (orders, trades, positions, users)
- **Redis** — market data cache (latest ticks + rolling history)
- **Apache Kafka** (Confluent) + **Zookeeper** — event streaming

### Monitoring
- **Prometheus** — metrics collection from `/actuator/prometheus`
- **Grafana** — real-time monitoring dashboards

### Infrastructure & DevOps
- **Docker** + **Docker Compose** — containerization and local orchestration
- **Nginx** — web server / reverse proxy for frontend
- **Terraform** — Infrastructure as Code for AWS provisioning
- **Jenkins** — CI/CD pipeline (Windows local + Linux cloud)
- **GitHub Actions** — CI/CD pipeline (alternative)

### AWS Services
- **ECS Fargate** — container orchestration
- **ECR** — Docker image registry
- **ALB** — Application Load Balancer with path-based routing
- **RDS** — managed PostgreSQL
- **ElastiCache** — managed Redis
- **EC2** — Kafka instance
- **Secrets Manager** — DB password and JWT secret
- **CloudWatch** — log aggregation
- **S3** + **DynamoDB** — Terraform state backend and locking
- **IAM** — roles, OIDC provider for GitHub Actions
- **VPC** — networking (public/private subnets, NAT Gateway)

### Testing
- **JUnit 5** + **Mockito** — backend unit tests
- **TestContainers** — backend integration tests (PostgreSQL, Kafka in containers)
- **H2 Database** — in-memory database for tests
- **Spring Security Test** — security layer testing
- **Karma** + **Jasmine** — frontend unit tests
- **Cypress** — frontend E2E tests

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
├── infra/terraform/            # AWS infrastructure as code (Terraform)
│   ├── scripts/                # User data scripts (Kafka, Jenkins)
│   └── envs/                   # Environment-specific tfvars
├── docker-compose.yml          # Full stack local orchestration
├── Jenkinsfile                 # Jenkins pipeline (Windows local)
├── Jenkinsfile.linux           # Jenkins pipeline (Linux cloud)
├── .github/workflows/ci.yml   # CI pipeline (PRs)
└── .github/workflows/deploy.yml # CD pipeline (GitHub Actions)
```

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21+ (for local dev)
- Node.js 20+ (for local dev)
- Maven 3.9+ (or use included Maven Wrapper `./mvnw`)
- AWS CLI v2 + Terraform 1.5+ (for cloud deployment)
- Jenkins (for CI/CD pipeline)

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

Full interactive API documentation available via **Swagger UI** at `/swagger-ui.html` (powered by SpringDoc OpenAPI).

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

- **JJWT**-based authentication with access (1h) / refresh (24h) tokens
- **BCrypt** password hashing (Spring Security)
- Role-based access control
- API rate limiting (100 req/min per IP) via **Bucket4j**
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

**Backend** (JUnit 5 + Mockito + TestContainers):
```bash
cd backend && ./mvnw test
```

**Frontend unit tests** (Karma + Jasmine):
```bash
cd frontend && npm test
```

**Frontend E2E tests** (Cypress):
```bash
cd frontend && npm run e2e
```

## Broker API Communication

### Abstraction Layer (Hexagonal Architecture)

The platform uses a `BrokerPort` interface that defines a common contract for all broker integrations. Each broker has its own adapter implementation:

```
BrokerPort (interface)
    ├── EtoroBrokerService   (eToro adapter)
    └── IBKRBrokerService    (IBKR adapter)
```

Routing to the correct broker is done dynamically in `OrderService` via a `Map<String, BrokerPort>`:

```java
BrokerPort broker = brokerPorts.get(command.brokerType().name());
return broker.placeOrder(savedOrder);
```

### Operations Exposed by Each Broker

| Method | Role |
|---|---|
| `placeOrder()` | Place a trading order |
| `cancelOrder()` | Cancel an existing order |
| `getOrderStatus()` | Check order status |
| `getPositions()` | Retrieve current positions |
| `getMarketData()` | Single quote (point-in-time) |
| `streamMarketData()` | Real-time stream (Reactor `Flux`) |

### Current State: Simulated (Mock)

Both adapters are **mock implementations** — they make no real HTTP calls to brokers:

- **eToro**: No public trading API available, so a full mock with synthetic data and `delayElement()` to simulate network latency.
- **IBKR**: In production, this would integrate with TWS/IB Gateway, but is currently also mocked.

There is **no broker SDK**, **no `WebClient`/`RestTemplate`**, and **no broker credential management** in the current codebase.

### Real-Time Data Flow

```
Broker Adapter (mock) --> Kafka (expotrade.market-data) --> Redis (cache) --> WebSocket/SSE --> Angular Frontend
```

- **Kafka** broadcasts events (orders, trades, market data).
- **Redis** caches prices and a rolling 500-item history.
- **WebSocket** (`/ws/market-data`) pushes data to the frontend in real time.

### Path to Production Integration

The architecture is **ready for real broker integration** — it would require:

1. Implementing actual HTTP calls in the broker adapters (via Spring `WebClient`).
2. Adding broker credentials as environment variables (or a secrets vault).
3. Mapping broker API responses to domain models (`Order`, `Position`, `MarketData`).

**No changes needed in the application layer** — this is the key benefit of the hexagonal architecture pattern.

---

## CI/CD Pipeline & AWS Deployment

### Cloud Architecture Overview

```
Internet -> ALB (public subnets, eu-west-1)
              |-- /api/*, /ws/* -> ECS Backend (private subnets) -> RDS PostgreSQL
              |-- /*           -> ECS Frontend (private subnets)    ElastiCache Redis
                                                                    EC2 Kafka (private subnet)
```

### Infrastructure (Terraform)

All AWS infrastructure is defined as code in `infra/terraform/`:

| File | Purpose |
|------|---------|
| `main.tf` | Provider config, S3 backend for state |
| `variables.tf` | Input variables (region, instance sizes, environment) |
| `outputs.tf` | ALB DNS, ECR URIs, RDS/Redis/Kafka endpoints |
| `vpc.tf` | VPC `10.0.0.0/16`, 2 public + 2 private subnets, NAT GW, IGW |
| `security-groups.tf` | SGs: ALB(80,443), backend(8080), frontend(80), RDS(5432), Redis(6379), Kafka(9092) |
| `ecr.tf` | 2 ECR repos (backend, frontend), keep last 10 images |
| `rds.tf` | PostgreSQL 16, db.t3.micro, private subnets only |
| `elasticache.tf` | Redis 7, cache.t3.micro, private subnets |
| `kafka.tf` | EC2 t3.micro with Kafka+Zookeeper via Docker Compose, private subnet |
| `alb.tf` | ALB in public subnets, path-based routing, health checks |
| `ecs.tf` | Fargate cluster + backend/frontend services and task definitions |
| `iam.tf` | GitHub OIDC provider, deploy role, ECS task/execution roles |
| `secrets.tf` | Secrets Manager: db-password, jwt-secret |
| `monitoring.tf` | CloudWatch Log Groups (30d retention) |
| `jenkins.tf` | EC2 Jenkins with Docker, Java 21, Maven, Node 20, AWS CLI |
| `envs/staging.tfvars` | Staging-specific values |

### Key Infrastructure Details

**VPC**: 2 AZs in eu-west-1, public subnets for ALB/NAT, private subnets for ECS/RDS/Redis/Kafka. Single NAT Gateway for staging cost savings.

**ALB Routing**:
- `/api/*`, `/ws/*`, `/actuator/*` -> backend target group (port 8080, health: `/actuator/health`)
- `/*` -> frontend target group (port 80, health: `/`)
- HTTP only for staging (HTTPS with ACM cert for production later)
- Idle timeout 300s for WebSocket connections

**ECS Fargate**:
- Backend: 512 CPU, 1024 MB, env vars for DB/Redis/Kafka/JWT from Secrets Manager
- Frontend: 256 CPU, 512 MB
- Rolling deployment: min 50%, max 200%

**Kafka EC2**: t3.micro with Amazon Linux 2023, Kafka + Zookeeper via Docker Compose, PLAINTEXT protocol within the VPC (same as local docker-compose, no code changes needed).

### Application Changes for AWS

| File | Change |
|------|--------|
| `backend/src/main/resources/application-aws.yml` | Spring profile: `ddl-auto: update`, INFO logging |
| `frontend/nginx-aws.conf` | Nginx config without `proxy_pass` (ALB handles API/WS routing) |
| `frontend/Dockerfile` | `NGINX_CONF` build arg to select nginx config |
| `frontend/angular.json` | `fileReplacements` for production build (relative API URLs) |

### CI/CD Options (3 pipelines available)

| File | Usage |
|------|-------|
| `Jenkinsfile` | Jenkins local (Windows, `bat` commands, AWS credentials binding) |
| `Jenkinsfile.linux` | Jenkins cloud (EC2 Linux, `sh` commands, IAM Instance Role) |
| `.github/workflows/deploy.yml` | GitHub Actions (OIDC auth, no stored AWS keys) |

### Pipeline Stages

```
test-backend --┐
               ├-- build-and-push (ECR) -- deploy (ECS) -- smoke-test
test-frontend -┘
```

1. **Test Backend**: `mvn clean verify`
2. **Test Frontend**: `npm ci && npm run build --configuration production`
3. **ECR Login**: authenticate to Elastic Container Registry
4. **Build & Push Images**: Docker build + push with commit SHA and `latest` tags
5. **Deploy to ECS**: register new task definitions, update services, force new deployment
6. **Wait for Stability**: `aws ecs wait services-stable`
7. **Smoke Test**: verify ALB health endpoint returns 200

### AWS Account Bootstrap (one-time)

```bash
# 1. Create S3 bucket for Terraform state
aws s3api create-bucket --bucket expotrade-terraform-state \
  --region eu-west-1 --create-bucket-configuration LocationConstraint=eu-west-1

aws s3api put-bucket-versioning --bucket expotrade-terraform-state \
  --versioning-configuration Status=Enabled

# 2. Create DynamoDB table for Terraform state locking
aws dynamodb create-table --table-name expotrade-terraform-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST --region eu-west-1

# 3. Deploy infrastructure
cd infra/terraform
terraform init
terraform plan -var-file=envs/staging.tfvars
terraform apply -var-file=envs/staging.tfvars
```

### Deployment Verification

1. `terraform apply` creates all AWS infrastructure
2. Push to main (or Jenkins Build Now) triggers the pipeline
3. Access ALB DNS in browser -> Angular app loads
4. `/actuator/health` returns `{"status":"UP"}`
5. Check CloudWatch Logs: DB connected, Redis connected, Kafka consumers registered

### Estimated Monthly Cost (staging)

| Service | Cost |
|---------|------|
| NAT Gateway | ~$32 |
| ALB | ~$16 |
| ECS Fargate (2 tasks) | ~$15 |
| RDS t3.micro | $0-15 (free tier eligible) |
| ElastiCache t3.micro | ~$12 |
| EC2 t3.micro (Kafka) | ~$8 |
| ECR | < $1 |
| **Total** | **~$85-100/month** |
