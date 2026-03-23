# Colección Postman del simulador RBM

Los archivos `RBM-Simulator.postman_collection.json` (colección) y `RBM-Simulator.postman_environment.json` (entorno) facilitan las pruebas manuales de los endpoints del simulador.

## Importar la colección y el entorno

1. Abre Postman y haz clic en **Import**.
2. Selecciona ambos archivos ubicados en `docs/postman/`.
   - La colección aparecerá como **RBM Simulator**.
   - El entorno se llamará **RBM Simulator**.
3. Activa el entorno importado.
4. Verifica que la variable `baseUrl` apunte a la URL donde se ejecuta la aplicación (por defecto `http://localhost:8080`).

## Probar los endpoints

Con el entorno activo:

1. Ejecuta el simulador con `./mvnw spring-boot:run`.
2. Abre cualquiera de las requests de la colección y pulsa **Send**.
3. Ajusta la variable `authToken` del entorno si algún endpoint requiere autenticación.

Estas plantillas permiten enviar mensajes de prueba y observar las respuestas del simulador sin necesidad de escribir manualmente las solicitudes.

## Flujo recomendado para PCM

Dentro de la carpeta **PCM** de la colección encontrarás un flujo completo:

1. **Provision PCM webhook (PUT)**
   - Configura por `sender` la URL de delivery report y las credenciales del callback.
   - Body de ejemplo incluido:
     - `deliveryReportUrl`
     - `smsDeliverUrl`
     - `username`
     - `password`
2. **Get PCM webhook provisioning (GET)**
   - Verifica que la provisión quedó guardada.
3. **smsTextSubmit — mensaje simple**
   - Envía un mensaje MT de texto plano.
4. **smsTextSubmit — comandos embebidos (REJECTED/EXPIRED)**
   - Simula callbacks asíncronos con comandos en `smsText`.

Variables de entorno útiles para PCM:
- `PCM_SENDER`
- `PCM_DELIVERY_REPORT_URL`
- `PCM_SMS_DELIVER_URL`
- `PCM_WEBHOOK_USERNAME`
- `PCM_WEBHOOK_PASSWORD`
- `pcm_basic_auth` (`base64(username:password)` para autenticación Basic)


## Flujo recomendado para OSP

Dentro de la carpeta **OSP** de la colección encontrarás requests para:

1. **OSP Auth Token**
   - Solicita token con `grant_type=client_credentials` usando Basic Auth.
2. **OSP Send MT Message**
   - Envía un MT a `/v3/bot/v1/{orange_chatbot_id}/messages` con `Bearer`.
3. **OSP Webhook TextMessage**
   - Simula callback de Orange en `/webhook/orange/{botId}/{uuid}`.

Variables de entorno útiles para OSP:
- `OSP_CLIENT_ID`
- `OSP_CLIENT_SECRET`
- `OSP_BASIC_AUTH` (`base64(client_id:client_secret)`)
- `OSP_ACCESS_TOKEN`
- `OSP_BOT_ID`
- `OSP_WEBHOOK_UUID`

## Triggers de eventos

Los mensajes pueden incluir triggers especiales para simular eventos:

- `#READ`: envía un evento `READ`.
- `#DELIVERED`: envía un evento `DELIVERED`.
- `#USER:<txt>`: genera un mensaje entrante del usuario con el texto `<txt>`.

Cada trigger admite un parámetro `delay` para diferir su envío usando la sintaxis `#EVENT(delay=ms)`, donde `ms` se expresa en milisegundos.

Ejemplo: `Hola #READ(delay=500)`

### Comandos embebidos específicos de PCM

Para `smsTextSubmit` el parser PCM soporta:

- `#DELIVERED`
- `#REJECTED`
- `#EXPIRED`

Todos admiten `delay` opcional. Ejemplo:

`Prueba PCM #REJECTED(delay=400) #EXPIRED(delay=1200)`
