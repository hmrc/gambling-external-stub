# UpdateClientList (Agent) (ClientExchangeProxy)

**GET**

```
GET /:serviceId/:credentialId/:agentId/clientlist
```

Full URL:

```
http://localhost:10405/client-exchange-proxy/:serviceId/:credentialId/:agentId/clientlist
```

Controller mapping:

`uk.gov.hmrc.gamblingexternalstub.controllers.clientExchangeProxy.ClientExchangeProxyController.updateClientList(serviceId, credentialId, agentId)`

Path & Query parameters:

| Parameter | Type   | Default | Description                    |
|-----------|--------|---------|--------------------------------|
| `serviceId`  | String |        | One of MGD, GTR_GBD, GTR_PBD, GTR_RGD |
| `credentialId` | String |      |                                |
| `agentId` | String    |    |                                |

---

## Validation
Currently there is no validation in the stub

---

## agentReference encoding convention

Once the regime is valid, the stub derives its behaviour entirely from the agentReference which is passed in the Enrolment header.

**Last 3 digits** control the HTTP status code returned:

| Last 3 digits | Response                  |
|---------------|---------------------------|
| `400`         | 400 BAD_REQUEST           |
| None          | 500 INTERNAL_SERVER_ERROR |
| `500`         | 500 INTERNAL_SERVER_ERROR |
| anything else | 200 OK - Succeeded                 |  


---

## Return Values

Valid return codes are :

| Status       | Json                      |
|--------------|---------------------------|
| `OK`         | Valid XML                 |


---

## Behaviour

### 400 - Invalid regime

Request:

```
GET //cred-123/abc123/clientlist

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
GET /MGD//abc123/clientlist
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
GET /MGD/cred-123/abc123/clientlist
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
curl "http://localhost:10405/client-exchange-proxy/MGD/cred-123/abc123/clientlist"

```

