# Google RBM Simulator

Google RBM Simulator es una aplicación **Spring Boot 3** basada en **WebFlux** que expone endpoints de simulación compatibles con la API de Google RBM.

## Inicio rápido

```bash
# compila y ejecuta pruebas unitarias
./mvnw test

# ejecuta la suite completa (incluye integración)
./mvnw verify

# levanta la aplicación en http://localhost:8080
./mvnw spring-boot:run
```

## Arquitectura

- **Spring Boot 3 / WebFlux** para un stack reactivo no bloqueante.
- Controladores `TokenController` y `AgentMessageController` para tokens y mensajes de ejemplo.

## Estructura del proyecto

```text
src/
 ├─ main/
 │  ├─ java/com/messi/rbm/simulator/
  │  │  ├─ GoogleRbmSimulatorApplication.java
 │  │  ├─ config/
 │  │  │  ├─ AuthProperties.java
 │  │  │  └─ SecurityConfig.java
 │  │  ├─ controller/
 │  │  │  ├─ AgentMessageController.java
 │  │  │  └─ TokenController.java
 │  │  ├─ model/
 │  │  │  └─ Message.java
 │  │  └─ service/
 │  │     └─ JwtService.java
 │  └─ resources/application.properties
 └─ test/...
```

## Requisitos previos

- JDK 17
- Maven o el wrapper `mvnw`
- `jq` (para scripts en `tools/`)

## Variables de entorno

La aplicación utiliza variables para interactuar con la API de RBM y los scripts auxiliares:

- `RBM_PROJECT_ID`, `RBM_BRAND_ID`, `RBM_AUTH_SCOPES`, `RBM_BASE_URL`
- `GOOGLE_APPLICATION_CREDENTIALS` (para cuentas de servicio)
- `google_api_key_json_envvar` (JSON con `apiKey` para scripts de *Discovery*)

## Arrancar la aplicación

### Desde IntelliJ IDEA
1. *File > New > Project from Existing Sources* y selecciona el fichero `pom.xml`.
2. Espera a que el IDE importe las dependencias de Maven.
3. Ejecuta la clase `GoogleRbmSimulatorApplication` o usa la configuración automática de Spring Boot.

### Desde línea de comandos
```bash
./mvnw spring-boot:run
```
La aplicación quedará disponible en `http://localhost:8080`.

## Pruebas
Para validar el código localmente:

- Tests unitarios:
  ```bash
  ./mvnw test
  ```
- Todos los tests (incluye integración):
  ```bash
  ./mvnw verify
  ```

### Pruebas de carga

Se incluye una simulación de carga con [Gatling](https://gatling.io) que envía mensajes al simulador. Estas pruebas no se ejecutan por defecto.
Antes de lanzarlas, asegúrate de que el simulador esté corriendo (por ejemplo, con `./mvnw spring-boot:run`).
Para ejecutarlas manualmente:

```bash
./mvnw gatling:test
```

Al finalizar se genera un informe HTML en `target/gatling/agentmessagesloadtest-<timestamp>/index.html`.

### Ajustar recursos del simulador

Para pruebas masivas es posible asignar más memoria y tunear hilos y conexiones de Netty:

```bash
# empaqueta el proyecto
./mvnw clean package

# ejecuta el JAR con parámetros de rendimiento
java -Xms512m -Xmx2g \
  -jar target/google-rbm-simulator-0.0.1-SNAPSHOT.jar \
  --reactor.netty.ioWorkerCount=32 \
  --reactor.netty.pool.maxConnections=10000 \
  --server.netty.connection-timeout=30s \
  --spring.codec.max-in-memory-size=10MB
```

- `-Xms` / `-Xmx`: tamaño inicial y máximo del heap.
- `ioWorkerCount`: hilos de I/O de Netty.
- `maxConnections`: conexiones simultáneas permitidas.
- `connection-timeout` y `max-in-memory-size`: límites de tiempo y buffer.

Los mismos parámetros pueden usarse con `spring-boot:run`:

```bash
JAVA_OPTS="-Xms512m -Xmx2g" \
./mvnw spring-boot:run \
  -Dreactor.netty.ioWorkerCount=32 \
  -Dreactor.netty.pool.maxConnections=10000 \
  -Dserver.netty.connection-timeout=30s \
  -Dspring.codec.max-in-memory-size=10MB
```

Modifica los valores según la carga que desees simular.

## Análisis estático

Ejecuta linters y análisis estático para detectar posibles bugs y problemas de estilo:

```bash
./mvnw spotbugs:check pmd:check checkstyle:check
```

## Integración continua

La CI se ejecuta mediante [GitHub Actions](.github/workflows/ci.yml). El flujo se dispara al hacer `push` sobre la rama `main` y
al abrir o actualizar cualquier `pull_request`. Los trabajos cancelan ejecuciones previas sobre la misma referencia para evitar
duplicados. Para reproducir la validación localmente utiliza:

```bash
./mvnw verify
```

## Discovery API

Para obtener el documento de *discovery* de RBM utiliza el script `tools/update-discovery.sh`.
Requiere que la variable de entorno `google_api_key_json_envvar` contenga un JSON con el
campo `apiKey`:

```bash
export google_api_key_json_envvar='{ "apiKey": "TU_API_KEY" }'
./tools/update-discovery.sh
```

El script extrae `apiKey` y la envía como parámetro `key` a la API de Discovery,
guardando el resultado en `docs/discovery/rbm-v1.json`.

## Autenticación

Todos los endpoints protegidos requieren un token **Bearer**. Puedes obtenerlo mediante el endpoint `/token`:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&client_id=test&client_secret=test" | jq -r .access_token)
```

El valor de `TOKEN` se emplea en las peticiones posteriores.

## Peticiones de ejemplo

Todas las peticiones requieren el parámetro de query `agentId` que identifica al agente remitente.
Para `agentMessages` también es obligatorio incluir el parámetro `messageId`.

### Mensajes de agente

#### Texto simple
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID&messageId=msg-12345" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "contentMessage": {
      "text": "¡Hola! Este es un mensaje de prueba desde el simulador."
    }
  }'
```

La respuesta del simulador imita a RBM:

```json
{
  "name": "phones/+5215512345678/agentMessages/msg-12345",
  "sendTime": "2025-08-22T10:26:33.509997Z",
  "contentMessage": {
    "text": "¡Hola! Este es un mensaje de prueba desde el simulador."
  }
}
```

#### Rich card
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID&messageId=msg-67890" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "contentMessage": {
      "text": "Aquí tienes una imagen:",
      "richCard": {
        "standaloneCard": {
          "cardContent": {
            "title": "Ejemplo RBM",
            "description": "Imagen enviada con la API",
            "media": {
              "height": "MEDIUM",
              "contentInfo": {
                "fileUrl": "https://example.com/imagen.png",
                "thumbnailUrl": "https://example.com/thumb.png",
                "forceRefresh": false
              }
            }
          }
        }
      }
    }
  }'
```

#### Texto con sugerencias
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID&messageId=msg-13579" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "contentMessage": {
      "text": "¿Continuar?",
      "suggestions": [
        {"action": {"text": "Sí"}},
        {"action": {"text": "No"}}
      ]
    }
  }'
```

### Eventos de agente
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentEvents?agentId=AGENT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId":"evt-12345",
    "eventType":"READ",
    "messageId":"msg-12345"
  }'
```

### Mensajes de usuario
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/messages?agentId=AGENT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "messageId":"msg-54321",
    "representative":{"representativeType":"USER"},
    "text":"Mensaje enviado desde el dispositivo"
  }'
```

## Próximos pasos
- Añadir más endpoints de simulación.
- Construir imágenes Docker y despliegues.

## Licencia
Este proyecto se distribuye sin licencia explícita. Añade la tuya según tus necesidades.
