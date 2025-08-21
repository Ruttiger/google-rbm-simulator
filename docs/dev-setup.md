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
- Para actualizar el documento de discovery, exporta `google_api_key_json_envvar`
  con un JSON `{ "apiKey": "<tu_api_key>" }` y ejecuta `./tools/update-discovery.sh`.
- Configura formateo automático con `google-java-format` o `spotless` según lo
  definido en el proyecto.
