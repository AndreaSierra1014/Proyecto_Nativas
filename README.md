# TechStore

Aplicación Android nativa en Kotlin para una tienda de tecnología. Este repositorio contiene el scaffold inicial y las pantallas compartidas hasta ahora; catálogo, pedidos, administración y vendedor se irán completando conforme se definan sus archivos.

## Requisitos

- Android Studio con JDK 17
- Android SDK 34
- Un proyecto Firebase con Authentication, Cloud Firestore y Cloud Storage habilitados

## Configuración Firebase

Descarga `google-services.json` desde la configuración de tu app Android Firebase (`com.techstore.app`) y colócalo en `app/google-services.json`. Ese archivo está excluido de Git.

## Compilar

Abre el proyecto en Android Studio y sincroniza Gradle. Sin `google-services.json`, el código puede compilar, pero las funciones Firebase no estarán disponibles al ejecutar la app.
