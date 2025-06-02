package graphApp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import graphApp.model.algorithms.AlgorithmType
import graphApp.model.graph.Vertex
import graphApp.view.components.canvas.GraphCanvas
import graphApp.view.components.dialogs.AlgorithmDialog
import graphApp.view.components.dialogs.FileOpenDialog
import graphApp.view.components.dialogs.FileSaveDialog
import graphApp.view.components.dialogs.GenerateGraphDialog
import graphApp.view.components.panels.ControlPanel
import graphApp.view.themes.MyAppTheme
import graphApp.viewmodel.AlgorithmResult
import graphApp.viewmodel.GraphViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrameWindowScope.MainView(viewModel: GraphViewModel, onNewWindow: () -> Unit, onCloseWindow: () -> Unit) {
    // Состояния
    var darkTheme by remember { mutableStateOf(false) }
    var showAlgorithmDialog by remember { mutableStateOf(false) }
    var showEdgeDialog by remember { mutableStateOf(false) }
    var selectedVertex by remember { mutableStateOf<Vertex?>(null) }
    var mergeMode by remember { mutableStateOf(false) }
    var showOpenDialog by remember { mutableStateOf(false) }
    var openDialogShown by remember { mutableStateOf(false) }
    var saveDialogShown by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showGenerateDialog by remember { mutableStateOf(false) }
    var showVertexWeightDialog by remember { mutableStateOf<Vertex?>(null) }
    var hoveredVertex by remember { mutableStateOf<Vertex?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var isPanning by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var mergeSourceVertex by remember { mutableStateOf<Vertex?>(null) }
    var dijkstraStart by remember { mutableStateOf<Vertex?>(null) }
    var dijkstraEnd by remember { mutableStateOf<Vertex?>(null) }
    var selectionMode by remember { mutableStateOf<SelectionMode>(SelectionMode.NONE) }

    // Состояния для V-режима
    var vMode by remember { mutableStateOf(false) }
    var vFirstVertex by remember { mutableStateOf<Vertex?>(null) }
    var vHintVisible by remember { mutableStateOf(false) }
    var vHintMessage by remember { mutableStateOf("") }

    // Фокус для обработки клавиатуры
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    // Диалоги
    if (showSaveDialog && !saveDialogShown) {
        saveDialogShown = true
        FileSaveDialog { file: File? ->
            file?.let {
                try {
                    val json = viewModel.exportToJson()
                    it.writeText(json)
                } catch (e: Exception) {
                    println("Ошибка сохранения: ${e.message}")
                }
            }
            showSaveDialog = false
            saveDialogShown = false
        }
    }

    if (showOpenDialog && !openDialogShown) {
        openDialogShown = true
        FileOpenDialog { file: File? ->
            file?.let {
                try {
                    val json = it.readText()
                    viewModel.importFromJson(json)
                } catch (e: Exception) {
                    println("Ошибка загрузки: ${e.message}")
                }
            }
            showOpenDialog = false
            openDialogShown = false
        }
    }

    if (showEdgeDialog) {
        var fromVertex by remember { mutableStateOf<Vertex?>(null) }
        var toVertex by remember { mutableStateOf<Vertex?>(null) }
        var weight by remember { mutableStateOf("1.0") }
        var isDirected by remember { mutableStateOf(false) }
        val vertices = viewModel.graph.value?.vertices?.toList() ?: emptyList()

        AlertDialog(
            onDismissRequest = { showEdgeDialog = false },
            title = { Text("Add Edge") },
            text = {
                Column {
                    var fromExpanded by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { fromExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(fromVertex?.id ?: "Select start vertex")
                        }
                        DropdownMenu(
                            expanded = fromExpanded,
                            onDismissRequest = { fromExpanded = false }
                        ) {
                            vertices.forEach { vertex ->
                                DropdownMenuItem(
                                    text = { Text(vertex.id) },
                                    onClick = {
                                        fromVertex = vertex
                                        fromExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var toExpanded by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { toExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(toVertex?.id ?: "Select end vertex")
                        }
                        DropdownMenu(
                            expanded = toExpanded,
                            onDismissRequest = { toExpanded = false }
                        ) {
                            vertices.forEach { vertex ->
                                DropdownMenuItem(
                                    text = { Text(vertex.id) },
                                    onClick = {
                                        toVertex = vertex
                                        toExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isDirected,
                            onCheckedChange = { isDirected = it }
                        )
                        Text("Directed edge")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (fromVertex != null && toVertex != null) {
                            val weightValue = weight.toDoubleOrNull() ?: 1.0

                            if (isDirected) {
                                viewModel.addEdge(fromVertex!!, toVertex!!, weightValue, true)
                            } else {
                                viewModel.addEdge(fromVertex!!, toVertex!!, weightValue)
                                viewModel.addEdge(toVertex!!, fromVertex!!, weightValue)
                            }
                            showEdgeDialog = false
                        }
                    },
                    enabled = fromVertex != null && toVertex != null && weight.isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEdgeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showGenerateDialog) {
        GenerateGraphDialog(
            onGenerate = { type, vertexCount, edgeProbability, minWeight, maxWeight ->
                viewModel.generateGraph(type, vertexCount, edgeProbability, minWeight, maxWeight)
                showGenerateDialog = false
            },
            onDismiss = { showGenerateDialog = false }
        )
    }

    MyAppTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Graph Visualizer") },
                    navigationIcon = {
                        Row {
                            var windowMenuExpanded by remember { mutableStateOf(false) }
                            IconButton(onClick = { windowMenuExpanded = true }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Window menu")
                            }
                            DropdownMenu(
                                expanded = windowMenuExpanded,
                                onDismissRequest = { windowMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("New Window") },
                                    onClick = {
                                        windowMenuExpanded = false
                                        onNewWindow()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Exit") },
                                    onClick = {
                                        windowMenuExpanded = false
                                        onCloseWindow()
                                    }
                                )
                            }
                        }
                    },
                    actions = {
                        var editMenuExpanded by remember { mutableStateOf(false) }
                        var fileMenuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { fileMenuExpanded = true }) {
                            Icon(Icons.Filled.Folder, contentDescription = "File menu")
                        }
                        DropdownMenu(
                            expanded = fileMenuExpanded,
                            onDismissRequest = { fileMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Save") },
                                onClick = {
                                    showSaveDialog = true
                                    fileMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Open") },
                                onClick = {
                                    showOpenDialog = true
                                    fileMenuExpanded = false
                                }
                            )
                        }
                        Box {
                            IconButton(onClick = { editMenuExpanded = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            DropdownMenu(
                                expanded = editMenuExpanded,
                                onDismissRequest = { editMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Undo (Ctrl+Z)") },
                                    onClick = {
                                        viewModel.undo()
                                        editMenuExpanded = false
                                    },
                                    enabled = viewModel.canUndo
                                )
                                DropdownMenuItem(
                                    text = { Text("Redo (Ctrl+Y)") },
                                    onClick = {
                                        viewModel.redo()
                                        editMenuExpanded = false
                                    },
                                    enabled = viewModel.canRedo
                                )
                            }
                        }
                        IconButton(onClick = { darkTheme = !darkTheme }) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    }
                )
            },
            content = { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(if (mergeMode) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent)

                            // Обработка позиции курсора
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val position = event.changes.first().position
                                        val graphX = (position.x - viewModel.offset.x) / viewModel.scale
                                        val graphY = (position.y - viewModel.offset.y) / viewModel.scale
                                        hoveredVertex = viewModel.findVertexAt(graphX, graphY)
                                    }
                                }
                            }

                            // Обработка масштабирования колесиком мыши
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Scroll) {
                                            val scrollDelta = event.changes.first().scrollDelta.y
                                            viewModel.handleZoom(scrollDelta * 0.05f)
                                        }
                                    }
                                }
                            }

                            // Обработка жестов трансформации
                            .pointerInput(Unit) {
                                detectTransformGestures(
                                    onGesture = { _, pan, zoom, _ ->
                                        viewModel.handleZoom(zoom - 1)
                                        viewModel.handlePan(pan.x, pan.y)
                                    }
                                )
                            }

                            // Обработка перетаскивания
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val graphX = (offset.x - viewModel.offset.x) / viewModel.scale
                                        val graphY = (offset.y - viewModel.offset.y) / viewModel.scale
                                        val vertex = viewModel.findVertexAt(graphX, graphY)

                                        if (vertex != null) {
                                            selectedVertex = vertex
                                            isDragging = true
                                        } else {
                                            isPanning = true
                                        }
                                    },
                                    onDrag = { _, dragAmount ->
                                        if (isDragging) {
                                            selectedVertex?.let { vertex ->
                                                viewModel.moveVertex(vertex, dragAmount.x, dragAmount.y)
                                            }
                                        } else if (isPanning) {
                                            viewModel.handlePan(dragAmount.x, dragAmount.y)
                                        }
                                    },
                                    onDragEnd = {
                                        isDragging = false
                                        isPanning = false
                                        selectedVertex = null
                                    }
                                )
                            }

                            // Обработка клавиш
                            .onKeyEvent { event ->
                                when {
                                    // Активация V-режима
                                    event.key == Key.V && event.type == KeyEventType.KeyDown -> {
                                        vMode = true
                                        vHintVisible = true
                                        vHintMessage = "Режим соединения: выберите первую вершину (Esc - отмена)"
                                        true
                                    }

                                    // Отмена V-режима
                                    event.key == Key.Escape && vMode -> {
                                        vMode = false
                                        vFirstVertex = null
                                        vHintVisible = false
                                        true
                                    }

                                    // Обработка Ctrl+Z
                                    event.isCtrlPressed && event.key == Key.Z -> {
                                        viewModel.undo()
                                        true
                                    }

                                    // Обработка Ctrl+Y
                                    event.isCtrlPressed && event.key == Key.Y -> {
                                        viewModel.redo()
                                        true
                                    }
                                    else -> false
                                }
                            }

                            // Обработка кликов
                            .pointerInput(vMode, mergeMode) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        // Запрос фокуса при клике
                                        focusRequester.requestFocus()

                                        val graphX = (offset.x - viewModel.offset.x) / viewModel.scale
                                        val graphY = (offset.y - viewModel.offset.y) / viewModel.scale
                                        val vertex = viewModel.findVertexAt(graphX, graphY)

                                        when (selectionMode) {
                                            SelectionMode.DIJKSTRA_START -> {
                                                dijkstraStart = vertex
                                                selectionMode = SelectionMode.NONE
                                            }

                                            SelectionMode.DIJKSTRA_END -> {
                                                dijkstraEnd = vertex
                                                selectionMode = SelectionMode.NONE
                                            }
                                            SelectionMode.NONE -> {}
                                        }

                                        // Обработка V-режима
                                        if (vMode) {
                                            if (vertex != null) {
                                                if (vFirstVertex == null) {
                                                    // Выбираем первую вершину
                                                    vFirstVertex = vertex
                                                    vHintMessage = "Режим соединения: выберите вторую вершину (Esc - отмена)"
                                                } else if (vertex != vFirstVertex) {
                                                    // Соединяем вершины
                                                    viewModel.addEdge(vFirstVertex!!, vertex)
                                                    // Сбрасываем режим
                                                    vMode = false
                                                    vFirstVertex = null
                                                    vHintVisible = false
                                                } else {
                                                    // Клик на ту же вершину - сбрасываем выбор
                                                    vFirstVertex = null
                                                    vHintMessage = "Режим соединения: выберите первую вершину"
                                                }
                                            } else {
                                                // Клик по пустому месту
                                                if (vFirstVertex != null) {
                                                    // Сбрасываем выбранную вершину
                                                    vFirstVertex = null
                                                    vHintMessage = "Режим соединения: выберите первую вершину"
                                                } else {
                                                    // Отменяем V-режим
                                                    vMode = false
                                                    vHintVisible = false
                                                }
                                            }
                                        }
                                        // Обработка режима кнопки Merge
                                        else if (mergeMode) {
                                            if (vertex != null) {
                                                if (mergeSourceVertex == null) {
                                                    mergeSourceVertex = vertex
                                                } else if (vertex != mergeSourceVertex) {
                                                    viewModel.addEdge(mergeSourceVertex!!, vertex)
                                                    mergeSourceVertex = null
                                                    mergeMode = false
                                                }
                                            }
                                        }
                                        // Обычная обработка клика
                                        else {
                                            selectedVertex = vertex
                                        }
                                    },
                                    onDoubleTap = { offset ->
                                        val graphX = (offset.x - viewModel.offset.x) / viewModel.scale
                                        val graphY = (offset.y - viewModel.offset.y) / viewModel.scale
                                        val vertex = viewModel.findVertexAt(graphX, graphY)
                                        if (vertex != null) {
                                            showVertexWeightDialog = vertex
                                        }
                                    }
                                )
                            }

                            // Обработка правой кнопки мыши - создание вершины при ЛКМ + ПКМ
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Release && event.buttons.isSecondaryPressed) {
                                            val position = event.changes.first().position
                                            val graphX = (position.x - viewModel.offset.x) / viewModel.scale
                                            val graphY = (position.y - viewModel.offset.y) / viewModel.scale
                                            viewModel.addVertexAtPosition(graphX, graphY)
                                            event.changes.first().consume()
                                        }
                                    }
                                }
                            }

                            // Фокус для обработки клавиатуры
                            .focusRequester(focusRequester)
                            .onFocusChanged { state ->
                                isFocused = state.isFocused
                            }
                            .focusable()
                    ) {
                        val graph = viewModel.graph.value
                        GraphCanvas(
                            vertices = graph?.positions ?: emptyMap(),
                            edges = graph?.getAllEdges() ?: emptyList(),
                            algorithmResult = viewModel.algorithmResult.value,
                            selectedVertex = selectedVertex,
                            hoveredVertex = hoveredVertex,
                            firstVertexForMerge = vFirstVertex,
                            scale = viewModel.scale,
                            offset = viewModel.offset,
                            dijkstraStart = dijkstraStart,
                            dijkstraEnd = dijkstraEnd,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Подсказка для V-режима
                        if (vHintVisible) {
                            Text(
                                text = vHintMessage,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(16.dp)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(8.dp),
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        viewModel.algorithmResult.value?.let { result ->
                            val message = when (result) {
                                is AlgorithmResult.ShortestPath ->
                                    "Кратчайший путь: ${result.path.size} вершин, вес: ${"%.2f".format(result.distance)}"
                                is AlgorithmResult.ConnectedComponents ->
                                    "Найдено компонент сильной связности: ${result.components.size}"
                                is AlgorithmResult.MessageResult ->
                                    result.message
                            }
                            Text(
                                text = message,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(16.dp)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(8.dp),
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }

                        ControlPanel(
                            onAddVertex = { viewModel.addVertexAtPosition(0f, 0f) },
                            onAddEdge = { showEdgeDialog = true },
                            onAlgorithms = { showAlgorithmDialog = true },
                            onClear = { viewModel.clearGraph() },
                            onMerge = { mergeMode = !mergeMode },
                            onGenerate = { showGenerateDialog = true },
                            mergeMode = mergeMode,
                            onHelp = { showHelpDialog = true },
                            onUndo = { viewModel.undo() },
                            onRedo = { viewModel.redo() },
                            canUndo = viewModel.canUndo,
                            canRedo = viewModel.canRedo,
                            onSetDijkstraStart = { selectionMode = SelectionMode.DIJKSTRA_START },
                            onSetDijkstraEnd = { selectionMode = SelectionMode.DIJKSTRA_END },
                            dijkstraStart = dijkstraStart,
                            dijkstraEnd = dijkstraEnd,
                            modifier = Modifier
                                .width(200.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        )

                        // Эффект отмены/повтора
                        if (viewModel.showUndoRedoEffect.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Green.copy(alpha = 0.2f))
                            )
                        }

                        // Элементы управления масштабом
                        ZoomControls(
                            scale = viewModel.scale,
                            onZoomIn = { viewModel.handleZoom(0.1f) },
                            onZoomOut = { viewModel.handleZoom(-0.1f) },
                            onReset = {
                                viewModel.scale = 1f
                                viewModel.offset = Offset.Zero
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        )
                    }
                }
            }
        )

        // Диалоговые окна
        if (showAlgorithmDialog) {
            AlgorithmDialog(
                onDismiss = { showAlgorithmDialog = false },
                onAlgorithmSelected = { algorithm ->
                    when (algorithm) {
                        AlgorithmType.DIJKSTRA -> {
                            if (dijkstraStart == null || dijkstraEnd == null) {
                                // Используем метод viewModel вместо прямого доступа
                                viewModel.setAlgorithmResult(
                                    AlgorithmResult.MessageResult(
                                        "Select start and end vertices for Dijkstra"
                                    )
                                )
                            } else {
                                viewModel.selectedStart = dijkstraStart
                                viewModel.selectedEnd = dijkstraEnd
                                viewModel.runAlgorithm(algorithm)
                            }
                        }
                        else -> viewModel.runAlgorithm(algorithm)
                    }
                    showAlgorithmDialog = false
                }
            )
        }

        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("Управление") },
                text = {
                    Column {
                        Text("• V: активирует режим соединения вершин")
                        Text("• Esc: отменяет текущее действие")
                        Text("• Двойной клик: изменить вес вершины")
                        Text("• Add Vertex или ЛКМ + ПКМ: добавить вершину")
                        Text("• ЛКМ по вершине: перемещение вершин")
                        Text("• Колёсико: масштабирование")
                        Text("• Ctrl+Z/Ctrl+Y и кнопки снизу: отмена/повтор действий")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ZoomControls(
    scale: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${(scale * 100).toInt()}%",
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        IconButton(onClick = onZoomOut) {
            Icon(Icons.Default.Remove, "Zoom Out")
        }
        IconButton(onClick = onReset) {
            Icon(Icons.Default.Refresh, "Reset Zoom")
        }
        IconButton(onClick = onZoomIn) {
            Icon(Icons.Default.Add, "Zoom In")
        }
    }
}
