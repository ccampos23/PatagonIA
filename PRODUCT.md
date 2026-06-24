# Product

## Register

product

## Users

PataGOnIA sirve a tres audiencias que se cruzan en el mismo terreno. El contexto de diseño dominante es el primero.

- **Excursionistas y naturópatas en campo (contexto primario).** Usan la app caminando senderos de la Patagonia chilena: luz exterior intensa, a menudo una mano ocupada, conectividad pobre o nula. El dispositivo funciona offline tras una descarga inicial de mapas y modelos. La legibilidad rápida y la confianza en terreno son innegociables.
- **Estudiantes y educadores.** Uso en sala y salidas a terreno para aprender la fauna y flora endémica de Chile. Cada avistamiento enseña algo real.
- **Científicos ciudadanos.** Colaboradores que registran avistamientos verificables (especie, ubicación, foto, confianza del modelo) que alimentan datasets de conservación.

## Product Purpose

App móvil educativa y compañera de trekking para registrar avistamientos de especies endémicas de Chile. Usa IA on-device (ML Kit / TensorFlow Lite) para reconocer fauna y flora desde una foto, funciona offline con mapas Mapbox descargados, y gamifica la "captura" y progresión para sostener el aprendizaje. El éxito se ve así: un excursionista identifica con confianza una especie en el sendero, aprende algo real sobre ella y deja un registro verificable para conservación, todo sin depender de la red.

## Brand Personality

**Cercana, curiosa, didáctica.**

Voz experta pero accesible: invita a explorar y aprender la biodiversidad sin infantilizar. El tono didáctico aparece en el copy, las microcopias y los momentos educativos; la precisión aparece en el layout y el contraste. Cercana = humana y local (Patagonia chilena), no corporativa ni distante.

## Anti-references

- **App de caricatura/juguete.** No colores saturados de juguete ni ilustraciones caricaturescas. La gamificación ("captura", progresión) debe sentirse adulta y de campo, no tipo Pokémon infantil.
- **Densidad de panel admin.** No tablas densas, paneles de control ni sobrecarga informativa. Esto es campo, no oficina.
- **AI beige/cream.** Evitar la banda warm-neutral por defecto (fondos crema/arena/paper tibios usados como move decorativo). La calidez de marca se gana con acento, tipografía e imagen, no con un fondo tibio cerca del blanco.

## Design Principles

1. **Legibilidad en terreno primero.** Bajo sol intenso y a una mano, el texto y los datos clave deben leerse al instante. Contraste y tamaño ganan al refinamiento decorativo. Ante la duda, oscurecer la tinta y subir el contraste.
2. **Herramienta, no juguete.** Utilidad outdoor nítida: precisa, de alto contraste, tipo herramienta de campo. La gamificación se gana con momentos adultos y veraces, no con estética infantil.
3. **Enseña al identificar.** Cada reconocimiento es una oportunidad didáctica: mostrar qué se vio, por qué importa y cómo saberlo. Didáctica integrada al flujo, no como sección aparte.
4. **Confianza offline.** La app debe sentirse confiable sin red: estados claros de descarga, modelos y disponibilidad; nunca dejar al usuario sin saber si algo está listo o falló.
5. **Calidez por voz e imagen, no por fondo.** La marca se siente cercana y local a través del copy y la fotografía de la Patagonia, no a través de neutros tibios cerca del blanco.

## Accessibility & Inclusion

- **Prioridad declarada: alto contraste legible en exteriores.** Texto y datos clave deben cumplir y superar WCAG AA bajo luz intensa. Verificar razones de contraste reales sobre los fondos del tema — en particular texto secundario/muted sobre el fondo claro actual, que es el fallo más común.
- Práctica estándar mantenida como base: movimiento con alternativa reducida (`prefers-reduced-motion`), objetivos táctiles amplios y glanceable a una mano, e identificación de especies que no depende solo del color (daltonismo).

## Surfaces & Tooling

- **Superficie primaria:** la app Android (Kotlin + Jetpack Compose) en `app/`. Impeccable aporta **guía de diseño** — estrategia de color, jerarquía tipográfica, crítica, copy, principios — que se traduce al tema Compose (`presentation/theme/Color.kt`, `Type.kt`) y a las pantallas. Impeccable no edita Kotlin en vivo; los comandos web (live mode en navegador) aplican solo a superficies web.
- **Superficie secundaria:** existe un deck Slidev en `PatagonIA/` (hoy el starter por defecto) que puede tratarse aparte como superficie de marca cuando se requiera; ahí impeccable sí trabaja de forma nativa.
