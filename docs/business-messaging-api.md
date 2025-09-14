# Business Messaging API (Simulador)

Este documento resume los endpoints expuestos para las pruebas de **Business Messaging** del simulador RBM.

## Endpoints

### Registrar Webhook

`POST /v1/webhooks`

Cuerpo:
```json
{
  "agentId": "AGENT_ID",
  "webhookUrl": "http://localhost:8081/callback"
}
```

Registra un webhook en memoria para el agente indicado.

### Registrar Webhook con verificación

`POST /v1/brands/{brandId}/agents/{agentId}/webhooks`

Cuerpo:
```json
{
  "webhookUrl": "http://localhost:8081/callback",
  "clientToken": "s3cr3t"
}
```

El simulador enviará un `challenge` a la URL indicada:

```json
{ "clientToken": "s3cr3t", "secret": "<uuid>" }
```

La webhook debe responder `200 OK` con el mismo secreto para quedar registrada:

```json
{ "secret": "<uuid>" }
```

### Enviar mensaje del agente

`POST /v1/phones/{msisdn}/agentMessages?agentId=...&messageId=...`

Envía un mensaje desde el agente al usuario. El simulador detecta **triggers** especiales en `contentMessage.text`:

| Trigger        | Acción simulada |
|----------------|-----------------|
| `#USER:<txt>`  | genera un mensaje entrante del usuario con `<txt>` |
| `#READ`        | envía un evento `READ` |
| `#DELIVERED`   | envía un evento `DELIVERED` |
| `#IS_TYPING`   | envía un evento `IS_TYPING` |
| `#SUBSCRIBE`   | evento de suscripción |
| `#UNSUBSCRIBE` | evento de baja |

Los eventos se entregan de forma asíncrona al webhook registrado. Cuando la webhook tiene `clientToken`, el payload se envía envuelto en Pub/Sub y firmado con `X-Goog-Signature`.

Ejemplo de evento recibido:

```json
{
  "message": {
    "data": "eyJzZW5kZXJQaG9uZU51bWJlciI6IjUyMTIzNDUiLCJldmVudFR5cGUiOiJSRUFEIiwiZXZlbnRJZCI6IjEyMyIsIm1lc3NhZ2VJZCI6IjEiLCJhZ2VudElkIjoiYWdlbnQifQ==",
    "messageId": "uuid",
    "publishTime": "2025-09-13T12:34:56Z"
  }
}
```

Para verificar la firma:

```bash
expected=$(echo -n '<decoded_json>' | openssl dgst -sha512 -hmac 's3cr3t' -binary | base64)
```

## Ejemplo de uso

1. Registrar webhook:
   ```bash
   curl -X POST http://localhost:8080/v1/webhooks \
     -H 'Content-Type: application/json' \
     -d '{"agentId":"my-agent","webhookUrl":"http://localhost:8081/callback"}'
   ```
2. Enviar mensaje con trigger:
   ```bash
   curl -X POST 'http://localhost:8080/v1/phones/5212345/agentMessages?agentId=my-agent&messageId=1' \
     -H 'Content-Type: application/json' \
     -d '{"contentMessage":{"text":"#READ Hola"}}'
   ```
   El simulador responderá con `200 OK` y enviará al webhook un callback con el evento `READ`.

### Otros endpoints clave

- **Mensajes de usuario**: `POST /v1/phones/{msisdn}/messages?agentId=...`
- **Eventos del agente**: `POST /v1/phones/{msisdn}/agentEvents?agentId=...`
- **Capacidades**:
  - `GET /v1/phones/{msisdn}/capabilities?agentId=...`
  - `POST /v1/phones/{msisdn}/capability:requestCapabilityCallback`
- **Testers**: `POST /v1/phones/{msisdn}/testers?agentId=...`
- **Users batch**: `POST /v1/users:batchGet`
- **Dialogflow**: `POST /v1/phones/{msisdn}/dialogflowMessages?agentId=...`
- **Files**: `POST /v1/files`

## Business Communications

Estos endpoints simulan la API de Business Communications para administrar entidades previas al envío de mensajes.

### Autenticación JWT
Obtén un token de ejemplo y utilízalo en el header `Authorization`:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&client_id=test-client&client_secret=secret" | jq -r .access_token)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/v1/regions
```

### Brands
- `POST /v1/brands`
- `GET /v1/brands`
- `PATCH /v1/brands/{brandId}`
- `DELETE /v1/brands/{brandId}`

Ejemplo:
```bash
curl -X POST http://localhost:8080/v1/brands \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"displayName":"Mi Empresa S.A."}'
```

### Agents
- `POST /v1/brands/{brandId}/agents`
- `GET /v1/brands/{brandId}/agents`
- `PATCH /v1/brands/{brandId}/agents/{agentId}`
- `DELETE /v1/brands/{brandId}/agents/{agentId}`
- `POST /v1/brands/{brandId}/agents/{agentId}:requestVerification`
- `POST /v1/brands/{brandId}/agents/{agentId}:requestLaunch`

### Flujo de verificación y lanzamiento
1. Crear el agente.
2. `:requestVerification` genera un recurso `verification` en estado `PENDING`.
3. `PATCH /verification` con `{"state":"VERIFIED"}` marca al agente como verificado.
4. `:requestLaunch` crea el recurso `launch` en `PENDING`.
5. `PATCH /launch` con `{"state":"APPROVED"}` finaliza el proceso.

### Integrations
- `POST /v1/brands/{brandId}/agents/{agentId}/integrations`
- `GET /v1/brands/{brandId}/agents/{agentId}/integrations`

Ejemplo:
```bash
curl -X POST http://localhost:8080/v1/brands/1/agents/1/integrations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"agentWebhookIntegration":{"webhookUri":"http://localhost:8081/callback"}}'
```

### Regions
`GET /v1/regions` devuelve un listado estático de regiones RBM soportadas.

### Registro de webhooks con challenge
`POST /v1/brands/{brandId}/agents/{agentId}/webhooks` envía un desafío `{clientToken, secret}` y espera el eco del `secret`.

### Endpoint de sink
`POST /webhook/google/{agentId}` responde el `secret` recibido y actúa como sumidero para otros eventos.

### Endpoints auxiliares
- `/token` para generación de JWT de prueba.
- `/webhook/google/{agentId}` como endpoint de autoverificación.

Ejemplos adicionales se encuentran en `docs/curl/`.
