# gambling-external-stub

The gambling-external-stub provides stubs for downstream services used by gambling-related backend services. It is used to simulate external dependencies for local development and integration testing.

This module includes a stub for the RDS-Cache-repository  service used by downstream data cache proxy integrations.

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

**GET** `/mgd/reg-number?mgdRegNumber={mgdRegNumber}`

Controller mapping:
`uk.gov.hmrc.gamblingexternalstub.mgd.controllers.MgdController.getReturnSummary(mgdRegNumber: String)`

---

## Behaviour

### Happy path

Request:
```
GET /mgd/reg-number?mgdRegNumber=ABC123
```

Response:
```
200 OK
```

```json
{
  "mgdRegNumber": "ABC123",
  "returnsDue": 2,
  "returnsOverdue": 1
}
```

---

### Missing mgdRegNumber

Request:
```
GET /mgd/reg-number?mgdRegNumber=
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

### Forced error

Request:
```
GET /mgd/reg-number?mgdRegNumber=error
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
curl "http://localhost:9000/mgd/reg-number?mgdRegNumber=ABC123"
```

---

## License

This project is licensed under the Apache 2.0 License.