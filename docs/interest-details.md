# interest-details

**GET**

```
/gambling/interest-details/{regime}/{regNumber}?pageNo={pageNo}&pageSize={pageSize}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/interest-details/{regime}/{regNumber}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingInterestDetailsController.getInterestDetails(regime: String, regNumber: String, pageNo: Int, pageSize: Int)`

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
| `XWM00003350200` | 200    | 35            |

The record count defaults to 0 if the reg number is shorter than 5 characters.

---

## Item structure

Each InterestDetails item has the following fields:

| Field             | Type       | Description                                                                                                                                                                                                                                                                             |
|-------------------|------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `descriptionCode` | Int        | `2640` = PPLR interest bearing; `2650` = Return Charge;  `2655` = Return Interest; `2680` = Late Filing Penalty; `2685` = Late Filing Penalty interest; `2690` = Late Payment Penalty; `2695` = Late Payment Penalty interest; `2660` = Central Assessment; `2670` = Officer Assessment |
| `amount`          | BigDecimal | Interest charge amount (negatively signed)                                                                                                                                                                                                                                              |
| `interestId`      | String     | Charge reference (safe_charge_ref)                                                                                                                                                                                                                                                      |
| `periodStartDate` | LocalDate  | Charge period start date                                                                                                                                                                                                                                                                |
| `periodEndDate`   | LocalDate  | Charge period end date                                                                                                                                                                                                                                                                  |

Records alternate between description codes. Amounts are `i * 100` negated (record 1 = -100, record 2 = -200, etc.).

---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/interest-details/INVALID/XWM00003103200
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
GET /gambling/interest-details/gbd/XWM00003100400
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
GET /gambling/interest-details/gbd/XWM00003100401
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

### 404 - No InterestDetails found

Request:

```
GET /gambling/interest-details/gbd/XWM00003100404
```

Response:

```
404 NOT_FOUND
```

```json
{
  "code": "NOT_FOUND",
  "message": "No InterestDetails found for the given registration number"
}
```

---

### 500 - Unexpected error

Request:

```
GET /gambling/interest-details/gbd/XWM00003100500
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
GET /gambling/interest-details/gbd/XWM00003100200
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2023-03-01",
  "periodEndDate": "2026-03-11",
  "total": 0,
  "totalRecords": 0,
  "items": []
}
```

---

### 200 - Small result set (3 records, fits one page)

Request:

```
GET /gambling/interest-details/gbd/XWM00003103200
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2023-03-01",
  "periodEndDate": "2026-03-11",
  "total": -1200,
  "totalRecords": 3,
  "items": [
    {
      "descriptionCode": 2740,
      "amount": -500,
      "interestId": "SAFE-CHG-00003",
      "periodStartDate": "2024-01-01",
      "periodEndDate": "2014-03-31"
    },
    {
      "descriptionCode": 2740,
      "amount": -400,
      "interestId": "SAFE-CHG-00004",
      "periodStartDate": "2024-10-01",
      "periodEndDate": "2014-12-31"
    },
    {
      "descriptionCode": 2740,
      "amount": -300,
      "interestId": "SAFE-CHG-00005",
      "periodStartDate": "2023-04-01",
      "periodEndDate": "2013-06-30"
    }
  ]
}
```

---

### 200 - Large result set with pagination (35 records)

Request - page 1 of 4:

```
GET /gambling/interest-details/gbd/XWM00003350200?pageNo=1&pageSize=10
```

Response:

```
200 OK
```

```json
{
  "periodStartDate": "2023-11-01",
  "periodEndDate": "2026-07-31",
  "total": -63000,
  "totalRecords": 35,
  "items": [
    { "descriptionCode": 2740, "amount": -100, "interestId": "SAFE-CHG-00003", "periodStartDate": "...", "periodEndDate": "..." },
    "...",
    { "descriptionCode": 2740, "amount": -1000, "interestId": "SAFE-CHG-00004", "periodStartDate": "...", "periodEndDate": "..." }
  ]
}
```

`totalRecords` always reflects the full count regardless of the page requested. The caller uses this to determine how many pages exist.

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/interest-details/gbd/XWM00003103200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/interest-details/gbd/XWM00003350200?pageNo=2&pageSize=10"
```
