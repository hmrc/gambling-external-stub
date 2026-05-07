# Returns Submitted

**GET**

```
/gambling/returns-submitted/{regime}/{regNumber}?pageNo={pageNo}&pageSize={pageSize}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/returns-submitted/{regime}/{regNumber}
```

Controller mapping:
`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingReturnsController.getReturnsSubmitted(regime: String, regNumber: String, pageNo: Int, pageSize: Int)`

Query parameters:

| Parameter  | Type | Default | Description                |
|------------|------|---------|----------------------------|
| `pageNo`   | Int  | 1       | Page number (1-based)      |
| `pageSize` | Int  | 10      | Number of records per page |

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

**4th and 5th digits from the right** form a 2-digit number (00-99) controlling how many total records the stub holds for that reg number. The response returns a paginated slice based on `pageNo` and `pageSize`. Ignored for error status codes.

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
GET /gambling/returns-submitted/INVALID/XWM00003103200
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
GET /gambling/returns-submitted/MGD/XWM00003100400
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
GET /gambling/returns-submitted/MGD/XWM00003100401
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

### 404 - No returns found

Request:

```
GET /gambling/returns-submitted/MGD/XWM00003100404
```

Response:

```
404 NOT_FOUND
```

```json
{
  "code": "NOT_FOUND",
  "message": "No returns found for the given registration number"
}
```

---

### 500 - Unexpected error

Request:

```
GET /gambling/returns-submitted/MGD/XWM00003100500
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
GET /gambling/returns-submitted/MGD/XWM00003100200
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2026-01-01",
  "periodEndDate": "2026-12-31",
  "total": 0,
  "totalPeriodRecords": 0,
  "amountDeclared": []
}
```

---

### 200 - Small result set (3 records, fits one page)

Request:

```
GET /gambling/returns-submitted/MGD/XWM00003103200
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2026-01-01",
  "periodEndDate": "2026-12-31",
  "total": 6000,
  "totalPeriodRecords": 3,
  "amountDeclared": [
    { "descriptionCode": 1, "periodStartDate": "2025-01-01", "periodEndDate": "2025-01-31", "amount": 1000 },
    { "descriptionCode": 2, "periodStartDate": "2025-02-01", "periodEndDate": "2025-02-28", "amount": 2000 },
    { "descriptionCode": 3, "periodStartDate": "2025-03-01", "periodEndDate": "2025-03-31", "amount": 3000 }
  ]
}
```

---

### 200 - Large result set with pagination (50 records)

Request - page 1 of 5:

```
GET /gambling/returns-submitted/MGD/XWM00003150200?pageNo=1&pageSize=10
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2026-01-01",
  "periodEndDate": "2026-12-31",
  "total": 1275000,
  "totalPeriodRecords": 50,
  "amountDeclared": [
    { "descriptionCode": 1, "periodStartDate": "2025-01-01", "periodEndDate": "2025-01-31", "amount": 1000 },
    ...
    { "descriptionCode": 10, "periodStartDate": "2025-10-01", "periodEndDate": "2025-10-31", "amount": 10000 }
  ]
}
```

`totalPeriodRecords` always reflects the full count regardless of the page requested. The caller uses this to determine how many pages exist.

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/returns-submitted/MGD/XWM00003150200?pageNo=1&pageSize=10"
```