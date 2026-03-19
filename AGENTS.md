# AGENTS.md — Guía para el agente de Codex

> Proyecto: **Simulador MaaP multi-interfaz (RBM + PCM)**  
> Lenguaje/Stack: **Java 17 + Spring Boot**  
> Objetivo: Automatizar tareas de desarrollo, investigación y PRs manteniendo calidad y trazabilidad.

---

## 1) Contexto y objetivos
Este agente debe:
- Investigar y usar la API de **RCS Business Messaging (RBM)** de Google.
- Implementar/mantener un **gateway simulador** en Spring Boot.
- Producir PRs pequeñas, revisables y con buenas prácticas.
- Mantener documentación viva (este archivo + README/ADR).

### Enlaces fuente (¡puedes navegar!)
- **Referencia RBM (REST) — ES**:
  https://developers.google.com/business-communications/rcs-business-messaging/reference/rest?hl=es-419

---

## 2) Reglas de trabajo del agente
1. **Prioriza la fuente oficial**: cualquier duda sobre contratos de API, estados, errores o límites → verifica en los enlaces anteriores.
2. **Traza tus hallazgos** en `/docs/research/yyyymmdd-topic.md` con links y citas a la referencia.
3. **Propuestas > cambios directos**: si la modificación es no trivial (contratos, modelos, flujos), abre primero un RFC corto (`/docs/rfc/NNN-titulo.md`).
4. **PRs atómicas**: una PR = un objetivo claro (máx. ~300 LOC efectivos). Si superas, divide.
5. **Automatiza**: añade tests y tareas Gradle/Maven; configura CI para validar.
6. **Idempotencia y resiliencia** para integraciones RBM: reintentos con backoff, timeouts, circuit breaker, logs estructurados, correlación.
7. **Documentación siempre al día**: modifica README, AGENTS, ADRs y demás documentos cuando sea necesario para reflejar el estado actual del proyecto.

---

## 3) Setup del entorno
- **Requisitos mínimos**: Java 17, Maven ≥3.8
- **Java**: 17 (Temurin/Zulu)
- **Build**: Maven o Gradle (consistente con el repo)
- **Spring Boot**: versión LTS alineada con Java 17
- **Librerías útiles**:
    - Web: `spring-boot-starter-webflux` (WebClient) o `spring-boot-starter-web` (RestTemplate)
    - Validación: `spring-boot-starter-validation`
    - Observabilidad: `micrometer`, `spring-boot-starter-actuator`
    - Test: JUnit 5, Mockito, WireMock/Testcontainers
- **Variables de entorno** (ejemplo):
    - `RBM_PROJECT_ID`, `RBM_BRAND_ID`, `RBM_AUTH_SCOPES`, `RBM_BASE_URL`
    - `GOOGLE_APPLICATION_CREDENTIALS` (si se usa SA)

> Nunca comprometas credenciales. Usa `.env.local` (gitignored) o secret manager.

### Pasos iniciales
1. `git clone` del repositorio.
2. `./mvnw clean verify` para compilar y ejecutar tests.
3. `./mvnw spring-boot:run` indicando variables de entorno requeridas.

---

## 4) Autenticación y autorización
- Revisa la sección de **Auth/OAuth 2.0** en la referencia de RBM.
- Preferencia: **Service Account** + **JWT/OAuth** con scoping mínimo necesario.
- Implementa un **token provider** centralizado con cache y refresh proactivo.
- Añade test de integración que valida `401/403` y recuperación automática.

> Si la referencia indica scopes específicos para RBM, documenta y fija en configuración tipada (`@ConfigurationProperties`).

---

## 5) Uso de la API RBM
### Cliente HTTP
- Usa **WebClient** (reactivo) o RestTemplate con **timeouts**, **retry con backoff** y **resolución de errores** por familia de status.
- **Headers**: `Authorization: Bearer <token>`, `Content-Type: application/json`, `Accept: application/json`.
- **Telemetry**: log de request id, latencia, tamaño payload, status.

#### Snippet con WebClient y TokenProvider
```java
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

interface TokenProvider {
    Mono<String> getToken();
}

TokenProvider tokenProvider = /* implementation */;

WebClient rbmClient = WebClient.builder()
    .baseUrl("https://rcsbusinessmessaging.googleapis.com/v1")
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .filter((request, next) ->
        tokenProvider.getToken().flatMap(token ->
            next.exchange(
                ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build()
            )
        )
    )
    .build();
```

#### Ejemplo: `POST /v1/phones/{phone}/messages`
Request:
```http
POST /v1/phones/{phone}/messages
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": { "text": "Hola desde RBM" }
}
```

Response:
```json
{
  "name": "phones/{phone}/messages/MSG_ID",
  "sendTime": "2023-01-01T00:00:00Z"
}
```

### 5.3 Errores y resiliencia
- Mapear errores RBM a excepciones propias con códigos de dominio.
- Retries solo en transitorios (`5xx`, `429`) respetando `Retry-After`.
- Implementar **circuit breaker** para servicios aguas abajo.

### 5.4 Versionado
- Fijar **versión** de RBM (`v1`) en configuración; encapsular en un `RbmApiClient` para facilitar upgrades.

---

## 6) Arquitectura sugerida
```
app/
  web/              # Controllers (REST) – DTOs de entrada/salida
  service/          # Orquestación de casos de uso
  domain/           # Modelos y lógica de dominio
  auth/             # Filtros y utilidades de autenticación
  repo/             # Repositorios en memoria
  infra/
    rbm/            # Cliente(s) RBM, mapeos, auth, config
    http/           # Cross-cutting HTTP (WebClientFactory, retry, CB)
  config/           # @Configuration y properties tipadas
  support/          # utilidades, mappers, fixtures
```
- **Reglas**: `web` no conoce `infra`; `service` media entre `web` y `domain/infra`.
- **DTOs** separados de modelos de dominio (mappers explícitos).

---

## 7) Calidad, testing y CI
- **Unit tests** (≥80% en lógica de dominio).
- **Contract tests** (schemas RBM vs. ejemplos reales).
- **Integration tests** con WireMock/Testcontainers para HTTP.
- **Mutation testing** opcional (Pitest) para módulos críticos.
- **Static analysis / Linter**: tras cada cambio ejecuta `./mvnw spotbugs:check pmd:check checkstyle:check` para detectar bugs, vulnerabilidades y *code smells*. El plugin `spotbugs-maven-plugin` ya está configurado; si falla o no está disponible, usa una alternativa como SonarQube. Si los linters detectan problemas, tómate el tiempo para corregirlos y vuelve a ejecutarlos hasta que no queden errores antes de commitear.
- **CI**: build limpia, tests, lints, cobertura, análisis estático, SBOM (CycloneDX).

Checklist de PR (auto):
- [ ] Build OK + tests pasan.
- [ ] Linter (SpotBugs/Checkstyle/PMD) ejecutado sin errores, corrigiendo los issues detectados.
- [ ] Nuevos endpoints/POJOs documentados.
- [ ] Manejo de errores probado (feliz y no feliz).
- [ ] Logs/telemetría adecuados.
- [ ] Config segura (sin secretos, propiedades tipadas).
- [ ] Changelog/Notas de cambio.

---

## 8) Flujo de trabajo para tareas
1. **Descubrir**: lee la referencia RBM; anota endpoints, métodos, esquemas y scopes.
2. **Diseñar**: define DTOs, interfaces y diagramas simples en `/docs`.
3. **Planificar**: crea issue con alcance, riesgos y criterios de aceptación.
4. **Implementar**: pequeña, incremental, centrada en un caso de uso.
5. **Probar**: unitario + integración + contrato desde el primer commit verde.
6. **Documentar**: README secciones afectadas + ejemplos de requests/responses.
7. **Abrir PR**: sigue plantilla y responde feedback.

---

## 9) Estándares de código
- **Java 17** features: records (para DTOs inmutables), `sealed` si aplica, `switch` mejorado.
- **Null-safety**: `@NotNull/@Nullable`; valida inputs.
- **Inmutabilidad** donde sea posible.
- **Conventional Commits**: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`, `build:`.
- **Formateo**: `spotless`/`google-java-format`.
- **Nombres** claros y específicos del dominio RBM.

---

## 10) Buenas PRs
- **Título**: conciso y accionable.
- **Descripción**: contexto, decisión, alcance, cómo probar, riesgos, screenshots/logs.
- **Scope pequeño**: evita refactors masivos mezclados.
- **Evidencia**: ejemplos de requests/responses, curl/HTTPie o tests.
- **Checklist**: incluye la checklist de la sección 7.
- **Etiquetas**: `area:rbm`, `type:feat|fix|techdebt`, `breaking-change` si corresponde.

Plantilla sugerida:
```
## Contexto

## Qué cambia

## Cómo probar
- Paso a paso
- Datos de ejemplo

## Impacto / Riesgos

## Checklist
- [ ] Tests añadidos/actualizados
- [ ] Docs actualizadas
- [ ] Backward compatible
```

---

## 11) Instrumentación y observabilidad
- **Logs estructurados** (JSON) con `traceId/spanId`.
- **Métricas**: latencia por endpoint RBM, tasa de éxito, reintentos, errores por familia.
- **Healthchecks**: readiness/liveness; endpoint de sanity contra RBM (mockeado en tests).
- **Dashboards**: prepara panel base (latencias, error rate, throughput).

---

## 12) Seguridad y cumplimiento
- Secrets fuera del repo.
- Principle of least privilege (scopes).
- Sanitiza PII en logs.
- Dependabot/Renovate + políticas de actualización.
- SBOM y análisis SCA (vulnerabilidades).

---

## 13) Documentación viva
- Mantén `/docs/adr/` (Architecture Decision Records) para decisiones relevantes (auth, cliente HTTP, esquema de eventos).
- En PRs que toquen contratos, incluye **diff de esquemas**.
- Todo cambio de funcionalidad debe reflejarse de inmediato en `README.md`, `AGENTS.md` y los archivos relevantes de `docs/`; incluye ejemplos de requests/responses y actualiza las rutas de los controladores.

---

## 14) Atajos útiles para investigación
- **Buscar en la referencia RBM**: usa el buscador de la página; filtra por recurso (brands, agents, messages, etc.).
- **Curls reproducibles**: guarda en `/docs/curl/` con variables `${TOKEN}`/`${PROJECT}`.

---

## 15) Definición de Hecho (DoD)
- [ ] Cumple criterios de aceptación.
- [ ] Cobertura y pruebas de resiliencia.
- [ ] Observabilidad implantada.
- [ ] Docs y ADRs actualizados.
- [ ] PR aprobada y mergeada sin secretos, sin flakiness.

---

## 16) Próximos pasos sugeridos
- Crear `RbmApiClient` con WebClient + TokenProvider.
- Esqueleto de simulador (endpoints locales que emulan RBM).
- Pipeline CI con lints, tests, cobertura y SBOM.

---

## 17) Nuevos tests
- Pruebas unitarias para `TokenController` y `AgentMessageController`. Ejecuta la suite básica con `./mvnw test`.
- Para validar también las pruebas de integración utiliza `./mvnw verify`.
- No es necesario verificar el contenido de los logs en las pruebas.

## 18) Pipeline
- Workflow de GitHub Actions en `.github/workflows/ci.yml` que compila y ejecuta la suite de tests.
- Los trabajos cancelan ejecuciones previas sobre la misma referencia para evitar duplicados.

## 19) Buenas prácticas detectadas
- Documentar instrucciones de arranque, ejemplos de mensajes y comandos de prueba en el `README`.
- Estructurar los ejemplos por tipo de mensaje para facilitar las pruebas manuales.



## Nota de evolución
Este repositorio ya no es exclusivo de RBM: debe mantener arquitectura MaaP común y adaptadores por canal (RBM/PCM), dejando OSP como extensión futura.
