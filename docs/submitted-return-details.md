# SubmittedReturnSingle

**GET**

```
/gambling/submitted-return-details/{regNumber}/{consecNo}
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/submitted-return-details/{regNumber}/{consecNo}
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingSubmittedReturnsController.getSubmittedReturnSingle(regNumber: String, consecNo: Int)`

Path parameters:

| Parameter  | Type   | Default | Description                                    |
|------------|--------|---------|------------------------------------------------|
| `regNumber` | String |        | id number used to generate different data sets |
| `consecNo` | Int    |        | index number                                   |

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

It will return a maximum of 1 record
---

## Item structure

Each SubmittedReturnSingle has the following fields:

| Field                          | Type       | Description                                            |
|--------------------------------|------------|--------------------------------------------------------|
| `consec_no`                    | Int        | eg: `1500`                                             |
| `mgd_period`                   | String     | eg: `"31/03/2026"`                                     |
| `submitted_date`               | LocalDate  | eg: `"2026-04-16"`                                     |
| `ack_ref`                      | String     |  eg: `3UBK ULKP TJNX TKM` generated based of consec_no |
| `noOfMachines`                 | Int        | eg: `16'                                               |
| `netTakingsHigherRate`         | BigDecimal | eg: '1101.1'                                           |
| `netTakingsStdRate`            | BigDecimal | eg: '	220'                                             |
| `netTakingsLowerRate`	         | BigDecimal | eg: '	2202.2'                                          |
| `totalDueHigherRate`           | BigDecimal | eg: '	110'                                             |
| `totalDueStdRate`	             | BigDecimal | eg: '	3303.3'                                          |
| `totalDueLowerRate`            | BigDecimal | eg: '	55'                                              |
| `dutyPayable`                  | BigDecimal | eg: '	385'                                             |
| `underDeclaredDuty`            | BigDecimal | eg: '	440'                                             |
| `previousReturnAmount`         | BigDecimal | eg: '	1100'                                            |
| `negativeAmountCarriedForward` | BigDecimal | eg: '1099.89'                                          |
| `totalNetDutyPayable`          | BigDecimal | eg: '830.39'                                           |

---

## Behaviour

---

### 400 - Bad request (via reg number)

Request:

```
GET /gambling/submitted-return-details/XWM00003100400/1
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
GET /gambling/submitted-return-details/XWM00003100401/1
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
GET /gambling/submitted-return-details/XWM00003100404/1
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
GET /gambling/submitted-return-details/XWM00003100500/1
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
GET /gambling/submitted-return-details/XWM00003100200/1
```

Response:

```
200 OK
```
It will always return 1 record

---

### 200 - result set (always 1 record)

Request:

```
GET /gambling/submitted-return-details/XWM00003103200/1
```

Response:

```
200 OK
```

```json

{
  "consecNo": 11,
  "mgdPeriod": "01/04/2023 - 31/07/2023",
  "submittedDate": "2024-07-31",
  "ackRef": "3UBK ULKP TJNX TKM",
  "noOfMachines": 16,
  "netTakingsHigherRate": 1101.1,
  "netTakingsStdRate": 220,
  "netTakingsLowerRate": 2202.2,
  "totalDueHigherRate": 110,
  "totalDueStdRate": 3303.3,
  "totalDueLowerRate": 55,
  "dutyPayable": 385,
  "underDeclaredDuty": 440,
  "previousReturnAmount": 1100,
  "negativeAmountCarriedForward": 1099.89,
  "totalNetDutyPayable": 830.39
}

```

---

## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/submitted-return-details/XWM03203122200/11"
```

