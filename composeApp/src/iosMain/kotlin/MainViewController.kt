import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.engine.darwin.Darwin
import networking.createHttpClient
import screens.App

fun MainViewController() = ComposeUIViewController {
    App(
    )
}