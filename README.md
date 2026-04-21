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
http://localhost:10405/rds-datacache-proxy
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
http://localhost:10405/rds-datacache-proxy/mgd/{mgdRegNumber}
```

Controller mapping:
`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.MgdController.getReturnSummary(mgdRegNumber: String)`

---

## Behaviour

### Happy path - Scenario 1

Request:
```
GET http://localhost:10405/rds-datacache-proxy/mgd/GAM0000000001
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
GET http://localhost:10405/rds-datacache-proxy/mgd/GAM0000000002
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
GET http://localhost:10405/rds-datacache-proxy/mgd/invalid
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
GET http://localhost:10405/rds-datacache-proxy/mgd/error
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
│       └── GamblingController.scala
├── models/
│   └── ReturnSummary.scala
```

---

## Example curl

```
curl http://localhost:10405/rds-datacache-proxy/mgd/GAM0000000001
```

```
curl http://localhost:10405/rds-datacache-proxy/mgd/invalid
```

```
curl http://localhost:10405/rds-datacache-proxy/mgd/error
```

---
---

### 2. MGD Certificate

**GET**

```
/mgd/{mgdRegNumber}/certificate
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/mgd/{mgdRegNumber}/certificate
```

Controller mapping:
`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingController.getMgdCertificate(mgdRegNumber: String)`

---

## Behaviour

### Happy path - Scenario 1 (Full data)

Request:

```
GET /mgd/GAM0000000001/certificate
```

Response:

```
200 OK
```

```json
{
  "mgdRegNumber": "GAM0000000001",
  "registrationDate": "2023-01-15",
  "businessName": "Acme Gaming Ltd",
  "typeOfBusiness": "Corporate Body",
  "noOfPartners": 2,
  "groupReg": "Y",
  "noOfGroupMems": 1,
  "dateCertIssued": "2024-02-01"
}
```

---

### Happy path - Scenario 2 (Minimal data)

Request:

```
GET /mgd/GAM0000000002/certificate
```

Response:

```
200 OK
```

```json
{
  "mgdRegNumber": "GAM0000000002",
  "registrationDate": "2022-10-05",
  "businessName": "Example Sole Trader",
  "typeOfBusiness": "Sole proprietor",
  "noOfPartners": 0,
  "groupReg": "N",
  "noOfGroupMems": 0,
  "dateCertIssued": "2024-01-10"
}
```

---

### Default scenario

Request:

```
GET /mgd/{anyOtherReg}/certificate
```

Response:

```
200 OK
```

* Returns a generic payload
* No partners or group members

---

### Invalid MGD registration number

Request:

```
GET /mgd/invalid/certificate
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
GET /mgd/error/certificate
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


## License

This project is licensed under the Apache 2.0 License.