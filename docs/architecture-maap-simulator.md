# Arquitectura del simulador MaaP

Este documento define la separación entre `core` y `channel/*` para soportar múltiples canales de mensajería sin romper compatibilidad con RBM.

## Objetivo

- Mantener **RBM** como canal plenamente soportado y retrocompatible.
- Operar **PCM** como canal activo con contrato mínimo estable.
- Reservar **OSP** como extensión futura (placeholder) sin acoplar el núcleo a su implementación.

## Capas y responsabilidades

```text
app/
  core/
    web/
    service/
    domain/
    ports/
  channel/
    rbm/
    pcm/
    osp/
  infra/http/
  config/
  support/
```

### `core`

- Contiene modelos de dominio MaaP, casos de uso y puertos (`interfaces`).
- Define contratos como `OutboundMessageChannel`, `InboundEventChannel` o `ChannelRouter`.
- No depende de clases concretas de `channel/*`.

### `channel/*`

- Implementa los puertos del `core` por tecnología/proveedor.
- `channel/rbm`: adaptación a endpoints y payloads RBM.
- `channel/pcm`: adaptación a submit/callback y auth Basic de PCM.
- `channel/osp`: módulo vacío/placeholder con contrato base para futura activación.

## Dependencias permitidas

- `core ->` (ninguna dependencia hacia `channel/*`).
- `channel/* -> core` (implementan puertos).
- `web -> core` (nunca `web -> channel/*` directo).
- `infra/http -> core|channel/*` para concerns transversales.

## Estrategia de extensibilidad para OSP

1. Definir puerto en `core` para capacidades mínimas (send, status, callback).
2. Añadir `channel/osp` con implementación `NotImplemented`/feature flag.
3. Exponer rutas OSP solo cuando el flag esté activo.
4. Mantener suite RBM/PCM como regresión obligatoria antes de activar OSP.

## Compatibilidad RBM

- Los endpoints RBM existentes se consideran contrato estable.
- Cualquier refactor multi-canal debe preservar rutas, campos y códigos de respuesta RBM.
- Los cambios incompatibles requieren versionado explícito y documentación de migración.
