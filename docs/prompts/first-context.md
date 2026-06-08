# PataGOnIA – Plantilla de Arquitectura y Setup (para agente de IA)

## 0. Objetivo de este documento

Este documento está pensado para que un **agente de IA de desarrollo** configure un entorno de trabajo Android moderno para el proyecto **PataGOnIA**, una app nativa Android escrita en **Kotlin** usando **Jetpack Compose** como toolkit de UI.

El agente debe:

1. Preparar el entorno local de desarrollo.
2. Crear un proyecto base de Android con Compose.
3. Configurar la arquitectura interna (MVVM + Clean Architecture).
4. Añadir las dependencias base del stack (Compose, Hilt, Room, coroutines, networking, ML Kit, etc.).
5. Configurar la base del stack de IA on‑device (ML Kit Vision).
6. Dejar comandos de build y test funcionando.

---

## 1. Requisitos del entorno local

El agente debe verificar o indicar al usuario:

- **Sistema operativo**: Windows 10/11 64‑bit, macOS reciente (ARM o Intel) o Linux 64‑bit.
- **RAM mínima**: 8 GB (recomendado 16 GB).
- **Espacio en disco**: al menos 16 GB libres (IDE + SDK + emuladores).
- **CPU**: 64‑bit con soporte de virtualización (para emulador Android).

---

## 2. Instalación de Android Studio y SDK

1. Descargar la última versión estable de **Android Studio** desde la página oficial de Android Developers.
2. Instalar con los valores por defecto.
3. Abrir Android Studio y completar el asistente inicial:
   - Aceptar la instalación del **Android SDK**, **Android SDK Platform Tools**, **Android Emulator**.
   - Verificar que se haya instalado al menos una **Android SDK Platform** basada en Android 8.0+.
4. En **SDK Manager**:
   - Verificar que estén instalados:
     - Android SDK Platform correspondiente a la versión objetivo (por ejemplo Android 14 / API 34).
     - Android SDK Build‑Tools.
     - Android Emulator.
     - Android SDK Command‑line Tools.

---

## 3. Creación del proyecto base con Jetpack Compose

El agente debe crear un proyecto nuevo con soporte a Compose:

1. En Android Studio, crear un **New Project**.
2. Seleccionar la plantilla **Empty Activity** (esta plantilla crea un proyecto con Jetpack Compose por defecto en versiones recientes).
3. Configuración del proyecto:
   - **Language**: `Kotlin` (única opción soportada por Compose).
   - **Minimum SDK**: API 21 o superior (recomendado API 23+).
   - **Use legacy Views**: desactivado (solo Compose).
4. Generar el proyecto y asegurarse de que compila y corre el `MainActivity` de ejemplo en un emulador o dispositivo físico.

---

## 4. Configuración de Gradle (nivel proyecto y módulo app)

### 4.1. build.gradle/settings.gradle a nivel proyecto

El agente debe:

- Usar el sistema de configuración por defecto de Android Studio con **Kotlin DSL** (`build.gradle.kts`).
- Asegurarse de que:
  - El plugin de Kotlin para Android está aplicado.
  - Se habilita Compose en las opciones del módulo `app`.

Ejemplo (pseudo‑configuración, no requiere versiones exactas):

```kotlin
// settings.gradle.kts (ejemplo)
rootProject.name = "PataGOnIA"
include(":app")
```

```kotlin
// build.gradle.kts (raíz, ejemplo simplificado)
plugins {
    id("com.android.application") version "<latest>"
    id("org.jetbrains.kotlin.android") version "<latest>"
}
```

El agente debe resolver y rellenar las versiones con las últimas recomendadas por el asistente de Android Studio y la documentación oficial.

### 4.2. build.gradle.kts del módulo `app`

El agente debe:

1. Habilitar Compose:
   - `buildFeatures { compose = true }`
   - `composeOptions { kotlinCompilerExtensionVersion = "<latest>" }`

2. Establecer configuraciones básicas:
   - `applicationId = "cl.patagoniapp"` (puede ajustarse).
   - `minSdk = 23` (o superior, pero no menor a 21).
   - `targetSdk` y `compileSdk` a la última API estable.

3. Añadir las dependencias básicas del stack:

Sin versiones concretas (el agente debe consultar las últimas versiones oficiales):

- **Jetpack Compose UI**:
  - `androidx.activity:activity-compose`
  - `androidx.compose.ui:ui`
  - `androidx.compose.material3:material3`
  - `androidx.compose.ui:ui-tooling-preview`
  - `androidx.compose.ui:ui-tooling` (solo debug)

- **Arquitectura y ciclo de vida**:
  - `androidx.lifecycle:lifecycle-viewmodel-compose`
  - `androidx.lifecycle:lifecycle-runtime-ktx`

- **Navegación Compose**:
  - `androidx.navigation:navigation-compose`

- **Coroutines**:
  - `org.jetbrains.kotlinx:kotlinx-coroutines-core`
  - `org.jetbrains.kotlinx:kotlinx-coroutines-android`

- **Room (persistencia local)**:
  - `androidx.room:room-ktx`
  - `androidx.room:room-runtime`
  - KAPT o KSP para Room (p. ej. `ksp("androidx.room:room-compiler")`).

- **Hilt (inyección de dependencias)**:
  - `com.google.dagger:hilt-android`
  - `com.google.dagger:hilt-compiler` (KAPT o KSP)
  - `androidx.hilt:hilt-navigation-compose` para integración con Compose.

- **Networking (a elección)**:
  - Por ejemplo, `com.squareup.retrofit2:retrofit` + convertidor JSON
    o `Ktor Client` para HTTP.

- **Testing**:
  - JUnit4/5 para tests unitarios.
  - `androidx.test.ext:junit` y `androidx.test.espresso:espresso-core` (si se requiere).
  - `androidx.compose.ui:ui-test-junit4` para tests de UI en Compose.

---

## 5. Estructura de arquitectura (MVVM + Clean Architecture)

El agente debe crear la estructura de paquetes siguiendo una arquitectura en capas:

- `cl.patagoniapp`
  - `data`
    - `local` (Room DAOs, entidades de persistencia)
    - `remote` (API services)
    - `repository` (implementaciones de repositorios)
  - `domain`
    - `model` (modelos de dominio)
    - `repository` (interfaces de repositorios)
    - `usecase` (casos de uso: p. ej. `GetSpecies`, `RegisterSighting`)
  - `presentation`
    - `ui` (pantallas Compose, componentes UI reutilizables)
    - `viewmodel` (ViewModels)
    - `navigation` (grafo de navegación Compose)
  - `di`
    - Módulos Hilt (por ejemplo `DatabaseModule`, `NetworkModule`, `RepositoryModule`, etc.)
  - `core` (utilidades compartidas, constantes, etc.)

### 5.1. Principios

- **Presentation** depende solo de **domain** (no de data).
- **Domain** no depende de ninguna capa externa (pure Kotlin).
- **Data** implementa interfaces definidas en **domain**.
- **DI** (Hilt) es responsable de cablear las dependencias entre capas.

---

## 6. Stack de IA (en la app)

La app utilizará principalmente **ML Kit** como stack de IA on‑device para visión por computador (reconocimiento de especies).

### 6.1. Dependencias de ML Kit

El agente debe:

1. Añadir las dependencias de ML Kit Vision apropiadas en `app/build.gradle.kts`, por ejemplo:
   - Image Labeling (etiquetado de imágenes genérico).
   - Object Detection and Tracking (si se usa seguimiento en cámara en tiempo real).

2. Las dependencias concretas (groupId, artifactId, versión) deben obtenerse de la documentación oficial de ML Kit, y añadirse en la sección `dependencies { ... }`.

### 6.2. Configuración de permisos y cámara

El agente debe:

1. Añadir permisos al `AndroidManifest.xml`:
   - `android.permission.CAMERA`
   - Permisos de almacenamiento si se requiere acceso a galería.

2. Definir un provider de archivos si se usan imágenes capturadas y almacenadas temporalmente.

3. Crear una capa de servicio/fachada para la cámara y ML Kit:
   - Servicios en la capa `data` o `core` que encapsulen:
     - Captura de imagen (cámara / galería).
     - Envío de la imagen a ML Kit.
     - Devolución de una lista de etiquetas/objetos detectados.

### 6.3. Futuras extensiones de IA

La arquitectura debe permitir:

- Sustituir fácilmente el modelo de ML Kit o agregar modelos personalizados (TensorFlow Lite u otros) más adelante.
- Añadir llamadas a backends de IA externos (por ejemplo, APIs de clasificación especializadas) a través de casos de uso en la capa `domain`.

---

## 7. Stack de IA para desarrollo (agente de codificación)

Este mismo documento está orientado a un **agente de codificación** (por ejemplo, un agente GSD) con las siguientes capacidades:

1. Comprender y modificar:
   - Archivos `build.gradle.kts` a nivel raíz y módulo.
   - Código Kotlin de capas `data`, `domain`, `presentation`, `di`.
   - Archivos de configuración de Android (AndroidManifest, recursos).

2. Ejecutar comandos:
   - `./gradlew clean`
   - `./gradlew assembleDebug`
   - `./gradlew test`
   - `./gradlew connectedAndroidTest` (si hay dispositivos conectados)

3. Ajustar versiones de dependencias informándose desde:
   - Documentación oficial de Android/Jetpack.
   - Documentación oficial de ML Kit.

El agente debe respetar la arquitectura descrita y evitar introducir dependencias cruzadas entre capas.

---

## 8. Comandos de build y ejecución

El agente debe preparar scripts o instrucciones para:

- **Build debug**:
  - `./gradlew assembleDebug`

- **Tests unitarios**:
  - `./gradlew test`

- **Tests instrumentados (si configurados)**:
  - `./gradlew connectedAndroidTest`

- **Lanzar la app desde Android Studio**:
  - Configurar una **Run configuration** para el módulo `app`.
  - Ejecutar en un emulador Android o dispositivo físico conectado.

---

## 9. Checklist final para el agente

Al terminar el setup inicial, el agente debe verificar:

- [ ] El proyecto compila correctamente en modo Debug.
- [ ] La app de ejemplo se ejecuta en emulador/dispositivo.
- [ ] Jetpack Compose está habilitado y se utiliza en la UI principal.
- [ ] Existen los paquetes `data`, `domain`, `presentation`, `di`, `core`.
- [ ] Hilt está configurado y se inyecta correctamente un ViewModel de ejemplo.
- [ ] Room está configurado con al menos una entidad y un DAO de ejemplo.
- [ ] Existen dependencias de ML Kit Vision añadidas al proyecto.
- [ ] Están definidos los comandos de build y test necesarios.
