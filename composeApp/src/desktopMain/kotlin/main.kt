import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import screens.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GePro",
    ) {
        App()
    }
}