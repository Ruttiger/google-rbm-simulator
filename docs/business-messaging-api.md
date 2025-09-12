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

Los eventos se entregan de forma asíncrona al webhook registrado.

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