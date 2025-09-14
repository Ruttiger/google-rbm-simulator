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

## Triggers de eventos

Los mensajes pueden incluir triggers especiales para simular eventos:

- `#READ`: envía un evento `READ`.
- `#DELIVERED`: envía un evento `DELIVERED`.
- `#USER:<txt>`: genera un mensaje entrante del usuario con el texto `<txt>`.

Cada trigger admite un parámetro `delay` para diferir su envío usando la sintaxis `#EVENT(delay=ms)`, donde `ms` se expresa en milisegundos.

Ejemplo: `Hola #READ(delay=500)`
