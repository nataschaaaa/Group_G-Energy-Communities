# Energy Community

## Team
- Deniz – REST API (Models + Controller)
- Natascha – REST API (Repositories + Configuration)
- Lea – JavaFX GUI (Documentation + App entry point)
- Damla – JavaFX GUI (Controller + Layout)

## Milestone 1 – How to Run

### 1. REST API starten
```bash
cd energy-rest-api
mvn spring-boot:run
```

### 2. JavaFX GUI starten
> REST API muss zuerst laufen
```bash
cd energy-javafx-gui
mvn javafx:run
```

## Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/energy/current` | Current hour data |
| GET | `/energy/historical?start=...&end=...` | Filter historic data |

## Example Requests