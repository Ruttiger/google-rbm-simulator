# Google RBM Simulator

Google RBM Simulator es una aplicación **Spring Boot 3** basada en **WebFlux** que expone un endpoint de simulación compatible con la API de Google RBM.

## Arquitectura

- **Spring Boot 3 / WebFlux** para un stack reactivo no bloqueante.
- Filtro `RequestLoggingFilter` que registra método, URL, cabeceras y cuerpo de cada petición.
- Controlador `AgentMessagesSimulatorController` que emula el envío de mensajes de agente.

## Estructura del proyecto

```text
src/
 ├─ main/
 │  ├─ java/com/example/rbm/simulator/
 │  │  ├─ GoogleRbmSimulatorApplication.java
 │  │  ├─ controller/AgentMessagesSimulatorController.java
 │  │  ├─ dto/...
 │  │  ├─ error/...
 │  │  └─ logging/RequestLoggingFilter.java
 │  └─ resources/application.properties
 └─ test/java/com/example/rbm/simulator/...
```

## Requisitos previos

- JDK 17
- Maven o el wrapper `mvnw`

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

## Peticiones de ejemplo

### Texto simple
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages" \
  -H "Authorization: Bearer test-token" \
  -H "Content-Type: application/json" \
  -d '{
    "messageId":"msg-12345",
    "representative":{"representativeType":"BOT"},
    "text":"¡Hola! Este es un mensaje de prueba desde el simulador."
  }'
```

### Rich card + echo + estado forzado
```bash
curl -i -X POST "http://localhost:8080/v1/phones/+5215512345678/agentMessages?forceState=SENT&echo=true" \
  -H "Authorization: Bearer test-token" \
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
