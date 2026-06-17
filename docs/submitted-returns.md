# SubmittedReturns

**GET**

```
/gambling/submitted-returns/{regNumber}?sortBy={sortBy}&orderBy={orderBy}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/submitted-returns/{regNumber}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingSubmittedReturnsController.getSubmittedReturns(regNumber: String, sortBy: Int, orderBy: Int)`

Query parameters:

| Parameter  | Type        | Default    | Description                                                                                                                                                                            |
|------------|-------------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `sortBy`   | Int         | 3          | 1 = period_start_date<br/>2 = submitted_date<br/>else = period_end_date<br/>This value does NOT affect the output apart from the fact it is output in the `ack_ref` field of the items |
| `orderBy` | String      | ASC        | ASC or DESC<br/>This value DOES affect the output order of the items returned, they will be sorted on `submitted_date`                                                                 |   


---

## Regime validation

None - it is not Regime specific

---

## Reg number encoding convention

The stub derives its behaviour entirely from the reg number. No special test strings are needed.

**Last 3 digits** control the HTTP status code returned:

| Last 3 digits | Response                  |
|---------------|---------------------------|
| `400`         | 400 BAD_REQUEST           |
| `401`         | 401 UNAUTHORIZED          |
| `404`         | 404 NOT_FOUND             |
| `500`         | 500 INTERNAL_SERVER_ERROR |
| anything else | 200 OK                    |

**4th and 5th digits from the right** form a 2-digit number (00-99) controlling how many total records the stub holds for that reg number. There is NO pagination. Ignored for error status codes.

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

Each SubmittedReturnsItem has the following fields:

| Field            | Type             | Description                                                                                                                 |
|------------------|------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `consec_no`      | Int              | eg: `1500`                                                                                                                  |
| `mgd_period`     | String           | eg: `"31/03/2026"`                                                                                                          |
| `submitted_date` | LocalDate        | eg: `"2026-04-16"`                                                                                                          |
| `ack_ref`        | String           | eg: `"1500__sortBy=2__orderBy=DESC"` it contains the sortBy & orderBy params that were translated from the URL query params |


---

## Behaviour

---

### 400 - Bad request (via reg number)

Request:

```
GET /gambling/submitted-returns/gbd/XWM00003100400
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
GET /gambling/submitted-returns/gbd/XWM00003100401
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

### 404 - No submitted-returns found

Request:

```
GET /gambling/submitted-returns/gbd/XWM00003100404
```

Response:

```
404 NOT_FOUND
```

```json
{
  "code": "NOT_FOUND",
  "message": "No submitted-returns found for the given registration number"
}
```

---

### 500 - Unexpected error

Request:

```
GET /gambling/submitted-returns/gbd/XWM00003100500
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
GET /gambling/submitted-returns/gbd/XWM00003100200
```

Response:

```
200 OK
```

```json
{
  "items": []
}
```

---

### 200 - Small result set (1 record, fits one page)

Request:

```
GET /gambling/submitted-returns/gbd/XWM00003103200?sortBy=2&orderBy=DESC
```

Response:

```
200 OK
```

```json
{
  "items": [
    {
      "consec_no": 1100,
      "mgd_period": "31/05/2026",
      "submitted_date": "2026-06-16",
      "ack_ref": "1100__sortBy=2__orderBy=DESC"
    }
  ]
}
```

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/submitted-returns/XWM00003103200"
```

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/submitted-returns/XWM00003350200?sortBy=2&orderBy=DESC"
```
