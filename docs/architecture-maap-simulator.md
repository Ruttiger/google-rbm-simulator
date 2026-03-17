# Arquitectura MaaP multi-interfaz

El simulador separa un **core MaaP** neutral (`core/model`, `core/repo`, `core/service`) de adaptadores por canal.

- `channel/rbm` (existente) mantiene contratos RBM.
- `controller/pcm` y `service/pcm` implementan contrato PCM.
- `provisioning/*` centraliza configuración operativa PCM.
- `config/InterfaceSelectionWebFilter` habilita/deshabilita interfaces por `maap.simulator.enabled-interfaces`.

Objetivo: agregar futuros canales (ej. OSP) sin duplicar lógica de estado/provisión.
