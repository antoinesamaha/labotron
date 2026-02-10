# Labotron - Laboratory Automation System

## System Purpose
Bridge between Laboratory Information Systems (LIS) and physical medical analyzers. Routes lab test samples to appropriate instruments and returns results back to LIS.

## Architecture Overview
- **Type**: Multi-module Maven project with microservices-inspired architecture
- **Backend**: Java 17 + Spring Boot 3.4.1
- **Frontend**: Flutter 3.2.3+
- **Message Queue**: RabbitMQ 3
- **Database**: PostgreSQL 17.6 (Oracle 21.7 alternative configured)
- **Deployment**: Docker Compose (5 containers)

---

## Project Structure

```
labotron/
├── labotron-foundations/     # Core domain, services, drivers base, connectors
├── labotron-drivers/         # 48 driver implementations for 11+ analyzer brands
├── labotron-service/         # Spring Boot REST API + business logic
├── labotron-ui/             # Flutter frontend (web/mobile/desktop)
├── labotron-simulator/      # Java-based analyzer simulator for testing
└── docker-compose/          # Container orchestration configs
```

### Key Entry Points
- Backend: `/labotron-service/src/main/java/com/neofoc/app/LabotronApplication.java`
- Frontend: `/labotron-ui/labotronui/lib/main.dart`
- Simulator: `/labotron-simulator/src/main/java/com/neofoc/simulator/Main.java`

---

## Message Flow

```
LIS System
  ↓ publishes sample request
RabbitMQ Queue: lis-2-connector
  ↓ consumed by
RabbitMQListenerService
  ↓ delegates to
ConnectorService.processSampleFromLis()
  ↓ routes sample
DispatcherService.getInstrumentForTest()
  ↓ assigns test to instrument
Driver (specific analyzer)
  ↓ sends inquiry/sample
Analyzer (physical device)
  ↓ returns results
Driver
  ↓ publishes results
RabbitMQ Queue: connector-2-lis
  ↓ consumed by
LIS System
```

**Additional Queues:**
- `driver-2-connector`: Driver notifications to connector
- Instrument-specific queues: dynamically created per instrument

---

## Driver Architecture

### Hierarchy
```
IDriver (interface)
└── Driver (abstract base)
    └── DriverSerialPort (serial communication base)
        └── AstmDriver (ASTM protocol implementation)
            ├── Cobas501Driver
            ├── CobasU601Driver → CobasU601701Driver
            ├── CobasInfinityDriver (extends Cobas501Driver)
            ├── AlegriaDriver
            ├── OctaDriver
            ├── CS2500Driver
            ├── YumizenP8000Driver
            └── MaglumiDriver
```

### Driver Locations
- **Interfaces**: `/labotron-foundations/src/main/java/com/neofoc/app/driver/IDriver.java`
- **Base Classes**: `/labotron-foundations/src/main/java/com/neofoc/app/driver/`
  - `Driver.java` - Abstract base with common driver functionality
  - `DriverSerialPort.java` - Serial port communication base
  - `AstmDriver.java` - ASTM protocol standard implementation
- **Implementations**: `/labotron-drivers/src/main/java/com/neofoc/app/driver/`
  - 48 driver files implementing 11+ analyzer brands

### Supported Analyzers
1. Alegria
2. ASTM (generic protocol)
3. Cobas 501
4. Cobas U601/U601701
5. Cobas Infinity
6. CS2500
7. Maglumi
8. Octa
9. Yumizen P8000

### Driver Operation Modes
- **Inquiry Mode**: Driver waits for analyzer to request samples (pull model)
- **Push Mode**: Driver actively sends samples to analyzer
- **Bidirectional**: Combination of both modes

---

## Core Services

Location: `/labotron-foundations/src/main/java/com/neofoc/app/service/`

| Service | Purpose | Key Methods |
|---------|---------|-------------|
| ConnectorService | LIS integration & sample processing | `processSampleFromLis()` |
| DispatcherService | Test routing to instruments | `getInstrumentForTest()` |
| RabbitMQListenerService | Consumes LIS messages | Queue listeners |
| RabbitMQSendingService | Publishes results to LIS | Result publishing |
| CommunicationLogService | Audit trail for all communications | Log persistence |
| InstrumentReceiverListener | Handles instrument responses | Response processing |

---

## Data Model

Location: `/labotron-foundations/src/main/java/com/neofoc/app/model/`

### Core Entities (~17 total)

**Instrument**
- Fields: `id`, `code`, `name`, `driverClassName`, `connected`, `started`
- Communication settings: timeouts, retry counts, serial/socket config
- Relationship: One-to-many with `TestLabelMap`
- Note: `connected` flag separated from `started` flag (recent change)

**LabSample**
- Fields: `sampleId`, `sampleType`, `patientId`, demographics, timestamps
- Relationship: One-to-many with `LabTest`

**LabTest**
- Fields: `testId`, `testCode`, `testDesc`, `status`, `result`, `unit`, `analyzerCode`
- `analyzerCode`: Actual analyzer assigned to process test
- References: Instrument via TestLabelMap

**TestLabelMap**
- Maps LIS test codes to specific Instruments
- Determines test routing/dispatching logic
- Analyzer-specific test configuration

**CommunicationLog**
- Records all instrument-system communication
- Used for debugging and audit trails

**L3Message / LabMessage**
- Message protocol abstractions for driver communication
- Used in serial port and socket communications

---

## REST API

Location: `/labotron-service/src/main/java/com/neofoc/app/controller/`

### Controllers (5+)
- `HealthCheckController` - System health/status endpoints
- `HelloController` - Basic test endpoint
- `LabSampleController` - Sample CRUD operations
- `InstrumentController` - Instrument management
- Additional service-specific controllers

**Typical Endpoints:**
- `/api/instruments` - Instrument management
- `/api/samples` - Lab sample operations
- `/api/health` - System health check

---

## Communication Layer

Location: `/labotron-foundations/src/main/java/com/neofoc/app/connection/`

### Connection Types
- **basicsocket/**: Basic socket server/client framework
- **socket/**: Socket-based instrument communication (recent async updates)
- **Serial Port**: javax.comm-based serial communication
- **L3 Protocol**: Custom frame protocol for message passing

### Communication Patterns
- Listener pattern for async message handling
- Remote launcher server support for external connections
- Recent change: Added async to socket connections

---

## Configuration

### Spring Boot Config
File: `/labotron-service/src/main/resources/application.yml`

```yaml
Server: port 8099
Database: PostgreSQL localhost:5432/labotron (user: postgres/123456)
Connection Pool: HikariCP (max 10 connections)
RabbitMQ: localhost:9056 (user: guest/guest)
Logging: /home/antoin/tmp/log (console + file + DB requests)
```

### Docker Environment
File: `/docker-compose/.env`
- Service ports (Postgres: 9054, RabbitMQ: 9056/9156, pgAdmin: 9055)
- Credentials
- API URLs
- Serial port mappings (10000-10005)

### Docker Compose
File: `/docker-compose/docker-compose.yml`
- 5 services: postgres, pgadmin, rabbitmq, labotron-service, labotron-web
- Dependency chain: DB healthy → RabbitMQ healthy → Service starts
- Volume persistence for DB and RabbitMQ

---

## Testing

### Test Locations
- `/labotron-foundations/src/test/java/`
- `/labotron-simulator/src/test/java/`
- `/labotron-ui/labotronui/test/`

### Simulators
- `AlegriaSimulator` - Simulates Alegria analyzer
- `InfinitySimulator` - Simulates Infinity analyzer
- `AbstractSimulator` - Base simulation framework
- `SimServer` - Socket server for test communications

---

## Key Patterns & Conventions

### Naming Conventions
- Drivers: `{AnalyzerName}Driver.java`
- Services: `{Purpose}Service.java` with `{Purpose}ServiceImpl.java`
- Controllers: `{Entity}Controller.java`
- Models: Domain-driven naming (LabSample, LabTest, Instrument)

### Design Patterns
- **Factory Pattern**: Driver instantiation via className
- **Listener Pattern**: Async message handling
- **Service Layer**: Separation of business logic from controllers
- **Repository Pattern**: Spring Data JPA for persistence

### Recent Architectural Changes
- Separated `connected` flag from `started` flag at instrument level
- Added async capability to socket connections
- Introduced object detachment capability

---

## Common Task Locations

| Task | Location |
|------|----------|
| Add new analyzer driver | `/labotron-drivers/src/main/java/com/neofoc/app/driver/` |
| Modify LIS integration | `/labotron-foundations/.../service/ConnectorService*.java` |
| Change test routing logic | `/labotron-foundations/.../service/DispatcherService*.java` |
| Update data model | `/labotron-foundations/.../model/` |
| Add REST endpoint | `/labotron-service/.../controller/` |
| Modify UI | `/labotron-ui/labotronui/lib/` |
| Update communication protocol | `/labotron-foundations/.../connection/` |
| Configure deployment | `/docker-compose/` files |

---

## Dependencies

### Backend (Maven)
- Spring Boot 3.4.1 (web, data-jpa, security, amqp)
- PostgreSQL driver (or Oracle)
- RabbitMQ AMQP 2.7.0
- Jackson (JSON/datetime)
- Lombok

### Frontend (Flutter)
- http: 1.2.0
- flutter_form_builder: 10.0.1
- form_builder_validators: 11.1.0
- focui: local custom UI framework

---

## Git Workflow

- **Main branch**: `main` (for PRs)
- **Current branch**: `dev`
- **Recent commits**: Focus on Alegria driver fixes, connected/started flag separation, async socket connections
