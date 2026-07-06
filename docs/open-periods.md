# OpenReturnPeriods

**GET**

```
/gambling/open-periods/{regime}/{regNumber}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/open-periods/{regime}/{regNumber}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingOpenReturnsController.getOpenPeriods(regime: String, regNumber: String)`

---

## Regime validation

The `regime` path segment is validated against the `Regime` enum before the reg number is inspected. Valid values (case-insensitive):

| Value | Regime               |
|-------|----------------------|
| `mgd` | Machine Games Duty   |

Any other value returns:

```
400 BAD_REQUEST
```

```json
{
  "code": "INVALID_REGIME",
  "message": "regime must be one of: mgd"
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

**4th and 5th digits from the right** form a 2-digit number (00-99) controlling how many open periods the stub returns for that reg number. There is NO pagination. Ignored for error status codes.

Examples:

| Reg number       | Status | Total records |
|------------------|--------|----------------|
| `XWM00003100400` | 400    | n/a            |
| `XWM00003100401` | 401    | n/a            |
| `XWM00003100404` | 404    | n/a            |
| `XWM00003100500` | 500    | n/a            |
| `XWM00003100200` | 200    | 0              |
| `XWM00003103200` | 200    | 3              |
| `XWM00003109200` | 200    | 9              |
| `XWM00003350200` | 200    | 35             |

The record count defaults to 0 if the reg number is shorter than 5 characters.

---

## Item structure

Each `OpenReturnPeriod` has the following fields:

| Field       | Type      | Description                                                                          |
|-------------|-----------|---------------------------------------------------------------------------------------|
| `consecNo`  | Int       | 1-based sequence number, e.g. `1`, `2`, `3`...                                        |
| `mgdPeriod` | String    | Quarterly period covered, e.g. `"01/07/2026 - 30/09/2026"`, starting from the current month |
| `dueDate`   | LocalDate | Due date for the period, one month after the period end                             |
| `status`    | Int       | Status of the open period                                                            |

The response wraps the items in an `openPeriods` array:

```json
{
  "openPeriods": [ ... ]
}
```

---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/open-periods/INVALID/XWM00003103200
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
GET /gambling/open-periods/gbd/XWM00003100400
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
GET /gambling/open-periods/gbd/XWM00003100401
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

### 404 - No open periods found

Request:

```
GET /gambling/open-periods/gbd/XWM00003100404
```

Response:

```
404 NOT_FOUND
```

```json
{
  "code": "NOT_FOUND",
  "message": "No OpenReturnPeriods found for the given registration number"
}
```

---

### 500 - Unexpected error

Request:

```
GET /gambling/open-periods/gbd/XWM00003100500
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
GET /gambling/open-periods/gbd/XWM00003100200
```

Response:

```
200 OK
```

```json
{
  "openPeriods": []
}
```

---

### 200 - Small result set (3 records)

Request:

```
GET /gambling/open-periods/gbd/XWM00003103200
```

Response:

```
200 OK
```

```json
{
  "openPeriods": [
    { "consecNo": 1, "period": "01/07/2026 - 30/09/2026", "dueDate": "2026-10-31", "status": 1 },
    { "consecNo": 2, "period": "01/10/2026 - 31/12/2026", "dueDate": "2027-01-31", "status": 1 },
    { "consecNo": 3, "period": "01/01/2027 - 31/03/2027", "dueDate": "2027-04-30", "status": 1 }
  ]
}
```

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/open-periods/gbd/XWM00003103200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/open-periods/gbd/XWM00003100200"
```
