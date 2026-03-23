# API OSP del simulador

## Autenticación

`POST /v3/auth/` (OAuth2 client credentials con Basic Auth)

```bash
curl -X POST http://localhost:8080/v3/auth/ \
  -H "Authorization: Basic $(printf 'osp-client:osp-secret' | base64)" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=osp.send"
```

Respuesta de ejemplo:

```json
{
  "access_token": "<token_opaco>",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "osp.send"
}
```

## Envío MT OSP

`POST /v3/bot/v1/{orange_chatbot_id}/messages`

```bash
curl -X POST http://localhost:8080/v3/bot/v1/orange-bot/messages \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{"message":{"text":"Hola OSP"}}'
```

Respuesta de ejemplo:

```json
{
  "messageId": "6e7d4b95-3e87-45fb-8a3b-820caf907a96",
  "orangeChatbotId": "orange-bot",
  "status": "accepted"
}
```

## Webhook Orange

`POST /webhook/orange/{botId}/{uuid}`

Se aceptan payloads con las ramas `message`, `response` y `messageStatus`.

```bash
curl -X POST http://localhost:8080/webhook/orange/orange-bot/uuid-123 \
  -H "Content-Type: application/json" \
  -d '{"messageStatus":{"status":"Delivered"}}'
```

Respuesta de ejemplo:

```json
{
  "status": "received",
  "notificationType": "messageStatus"
}
```

## Activación por interfaz

Para exponer rutas OSP, añade `OSP` en la propiedad:

```yaml
maap:
  simulator:
    enabled-interfaces: RBM,PCM,OSP
```

Si OSP no está habilitado, `/v3/auth/`, `/v3/bot/**` y `/webhook/orange/**` responderán `404`.
