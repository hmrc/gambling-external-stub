# Interest Drilldown / Interest Accruing Drilldown

Two endpoints are provided: one for interest accruing drilldown and one for interest drilldown.

**GET**

```
/gambling/interest-accruing-drilldown/{regime}/{regNumber}/{interestId}?pageNo={pageNo}&pageSize={pageSize}
/gambling/interest-drilldown/{regime}/{regNumber}/{interestId}?pageNo={pageNo}&pageSize={pageSize}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/interest-accruing-drilldown/{regime}/{regNumber}/{interestId}
http://localhost:10405/rds-datacache-proxy/gambling/interest-drilldown/{regime}/{regNumber}/{interestId}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingInterestAccruingController.getInterestAccruingDrilldown(regime: String, regNumber: String, interestId: String, pageNo: Int, pageSize: Int)`

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingInterestController.getInterestDrilldown(regime: String, regNumber: String, interestId: String, pageNo: Int, pageSize: Int)`

Query parameters:

| Parameter  | Type | Default | Description                |
|------------|------|---------|----------------------------|
| `pageNo`   | Int  | 1       | Page number (1-based)      |
| `pageSize` | Int  | 10      | Number of records per page |

The `interestId` path parameter is accepted but does not affect the stub response — behaviour is determined entirely by `regime` and `regNumber`.

---

## Regime validation

The `regime` path segment is validated against the `Regime` enum before the reg number is inspected. Valid values (case-insensitive):

| Value | Regime               |
|-------|----------------------|
| `gbd` | General Betting Duty |
| `pbd` | Pool Betting Duty    |
| `rgd` | Remote Gaming Duty   |
| `mgd` | Machine Games Duty   |

Any other value returns:

```
400 BAD_REQUEST
```

```json
{
  "code": "INVALID_REGIME",
  "message": "regime must be one of: gbd, pbd, rgd, mgd"
}
```

---

## Reg number encoding convention

Once the regime is valid, the stub derives its behaviour entirely from the reg number. No special test strings are needed.

**Last 3 digits** control the HTTP status code returned:

| Last 3 digits | Response                  |
|---------------|---------------------------|
| `400`         | 400 BAD_REQUEST           |
| `401`         | 401 UNAUTHORIZED          |
| `404`         | 404 NOT_FOUND             |
| `500`         | 500 INTERNAL_SERVER_ERROR |
| anything else | 200 OK                    |

**4th and 5th digits from the right** form a 2-digit number (00–99) controlling how many total records the stub holds for that reg number. The response returns a paginated slice based on `pageNo` and `pageSize`. Ignored for error status codes.

Examples:

| Reg number       | Status | Total records |
|------------------|--------|---------------|
| `XWM00003100400` | 400    | n/a           |
| `XWM00003100401` | 401    | n/a           |
| `XWM00003100404` | 404    | n/a           |
| `XWM00003100500` | 500    | n/a           |
| `XWM00003100200` | 200    | 0             |
| `XWM00003103200` | 200    | 3             |
| `XWM00003109200` | 200    | 9             |
| `XWM00003150200` | 200    | 50            |

The record count defaults to 0 if the reg number is shorter than 5 characters.

---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/interest-accruing-drilldown/INVALID/XWM00003103200/INT-001
```

Response:

```
400 BAD_REQUEST
```

```json
{
  "code": "INVALID_REGIME",
  "message": "regime must be one of: gbd, pbd, rgd, mgd"
}
```

---

### 400 - Bad request (via reg number)

Request:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003100400/INT-001
```

Response:

```
400 BAD_REQUEST
```

```json
{
  "code": "INVALID_REQUEST",
  "message": "Bad request"
}
```

---

### 401 - Unauthorized

Request:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003100401/INT-001
```

Response:

```
401 UNAUTHORIZED
```

```json
{
  "code": "UNAUTHORIZED",
  "message": "Unauthorized to access this resource"
}
```

---

### 404 - No records found

Request:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003100404/INT-001
```

Response:

```
404 NOT_FOUND
```

```json
{
  "code": "NOT_FOUND",
  "message": "No interest accruing drilldown found for the given registration number"
}
```

For `interest-drilldown` the message is `"No interest drilldown found for the given registration number"`.

---

### 500 - Unexpected error

Request:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003100500/INT-001
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

### 200 - Empty result set

Request:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003100200/INT-001
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2024-12-01",
  "periodEndDate":   "2026-09-30",
  "total":           0,
  "totalRecords":    0,
  "descriptionCode": 2660,
  "items":           []
}
```

---

### 200 - Small result set (3 records, fits one page)

Request:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003103200/INT-001
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2024-12-01",
  "periodEndDate":   "2026-09-30",
  "total":           601.65,
  "totalRecords":    3,
  "descriptionCode": 2660,
  "items": [
    { "interestOn": 1000, "dateFrom": "2024-12-01", "dateTo": "2025-01-01", "noOfDays": 30, "rate": 2.6, "amount": 100.55 },
    { "interestOn": 2000, "dateFrom": "2025-01-01", "dateTo": "2025-02-01", "noOfDays": 30, "rate": 2.6, "amount": 200.55 },
    { "interestOn": 3000, "dateFrom": "2025-02-01", "dateTo": "2025-03-01", "noOfDays": 30, "rate": 2.6, "amount": 300.55 }
  ]
}
```

---

### 200 - Large result set with pagination (50 records)

Request — page 1 of 5:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003150200/INT-001?pageNo=1&pageSize=10
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2024-12-01",
  "periodEndDate":   "2026-09-30",
  "total":           127527.5,
  "totalRecords":    50,
  "descriptionCode": 2660,
  "items": [
    { "interestOn": 1000,  "dateFrom": "2024-12-01", "dateTo": "2025-01-01", "noOfDays": 30, "rate": 2.6, "amount": 100.55 },
    "...",
    { "interestOn": 10000, "dateFrom": "2025-09-01", "dateTo": "2025-10-01", "noOfDays": 30, "rate": 2.6, "amount": 1000.55 }
  ]
}
```

Request — page 2 of 5:

```
GET /gambling/interest-accruing-drilldown/MGD/XWM00003150200/INT-001?pageNo=2&pageSize=10
```

```json
{
  "periodStartDate": "2024-12-01",
  "periodEndDate":   "2026-09-30",
  "total":           127527.5,
  "totalRecords":    50,
  "descriptionCode": 2660,
  "items": [
    { "interestOn": 11000, "dateFrom": "2025-10-01", "dateTo": "2025-11-01", "noOfDays": 30, "rate": 2.6, "amount": 1100.55 },
    "...",
    { "interestOn": 20000, "dateFrom": "2026-07-01", "dateTo": "2026-08-01", "noOfDays": 30, "rate": 2.6, "amount": 2000.55 }
  ]
}
```

`total` always reflects the sum across all records regardless of the page requested. The caller uses this to determine how many pages exist.

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/interest-accruing-drilldown/MGD/XWM00003150200/INT-001?pageNo=1&pageSize=10"

curl "http://localhost:10405/rds-datacache-proxy/gambling/interest-drilldown/MGD/XWM00003150200/INT-001?pageNo=1&pageSize=10"
```
