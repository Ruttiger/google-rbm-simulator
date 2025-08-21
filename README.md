# Google RBM Simulator

Google RBM Simulator es una aplicación **Spring Boot 3** basada en **WebFlux** que expone endpoints de simulación compatibles con la API de Google RBM.

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
Ejecuta solo los tests unitarios (se omiten los de integración que dependen de Testcontainers) con:
```bash
./mvnw test
```

Para lanzar todos los tests, incluidos los de integración, utiliza:
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

### Texto simple
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "messageId":"msg-12345",
    "representative":{"representativeType":"BOT"},
    "text":"¡Hola! Este es un mensaje de prueba desde el simulador."
  }'
```

### Rich card + echo + estado forzado
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID&forceState=SENT&echo=true" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "messageId":"msg-67890",
    "representative":{"representativeType":"BOT"},
    "text":"Aquí tienes una imagen:",
    "richCard":{
      "standaloneCard":{
        "cardContent":{
          "title":"Ejemplo RBM",
          "description":"Imagen enviada con la API",
          "media":{
            "height":"MEDIUM",
            "contentInfo":{
              "fileUrl":"https://example.com/imagen.png",
              "thumbnailUrl":"https://example.com/thumb.png",
              "forceRefresh":false
            }
          }
        }
      }
    }
  }'
```

## Próximos pasos
- Añadir más endpoints de simulación.
- Construir imágenes Docker y despliegues.

## Licencia
Este proyecto se distribuye sin licencia explícita. Añade la tuya según tus necesidades.
