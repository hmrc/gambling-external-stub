# Statement Overview

**GET**

```
/gambling/statement-overview/{regime}/{regNumber}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/statement-overview/{regime}/{regNumber}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingStatementOverviewController.getStatementOverview(regime: String, regNumber: String)`

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

**4th and 5th digits from the right** form a 2-digit seed (00-99) used to scale the monetary amounts in the response. Ignored for error status codes.

**7th digit from the right** controls the response variant:

| 7th digit | Variant                                                  |
|-----------|----------------------------------------------------------|
| `0`       | All monetary fields are zero                             |
| `2`       | High payments, resulting in negative total               |
| anything else | Default: moderate payments, resulting in positive total |

---

## StatementOverview structure

| Field                | Type              | Description                              |
|----------------------|-------------------|------------------------------------------|
| `gtrPeriodStartDate` | LocalDate (optional) | Start of the GTR period               |
| `gtrPeriodEndDate`   | LocalDate (optional) | End of the GTR period                 |
| `total`              | BigDecimal        | Computed total across all line items     |
| `balance`            | BigDecimal        | Balance after payments                   |
| `amountDeclared`     | BigDecimal        | Amount declared                          |
| `assessments`        | BigDecimal        | Assessments amount                       |
| `penalties`          | BigDecimal        | Penalties amount                         |
| `adjustments`        | BigDecimal        | Adjustments amount                       |
| `reallocations`      | BigDecimal        | Reallocations amount                     |
| `otherAssessments`   | BigDecimal        | Other assessments amount                 |
| `interest`           | BigDecimal        | Interest amount                          |
| `payments`           | BigDecimal        | Payments amount                          |
| `repayments`         | BigDecimal (optional) | Repayments amount                    |

---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/statement-overview/INVALID/XWM00003103200
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
GET /gambling/statement-overview/gbd/XWM00003100400
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
GET /gambling/statement-overview/gbd/XWM00003100401
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

### 404 - Not found

Request:

```
GET /gambling/statement-overview/gbd/XWM00003100404
```

Response:

```
404 NOT_FOUND
```

```json
{
  "code": "NOT_FOUND",
  "message": "No statement overview found for the given registration number"
}
```

---

### 500 - Unexpected error

Request:

```
GET /gambling/statement-overview/gbd/XWM00003100500
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

### 200 - All zeros (variant 0)

Request:

```
GET /gambling/statement-overview/gbd/XWM00000103200
```

Response:

```
200 OK
```

```json
{
  "gtrPeriodStartDate": "2025-06-01",
  "gtrPeriodEndDate": "2026-06-30",
  "total": 0,
  "balance": 0,
  "amountDeclared": 0,
  "assessments": 0,
  "penalties": 0,
  "adjustments": 0,
  "reallocations": 0,
  "otherAssessments": 0,
  "interest": 0,
  "payments": 0,
  "repayments": 0
}
```

---

### 200 - Positive total (default variant)

Request:

```
GET /gambling/statement-overview/gbd/XWM00001103200
```

Response:

```
200 OK
```

```json
{
  "gtrPeriodStartDate": "2025-06-01",
  "gtrPeriodEndDate": "2026-06-30",
  "total": 813,
  "balance": 613,
  "amountDeclared": 1000,
  "assessments": 50,
  "penalties": 25,
  "adjustments": 10,
  "reallocations": 5,
  "otherAssessments": 15,
  "interest": 8,
  "payments": 200,
  "repayments": 100
}
```

---

### 200 - Negative total (variant 2)

Request:

```
GET /gambling/statement-overview/gbd/XWM00002103200
```

Response:

```
200 OK
```

```json
{
  "gtrPeriodStartDate": "2025-06-01",
  "gtrPeriodEndDate": "2026-06-30",
  "total": -987,
  "balance": -2987,
  "amountDeclared": 1000,
  "assessments": 50,
  "penalties": 25,
  "adjustments": 10,
  "reallocations": 5,
  "otherAssessments": 15,
  "interest": 8,
  "payments": 2000,
  "repayments": 100
}
```

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/statement-overview/gbd/XWM00001103200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/statement-overview/gbd/XWM00002103200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/statement-overview/gbd/XWM00000103200"
```
