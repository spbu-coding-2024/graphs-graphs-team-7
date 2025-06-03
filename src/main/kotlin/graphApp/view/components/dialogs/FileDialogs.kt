package graphApp.view.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import java.awt.FileDialog
import java.io.File

@Composable
fun FrameWindowScope.FileSaveWithFormatDialog(onResult: (File?, String) -> Unit) {
    var selectedFormat by remember { mutableStateOf("json") }

    AlertDialog(
        onDismissRequest = { onResult(null, "") },
        title = { Text("Выберите формат") },
        text = {
            Column {
                Text("Сохранить граф в формате:")
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "json",
                        onClick = { selectedFormat = "json" }
                    )
                    Text("JSON", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = selectedFormat == "csv",
                        onClick = { selectedFormat = "csv" }
                    )
                    Text("CSV")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    FileDialog(window, "Сохранить граф", FileDialog.SAVE).apply {
                        directory = System.getProperty("user.home")
                        file = "graph.${selectedFormat}"
                        isVisible = true

                        val file = if (file != null) {
                            var fileName = file!!
                            if (!fileName.endsWith(".${selectedFormat}")) {
                                fileName += ".${selectedFormat}"
                            }
                            File(directory, fileName)
                        } else null

                        onResult(file, selectedFormat)
                    }
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult(null, "") }) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun FrameWindowScope.FileOpenDialog(onResult: (File?) -> Unit) {
    AwtWindow(
        create = {
            FileDialog(window, "Open Graph", FileDialog.LOAD).apply {
                directory = System.getProperty("user.home")
                isMultipleMode = false
            }
        },
        dispose = FileDialog::dispose
    ) { dialog ->
        dialog.isVisible = true
        val file = if (dialog.file != null) File(dialog.directory, dialog.file) else null
        onResult(file)
    }
}

@Composable
fun OpenFormatDialog(
    onResult: (String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf("json") }

    AlertDialog(
        onDismissRequest = { onResult("") },
        title = { Text("Выберите формат") },
        text = {
            Column {
                Text("Открыть граф в формате:")
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFormat == "json",
                        onClick = { selectedFormat = "json" }
                    )
                    Text("JSON", modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = selectedFormat == "csv",
                        onClick = { selectedFormat = "csv" }
                    )
                    Text("CSV")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onResult(selectedFormat) }
            ) {
                Text("Открыть")
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult("") }) {
                Text("Отмена")
            }
        }
    )
}