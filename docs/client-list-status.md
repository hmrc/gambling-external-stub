# GetClientListDownloadStatus (Agent) (RDS)

**GET**

```
GET /gambling/agent/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/agent/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.AgentController.getClientListDownloadStatus(credentialId: String, regime: String, gracePeriod: Int ?= 14400)`

Query parameters:

| Parameter | Type   | Default | Description |
|-----------|--------|---------|-------------|
| `credentialId`  | String |        |             |
| `regime` | String |      | One of MGD, GBD, PBD, RGD     |
| `gracePeriod` | Int    | 14400   |             |

---

## credentialId validation

The `credentialId` query param is validated to check it is NOT EMPTY

If it is empty it returns:

```
400 BAD_REQUEST
```

```json
{
  "error": "credentialId must be provided"
}
```

## Regime validation

The `regime` query param is validated against the `Regime` enum. Valid values (case-insensitive):

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
  "message": "Invalid Regime Code"
}
```

---

## agentReference encoding convention

Once the regime is valid, the stub derives its behaviour entirely from the agentReference which is passed in the Enrolment header.

**Last 3 digits** control the HTTP status code returned:

| Last 3 digits | Response                  |
|---------------|---------------------------|
| `400`         | 400 BAD_REQUEST           |
| None          | 500 INTERNAL_SERVER_ERROR |
| `500`         | 500 INTERNAL_SERVER_ERROR |
| `101`         | 200 OK - InitiateDownload |
| `102`         | 200 OK - InProgress       |
| `103`         | 200 OK - Failed           |
| anything else | 200 OK - Succeeded                 |  


---

## Return Code

Valid return codes are :

| Status      | Json                                        |
|-------------|---------------------------------------------|
| `InitiateDownload`  | {"status": "InitiateDownload"} |
| `InProgress` |   {"status": "InProgress"}                                          |
| `Succeeded`   |   {"status": "Succeeded"}                                          |
| `Failed`    |   {"status": "Failed"}                                          |


---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/agent/client-list-status?credentialId=cred-123&regime=INVALID&gracePeriod=14400

```

Response:

```
400 BAD_REQUEST
```

```json
{
  "code": "INVALID_REGIME",
  "message": "Invalid Regime Code"
}
```

---

### 400 - Bad request (credentialId)

Request:

```
GET /gambling/agent/client-list-status?credentialId=&regime=MGD&gracePeriod=14400
```

Response:

```
400 BAD_REQUEST
```

```json
{
  "error": "credentialId must be provided"
}
```
---

### 500 - Unexpected error

Request:

```
GET /gambling/agent/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400
agentReference in Enrolment header ends with 500
```

Response:

```
500 INTERNAL_SERVER_ERROR
```

```json
{
  "error": "Could not map client list download status"
}
```

---


## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/agent/client-list-status?credentialId=cred-123&regime=MGD&gracePeriod=14400"

```

