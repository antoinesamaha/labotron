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
            ├── MaglumiDriver
            └── GemPremier3500Driver
```

### Driver Locations
- **Interfaces**: `/labotron-foundations/src/main/java/com/neofoc/app/driver/IDriver.java`
- **Base Classes**: `/labotron-foundations/src/main/java/com/neofoc/app/driver/`
  - `Driver.java` - Abstract base with common driver functionality
  - `DriverSerialPort.java` - Serial port communication base
- **Base ASTM**: `/labotron-drivers/src/main/java/com/neofoc/app/drivers/astm/`
  - `AstmDriver.java` - ASTM protocol standard implementation
  - `AstmFrame.java` - Standard ASTM frame structure
  - `AstmFrameCreator.java` - Standard ASTM frame builder
  - `AstmParams.java` - Configurable ASTM parameters
  - `AstmReceiver.java` - ASTM frame receiver/parser
- **Implementations**: `/labotron-drivers/src/main/java/com/neofoc/app/drivers/`
  - 49 driver files implementing 12+ analyzer brands
  - Each analyzer in its own package folder

### Supported Analyzers
1. Alegria
2. ASTM (generic protocol)
3. Cobas 501
4. Cobas U601/U601701
5. Cobas Infinity
6. CS2500
7. GEM Premier 3500 (Blood Gas Analyzer)
8. Maglumi
9. Octa
10. Yumizen P8000

### Driver Operation Modes
- **Inquiry Mode**: Driver waits for analyzer to request samples (pull model)
- **Push Mode**: Driver actively sends samples to analyzer
- **Bidirectional**: Combination of both modes

---

## Driver Development Guide

### Critical Rule: Start Simple, Add Complexity Only When Needed

**ALWAYS verify if standard ASTM is sufficient before creating custom frame implementations.**

### Step-by-Step Process for New Driver Development

#### Step 1: Analyze Protocol Documentation
Read the analyzer's interface protocol document and answer:

1. **Is it ASTM-based?**
   - Look for: ASTM E1394, E1381, ASTM standard mentions
   - Frame structure: `[STX][FN]Data[ETX/ETB][CS][CR][LF]`
   - Record types: H (Header), P (Patient), O (Order), R (Result), C (Comment), L (Last)
   - Delimiters: `|` (field), `^` (component), `\` (repeat), `&` (escape)

2. **Does it use standard ASTM frame sequence?**
   - Standard: ENQ → H → P → O → [C] → L → EOT
   - Non-standard: Concatenated frames, different sequence, custom protocol

3. **What are the protocol variations?**
   - Test code length (typically 3-4 characters)
   - Patient ID handling
   - Date/time formats
   - Comment frame requirements
   - Result frame structure

4. **Communication method?**
   - Serial port (RS-232)
   - TCP/IP socket
   - Other

#### Step 2: Check Existing Similar Drivers
Compare with existing drivers to find patterns:

```bash
# Find all drivers
ls labotron-drivers/src/main/java/com/neofoc/app/drivers/

# Check driver complexity
wc -l labotron-drivers/src/main/java/com/neofoc/app/drivers/*/*.java
```

#### Step 3: Determine Required Customization Level

**Level 0: Pure Standard ASTM** (35-45 lines)
- Use base `AstmDriver` with NO custom frame creators
- Only configure ASTM parameters
- Examples: **MaglumiDriver** (35 lines), **GemPremier3500Driver** (41 lines)

**Level 1: Minor ASTM Variations** (50-100 lines)
- Use base `AstmDriver` with custom FrameCreator
- Override 1-2 specific frame methods
- Examples: **CobasU601Driver**, **YumizenP8000Driver**

**Level 2: Significant ASTM Customization** (150-300 lines)
- Custom FrameCreator with multiple overrides
- Non-standard frame sequences
- Examples: **Cobas501Driver** (concatenated frames), **AlegriaDriver**

**Level 3: Non-ASTM Protocol** (varies)
- May need to extend `DriverSerialPort` directly
- Implement custom protocol from scratch

#### Step 4: Implement Using Minimal Override Principle

### Template: Level 0 - Standard ASTM Driver (Preferred)

```java
package com.neofoc.app.drivers.{analyzer};

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import java.util.Properties;

public class {Analyzer}Driver extends AstmDriver {

    public {Analyzer}Driver() {
        super();

        // Configure ASTM parameters only
        getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
        getAstmParams().setTestCodeLength(4);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(true);
        getAstmParams().setConcatenatedFrames(true);
        getAstmParams().setReadComment3(true);
        getAstmParams().setReadResultComment(true);
        getAstmParams().setSendPatientIdToInstrument(true);
        // Add other params as needed
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        // Set communication mode if needed
        if (props != null) {
            props.put("tcpip", "1"); // For TCP/IP, omit for serial
        }
        super.init(instrument, props);
    }
}
```

**When to use**: Analyzer uses standard ASTM protocol with only parameter variations.

### Template: Level 1 - Custom FrameCreator

Only create custom FrameCreator if:
- Frame field ordering differs from standard
- Specific frame types need custom formatting
- Non-standard data in specific frames

```java
// In {Analyzer}Driver.java constructor:
if (frameCreator != null) {
    frameCreator.dispose();
    frameCreator = null;
}
frameCreator = new {Analyzer}FrameCreator();
```

**Override ONLY the specific frames that differ**, not the entire FrameCreator.

### Available ASTM Parameters to Configure

The base `AstmDriver` provides these parameters (configure instead of overriding frames):

```java
// Test code handling
setTestCodeLength(int)                    // Length of test codes (3-4 typical)
setIgnoreLastTestCodeDigit(boolean)       // Strip last digit from codes

// Patient data
setSendPatientIdToInstrument(boolean)     // Include patient ID
setSendPatientAgeAndSex(boolean)          // Include demographics
setSendPatientDateOfBirth(boolean)        // Send DOB vs age only

// Frame handling
setConcatenatedFrames(boolean)            // Multiple records in one frame
setCheckResultFrameTestCodeWithOrderFrameTestCode(boolean)
setTakeAllFramesFromBufferNotJustTheLast(boolean)

// Result parsing
setResultFrame_ComponentPositionForResultType(int)
setReadComment3(boolean)                  // Parse comment field 3
setReadResultComment(boolean)             // Parse result comments
setTreatHigherLessAlarmSeparately(boolean)

// Behavior
setReleaseWhenReceivedENQ(boolean)        // ENQ handling in slave mode
setSlaveBehaviour(boolean)                // Slave vs master mode
setDoNotSendOrdersBecauseOneWay(boolean) // One-way communication
setSendCommentFrameFromHost(boolean)      // Send comment frames
setSendProfileInsteadOfTestID(boolean)    // Profile-based ordering
```

### Base AstmFrameCreator Provides

The base `AstmFrameCreator` already implements:

**Standard Frames:**
- `newEnquiryFrame()` - ENQ
- `newHeaderFrame()` - H record with delimiters
- `newPatientFrame()` - P record with demographics (ID, name, DOB, age, sex)
- `newOrderFrame()` - O record with tests, priority, timestamps
- `newResultFrame()` - R record (for receiving results)
- `newCommentFrame()` - C record (override if needed)
- `newLastFrame()` - L terminator record
- `newEndOfTransmissionFrame()` - EOT

**Standard Sequence:**
- `buildFrameArray()` - ENQ → H → P → O → C → L → EOT

**ONLY override frames that have different field structures than standard ASTM.**

### Driver Complexity Comparison

| Driver | Lines | Files | Custom Frames? | Why? |
|--------|-------|-------|----------------|------|
| MaglumiDriver | 35 | 1 | ❌ No | Standard ASTM E1394 |
| GemPremier3500Driver | 41 | 1 | ❌ No | Standard ASTM E1394/E1381 |
| Cobas501Driver | 24 + 328 | 2 | ✅ Yes | Concatenated big frame format |
| AlegriaDriver | 61 + 190 | 3 | ✅ Yes | Custom frame extraction |
| CobasInfinityDriver | - | - | ✅ Yes | Extends Cobas501 custom format |

### Decision Tree: Do I Need Custom Frames?

```
Is the protocol ASTM-based?
├─ NO → Need custom protocol implementation (Level 3)
└─ YES → Does it use standard H/P/O/C/L sequence?
    ├─ NO → Need custom FrameCreator (Level 2)
    └─ YES → Are field positions standard ASTM?
        ├─ NO → Need custom FrameCreator (Level 1)
        └─ YES → Use base AstmDriver with params only (Level 0) ✓
```

### Common Mistakes to Avoid

❌ **Creating custom FrameCreator for standard ASTM protocol**
- First check if base implementation + parameters is sufficient
- Review MaglumiDriver and GemPremier3500Driver as examples

❌ **Overriding all frames when only one differs**
- Only override the specific frame method that differs
- Reuse base implementation for standard frames

❌ **Hardcoding values that should be parameters**
- Use ASTM parameters instead of custom code
- Check available setters on `AstmParams`

❌ **Not checking existing similar drivers**
- Review drivers for the same manufacturer first
- Similar analyzers often share protocol patterns

### Folder Structure for New Driver

```
labotron-drivers/src/main/java/com/neofoc/app/drivers/{analyzername}/
├── {Analyzer}Driver.java           // REQUIRED: Main driver class
├── {Analyzer}FrameCreator.java     // OPTIONAL: Only if custom frames needed
└── {Analyzer}Frame.java            // OPTIONAL: Only if custom frame parsing needed
```

**Start with just the Driver.java file. Add others only when proven necessary.**

### Testing New Driver

1. Configure instrument in database with `driverClassName`
2. Set up TCP/IP or serial port connection
3. Create test label mappings
4. Send sample order and verify frame sequence in logs
5. Validate analyzer receives correct format
6. Test result processing from analyzer

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
| Add new analyzer driver | `/labotron-drivers/src/main/java/com/neofoc/app/drivers/` (see Driver Development Guide) |
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
