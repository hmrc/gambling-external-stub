# gambling-external-stub

The gambling-external-stub provides stubs for downstream services used by gambling-related backend services. It is used to simulate external dependencies for local development and integration testing.

This module includes a stub for the rds-datacache-proxy service used by downstream data cache proxy integrations.

---

## How to use

The real `rds-datacache-proxy` service runs on port `6992`. This stub runs on port `10405` and provides the same API surface with deterministic, scenario-driven responses.

To point the [gambling](https://github.com/hmrc/gambling) backend at the stub, override the `rds-datacache-proxy` base URL in `application.conf`:

```
microservice.services.rds-datacache-proxy.port = 10405
```

This allows you to exercise various edge cases and error responses (400, 401, 404, 500) without depending on the real downstream service.

---

## Running the service

Service Manager:

```
sm2 --start DASS_GAMBLING_ALL
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


---

### 3. Returns Submitted

`GET /gambling/returns-submitted/{regime}/{regNumber}`

See [docs/returns-submitted.md](docs/returns-submitted.md) for full details including regime validation, reg number encoding convention, all response scenarios, and example curl commands.

---

### 4. Business Contact Details

`GET /gambling/business-contact-details/mgd/{mgdRegNumber}`

See [docs/business-contact-details.md](docs/business-contact-details.md) for full details including response scenarios and example curl commands.

---

### 5. Reallocations

`GET /gambling/reallocations-in/{regime}/{regNumber}`

`GET /gambling/reallocations-out/{regime}/{regNumber}`

See [docs/reallocations.md](docs/reallocations.md) for full details including regime validation, reg number encoding convention, all response scenarios, and example curl commands.

---

### 6. Other-assessments

`GET /gambling/other-assessments/{regime}/{regNumber}`

See [docs/other-assessments.md](docs/other-assessments.md) for full details including regime validation, reg number encoding convention, all response scenarios, and example curl commands.

---

### 7. Penalties

`GET /gambling/penalties/{regime}/{regNumber}`

See [docs/penalties.md](docs/penalties.md) for full details including regime validation, reg number encoding convention, item structure (description codes), all response scenarios, and example curl commands.

---

### 8. Payments

`GET /gambling/payments/{regime}/{regNumber}`

See [docs/payments.md](docs/payments.md) for full details including regime validation, reg number encoding convention, item structure (description codes), all response scenarios, and example curl commands.

---

### 9. Repayments Summary

`GET /gambling/repayment-summary/{regime}/{regNumber}`

See [docs/repayment-summary.md](docs/repayment-summary.md) for full details including regime validation, all response scenarios, and example curl commands.

---

### 10. Assessments In Absence Of Return Summary

`GET /gambling/assessments-without-returns/{regime}/{regNumber}`

See [docs/assessments-in-absence-of-returns.md](docs/repayment-summary.md) for full details including regime validation, all response scenarios, and example curl commands.

---

### 11. Statement Overview

`GET /gambling/statement-overview/{regime}/{regNumber}`

See [docs/statement-overview.md](docs/statement-overview.md) for full details including regime validation, reg number encoding convention, all response scenarios, and example curl commands.

---

### 12. RepaymentInterestRepaid

`GET /gambling/repayment-interest-repaid/{regime}/{regNumber}`

See [docs/repayment-interest-repaid.md](docs/repayment-interest-repaid.md) for full details including regime validation, reg number encoding convention, item structure , all response scenarios, and example curl commands.

---

### 13. MGD Details

`GET /gambling/mgd-details/mgd/{mgdRegNumber}`

Returns additional MGD metadata including seasonal flags and linked registration numbers.

Controller mapping:
`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingController.getMgdDetails(mgdRegNumber: String)`

---

### 14. InterestOverview

`GET /gambling/interest-overview/{regime}/{regNumber}`

See [docs/interest-overview.md](docs/interest-overview.md) for full details including regime validation, reg number encoding convention, item structure (description codes), all response scenarios, and example curl commands.

---

### 15. InterestDetails

`GET /gambling/interest-details/{regime}/{regNumber}`

See [docs/interest-details.md](docs/interest-details.md) for full details including regime validation, reg number encoding convention, item structure (description codes), all response scenarios, and example curl commands.

---

### 16. RepaymentInterestDetails

`GET /gambling/repayment-interest-details/{regime}/{regNumber}`

See [docs/repayment-interest-details.md](docs/repayment-interest-details.md) for full details including regime validation, reg number encoding convention, item structure (description codes), all response scenarios, and example curl commands.

---

### 17. SubmittedReturns

`GET /gambling/submitted-returns/{regNumber}`

See [docs/submitted-returns.md](docs/submitted-returns.md) for full details including reg number encoding convention, item structure, all response scenarios, and example curl commands.

---

### 18. SubmittedReturnSingle

`GET /gambling/submitted-return-details/{regNumber}?consecNo=1`

See [docs/submitted-return-details.md](docs/submitted-return-details.md) for full details including reg number encoding convention, all response scenarios, and example curl commands.

---

### 19. OpenReturnPeriods

`GET /gambling/open-periods/{regime}/{regNumber}`

See [docs/open-periods.md](docs/open-periods.md) for full details including regime validation, reg number encoding convention, item structure, all response scenarios, and example curl commands.

---

### 20. Business

See [docs/business.md](docs/business.md) for full details regarding available endpoints, testing values and expected responses.

---
## Behaviour

### Scenario 1 – Full linked history

Request: GET /gambling/mgd-details/mgd/XWM00000001770  
Response: 200 OK  
{
"mgdRegNumber": "XWM00000001770",
"isBusinessSeasonal": 1,
"previousMgdrn1": "XWM00000001774",
"previousMgdrn2": "XDM00000001309",
"previousMgdrn3": null,
"associatedMgdrn1": "XXM00000000723",
"associatedMgdrn2": "XQM00000001196",
"associatedMgdrn3": null,
"systemDate": "2026-05-31"
}

---

### Scenario 2 – Multiple previous & associated registrations

Request: GET /gambling/mgd-details/mgd/XMM00000000992  
Response: 200 OK  
{
"mgdRegNumber": "XMM00000000992",
"isBusinessSeasonal": 1,
"previousMgdrn1": "XMM00000000448",
"previousMgdrn2": "XBM00000000451",
"previousMgdrn3": "XYM00000000466",
"associatedMgdrn1": "XZM00000000469",
"associatedMgdrn2": "XJM00000000472",
"associatedMgdrn3": "XPM00000000475",
"systemDate": "2026-06-02"
}

---

### Default scenario

Request: GET /gambling/mgd-details/mgd/{anyOtherRegNumber}  
Response: 200 OK  
{
"mgdRegNumber": "{anyOtherRegNumber}",
"isBusinessSeasonal": 0,
"previousMgdrn1": null,
"previousMgdrn2": null,
"previousMgdrn3": null,
"associatedMgdrn1": null,
"associatedMgdrn2": null,
"associatedMgdrn3": null,
"systemDate": "2026-06-02"
}

---


### 21. Partner Details

**GET**

```text
/gambling/partner-details/{regime}/{regNumber}
```

Controller mapping:

```text
uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.PartnerDetailsController.getPartnerDetails(regime: String, regNumber: String)
```

Returns partner details for the supplied gambling regime and registration number.

### Behaviour

#### Scenario 1 – Full partner details

Request:

```text
GET /gambling/partner-details/{regime}/{regNumber}
```

The full scenario returns a partner with all available fields populated:

```json
{
  "partners": [
    {
      "mgdRegNumber": "{regNumber}",
      "businessPartnerNumber": "0100049899",
      "dateOfJoining": "2024-01-01",
      "dateOfLeaving": "2025-01-01",
      "solePropTitle": "Mx",
      "solePropFirstName": "solePropFirstName",
      "solePropMiddleName": "solePropMiddleName",
      "solePropLastName": "solePropLastName",
      "businessName": "Partner1",
      "tradingName": "tradingName",
      "dateOfBirth": "1999-09-09",
      "nino": "ni123456789no",
      "utr": "123456789",
      "vrn": "123456789",
      "crn": "123456789",
      "dateOfIncorporation": "2024-01-01",
      "countryOfIncorporation": "countryOfIncorporation",
      "foreignCorporateRef": "foreignCorporateRef",
      "address1": "address1",
      "address2": "address2",
      "address3": "address3",
      "address4": "address4",
      "postcode": "postcode",
      "country": "country",
      "adi": "adi",
      "iomOrCiFlag": "false",
      "phoneNumber": "phoneNumber",
      "mobilePhoneNumber": "mobilePhoneNumber",
      "faxNumber": "faxNumber",
      "emailAddr": "emailAddr",
      "isFutureLeaveDate": 0,
      "isFutureJoinDate": 0,
      "businessType": 2
    }
  ],
  "systemDate": "2026-05-31"
}
```

#### Scenario 2 – Partial partner details

Returns a partner with only a subset of the optional fields populated. This scenario can be used to test handling of missing optional partner information.

```json
{
  "partners": [
    {
      "mgdRegNumber": "{regNumber}",
      "businessPartnerNumber": "0100049899",
      "dateOfJoining": "2024-01-01",
      "dateOfLeaving": "2025-01-01",
      "solePropTitle": "Mx",
      "solePropFirstName": "solePropFirstName",
      "solePropLastName": "solePropLastName",
      "businessName": "Partner1",
      "tradingName": "tradingName",
      "dateOfBirth": "1999-09-09",
      "nino": "ni123456789no",
      "crn": "123456789",
      "dateOfIncorporation": "2024-01-01",
      "countryOfIncorporation": "countryOfIncorporation",
      "foreignCorporateRef": "foreignCorporateRef",
      "adi": "adi",
      "iomOrCiFlag": "false",
      "phoneNumber": "phoneNumber",
      "mobilePhoneNumber": "mobilePhoneNumber",
      "faxNumber": "faxNumber",
      "emailAddr": "emailAddr",
      "isFutureLeaveDate": 0,
      "isFutureJoinDate": 0,
      "businessType": 2
    }
  ],
  "systemDate": "2026-05-31"
}
```

#### Scenario 3 – No partner data

Returns a partner containing only the registration number.

```json
{
  "partners": [
    {
      "mgdRegNumber": "{regNumber}"
    }
  ],
  "systemDate": "2026-05-31"
}
```

### Regime and registration number

The endpoint accepts:

* `regime` – gambling regime used by the request
* `regNumber` – gambling registration number

Example:

```text
GET /gambling/partner-details/mgd/XWM00000001770
```

### Partner model

The `Partner` model contains:

* `mgdRegNumber: String`
* `businessPartnerNumber: Option[String]`
* `dateOfJoining: Option[LocalDate]`
* `dateOfLeaving: Option[LocalDate]`
* `solePropTitle: Option[String]`
* `solePropFirstName: Option[String]`
* `solePropMiddleName: Option[String]`
* `solePropLastName: Option[String]`
* `businessName: Option[String]`
* `tradingName: Option[String]`
* `dateOfBirth: Option[LocalDate]`
* `nino: Option[String]`
* `utr: Option[String]`
* `vrn: Option[String]`
* `crn: Option[String]`
* `dateOfIncorporation: Option[LocalDate]`
* `countryOfIncorporation: Option[String]`
* `foreignCorporateRef: Option[String]`
* `address1: Option[String]`
* `address2: Option[String]`
* `address3: Option[String]`
* `address4: Option[String]`
* `postcode: Option[String]`
* `country: Option[String]`
* `adi: Option[String]`
* `iomOrCiFlag: Option[String]`
* `phoneNumber: Option[String]`
* `mobilePhoneNumber: Option[String]`
* `faxNumber: Option[String]`
* `emailAddr: Option[String]`
* `isFutureLeaveDate: Option[Int]`
* `isFutureJoinDate: Option[Int]`
* `businessType: Option[Int]`

`PartnerDetails` contains:

```text
partners: List[Partner]
systemDate: Option[LocalDate]
```

### Example curl

```bash
curl http://localhost:10405/rds-datacache-proxy/gambling/partner-details/mgd/XGM00000001761
```

The stub provides deterministic responses and does not require authentication or a database connection.




## License

This project is licensed under the Apache 2.0 License.
