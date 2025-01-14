import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.okhttp.OkHttp
import networking.ClickUpClient
import networking.createHttpClient
import ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GePro",
    ) {
        App(
            client = remember {
                val httpClient = createHttpClient(OkHttp.create()) // Crea el cliente HTTP
                ClickUpClient(httpClient) // Crea ClickUpClient con el cliente HTTP
            }
        )
    }
}