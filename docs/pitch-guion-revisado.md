# Guion Revisado Power Pitch - PataGOnIA

Duracion objetivo: 5 minutos + preguntas  
Formato: pitch formal para inversores  
Estado base usado para esta revision: codigo actual del proyecto Android en `app/`

## Inconsistencias Detectadas en el Guion Original

El guion original vendia bien la vision, pero mezclaba funcionalidades implementadas, funcionalidades en prototipo y funcionalidades futuras. Para una presentacion formal ante inversores conviene evitar promesas tecnicas que el repositorio todavia no sostiene.

1. **MapLibre no corresponde al proyecto actual.** El codigo usa Mapbox Maps SDK para Android (`com.mapbox.maps:android:11.11.1`) y `maps-compose`, no MapLibre.

2. **Supabase no esta integrado en el codigo actual.** No hay dependencia de Supabase ni cliente configurado. El modelo `Capture` ya contempla `isSynced`, pero la sincronizacion remota aun no esta implementada.

3. **WorkManager no esta integrado.** El guion menciona sincronizacion en segundo plano con WorkManager, pero no hay dependencia `androidx.work` ni workers implementados.

4. **El mapa offline todavia no esta completamente operativo.** Existe pantalla de mapa, gestor de regiones y presets de parques chilenos, pero `MapScreen` usa un placeholder y `TileDownloadService` marca descargas como completadas sin descargar tiles reales. Conviene decir "modulo de mapas offline en desarrollo" o "arquitectura preparada para mapas offline con Mapbox".

5. **La georreferenciacion real no esta lista.** `CameraViewModel` guarda coordenadas simuladas cerca de Aysen/Torres del Paine. No conviene afirmar "GPS verificado" todavia.

6. **ML Kit no es el motor central real de inferencia.** El proyecto incluye dependencias de ML Kit image labeling, pero el flujo principal de clasificacion usa TensorFlow Lite (`Interpreter`) con un modelo `.tflite` y labels, alimentado por CameraX. La frase correcta es "modelo TFLite on-device" o "clasificacion on-device con CameraX + TensorFlow Lite".

7. **El modelo se descarga, pero tambien puede venir empaquetado.** `SpeciesAnalyzer` intenta cargar `species_model.tflite` y `species_labels.txt` desde assets; si no existen, los busca en `filesDir`. `LoadingViewModel` descarga ambos archivos desde GitHub via Ktor.

8. **Gamificacion, Sticker Book, XP y badges no aparecen implementados.** Son buena vision de producto, pero deben presentarse como siguiente capa de engagement, no como feature actual.

9. **Pins crowdsourced no estan implementados.** Existe modelo local de capturas y mapa con tooltip conceptual, pero no hay capa comunitaria remota.

10. **Cifras de mercado sin fuente.** TAM/SAM/SOM del guion original pueden sonar inventados si no se citan. Para 5 minutos es mejor usar oportunidad cualitativa o dejar cifras como placeholders hasta validar fuentes.

11. **Duracion desalineada.** El guion original declara 8-10 minutos, pero ahora el objetivo es un power pitch de 5 minutos. Hay que reducir slides, texto y transiciones.

## Version Corregida del Guion

### SLIDE 1 - Hero / Apertura

Tiempo: 30 segundos  
Habla: Integrante 1

> Imaginen esto: estan caminando por un sendero en Torres del Paine. Ven un animal entre los arbustos que nunca habian visto. Sacan el celular para buscarlo... y no hay senal. Ni una barra.
>
> Esa frustracion nos motivo a crear PataGOnIA.
>
> PataGOnIA es un diario de naturaleza con inteligencia artificial on-device. Permite identificar, registrar y revisar especies chilenas incluso cuando estas en terreno, lejos de internet.
>
> Queremos convertir cada caminata en una experiencia educativa, coleccionable y conectada con la biodiversidad local.

Visual sugerido: mockup grande de la app en modo camara, fondo fotografico de Patagonia chilena, frase central: "Identifica la naturaleza chilena, incluso sin senal".

### SLIDE 2 - Problema

Tiempo: 45 segundos  
Habla: Integrante 1

> Chile tiene una biodiversidad enorme, pero para la mayoria de las personas sigue siendo invisible.
>
> El primer problema es la conectividad: los lugares donde mas necesitas informacion de naturaleza suelen ser justamente los lugares donde no hay internet.
>
> El segundo problema es la falta de contexto local. Las apps genericas no estan pensadas desde la experiencia de recorrer Chile, sus parques, sus senderos y sus especies.
>
> Y el tercer problema es que las fotos quedan perdidas en el carrete. Sin nombre, sin historia, sin ubicacion util y sin aprendizaje.
>
> PataGOnIA ataca esos tres dolores: identificacion offline, registro personal y una experiencia disenada para explorar naturaleza chilena.

Visual sugerido: tres tarjetas: "Sin senal", "Sin contexto local", "Recuerdos perdidos".

### SLIDE 3 - Solucion / Producto

Tiempo: 55 segundos  
Habla: Integrante 2

> La solucion es una app offline-first para exploradores, turistas y amantes de la naturaleza.
>
> Hoy ya tenemos el loop central en desarrollo: una camara con CameraX, clasificacion on-device con TensorFlow Lite, descarga de modelo y etiquetas antes de salir, y guardado local de avistamientos con Room.
>
> El usuario puede apuntar la camara o subir una foto desde la galeria, recibir sugerencias de especie con nivel de confianza, revisar el resultado, agregar notas y guardar el avistamiento en su diario.
>
> Sobre esa base estamos construyendo la capa de mapa offline con Mapbox y la experiencia de coleccion: una guia visual que premia descubrir nuevas especies.
>
> El principio es simple: primero funciona en el celular; despues, cuando hay conexion, se sincroniza.

Visual sugerido: flujo en 3 pasos: "Capturar" -> "Identificar" -> "Guardar". Mostrar mockups de camara y pantalla de revision.

### SLIDE 4 - Demo / Estado Actual

Tiempo: 60 segundos  
Habla: Integrante 3

> Estas son las experiencias centrales que ya estamos armando.
>
> Primero, la camara inteligente. Usamos CameraX para analizar frames y un modelo TFLite en el dispositivo para reconocer especies sin depender de red.
>
> Segundo, la revision del avistamiento. Despues de capturar o importar una foto, la app muestra las principales coincidencias, el porcentaje de confianza y permite corregir o completar la informacion.
>
> Tercero, el diario local. Cada captura se guarda en Room con especie, nombre cientifico, foto, notas, timestamp y estado de sincronizacion pendiente.
>
> Y cuarto, el modulo de mapas offline. Ya tenemos estructura de presets para parques nacionales, gestor de descargas y base de integracion con Mapbox; la descarga real de tiles y el mapa productivo son parte de la siguiente iteracion.

Visual sugerido: cuatro cards con capturas reales o mockups: "Camara", "Revision", "Diario local", "Mapa offline en desarrollo".

Nota para presenter: no decir "100% terminado". Usar "en desarrollo", "ya tenemos la base" y "siguiente iteracion".

### SLIDE 5 - Diferenciacion

Tiempo: 40 segundos  
Habla: Integrante 2

> Nuestra diferenciacion no es solo reconocer especies. Es hacerlo en el contexto correcto.
>
> PataGOnIA esta pensada para terreno: offline-first, especies chilenas, parques nacionales, rutas, registro personal y aprendizaje.
>
> Google Lens requiere conectividad y no esta disenado para trekking. iNaturalist es potente como comunidad cientifica, pero no esta construido como companero offline de exploracion.
>
> Nosotros combinamos identificacion, diario de campo, mapa y motivacion coleccionable en una experiencia simple para usuarios no expertos.

Visual sugerido: matriz comparativa simple: "Offline", "Foco Chile", "Diario personal", "Gamificacion", "Mapa outdoor".

### SLIDE 6 - Oportunidad / Mercado

Tiempo: 45 segundos  
Habla: Integrante 4

> La oportunidad esta en la interseccion de tres tendencias: turismo de naturaleza, educacion ambiental e inteligencia artificial en dispositivos moviles.
>
> Chile tiene una posicion privilegiada: parques nacionales reconocidos mundialmente, especies endemicas, turismo internacional y una cultura outdoor en crecimiento.
>
> Nuestro primer mercado son excursionistas, turistas y fotografos de naturaleza que ya usan el celular como companero de viaje, pero todavia no tienen una herramienta pensada para Chile y para escenarios sin senal.
>
> Empezamos en Chile porque nos da foco, identidad y datos locales. Desde ahi, el modelo puede expandirse a otros destinos de naturaleza en Latinoamerica.

Visual sugerido: mapa de Chile con parques destacados y segmentos de usuario: "Excursionistas", "Turistas", "Fotografos", "Educacion ambiental".

Nota: si se quieren usar cifras de TAM/SAM/SOM, agregarlas solo con fuente validada.

### SLIDE 7 - Modelo de Negocio

Tiempo: 40 segundos  
Habla: Integrante 4

> Nuestra estrategia inicial es mantener la app accesible y monetizar alrededor de la experiencia.
>
> Primero, alianzas con marcas outdoor, operadores turisticos, parques y programas de educacion ambiental.
>
> Segundo, paquetes premium: mapas, guias tematicas, rutas curadas y contenido avanzado para exploradores frecuentes.
>
> Tercero, merchandising de especies chilenas y recompensas fisicas conectadas con la experiencia digital.
>
> Y cuarto, fondos y programas de innovacion ambiental, como apoyo publico o privado para conservacion, turismo sustentable y educacion.

Visual sugerido: cuatro fuentes de ingresos: "Partnerships", "Contenido premium", "Merchandising", "Fondos/impacto".

Nota: evitar decir que ya hay acuerdos si no existen. Decir "estrategia", "lineas" o "vías".

### SLIDE 8 - Tecnologia y Roadmap

Tiempo: 55 segundos  
Habla: Integrante 3

> Tecnologicamente estamos construyendo sobre una base nativa Android y escalable.
>
> La app usa Kotlin, Jetpack Compose con Material Design 3, arquitectura MVVM con capas data, domain y presentation, Hilt para inyeccion de dependencias, Room para persistencia local, CameraX para captura, TensorFlow Lite para inferencia on-device, Ktor para descargar modelos y Mapbox para la capa de mapas.
>
> El roadmap esta dividido en tres hitos.
>
> Primero: consolidar el MVP offline con reconocimiento, revision y diario local.
>
> Segundo: cerrar mapas offline reales, geolocalizacion y sincronizacion remota.
>
> Tercero: activar la capa de engagement: colecciones, logros, XP y comunidad.
>
> Esa secuencia reduce riesgo: primero demostramos valor individual offline, luego escalamos hacia comunidad y datos compartidos.

Visual sugerido: timeline de 3 hitos: "MVP offline", "Mapa + sync", "Coleccion + comunidad".

### SLIDE 9 - Cierre / Ask

Tiempo: 30 segundos  
Habla: Integrante 1

> PataGOnIA nace de una idea simple: si Chile tiene una biodiversidad unica, explorarla deberia ser facil, educativo y memorable.
>
> Estamos construyendo una app que une inteligencia artificial, experiencia outdoor y educacion ambiental, incluso sin conexion.
>
> Buscamos apoyo para acelerar el desarrollo del MVP, validar en terreno y abrir conversaciones con aliados del mundo outdoor, turismo y conservacion.
>
> Queremos que cada persona que camine por Chile pueda entender mejor lo que esta viendo.
>
> Exploremos Chile juntos. Muchas gracias.

Visual sugerido: pantalla final con logo, claim y tres asks: "inversion", "mentoria", "partners de terreno".

## Version Ultra Corta por Si el Tiempo Aprieta

Si el ensayo supera 5 minutos, fusionar slides:

1. Fusionar **Slide 4 Demo** con **Slide 8 Tecnologia**.
2. Fusionar **Slide 6 Mercado** con **Slide 7 Modelo de Negocio**.
3. Reducir integrantes: que negocio y tecnologia sean bloques de 40 segundos cada uno.

## Frases Que Conviene Evitar Por Ahora

- "Mapa 100% offline funcionando perfecto".
- "Sincroniza automaticamente con Supabase".
- "GPS verificado".
- "XP y Sticker Book ya disponibles".
- "Pins crowdsourced de la comunidad".
- "MapLibre".
- "ML Kit como motor principal".

## Frases Seguras y Alineadas al Codigo Actual

- "Clasificacion on-device con TensorFlow Lite".
- "Camara con CameraX".
- "Modelo y etiquetas descargables antes de salir".
- "Persistencia local con Room".
- "Arquitectura offline-first".
- "Base preparada para sincronizacion futura".
- "Modulo de mapas offline en desarrollo sobre Mapbox".
- "Roadmap hacia colecciones, logros y comunidad".

## Comandos Slidev Para Preparar la Presentacion

El proyecto Slidev esta en `PatagonIA/` dentro del repositorio.

```bash
cd PatagonIA
npm install
npm run dev
```

Para exportar cuando la presentacion este lista:

```bash
cd PatagonIA
npm run export
```

Si falla la exportacion por navegador/Playwright, instalar la dependencia de exportacion requerida por Slidev.
