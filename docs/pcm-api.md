# PCM API (simulador MaaP)

## Autenticación
Las rutas PCM usan `Authorization: Basic <base64(username:password)>` con `sim.pcm.username` y `sim.pcm.password`.

## Endpoints MT
- `POST /restadpt_generico1/smsTextSubmit`
- `POST /restadpt_generico1/smsBinarySubmit`

Respuesta `SubmitRes` simulada:
```json
{"statusCode":1000,"statusText":"Success","details":null,"messageId":"..."}
```

Códigos soportados:
- `1000 Success`
- `2000 Client error`
- `4001 Improper identification`
- `4004 Validation error`

## Routing de callbacks DR
Prioridad:
1. `deliveryReportURL` del request.
2. URL provisionada en `/v1/provisioning/pcm/webhooks/{sender}`.
3. Si no existe URL, se acepta el MT sin callback (o se rechaza con modo estricto `maap.simulator.strict-pcm-delivery-report-routing=true`).

## Provisión auxiliar PCM
- `PUT /v1/provisioning/pcm/webhooks/{sender}`
- `GET /v1/provisioning/pcm/webhooks/{sender}`
- `DELETE /v1/provisioning/pcm/webhooks/{sender}`
