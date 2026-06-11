# Interest Overview

**GET**

```
/gambling/interest-overview/{regime}/{regNumber}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/interest-overview/{regime}/{regNumber}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingInterestController.getInterestOverview(regime: String, regNumber: String)`

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

| 6th from last digit | interestAmount | interestAccruingAmount | repaymentInterestAmount | regime |
|---------------------|----------------|------------------------|-------------------------|--------|
| `0`                 | Y              | Y                      | Y                       | MGD    |
| `1`                 | Y              | N                      | N                       | ALL    |
| `2`                 | N              | Y                      | N                       | ALL    |
| `3`                 | N              | N                      | Y                       | MGD    |
| `4`                 | N              | N                      | N                       | ALL    |
| `5`                 | Y              | Y                      | N                       | ALL    |
| `6`                 | N              | Y                      | Y                       | MGD    |
| `7`                 | Y              | N                      | Y                       | MGD    |
| `anything else`     | N              | N                      | N                       | ALL    |



---

## InterestOverview structure

Each RepaymentsSummary has the following fields:

| Field                      | Type          | Description                     |
|----------------------------|---------------|---------------------------------|
| `periodStartDate`          | LocalDate     | Start of the period             |
| `periodEndDate`            | LocalDate     | End of the period               |
| `interestAmount`           | BigDecimal    | Total interest amount           |
| `interestAccruingAmount`   | BigDecimal    | Total Interest Accruing amount  |
| `repaymentInterestAmount`  | BigDecimal    | Total Repayment Interest amount |
| `total`                    | BigDecimal    | Combined total                  |



---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/interest-overview/INVALID/XWM00003103200
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
GET /gambling/interest-overview/gbd/XWM00003100400
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
GET /gambling/interest-overview/gbd/XWM00003100401
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
GET /gambling/interest-overview/gbd/XWM00003100500
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
GET /gambling/interest-overview/gbd/XWM00003100200
```

Response:

```
200 OK
```

```json

{
  "periodStartDate":"2013-03-01",
  "periodEndDate":"2014-03-11",
  "interestAmount":"£81.84",
  "interestAccruingAmount":-25.76,
  "repaymentInterestAmount":41.23,
  "total":66.37
}
```
---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/interest-overview/gbd/XWM00003003200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/interest-overview/gbd/XWM00003050200"
```
