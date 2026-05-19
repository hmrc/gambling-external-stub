# Business Contact Details Stub

## Endpoint

```text
GET /gambling/business-contact-details/mgd/{mgdRegNumber}
```

Full local URL:

```text
http://localhost:10405/rds-datacache-proxy/gambling/business-contact-details/mgd/{mgdRegNumber}
```

Controller:

```text
uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingBusinessContactDetailsController.getBusinessContactDetails(mgdRegNumber: String)
```

## Request

No request body or query parameters are required. Only `mgdRegNumber` is supplied as a path parameter.

## Stored Procedure Field Mapping

| Stored procedure field | Type | JSON response field | Description |
| --- | --- | --- | --- |
| `mgd_reg_number` | `VARCHAR` | `mgdRegNumber` | MGD registration number |
| `phone_number` | `VARCHAR2` | `phoneNumber` | Business contact phone number |
| `mobile_phone_number` | `VARCHAR2` | `mobilePhoneNumber` | Business contact mobile number |
| `fax_number` | `VARCHAR2(70 CHAR)` | `faxNumber` | Business contact fax number |
| `email_addr` | `VARCHAR2(70 CHAR)` | `emailAddr` | Business contact email |
| `system_date` | `Date` | `systemDate` | Current date |

## Response Scenarios

| Reg number pattern | Response |
| --- | --- |
| `XGM00000000400` | 400 Bad Request |
| `XGM00000000401` | 401 Unauthorized |
| `XGM00000000404` | 404 Not Found |
| `XGM00000000500` | 500 Internal Server Error |
| `XGM00000000200` | 200 OK no data |
| `invalid` | 400 Bad Request |
| `error` | 500 Internal Server Error |
| Anything else | 200 OK success response |

The return summary scenario registration numbers also return the standard business contact details success response:

| Scenario | Registration numbers |
| --- | --- |
| Overdue exists | `XGM00000001761`, `GAM0000000001` |
| Returns due | `XGM00000001762`, `GAM0000000010` |
| Returns due and overdue exists | `XGM00000001763`, `GAM0000000012` |

## Examples

### 200 OK

```text
GET /gambling/business-contact-details/mgd/XGM00000001761
```

```json
{
  "mgdRegNumber": "XGM00000001761",
  "phoneNumber": "07700900999",
  "mobilePhoneNumber": "07700900999",
  "faxNumber": "07700900999",
  "emailAddr": "viv@xyz.com",
  "systemDate": "2026-05-18"
}
```

### 200 OK No Data

```text
GET /gambling/business-contact-details/mgd/XGM00000000200
```

```json
{
  "mgdRegNumber": "",
  "phoneNumber": "",
  "mobilePhoneNumber": "",
  "faxNumber": "",
  "emailAddr": "",
  "systemDate": ""
}
```

### Curl

```bash
curl "http://localhost:10405/rds-datacache-proxy/gambling/business-contact-details/mgd/XGM00000001761"
curl "http://localhost:10405/rds-datacache-proxy/gambling/business-contact-details/mgd/XGM00000000200"
curl "http://localhost:10405/rds-datacache-proxy/gambling/business-contact-details/mgd/XGM00000000404"
```
