# gambling-external-stub

The gambling-external-stub provides stubs for downstream services used by gambling-related backend services. It is used to simulate external dependencies for local development and integration testing.

This module includes a stub for the RDS-Cache-repository service used by downstream data cache proxy integrations.

---

## Running the service

Service Manager:

```
sm2 --start GAMBLING_ALL
```

To start the server locally:

```
sbt run
```

Base URL:
```
http://localhost:10405/gambling-external-stub
```

---

## Testing

Run unit tests:

```
sbt test
```

Run integration tests:

```
sbt it/test
```

Check code coverage:

```
sbt clean coverage test it/test coverageReport
```

---

## Endpoints

### MGD (Machine Games Duty) Stub

**GET**
```
/mgd/{mgdRegNumber}
```

Full URL:
```
http://localhost:10405/gambling-external-stub/mgd/{mgdRegNumber}
```

Controller mapping:
`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.MgdController.getReturnSummary(mgdRegNumber: String)`

---

## Behaviour

### Happy path - Scenario 1

Request:
```
GET http://localhost:10405/gambling-external-stub/mgd/GAM0000000001
```

Response:
```
200 OK
```

```json
{
  "mgdRegNumber": "GAM0000000001",
  "returnsDue": 0,
  "returnsOverdue": 1
}
```

---

### Happy path - Scenario 2

Request:
```
GET http://localhost:10405/gambling-external-stub/mgd/GAM0000000002
```

Response:
```
200 OK
```

```json
{
  "mgdRegNumber": "GAM0000000002",
  "returnsDue": 0,
  "returnsOverdue": 0
}
```

---

### Invalid MGD registration number

Request:
```
GET http://localhost:10405/gambling-external-stub/mgd/invalid
```

Response:
```
400 BAD_REQUEST
```

```json
{
  "code": "INVALID_MGD_REG_NUMBER",
  "message": "mgdRegNumber must be provided"
}
```

---

### Forced unexpected error

Request:
```
GET http://localhost:10405/gambling-external-stub/mgd/error
```

Response:
```
500 INTERNAL_SERVER_ERROR
```

```json
{
  "code": "UNEXPECTED_ERROR",
  "message": "Unexpected error occurred"
}
```

---

## Stub rules

- No authentication required
- No database
- No service layer
- Deterministic responses only
- Errors are simulated using special path values (`invalid`, `error`)
- Used for local/dev/testing only

---

## Project structure

```
app/
├── controllers/
│   └── rdsDataCacheProxy/
│       └── MgdController.scala
├── models/
│   └── ReturnSummary.scala
```

---

## Example curl

```
curl http://localhost:10405/gambling-external-stub/mgd/GAM0000000001
```

```
curl http://localhost:10405/gambling-external-stub/mgd/invalid
```

```
curl http://localhost:10405/gambling-external-stub/mgd/error
```

---

## License

This project is licensed under the Apache 2.0 License.