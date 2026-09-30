# GetAllClients (Agent) (RDS)

**GET**

```
GET /gambling/agent/client-list?credentialId=CRED-ABC-123&regime=MGD
```

Full URL:

```
http://localhost:10405/rds-datacache-proxy/gambling/agent/client-list?credentialId=CRED-ABC-123&regime=MGD&start=2&count=10&sort=1&ascending=false
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.rdsDataCacheProxy.AgentController.getAllClients(credentialId: String, regime: String, start: Int ?= 0, count: Int ?= -1, sort: Int ?= 0, ascending: Boolean ?= true)`

Query parameters:

| Parameter | Type   | Default | Description |
|-----------|--------|---------|-------------|
| `credentialId`  | String |         |             |
| `regime` | String |         | One of MGD, GBD, PBD, RGD     |
| `start` | Int    | 0       |             |
| `count` | Int    | -1      |             |
| `sort` | Int    | 0       |             |
| `ascending` | Boolean    | True    |             |

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

| Last 3 digits | Response                              |
|---------------|---------------------------------------|
| `400`         | 400 BAD_REQUEST                       |
| None          | 500 INTERNAL_SERVER_ERROR             |
| `500`         | 500 INTERNAL_SERVER_ERROR             |
| `123`         | 200 OK - SMALL dataset of 2 records   |
| anything else | 200 OK - LARGE dataset of 106 records |  


---

## Item structure

Each `AgentClientListResponse` has the following fields:

| Field       | Type              | Description                                                                          |
|-------------|-------------------|---------------------------------------------------------------------------------------|
| `clients`  | List[AgentClient] |                                  |
| `totalCount` | Int               |  |
| `clientNameStartingCharacters`   | List[String]         |                              |


Each `AgentClient` has the following fields:

| Field       | Type              | Description                                                                          |
|-------------|-------------------|---------------------------------------------------------------------------------------|
| `regNumber`  | String |                                  |
| `clientName` | String               |  |
| `agentOwnRef`   | String         |                              |

For example :
```json
{
  "clients":
  [
    {
      "regNumber": "XEM00000000640",
      "clientName": "Alternate ABC Ltd",
      "agentOwnRef": "EF003"
    },
    {
      "regNumber": "XVM00000000495",
      "clientName": "Alternate XYZ Builders",
      "agentOwnRef": "GH005"
    }
  ],
  "totalCount": 2,
  "clientNameStartingCharacters": ["A"]
}
```

---

## Behaviour

### 400 - Invalid regime

Request:

```
GET /gambling/agent/client-list?credentialId=CRED-ABC-123&regime=INVALID

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
GET /gambling/agent/client-list?credentialId=&regime=MGD
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
GET /gambling/agent/client-list?credentialId=CRED-ABC-123&regime=MGD
agentReference in Enrolment header ends with 500
```

Response:

```
500 INTERNAL_SERVER_ERROR
```

```json
{
  "error": "Could not get client list"
}
```

---


## Example curl

```
curl "http://localhost:10405/rds-datacache-proxy/gambling/agent/client-list?credentialId=CRED-ABC-123&regime=MGD"

```

