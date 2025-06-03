package graphApp

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import graphApp.view.MainView
import graphApp.viewmodel.GraphViewModel
import java.awt.Dimension

fun main() = application {
    var windows by remember { mutableStateOf(listOf(0 to GraphViewModel())) } // Инициализация первого окна сразу

    windows.forEach { (id, viewModel) ->
        Window(
            title = "Graph Visualizer ${id + 1}",
            onCloseRequest = {
                windows = windows.filterNot { it.first == id }
                if (windows.isEmpty()) exitApplication()
            }
        ) {

            LaunchedEffect(Unit){
                window.minimumSize = Dimension(1024, 768)
            }

            MainView(
                viewModel = viewModel,
                onNewWindow = { windows = windows + (windows.size to GraphViewModel()) },
                onCloseWindow = {
                    windows = windows.filterNot { it.first == id }
                    if (windows.isEmpty()) exitApplication()
                }
            )
        }
    }
}
