# Business Communications API (Simulador)

Este documento describe los endpoints de **Business Communications** del simulador RBM para administrar entidades previas al envío de mensajes. Para los endpoints de mensajería consulta la [Business Messaging API](business-messaging-api.md).

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
3. `PATCH /verification` con `{\"state\":\"VERIFIED\"}` marca al agente como verificado.
4. `:requestLaunch` crea el recurso `launch` en `PENDING`.
5. `PATCH /launch` con `{\"state\":\"APPROVED\"}` finaliza el proceso.

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
