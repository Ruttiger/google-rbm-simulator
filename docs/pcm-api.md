# PCM API mínima para el simulador

Contrato base del canal PCM para pruebas funcionales dentro del simulador MaaP.

## 1) Autenticación

- Esquema: **HTTP Basic Auth**.
- Header requerido: `Authorization: Basic <base64(user:password)>`.
- Si falta o es inválido: `401 Unauthorized`.

## 2) Endpoint de submits

`POST /pcm/submits`

### Request mínimo

```json
{
  "messageId": "ext-123",
  "from": "MiMarca",
  "to": "+5491112345678",
  "channel": "PCM",
  "content": {
    "type": "text",
    "text": "Hola desde PCM"
  },
  "callback": {
    "url": "https://client.example/callbacks/pcm"
  }
}
```

### Response

- `202 Accepted` cuando el submit fue aceptado para procesamiento asíncrono.
- Body sugerido:

```json
{
  "submitId": "pcm-sub-001",
  "status": "ACCEPTED",
  "receivedAt": "2026-01-01T12:00:00Z"
}
```

## 3) Callbacks de estado

El simulador publica callbacks al `callback.url` del submit para informar cambios de estado.

### Evento de callback mínimo

```json
{
  "submitId": "pcm-sub-001",
  "messageId": "ext-123",
  "status": "DELIVERED",
  "timestamp": "2026-01-01T12:00:05Z",
  "detail": "Delivered to downstream operator"
}
```

Estados mínimos: `ACCEPTED`, `SENT`, `DELIVERED`, `FAILED`, `EXPIRED`.

## 4) Códigos de error mínimos

- `400 Bad Request`: payload inválido o campos requeridos ausentes.
- `401 Unauthorized`: Basic Auth ausente/incorrecto.
- `409 Conflict`: `messageId` duplicado (idempotencia).
- `422 Unprocessable Entity`: canal/contenido no soportado.
- `429 Too Many Requests`: throttling.
- `500/503`: error interno/transitorio.

## 5) Política de routing DR (Delivery Reports)

- Si el submit trae `callback.url`, los DR se envían a esa URL (prioridad alta).
- Si no trae `callback.url`, usar callback por defecto configurado por ambiente.
- Reintentar DR en errores transitorios (`429/5xx`) con backoff exponencial.
- Evitar duplicados usando llave idempotente (`submitId` + `status`).
- Si no se puede enrutar tras agotar reintentos, marcar estado `FAILED` y registrar incidente.
