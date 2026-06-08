# PataGOnIA – Cliente Android

App móvil educativa para registrar avistamientos de especies endémicas de Chile, usarla como compañero de trekking y aprender sobre fauna y flora local.

## Stack técnico

- **Plataforma**: Android nativo.
- **Lenguaje**: Kotlin.
- **UI**: Jetpack Compose.
- **Arquitectura**: MVVM + Clean Architecture (capas `data`, `domain`, `presentation`).
- **DI**: Hilt.
- **Persistencia local**: Room.
- **Red**: Retrofit o Ktor (por confirmar).
- **IA on‑device**: ML Kit (visión por computador para reconocer especies desde fotos).

## Requisitos

- Android Studio (última versión estable).
- JDK incluido con Android Studio.
- Emulador Android configurado o dispositivo físico con modo desarrollador.

## Cómo abrir y ejecutar el proyecto

1. Clonar el repo:

   ```bash
   git clone <URL_DEL_REPO>
   cd <NOMBRE_DEL_PROYECTO>
   ```

2. Abrir la carpeta del proyecto en **Android Studio** (`File > Open`).

3. Esperar a que Gradle sincronice las dependencias.

4. Ejecutar:

   - Desde Android Studio: botón **Run ▶** sobre el módulo `app`, seleccionando emulador o dispositivo.
   - O por consola:

     ```bash
     ./gradlew assembleDebug
     ```

## Organización del código

- `data/`: acceso a datos (APIs, Room, repositorios).
- `domain/`: modelos de dominio y casos de uso.
- `presentation/`: pantallas Compose, ViewModels, navegación.
- `di/`: módulos de Hilt (inyección de dependencias).
- `core/`: utilidades compartidas.

## Próximos pasos (alto nivel)

- Definir rutas de sendero y modelo de datos de especies.
- Integrar ML Kit para reconocimiento básico desde la cámara/galería.
- Diseñar mecánicas de “captura” y progresión (gamificación).

