# Repayment Summary

**GET**

```
/gambling/repayment-summary/{regime}/{regNumber}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/repayment-summary/{regime}/{regNumber}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingRepaymentsController.getRepaymentsSummary(regime: String, regNumber: String)`

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


**6th from last** digit of regNo controls the customisation

| 6th from last digit | Response                                         |
|---------------------|--------------------------------------------------|
| `0`                 | actualRepayments & repaymentsInterestRepaid      |
| `1`                 | ONLY actualRepayments                            |
| `2`                 | ONLY repaymentsInterestRepaid                    |
| `3`                 | NO actualRepayments, NO repaymentsInterestRepaid |
| anything else       | actualRepayments & repaymentsInterestRepaid      |


---

## RepaymentsSummary structure

Each RepaymentsSummary has the following fields:

| Field                            | Type       | Description                                      |
|----------------------------------|------------|--------------------------------------------------|
| `periodStartDate`                | LocalDate  | Start of the period      |
| `periodEndDate`                  | LocalDate  | End of the period         |
| `actualRepaymentsAmount`         | BigDecimal  | Total actual repayments amount                       |
| `repaymentsInterestRepaidAmount` | BigDecimal        | Total repayment interest repaid amount |
| `total`                          | BigDecimal | Combined repayment total                        |



---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/repayment-summary/INVALID/XWM00003103200
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
GET /gambling/repayment-summary/gbd/XWM00003100400
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
GET /gambling/repayment-summary/gbd/XWM00003100401
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


### 500 - Unexpected error

Request:

```
GET /gambling/repayment-summary/gbd/XWM00003100500
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
GET /gambling/repayment-summary/gbd/XWM00003100200
```

Response:

```
200 OK
```

```json

{
  "periodStartDate":"2013-03-01",
  "periodEndDate":"2014-03-11",
  "actualRepaymentsAmount":71.84,
  "repaymentsInterestRepaidAmount":-35.76,
  "total":36.08
}
```
---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/repayment-summary/gbd/XWM00003103200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/repayment-summary/gbd/XWM00003350200"
```
