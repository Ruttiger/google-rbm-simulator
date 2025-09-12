# Guía de configuración de desarrollo

Este documento amplía la sección de setup del `AGENTS.md` con recomendaciones
para el entorno local de desarrollo.

## Requisitos
- Java 17 instalado y configurado en `JAVA_HOME`.
- Maven 3.8 o superior disponible en el `PATH`.

## IDE recomendado
- **IntelliJ IDEA**: importar el proyecto como `Maven project` para que reconozca
  dependencias y tareas.
- **VS Code**: instalar las extensiones *Language Support for Java* y *Extension
  Pack for Java*; luego abrir la carpeta del repositorio.

## Tips
- Ejecuta `./mvnw clean verify` antes de cada commit para asegurar que los
  tests compilan y pasan.
- Usa variables de entorno (`.env.local`) para las credenciales requeridas por
  la aplicación.
- Configura formateo automático con `google-java-format` o `spotless` según lo
  definido en el proyecto.

## Colección Postman
Para probar manualmente los endpoints se incluye una colección y un entorno de Postman en `docs/postman/`.

1. Importa la colección `RBM-Simulator.postman_collection.json`.
2. Importa el entorno `RBM-Simulator.postman_environment.json` y actívalo.
3. Ajusta la variable `baseUrl` si es necesario y envía las solicitudes.

Consulta [postman/README.md](postman/README.md) para más detalles.
