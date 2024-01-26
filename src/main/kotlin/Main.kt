import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import commons.DefaultColors
import components.ConnectionIndicator
import components.TableView
import components.connectionForm
import services.ConnectionService
import services.VariableStore

val variableStore = VariableStore()
var connection: ConnectionService? = null

@Composable
@Preview
fun App() {

    MaterialTheme {
        Row(
            Modifier.background(DefaultColors.backgroundColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (variableStore.connecting && !variableStore.connected) {
                ConnectionIndicator()
            } else if (!variableStore.connecting && variableStore.connected) {
                TableView(variableStore)
            } else {
                connectionForm(variableStore)
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RW-DDB"
    ) {
        App()
    }
}
