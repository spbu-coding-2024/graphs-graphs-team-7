package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun GenerateGraphDialog(
    onGenerate: (GraphType, Int, Double, Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var graphType by remember { mutableStateOf(GraphType.RANDOM) }
    var vertexCount by remember { mutableStateOf("10") }
    var edgeProbability by remember { mutableStateOf("0.3") }
    var minWeight by remember { mutableStateOf("1.0") }
    var maxWeight by remember { mutableStateOf("10.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Graph") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Graph Type:", modifier = Modifier.padding(end = 8.dp))
                    RadioGroup(graphType) { graphType = it }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = vertexCount,
                    onValueChange = { vertexCount = it },
                    label = { Text("Vertex Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = edgeProbability,
                    onValueChange = { edgeProbability = it },
                    label = { Text("Edge Probability (0.0 - 1.0)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    TextField(
                        value = minWeight,
                        onValueChange = { minWeight = it },
                        label = { Text("Min Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = maxWeight,
                        onValueChange = { maxWeight = it },
                        label = { Text("Max Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val count = vertexCount.toIntOrNull() ?: 10
                    val prob = edgeProbability.toDoubleOrNull() ?: 0.3
                    val minW = minWeight.toDoubleOrNull() ?: 1.0
                    val maxW = maxWeight.toDoubleOrNull() ?: 10.0
                    onGenerate(graphType, count, prob, minW, maxW)
                }
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RadioGroup(
    selected: GraphType,
    onSelected: (GraphType) -> Unit
) {
    Row {
        GraphType.entries.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                RadioButton(
                    selected = type == selected,
                    onClick = { onSelected(type) }
                )
                Text(type.name, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

enum class GraphType {
    TREE,
    RANDOM,
    WEIGHTED_GRAPH
}

