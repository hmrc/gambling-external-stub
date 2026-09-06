# ActiveMQ Proxy — Send Message Stub

Stubs the [`activemq-proxy`](https://github.com/hmrc/activemq-proxy) `POST /queue/send` endpoint. It mirrors the real service's request/response contract and validation, but does **not** publish to any ActiveMQ broker — it validates the request, logs it, and returns the correlation id. This lets gambling services integrate against `activemq-proxy` without a running broker.

## Endpoint

```text
POST /activemq-proxy/queue/send
```

Full local URL:

```text
http://localhost:10405/activemq-proxy/queue/send
```

Controller:

```text
uk.gov.hmrc.gamblingexternalstub.controllers.activeMqProxy.ActiveMqProxyController.send
```

## Request

`Content-Type: application/json`. The body matches the real `activemq-proxy` contract:

```json
{
  "queueIdentifier": "AGENT_Filing_APRQ",
  "payload": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...",
  "properties": [
    { "key": "MESSAGE_CLASS", "value": "HMRC-AGENT-APR" },
    { "key": "LOB", "value": "Agent" }
  ],
  "correlationId": "54947df80e9e4471a2f99af509fb5889"
}
```

| Field | Description | Optionality | Type |
| --- | --- | --- | --- |
| `queueIdentifier` | Target queue. Must be one of the permitted values below. | Required | String |
| `payload` | Message body (any string; typically ChRIS XML). | Required | String |
| `properties` | JMS message properties as key/value pairs. Omit if none. | Optional | List |
| `correlationId` | Unique request id. Must be **exactly 32 characters** if supplied; otherwise the stub generates one. | Optional | String |

**Permitted `queueIdentifier` values**

`AGENT_Filing_RCLQ`, `AGENT_Filing_UORQ`, `SS_Filing_TrackingQ`, `NOVA_AuditShipping_IncomingQ`, `AGENT_Filing_APRQ`, `AGENT_Filing_AARQ`

## Response Scenarios

| Condition | Response |
| --- | --- |
| Valid request | `200 OK` — `{ "correlationId": "<supplied-or-generated>" }` |
| `correlationId` supplied and valid | `200 OK` — echoes the supplied `correlationId` |
| `correlationId` omitted | `200 OK` — generates a 32-char id (`UUID` with dashes removed) |
| Body not JSON / missing `payload` or `queueIdentifier` / unknown `queueIdentifier` | `400 Bad Request` |
| `correlationId` supplied but not exactly 32 chars | `400 Bad Request` |
| `Content-Type` not `application/json` | `415 Unsupported Media Type` |

## Examples

### 200 OK (supplied correlationId echoed)

```json
{ "correlationId": "54947df80e9e4471a2f99af509fb5889" }
```

### 200 OK (generated correlationId)

```json
{ "correlationId": "7eb541a6c0274b8fb2d1d95120cd8733" }
```

### 400 Bad Request (unknown queueIdentifier)

```json
{
  "statusCode": 400,
  "message": "Invalid request body",
  "errors": {
    "obj.queueIdentifier": [
      { "msg": ["'NOPE' is not a recognised queueIdentifier. Must be one of: AGENT_Filing_RCLQ, AGENT_Filing_UORQ, SS_Filing_TrackingQ, NOVA_AuditShipping_IncomingQ, AGENT_Filing_APRQ, AGENT_Filing_AARQ"], "args": [] }
    ]
  }
}
```

### 400 Bad Request (bad correlationId length)

```json
{ "statusCode": 400, "message": "correlationId must be exactly 32 characters in length, but was 5" }
```

### Curl

```bash
# Valid — echoes the supplied correlationId
curl -sS -X POST http://localhost:10405/activemq-proxy/queue/send \
  -H "Content-Type: application/json" \
  -d '{
    "queueIdentifier": "AGENT_Filing_APRQ",
    "payload": "<test/>",
    "properties": [{"key":"MESSAGE_CLASS","value":"HMRC-AGENT-APR"}],
    "correlationId": "54947df80e9e4471a2f99af509fb5889"
  }'

# Valid — no correlationId, stub generates one
curl -sS -X POST http://localhost:10405/activemq-proxy/queue/send \
  -H "Content-Type: application/json" \
  -d '{"queueIdentifier":"SS_Filing_TrackingQ","payload":"<tracking/>"}'

# 400 — unknown queueIdentifier
curl -sS -X POST http://localhost:10405/activemq-proxy/queue/send \
  -H "Content-Type: application/json" \
  -d '{"queueIdentifier":"NOPE","payload":"x"}'
```

The stub provides deterministic responses and does not require authentication, a database, or an ActiveMQ broker.
