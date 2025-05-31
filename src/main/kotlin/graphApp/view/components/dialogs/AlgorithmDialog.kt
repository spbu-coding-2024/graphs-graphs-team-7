package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import graphApp.model.algorithms.AlgorithmType

@Composable
fun AlgorithmDialog(
    onDismiss: () -> Unit,
    onAlgorithmSelected: (AlgorithmType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Algorithm") },
        text = {
            Column {
                AlgorithmType.entries.forEach { algorithm ->
                    Button(
                        onClick = {
                            onAlgorithmSelected(algorithm)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(algorithm.displayName)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
