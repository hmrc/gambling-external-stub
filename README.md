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

The JSON fixtures and success examples are described in [Business contact details RDS data](#26-business-contact-details-rds-data).

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

See [Previous and associated registration numbers RDS data](#28-previous-and-associated-registration-numbers-rds-data) for success examples and JSON fixtures.

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

See [Partner details RDS data](#30-partner-details-rds-data) for the available success scenarios, request examples and JSON fixtures.

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

---

### 22. ActiveMQ Proxy — Send Message

`POST /activemq-proxy/queue/send`

Stubs the [`activemq-proxy`](https://github.com/hmrc/activemq-proxy) service so gambling services can publish queue messages without a running ActiveMQ broker. Mirrors the real request/response contract and validation, but does not forward to a broker — it validates, logs, and returns the correlation id.

See [docs/activemq-proxy-queue-send.md](docs/activemq-proxy-queue-send.md) for full details including the request schema, permitted queue identifiers, all response scenarios, and example curl commands.

---

q### 23. Premises details RDS data

#### Success response (no data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/premises-details/MGD/XYM00000000699
```

Returns `200 OK` with an empty premises list:

```json
{
  "totalRows": 0,
  "premises": []
}
```

The complete response is stored in [the test data file](conf/data/premises-details/XYM00000000699.json).

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/premises-details/MGD/XGM00000001764
```

Returns `200 OK` with `totalRows: 100`. The supplied premises have 90 Dalford Court addresses and 10 Bricklin Mews
addresses. All records have `systemDate: "2026-09-01"`.

The complete response is stored in [the test data file](conf/data/premises-details/XGM00000001764.json).

#### Success response (partial data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/premises-details/MGD/XGM00000001763
```

Returns `200 OK` with `totalRows: 1000` and one premises record with no postcode.

The complete response is stored in [the test data file](conf/data/premises-details/XGM00000001763.json).

#### Success response (default)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/premises-details/MGD/GAM999
```

Returns `200 OK` with `totalRows: 1000` and two premises records, in Barcelona and Madrid.
Each record contains the requested registration number.

The response template is stored in [premises-details.json](conf/data/premises-details/premises-details.json).

---

### 24. Business name details RDS data

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/business-name/XGM00000001761
```

Returns `200 OK` with sole proprietor details, business name, trading name and `systemDate: "2026-01-01"`.

The complete response is stored in [the test data file](conf/data/business-name/XGM00000001761.json).
The other business types use the same endpoint with the following registration numbers:

| Registration number | Business type | Test data file |
| --- | --- | --- |
| `XGM00000001762` | Corporate body | [XGM00000001762.json](conf/data/business-name/XGM00000001762.json) |
| `XGM00000001763` | Unincorporated body | [XGM00000001763.json](conf/data/business-name/XGM00000001763.json) |
| `XGM00000001764` | Partnership | [XGM00000001764.json](conf/data/business-name/XGM00000001764.json) |
| `XGM00000001765` | Limited liability partnership | [XGM00000001765.json](conf/data/business-name/XGM00000001765.json) |

#### Success response (default)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/business-name/GAM999
```

Returns `200 OK` with default sole proprietor details, the requested registration number and `systemDate: "1992-01-01"`.
There is no separate no-data success scenario for business names.

The response template is stored in [business-name.json](conf/data/business-name/business-name.json).

---

### 25. Business address details RDS data

#### Success response (no data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/business-address/MGD/GAM999
```

Returns `200 OK` with an empty registration number and no address fields:

```json
{
  "mgdRegNumber": ""
}
```

The complete response is stored in [business-address.json](conf/data/business-address/business-address.json).

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/business-address/MGD/XGM00000001761
```

Returns `200 OK` with a full UK address, additional address information and `systemDate` set to the current date.

The response template is stored in [the test data file](conf/data/business-address/XGM00000001761.json).
The other address scenarios use the same endpoint with the following registration numbers:

| Registration number | Address scenario | Test data file |
| --- | --- | --- |
| `XGM00000001762` | Partial address in Scotland | [XGM00000001762.json](conf/data/business-address/XGM00000001762.json) |
| `XGM00000001763` | Non-UK address in the Netherlands | [XGM00000001763.json](conf/data/business-address/XGM00000001763.json) |
| `XGM00000001764` | Isle of Man address | [XGM00000001764.json](conf/data/business-address/XGM00000001764.json) |
| `XGM00000001765` | Address line 1 only | [XGM00000001765.json](conf/data/business-address/XGM00000001765.json) |

All populated address scenarios set `systemDate` to the current date when requested.

---

### 26. Business contact details RDS data

#### Success response (no data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/business-contact-details/mgd/XGM00000000200
```

Returns `200 OK` with empty strings for the registration number, phone numbers, fax number, email address and system date.

The complete response is stored in [the test data file](conf/data/business-contact-details/XGM00000000200.json).

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/business-contact-details/mgd/XGM00000001761
```

Returns `200 OK` with phone, mobile and fax numbers, `emailAddr: "test@example.com"`, the requested registration number
and `systemDate` set to the current date.

The response template is stored in [business-contact-details.json](conf/data/business-contact-details/business-contact-details.json).
This template is also used for other registration numbers, except the no-data and error scenarios.

---

### 27. Trade class RDS data

#### Success response (no data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/trade-class/mgd/XGM00000001762
```

Returns `200 OK` with no trade class or system date:

```json
{
  "mgdRegNumber": "",
  "businessActivityDesc": ""
}
```

The complete response is stored in [the test data file](conf/data/trade-class/XGM00000001762.json).
Other registration numbers without a specific scenario, such as `GAM999`, return the same empty response from
[trade-class.json](conf/data/trade-class/trade-class.json).

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/trade-class/mgd/XGM00000001761
```

Returns `200 OK` with `businessTradeClass: 9`, `businessActivityDesc: "Others Business Activity"` and `systemDate: "2026-06-02"`.

The complete response is stored in [the test data file](conf/data/trade-class/XGM00000001761.json).
The other populated scenarios use the same endpoint with the following registration numbers:

| Registration number | Trade class | Test data file |
| --- | --- | --- |
| `XGM00000001763` | `3` | [XGM00000001763.json](conf/data/trade-class/XGM00000001763.json) |
| `XGM00000001764` | `6` | [XGM00000001764.json](conf/data/trade-class/XGM00000001764.json) |
| `XGM00000001765` | `3`, with a family entertainment centre activity description | [XGM00000001765.json](conf/data/trade-class/XGM00000001765.json) |

---

### 28. Previous and associated registration numbers RDS data

Previous and associated registration numbers share the MGD details endpoint and its JSON response files.

#### Success response (no linked registration numbers)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/mgd-details/mgd/GAM999
```

Returns `200 OK` with the requested registration number and `systemDate: "2026-06-06"`.
Previous registration numbers, associated registration numbers and the seasonal flag are omitted.

The response template is stored in [mgd-details.json](conf/data/mgd-details/mgd-details.json).

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/mgd-details/mgd/XGM00000001765
```

Returns `200 OK` with three previous registration numbers, three associated registration numbers,
`isBusinessSeasonal: 1` and `systemDate: "2026-06-06"`.

The complete response is stored in [the test data file](conf/data/mgd-details/XGM00000001765.json).
The other populated scenarios use the same endpoint with the following registration numbers:

| Registration number | Previous registrations | Associated registrations | Test data file |
| --- | --- | --- | --- |
| `XGM00000001761` | 1 | 1 | [XGM00000001761.json](conf/data/mgd-details/XGM00000001761.json) |
| `XGM00000001762` | 3 | 0 | [XGM00000001762.json](conf/data/mgd-details/XGM00000001762.json) |
| `XGM00000001763` | 2 | 2 | [XGM00000001763.json](conf/data/mgd-details/XGM00000001763.json) |
| `XGM00000001764` | 2 | 2 | [XGM00000001764.json](conf/data/mgd-details/XGM00000001764.json) |

---

### 29. Licence details RDS data

#### Success response (no data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/licence-details/MGD/GAM999
```

Returns `200 OK` with an empty registration number and no licence fields:

```json
{
  "mgdRegNumber": ""
}
```

The complete response is stored in [license-details.json](conf/data/license-details/license-details.json).

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/licence-details/MGD/XGM00000001761
```

Returns `200 OK` with gambling licence number `123-456789-A-123456-789`, all licence flags and `systemDate` set to the current date.

The response template is stored in [the test data file](conf/data/license-details/XGM00000001761.json).

#### Success response (partial data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/licence-details/MGD/XGM00000001762
```

Returns `200 OK` with the gambling licence number, `haveGamblingLicenceNo`, `heldByLandlord` and `localAuthority` flags,
and `systemDate` set to the current date. Other licence flags are omitted.

The response template is stored in [the test data file](conf/data/license-details/XGM00000001762.json).

---

### 30. Partner details RDS data

#### Success response (no data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/partner-details/MGD/GAM999
```

Returns `200 OK` with one partner containing only the requested registration number, and a response-level `systemDate`.

The response template is stored in [partner-details.json](conf/data/partner-details/partner-details.json).
The registration number is trimmed and converted to uppercase. The system date is captured when the partner data is first initialised.

#### Success response (with data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/partner-details/MGD/XPM00000000600
```

Returns `200 OK` with 100 partners, alternating between sole proprietors and corporate bodies.

The response template is stored in [the test data file](conf/data/partner-details/XPM00000000600.json).
The system date is captured when the partner data is first initialised. Joining dates are that date plus the partner's
position in the list (1 to 100 days). Every fifth partner has a leaving date one year after the base date plus the same
number of days. These dates are calculated at runtime rather than taken from the JSON file.

Other populated scenarios use the same endpoint with the following registration numbers:

| Registration number | Partner scenario | Test data file |
| --- | --- | --- |
| `XMM00000001177` | Two partners; `systemDate: "2026-07-31"` | [XMM00000001177.json](conf/data/partner-details/XMM00000001177.json) |
| `XPM00000000985` | Eleven partners; `systemDate: "2026-08-06"` | [XPM00000000985.json](conf/data/partner-details/XPM00000000985.json) |

#### Success response (partial data)

```text
GET http://localhost:10405/rds-datacache-proxy/gambling/partner-details/MGD/XJM00000000570
```

Returns `200 OK` with one partner containing a subset of the optional fields. The system date is captured when the partner data is first initialised.

The response template is stored in [the test data file](conf/data/partner-details/XJM00000000570.json).

## License

This project is licensed under the Apache 2.0 License.
