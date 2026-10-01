# Agent Details Stub

## Endpoint

```text
GET /gambling/agent-details/{agentReference}
```

Full local URL:

```text
http://localhost:10405/rds-datacache-proxy/gambling/agent-details/{agentReference}
```

Controller:

```text
uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.GamblingAgentDetailsController.getAgentDetails(agentReference: String)
```

## Request

No request body or query parameters are required. Only `agentReference` is supplied as a path parameter.

## Response Fields

| JSON response field | Description |
| --- | --- |
| `businessName` | Agent business name |
| `addressLine1` - `addressLine4` | Agent address lines |
| `postcode` | Agent postcode |
| `country` | Agent country |
| `abroadSignal` | Whether the address is abroad (`Y`/`N`) |
| `phoneNumber` | Agent phone number |
| `mobilePhoneNumber` | Agent mobile number |
| `faxNumber` | Agent fax number |
| `email` | Agent email |

## Reference Encoding Convention

The reference is trimmed, then the last 3 characters are read as the status code (same convention as `regNumber` on the other RDS stubs).

| Agent reference pattern | Response |
| --- | --- |
| Ends in `400` (e.g. `XAM00000000400`) | 400 Bad Request |
| Ends in `401` (e.g. `XAM00000000401`) | 401 Unauthorized |
| Ends in `404` (e.g. `XAM00000000404`) | 404 Not Found |
| Ends in `500` (e.g. `XAM00000000500`) | 500 Internal Server Error |
| Anything else | 200 OK success response |

## Examples

### 200 OK

```text
GET /gambling/agent-details/XAM00000001234
```

```json
{
  "businessName": "Gambling company 1",
  "addressLine1": "1",
  "addressLine2": "Example street",
  "addressLine3": "Town",
  "addressLine4": "County",
  "postcode": "SW1A 1AA",
  "country": "United Kingdom",
  "abroadSignal": "N",
  "phoneNumber": "02079460000",
  "mobilePhoneNumber": "07700900999",
  "faxNumber": "02079460123",
  "email": "user@example.com"
}
```

### Error bodies

| Status | Body |
| --- | --- |
| 400 | `{"code": "INVALID_REQUEST", "message": "Bad request"}` |
| 401 | `{"code": "UNAUTHORIZED", "message": "Unauthorized to access this resource"}` |
| 404 | `{"code": "RECORD_NOT_FOUND", "message": "Record not found"}` |
| 500 | `{"code": "UNEXPECTED_ERROR", "message": "Unexpected error occurred"}` |

### Curl

```bash
curl "http://localhost:10405/rds-datacache-proxy/gambling/agent-details/XAM00000001234"
curl "http://localhost:10405/rds-datacache-proxy/gambling/agent-details/XAM00000000404"
curl "http://localhost:10405/rds-datacache-proxy/gambling/agent-details/XAM00000000500"
```
