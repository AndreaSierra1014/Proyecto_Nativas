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

## Configuración

1. Añade el `google-services.json` de Firebase en `app/`. Está excluido de Git.
2. Añade `googleMapsApiKey=TU_CLAVE_RESTRINGIDA` a `local.properties` para configurar Google Maps. Ese archivo está excluido de Git; restringe la clave al paquete y certificado de firma de la app.
3. Configura y despliega `firestore.rules` y `storage.rules` con Firebase CLI.
4. Crea la cuenta administradora desde una herramienta de confianza (Firebase Admin SDK o consola); el registro público solo permite roles de comprador o vendedor.

Las reglas de Firestore consideran administrador a una cuenta con `role: "ADMIN"` que haya sido creada de forma confiable. No permitas que el cliente asigne ese rol.

La eliminación desde el panel elimina el perfil de Firestore; borrar también la cuenta de Firebase Authentication requiere una función backend con Firebase Admin SDK.
