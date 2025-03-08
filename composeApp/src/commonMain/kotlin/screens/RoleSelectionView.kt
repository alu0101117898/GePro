package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionView(
    onRoleSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = {
            onRoleSelected("observer")
        }) {
            Text("Observador")
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = {
            onRoleSelected("admin")
        }) {
            Text("Administrador")
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = {
            onRoleSelected("resource")
        }) {
            Text("Recurso")
        }
    }
}
