# Google RBM Simulator

Google RBM Simulator es una aplicación **Spring Boot 3** basada en **WebFlux** que actúa como esqueleto para experimentar con una plataforma de simulación de RBM (Restricted Boltzmann Machine).

## Arquitectura

- **Spring Boot 3 / WebFlux** para un stack reactivo no bloqueante.
- Filtro `RequestLoggingFilter` que registra método, URL, cabeceras y cuerpo de cada petición.
- Estructura preparada para añadir controladores y servicios de simulación.

## Estructura del proyecto

```text
src/
 ├─ main/
 │  ├─ java/win/agus4the/google_rbm_simulator/
 │  │  ├─ GoogleRbmSimulatorApplication.java
 │  │  └─ logging/RequestLoggingFilter.java
 │  └─ resources/application.properties
 └─ test/java/win/agus4the/google_rbm_simulator/GoogleRbmSimulatorApplicationTests.java
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
Ejecuta los tests de JUnit con:
```bash
./mvnw test
```

## Peticiones de ejemplo
Actualmente no hay controladores expuestos. Aun así, puedes verificar el arranque y el filtro de logging enviando cualquier petición y observando los logs:
```bash
curl -v http://localhost:8080/
```
En la consola verás una línea con los detalles de la petición, aunque la respuesta será `404 Not Found` al no existir endpoints definidos.

## Próximos pasos
- Implementar controladores WebFlux que expongan las operaciones de simulación.
- Añadir tests de integración para la API.
- Construir imágenes Docker y despliegues.

## Licencia
Este proyecto se distribuye sin licencia explícita. Añade la tuya según tus necesidades.

