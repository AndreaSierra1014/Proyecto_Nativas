# TechStore

Aplicación Android nativa en Kotlin con autenticación Firebase y flujos separados para administración, vendedor y comprador.

## Módulos

- Autenticación por correo y contraseña con acceso biométrico para una sesión existente.
- Administración de usuarios.
- Gestión de productos para vendedores.
- Catálogo, carrito y órdenes para compradores.
- Selección de ubicación con Google Maps.
- Registro de órdenes de demostración: no procesa ni confirma pagos.

## Requisitos

- Android Studio Hedgehog o superior con JDK 17.
- Android SDK 34.
- Proyecto Firebase con Authentication, Firestore y Storage habilitados.
- API key de Google Maps restringida para el paquete `com.techstore.app`.

