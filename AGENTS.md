# AGENTS.md — Guía para el agente de Codex

> Proyecto: **Simulador Gateway Google RBM Platform**  
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
- **Discovery Document RBM v1**:  
  https://rcsbusinessmessaging.googleapis.com/$discovery/rest?version=v1
- **Discovery API (cómo usar discovery docs)**:  
  https://developers.google.com/discovery/v1/reference/apis

> Nota: Explora todos los subapartados de la referencia RBM (recursos, métodos, request/response, errores, cuotas, auth). Usa el Discovery Document para generar/validar clientes y esquemas.

---

## 2) Reglas de trabajo del agente
1. **Prioriza la fuente oficial**: cualquier duda sobre contratos de API, estados, errores o límites → verifica en los enlaces anteriores.
2. **Traza tus hallazgos** en `/docs/research/yyyymmdd-topic.md` con links y citas a la referencia.
3. **Propuestas > cambios directos**: si la modificación es no trivial (contratos, modelos, flujos), abre primero un RFC corto (`/docs/rfc/NNN-titulo.md`).
4. **PRs atómicas**: una PR = un objetivo claro (máx. ~300 LOC efectivos). Si superas, divide.
5. **Automatiza**: añade tests y tareas Gradle/Maven; configura CI para validar.
6. **Idempotencia y resiliencia** para integraciones RBM: reintentos con backoff, timeouts, circuit breaker, logs estructurados, correlación.

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
### 5.1 Descubrimiento y contratos
- **Discovery document** (`v1`) sirve para:
    - Generar clientes/POJOs,
    - Confirmar rutas y query params,
    - Verificar esquemas de request/response.
- Antes de codificar endpoints, **bloquea el contrato** (interfaces + DTOs) y añade **tests de contrato** (JSON de ejemplo validado con el discovery).

### 5.2 Cliente HTTP
- Usa **WebClient** (reactivo) o RestTemplate con **timeouts**, **retry con backoff** y **resolución de errores** por familia de status.
- **Headers**: `Authorization: Bearer <token>`, `Content-Type: application/json`, `Accept: application/json`.
- **Telemetry**: log de request id, latencia, tamaño payload, status.

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
- **Static analysis**: SpotBugs/Checkstyle/PMD + `errorprone` si aplica.
- **CI**: build limpia, tests, lints, cobertura, análisis estático, SBOM (CycloneDX).

Checklist de PR (auto):
- [ ] Build OK + tests pasan.
- [ ] Nuevos endpoints/POJOs documentados.
- [ ] Manejo de errores probado (feliz y no feliz).
- [ ] Logs/telemetría adecuados.
- [ ] Config segura (sin secretos, propiedades tipadas).
- [ ] Changelog/Notas de cambio.

---

## 8) Flujo de trabajo para tareas
1. **Descubrir**: lee la referencia RBM y discovery; anota endpoints, métodos, esquemas y scopes.
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
- Actualiza ejemplos RBM cuando cambie el discovery.
- En PRs que toquen contratos, incluye **diff de esquemas**.

---

## 14) Atajos útiles para investigación
- **Buscar en la referencia RBM**: usa el buscador de la página; filtra por recurso (brands, agents, messages, etc.).
- **Discovery doc**: valida rápidamente un campo o tipo buscando por nombre de recurso o método.
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
- Añadir tests de contrato validados contra el discovery `v1`.
- Esqueleto de simulador (endpoints locales que emulan RBM).
- Pipeline CI con lints, tests, cobertura y SBOM.

