# HasClient (Agent) (RDS)

**GET**

```
GET /gambling/agent/has-client/:regime/:regNumber?credentialId=CRED-ABC-123
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/agent/has-client/:regime/:regNumber?credentialId=CRED-ABC-123
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.AgentController.hasClient(regime: String, regNumber: String, credentialId: String)`

Query parameters:

| Parameter | Type   | Default | Description |
|-----------|--------|---------|-------------|
| `regime` | String |      | One of MGD, GBD, PBD, RGD     |
| `regNumber` | String |      |      |
| `credentialId`  | String |        |             |

---

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

## regNumber validation

The `regNumber` query param is validated to check it is NOT EMPTY

If it is empty it returns:

```
400 BAD_REQUEST
```

```json
{
  "error": "regNumber must be provided"
}
```

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

---

## agentReference encoding convention

Once the regime is valid, the stub derives its behaviour from the agentReference which is passed in the Enrolment header.

**Last 3 digits** control the HTTP status code returned:

| Last 3 digits                      | Response                  |
|------------------------------------|---------------------------|
| `400`                              | 400 BAD_REQUEST           |
| None                               | 500 INTERNAL_SERVER_ERROR |
| `500`                              | 500 INTERNAL_SERVER_ERROR |
| `999`                              | 200 OK - False            |
| anything  & RegNumber ends with `9` | 200 OK - False            |
| anything else                      | 200 OK - True             |  


---

## Return Code

Valid return codes are :

| Status  | Json                   |
|---------|------------------------|
| `True`  | {"hasClient" -> true}  |
| `False` | {"hasClient" -> false} |


---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/agent/has-client//XKM00000001007?credentialId=CRED-ABC-123

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
GET /gambling/agent/has-client/MGD/XKM00000001007?credentialId=
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
GET /gambling/agent/has-client/MGD/XKM00000001007?credentialId=CRED-ABC-123
agentReference in Enrolment header ends with 500
```

Response:

```
500 INTERNAL_SERVER_ERROR
```

```json
{
  "error": "Could not check hasClient"
}
```

---


## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/agent/has-client/MGD/XKM00000001007?credentialId=CRED-ABC-123"

```

