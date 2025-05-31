package graphApp.view.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import java.awt.FileDialog
import java.io.File

@Composable
fun FrameWindowScope.FileSaveDialog(onResult: (File?) -> Unit) {
    AwtWindow(
        create = {
            FileDialog(window, "Save Graph", FileDialog.SAVE).apply {
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
