# Energy Community

A distributed system that monitors real-time energy production and consumption within an energy community. Built with Spring Boot, RabbitMQ, PostgreSQL and JavaFX.

## Team

| Name | Component |
|---|---|
| Deniz | REST API + Database setup |
| Natascha | Current Percentage Service |
| Lea | Usage Service |
| Damla | Energy Producer + Energy User |

## Architecture

```
Energy Producer (Weather API)  +  Energy User (Time of Day)
        │                                │
        └──────────────┬─────────────────┘
                       ▼
              RabbitMQ (energy.queue)
                       │
                       ▼
          Usage Service ──► PostgreSQL (usage_data)
                       │
                       ▼ (update.queue)
     Current Percentage Service ──► PostgreSQL (current_percentage)
                                          │
                                    REST API :8080
                                          │
                                     JavaFX GUI
```

## Components

| Module | Port | Description |
|---|---|---|
| `energy-rest-api` | 8080 | Spring Boot REST API, reads from PostgreSQL |
| `energy-javafx-gui` | — | JavaFX GUI, communicates via REST API |
| `usage-service` | — | RabbitMQ listener, updates `usage_data` table |
| `current-percentage-service` | — | RabbitMQ listener, updates `current_percentage` table |
| `energy-producer` | — | Sends production messages based on Weather API |
| `energy-user` | — | Sends usage messages based on time of day |

## Prerequisites

- Java 21
- Maven
- Docker Desktop

## How to Run

### 1. Infrastructure starten (PostgreSQL + RabbitMQ)

```bash
docker compose up -d
```

RabbitMQ Management UI: http://localhost:15672 — Login: `guest` / `guest`

### 2. Services starten (Reihenfolge beachten)

```
1. EnergyRestApiApplication        (energy-rest-api)
2. UsageServiceApplication         (usage-service)
3. CurrentPercentageApplication    (current-percentage-service)
4. EnergyProducerApplication       (energy-producer)
5. EnergyUserApplication           (energy-user)
6. JavaFX GUI                      (energy-javafx-gui → mvn javafx:run)
```

> Docker muss laufen bevor die Services gestartet werden.

## REST API Endpoints

### GET /energy/current
Returns the current hour's community pool and grid portion.

```
GET http://localhost:8080/energy/current
```

Example Response:
```json
{
  "id": 1,
  "hour": "2025-01-10T14:00:00",
  "communityDepleted": 100.00,
  "gridPortion": 5.63
}
```

### GET /energy/historical
Returns usage data for a given time range.

```
GET http://localhost:8080/energy/historical?start=2025-01-09T00:00:00&end=2025-01-10T23:59:59
```

Example Response:
```json
[
  {
    "id": 1,
    "hour": "2025-01-09T08:00:00",
    "communityProduced": 143.024,
    "communityUsed": 130.101,
    "gridUsed": 14.75
  }
]
```

## Database

PostgreSQL läuft in Docker auf Port `5432`.

| Table | Description |
|---|---|
| `usage_data` | Hourly community produced, used and grid usage in kWh |
| `current_percentage` | Current hour's community depleted % and grid portion % |

Credentials: `disysuser` / `disyspw` — Database: `energy_db`

## Message Queue

RabbitMQ läuft in Docker auf Port `5672`.

| Queue | Producer | Consumer |
|---|---|---|
| `energy.queue` | Energy Producer, Energy User | Usage Service |
| `update.queue` | Usage Service | Current Percentage Service |

### Message Format — energy.queue

```json
{
  "type": "PRODUCER",
  "association": "COMMUNITY",
  "kwh": 0.0062,
  "datetime": "2025-01-10T14:33:00"
}
```

`type` is either `PRODUCER` or `USER`.
