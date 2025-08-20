# Google RBM Simulator

Google RBM Simulator es una aplicación **Spring Boot 3** basada en **WebFlux** que expone un endpoint de simulación compatible con la API de Google RBM.

## Arquitectura

- **Spring Boot 3 / WebFlux** para un stack reactivo no bloqueante.
- Controlador `TokenController` que emite tokens JWT de prueba.

## Estructura del proyecto

```text
src/
 ├─ main/
 │  ├─ java/com/messi/rbm/authsim/
  │  │  ├─ GoogleRbmSimApplication.java
 │  │  ├─ config/
 │  │  │  ├─ AuthProperties.java
 │  │  │  └─ SecurityConfig.java
 │  │  ├─ controller/
 │  │  │  └─ TokenController.java
 │  │  └─ service/
 │  │     └─ JwtService.java
 │  └─ resources/application.properties
 └─ test/...
```

## Requisitos previos

- JDK 17
- Maven o el wrapper `mvnw`

## Arrancar la aplicación

### Desde IntelliJ IDEA
1. *File > New > Project from Existing Sources* y selecciona el fichero `pom.xml`.
2. Espera a que el IDE importe las dependencias de Maven.
3. Ejecuta la clase `GoogleRbmSimApplication` o usa la configuración automática de Spring Boot.

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
