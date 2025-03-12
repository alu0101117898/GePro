# GePro: Prototipo de Aplicación Minimalista para la Gestión de Proyectos

Esta aplicación está diseñada para facilitar la administración de proyectos de forma sencilla y eficiente.

## Instrucciones de Uso

### Configuración del Entorno de Desarrollo

#### IDE Recomendado
- **Android Studio** es la opción preferida para la ejecución de la aplicación.
- **Alternativas:** Si no dispone de Android Studio, puede usar cualquier otro IDE que soporte el desarrollo en Android.
- **Ejecución en Modo Escritorio:**
  - La aplicación funciona correctamente en el entorno de escritorio.
  - Para ejecutarla, utilice la opción de Gradle con el comando:
    ```
    composeApp:run
    ```

### Generación y Uso del Token de Acceso

Para utilizar GePro, debe disponer de una cuenta activa en **ClickUp** y generar un token de acceso. Siga estos pasos:

1. **Acceso a su Cuenta ClickUp:**
  - Inicie sesión en su cuenta activa de ClickUp.
2. **Generación del Token:**
  - Haga clic en su icono de usuario ubicado en la esquina superior derecha.
  - Seleccione la opción **Ajustes**.
  - En la nueva ventana, en el menú lateral izquierdo, haga clic en **Aplicaciones**.
  - En el panel derecho, elija **Generar token de acceso**.
  - Se mostrará su token de acceso; cópielo y guárdelo de forma segura.

3. **Incorporación del Token en GePro:**
  - Abra el archivo [`Token.kt`](GePro/composeApp/src/commonMain/kotlin/util/Token.kt).
  - Pegue el token generado en el lugar indicado dentro del archivo.
  - Asegúrese de que el token esté correctamente formateado para que la aplicación pueda utilizarlo en las conexiones a la API de ClickUp.

   Por ejemplo, el contenido del archivo podría verse de la siguiente forma:
   ```kotlin
   package util

   const val token = "AQUÍ_TU_TOKEN_GENERADO"
 
