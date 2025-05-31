package graphApp

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import graphApp.view.MainView
import graphApp.viewmodel.GraphViewModel

fun main() = application {
    var windows by remember { mutableStateOf(1) }

    repeat(windows) { index ->
        Window(
            title = "Graph Visualizer ${index + 1}",
            onCloseRequest = { if (windows > 1) windows-- else exitApplication() }
        ) {
            val viewModel = remember { GraphViewModel() }
            MainView(viewModel)
        }
    }

    Window(
        title = "Controller",
        onCloseRequest = ::exitApplication
    ) {
        Button(onClick = { windows++ }) {
            Text("Open New Window")
        }
    }
}