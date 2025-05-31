package graphApp.view.components.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import graphApp.model.graph.Vertex
import graphApp.view.components.buttons.CustomButton

@Composable
fun ControlPanel(
    onNewWindow: () -> Unit,
    onAddVertex: () -> Unit,
    onAddEdge: () -> Unit,
    onAlgorithms: () -> Unit,
    onClear: () -> Unit,
    onMerge: () -> Unit,
    onSetDijkstraStart: () -> Unit,
    onSetDijkstraEnd: () -> Unit,
    dijkstraStart: Vertex?,
    dijkstraEnd: Vertex?,
    onGenerate : () -> Unit,
    onHelp: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    mergeMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomButton(
            text = "New Window",
            onClick = onNewWindow
        )
        CustomButton("Generate", onGenerate)
        CustomButton("Add Vertex", onAddVertex)
        CustomButton("Add Edge",onAddEdge)
        CustomButton("Algorithms", onAlgorithms)
        Button(
            onClick = onHelp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Управление")
        }
        Button(
            onClick = onMerge,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (mergeMode) Color.LightGray else MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Merge Vertices (select 2)")
        }
        Button(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        ) {
            Text("Clear Graph")
        }
        Text("Dijkstra Settings", style = MaterialTheme.typography.titleSmall)
        Button(
            onClick = onSetDijkstraStart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (dijkstraStart != null) Color.Green.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Set Start: ${dijkstraStart?.id ?: "Not selected"}")
        }
        Button(
            onClick = onSetDijkstraEnd,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (dijkstraEnd != null) Color.Red.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Set End: ${dijkstraEnd?.id ?: "Not selected"}")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.weight(1f).background(
                    if (canUndo) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
            }
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.weight(1f).background(
                    if (canRedo) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}