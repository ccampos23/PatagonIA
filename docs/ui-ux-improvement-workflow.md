# UI/UX Improvement Workflow: GSD + Impeccable

Flujo complementario para crear, rediseñar o mejorar visualmente la app usando **GSD** (proceso) e **Impeccable** (ejecución de diseño).

---

## Fase 0 — Setup inicial (una sola vez por proyecto)

```bash
npx impeccable install        # ya instalado
```

Luego en el agente:

```
/impeccable init              # genera PRODUCT.md y DESIGN.md
```

---

## Fase 1 — Investigación y contexto

**GSD** explora el estado actual del UI:

```
/gsd-quick "explora el estado actual del UI: componentes, pantallas, sistema de diseño"
```

Referencias útiles del proyecto:
- `mobile-android-design` — Material Design 3 + Jetpack Compose patterns
- `imagegen-frontend-mobile` — generación de conceptos visuales premium

**Impeccable** extrae el diseño existente:

```
/impeccable extract           # extrae componentes y tokens del código actual
/impeccable document          # genera/actualiza DESIGN.md desde el código
```

---

## Fase 2 — Exploración de diseño

Loop rápido de prototipado:

```
gsd-sketch                     # mockups HTML interactivos descartables
```

```
/impeccable shape             # planear UX/UI antes de escribir código
/impeccable critique          # review de los mockups: jerarquía, claridad, resonancia
```

Repetir: `sketch → critique → refine` hasta tener una dirección clara.

---

## Fase 3 — Especificación formal

**GSD** genera un contrato de diseño:

```
gsd-ui-phase                   # inserta UI-SPEC.md entre discuss-phase y plan-phase
```

El workflow orquesta:
1. `gsd-ui-researcher` — investiga patrones y genera UI-SPEC.md
2. `gsd-ui-checker` — verifica la spec (spacing, tipografía, color, copy, componentes)

En este punto quedan **lockeadas** las decisiones de diseño antes de planificar.

---

## Fase 4 — Implementación

Planificar y ejecutar con GSD:

```
gsd-plan-phase
gsd-execute-phase
```

Durante la implementación, los **hooks de Impeccable** corren automáticamente sobre ediciones de UI.

Usar comandos específicos de Impeccable según la necesidad:

| Comando | Cuándo usarlo |
|---------|---------------|
| `/impeccable colorize` | Introducir color estratégico, arreglar paletas |
| `/impeccable typeset` | Ajustar fuentes, jerarquía, tamaños |
| `/impeccable layout` | Corregir espaciado, ritmo visual, alineación |
| `/impeccable clarify` | Mejorar copy de UX (CTAs, errores, empty states) |
| `/impeccable animate` | Añadir movimiento con propósito |
| `/impeccable harden` | Edge cases, i18n, text overflow, estados vacíos |
| `/impeccable onboard` | First-run flows, empty states, activation paths |
| `/impeccable delight` | Añadir momentos de alegría |
| `/impeccable bolder` | Amplificar diseños muy planos |
| `/impeccable quieter` | Calmar diseños sobrecargados |

---

## Fase 5 — Pulido y auditoría

Primero Impeccable para el pulido fino:

```
/impeccable polish            # pasada final: alineación con design system, shipping readiness
/impeccable audit             # checks técnicos: a11y, responsivo, rendimiento
/impeccable distill           # simplificar, quitar complejidad innecesaria
```

Luego GSD para la auditoría formal:

```
gsd-ui-review                  # auditoría 6 pilares → UI-REVIEW.md
```

Evalúa: **copywriting, visuals, color, typography, spacing, experience design**.

```
gsd-verify                     # verificación con checkpoints de UI
```

---

## Fase 6 — Cierre

```
/gsd-quick "actualiza DESIGN.md con cambios finales"
```

Comitar con GSD manteniendo el registro de diseño actualizado.

---

## Resumen visual del flujo

```
[Setup]   init                         → PRODUCT.md + DESIGN.md
            │
[Invest]  quick / extract / document   → entender estado actual
            │
[Explore] sketch / shape / critique    → iterar mockups
            │
[Spec]    gsd-ui-phase                 → UI-SPEC.md (decisiones lockeadas)
            │
[Build]   plan + execute + comandos    → código + hooks automáticos
            │
[Polish]  polish / audit / distill     → afinar detalles
            │
[Audit]   gsd-ui-review + verify       → UI-REVIEW.md + verificación
            │
[Close]   commit + update docs         → registro actualizado
```

---

## Notas

- **Idioma**: Los comandos son fijos en inglés. Las descripciones/pedidos pueden ir en español sin problema.
- **TDD**: El proyecto exige TDD (RED → GREEN → REFACTOR). Aplica también a cambios de UI donde sea posible (tests de Compose, ViewModels).
- **Skills disponibles**: `mobile-android-design`, `imagegen-frontend-mobile`, `android-clean-architecture`.
- **Perfil de desarrollador**: no configurado aún. Correr `/gsd-profile-user` para personalizar respuestas del agente.
