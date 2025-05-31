package graphApp.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import graphApp.model.algorithms.AlgorithmType
import graphApp.model.algorithms.connectivity.Kosaraju
import graphApp.model.algorithms.layout.ForceAtlas2
import graphApp.model.algorithms.shortestpath.Dijkstra
import graphApp.model.graph.*
import graphApp.model.graph.serialization.SerializableGraph
import graphApp.view.components.dialogs.GraphType
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class GraphViewModel {
    private val _graph = mutableStateOf<Graph<Edge>?>(null)
    val graph: State<Graph<Edge>?> get() = _graph

    // Флаги для отслеживания состояния
    val canUndo get() = _undoStack.isNotEmpty()
    val canRedo get() = _redoStack.isNotEmpty()
    private val _undoStack = mutableStateListOf<GraphState>()
    private val _redoStack = mutableStateListOf<GraphState>()
    private val _showUndoRedoEffect = mutableStateOf(false)
    val showUndoRedoEffect: State<Boolean> get() = _showUndoRedoEffect

    private val _uiState = mutableStateOf<UiState>(UiState.Idle)

    private val layoutAlgorithm = ForceAtlas2()

    var selectedStart by mutableStateOf<Vertex?>(null)
    var selectedEnd by mutableStateOf<Vertex?>(null)

    var scale by mutableStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)

    private val _algorithmResult = mutableStateOf<AlgorithmResult?>(null)
    val algorithmResult: State<AlgorithmResult?> get() = _algorithmResult

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var vertexCounter = 0

    fun setAlgorithmResult(result: AlgorithmResult?) {
        _algorithmResult.value = result
    }

    init {
        saveState()
    }

    private fun saveState() {
        _undoStack.add(
            GraphState(
                graph = _graph.value?.copy(),
                vertexCounter = vertexCounter,
                scale = scale,
                offset = offset
            )
        )
        _redoStack.clear()
    }

    fun undo() {
        if (canUndo) {
            // Сохраняем текущее состояние в стек повтора
            _redoStack.add(
                GraphState(
                    graph = _graph.value?.copy(),
                    vertexCounter = vertexCounter,
                    scale = scale,
                    offset = offset
                )
            )

            // Восстанавливаем предыдущее состояние
            val state = _undoStack.removeLast()
            restoreState(state)
            _showUndoRedoEffect.value = true
            coroutineScope.launch {
                delay(500)
                _showUndoRedoEffect.value = false
            }
        }
    }

    fun redo() {
        if (canRedo) {
            // Сохраняем текущее состояние в стек отмены
            _undoStack.add(
                GraphState(
                    graph = _graph.value?.copy(),
                    vertexCounter = vertexCounter,
                    scale = scale,
                    offset = offset
                )
            )

            // Восстанавливаем состояние из стека повтора
            val state = _redoStack.removeLast()
            restoreState(state)
            _showUndoRedoEffect.value = true
            coroutineScope.launch {
                delay(500)
                _showUndoRedoEffect.value = false
            }
        }
    }

    private fun restoreState(state: GraphState) {
        _graph.value = state.graph
        vertexCounter = state.vertexCounter
        scale = state.scale
        offset = state.offset
    }

    private fun initializeGraphIfNeeded() {
        if (_graph.value == null) {
            _graph.value = Graph()
        }
    }

    private fun generateVertexId(): String {
        val existingIds = _graph.value?.vertices?.mapNotNull {
            it.id.removePrefix("V").toIntOrNull()
        }?.toMutableList() ?: mutableListOf()
        val newId = if (existingIds.isEmpty()) 1 else existingIds.max() + 1
        return "V$newId"
    }

    fun addVertexAtPosition(x: Float, y: Float) {
        saveState()
        initializeGraphIfNeeded()
        val vertex = Vertex(generateVertexId())
        _graph.value?.apply {
            addVertex(vertex)
            setPosition(vertex, x, y)

            coroutineScope.launch {
                for (i in 1..10) {
                    delay(10)
                    setPosition(vertex, x, y - i * 2f)
                }
                for (i in 10 downTo 0) {
                    delay(10)
                    setPosition(vertex, x, y - i * 2f)
                }
            }
            selectedStart = null
            selectedEnd = null
        }
    }

    fun moveVertex(vertex: Vertex, dx: Float, dy: Float) {
        val scaledDx = dx / scale
        val scaledDy = dy / scale
        _graph.value?.positions?.get(vertex)?.let { pos ->
            _graph.value?.setPosition(
                vertex,
                pos.x + scaledDx,
                pos.y + scaledDy
            )
            _graph.value = _graph.value?.copy()
        }
    }

    fun findVertexAt(x: Float, y: Float): Vertex? {
        return _graph.value?.positions?.entries?.firstOrNull { (_, pos) ->
            val dx = pos.x - x
            val dy = pos.y - y
            sqrt(dx * dx + dy * dy) < 20
        }?.key
    }

    fun generateGraph(
        type: GraphType,
        vertexCount: Int,
        edgeProbability: Double,
        minWeight: Double,
        maxWeight: Double
    ) {
        when (type) {
            GraphType.TREE -> generateTree(vertexCount, minWeight, maxWeight)
            GraphType.RANDOM -> generateRandomGraph(vertexCount, edgeProbability, minWeight, maxWeight)
            GraphType.WEIGHTED_GRAPH -> generateWeightedGraph(vertexCount, edgeProbability, minWeight, maxWeight)
        }
        centerAndScaleGraph()
    }

    private fun generateWeightedGraph(
        vertexCount: Int,
        edgeProbability: Double,
        minWeight: Double,
        maxWeight: Double
    ) {
        saveState()
        clearGraph()

        _graph.value = Graph<Edge>().apply {
            // Создание вершин
            repeat(vertexCount) { i ->
                val vertex = Vertex("V${i + 1}")
                addVertex(vertex)
                setPosition(
                    vertex,
                    Random.nextInt(100, 700).toFloat(),
                    Random.nextInt(100, 600).toFloat()
                )
            }

            // Создание ненаправленных взвешенных рёбер
            val verticesList = vertices.toList()
            for (i in verticesList.indices) {
                for (j in i + 1 until verticesList.size) {
                    if (Random.nextDouble() < edgeProbability) {
                        val weight = Random.nextDouble(minWeight, maxWeight)
                        addEdge(WeightedEdge(verticesList[i], verticesList[j], weight))
                        addEdge(WeightedEdge(verticesList[j], verticesList[i], weight))
                    }
                }
            }
        }
        centerAndScaleGraph()
    }

    private fun generateTree(vertexCount: Int, minWeight: Double, maxWeight: Double) {
        saveState()
        clearGraph()

        _graph.value = Graph<Edge>().apply {
            // Создание вершин
            repeat(vertexCount) { i ->
                val vertex = Vertex("V${i + 1}")
                addVertex(vertex)
                setPosition(
                    vertex,
                    Random.nextInt(100, 700).toFloat(),
                    Random.nextInt(100, 600).toFloat()
                )
            }

            val verticesList = vertices.toList()
            if (verticesList.isEmpty()) return@apply

            val root = verticesList.first()
            val visited = mutableSetOf(root)
            val queue = ArrayDeque<Vertex>().apply { add(root) }

            while (visited.size < verticesList.size) {
                val current = queue.removeFirstOrNull() ?: break
                val remaining = verticesList.size - visited.size
                val maxChildren = min(2, remaining)

                verticesList
                    .filter { it !in visited }
                    .shuffled()
                    .take((1..maxChildren).random())
                    .forEach { child ->
                        val weight = Random.nextDouble(minWeight, maxWeight)

                        val edge = if (Random.nextBoolean()) {
                            DirectedWeightedEdge(from = current, to = child, weight)
                        } else {
                            DirectedWeightedEdge(from = child, to = current, weight)
                        }

                        addEdge(edge)
                        visited.add(child)
                        queue.add(child)
                    }
            }

            verticesList
                .filter { it !in visited }
                .forEach { child ->
                    val weight = Random.nextDouble(minWeight, maxWeight)

                    val edge = if (Random.nextBoolean()) {
                        DirectedWeightedEdge(from = root, to = child, weight)
                    } else {
                        DirectedWeightedEdge(from = child, to = root, weight)
                    }

                    addEdge(edge)
                }
        }
        centerAndScaleGraph()
    }

    private fun generateRandomGraph(
        vertexCount: Int,
        edgeProbability: Double,
        minWeight: Double,
        maxWeight: Double
    ) {
        saveState()
        clearGraph()

        _graph.value = Graph<Edge>().apply {
            repeat(vertexCount) { i ->
                val vertex = Vertex("V${i + 1}")
                addVertex(vertex)
                setPosition(
                    vertex,
                    Random.nextInt(100, 700).toFloat(),
                    Random.nextInt(100, 600).toFloat()
                )
            }

            val verticesList = vertices.toList()
            for (i in verticesList.indices) {
                for (j in verticesList.indices) {
                    if (i != j && Random.nextDouble() < edgeProbability) {
                        val weight = Random.nextDouble(minWeight, maxWeight)
                        addEdge(DirectedWeightedEdge(verticesList[i], verticesList[j], weight))
                    }
                }
            }
        }
        centerAndScaleGraph()
    }

    fun addEdge(from: Vertex, to: Vertex, weight: Double = 1.0, isDirected: Boolean = false) {
        saveState()
        _graph.value?.let {
            val edge = if (isDirected) {
                DirectedWeightedEdge(from, to, weight)
            } else {
                WeightedEdge(from, to, weight)
            }

            if (!it.edges.any { e ->
                    e.from == from && e.to == to ||
                            (!isDirected && e.from == to && e.to == from)
                }) {
                it.addEdge(edge)
            }
        }
    }

    fun clearGraph() {
        _algorithmResult.value = null
        saveState()
        _graph.value = Graph()
        selectedStart = null
        selectedEnd = null
    }

    fun runAlgorithm(algorithm: AlgorithmType) {
        saveState()
        val currentGraph = _graph.value ?: run {
            _uiState.value = UiState.Error("Graph is empty")
            return
        }

        _uiState.value = UiState.Loading
        _algorithmResult.value = null

        coroutineScope.launch(Dispatchers.Default) {
            try {
                val result = when (algorithm) {
                    AlgorithmType.DIJKSTRA -> handleDijkstra(currentGraph)
                    AlgorithmType.KOSARAJU -> handleKosaraju(currentGraph)
                    AlgorithmType.FORCEATLAS2 -> {
                        applyForceLayout()
                        AlgorithmResult.MessageResult("Layout applied")
                    }
                }
                withContext(Dispatchers.Main) {
                    _algorithmResult.value = result
                    _uiState.value = UiState.Success("${algorithm.name} executed")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = UiState.Error(e.message ?: "Algorithm failed")
                }
            }
        }
    }

    private fun applyForceLayout() {
        _algorithmResult.value = null
        _graph.value?.let { graph ->
            val tempGraph = graph.copy()

            layoutAlgorithm.applyLayout(tempGraph)

            tempGraph.positions.forEach { (vertex, position) ->
                graph.setPosition(vertex, position.x, position.y)
            }

            _graph.value = graph.copy()

            _uiState.value = UiState.Success("Layout applied")
        } ?: run {
            _uiState.value = UiState.Error("Graph is empty")
        }
    }

    private fun centerAndScaleGraph() {
        val positions = _graph.value?.positions ?: return
        if (positions.isEmpty()) return

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        positions.values.forEach { pos ->
            minX = minOf(minX, pos.x)
            maxX = maxOf(maxX, pos.x)
            minY = minOf(minY, pos.y)
            maxY = maxOf(maxY, pos.y)
        }

        val width = maxX - minX
        val height = maxY - minY
        val maxDim = maxOf(width, height, 1f)

        // Автоматический подбор масштаба
        scale = minOf(1f, 800f / maxDim)

        // Центрирование
        val centerX = (minX + maxX) / 2
        val centerY = (minY + maxY) / 2

        positions.forEach { (vertex, pos) ->
            _graph.value?.setPosition(
                vertex,
                (pos.x - centerX) * scale,
                (pos.y - centerY) * scale
            )
        }
    }


    fun handlePan(dx: Float, dy: Float) {
        saveState()
        offset = Offset(offset.x + dx, offset.y + dy)
    }

    fun handleZoom(zoomDelta: Float) {
        saveState()
        val newScale = (scale * (1 + zoomDelta)).coerceIn(0.01f, 3f)
        scale = newScale
    }

    private fun handleDijkstra(currentGraph: Graph<Edge>): AlgorithmResult {
        // Проверка выбора вершин
        if (selectedStart == null || selectedEnd == null) {
            return AlgorithmResult.MessageResult("Select start and end vertices")
        }

        val start = selectedStart!!
        val end = selectedEnd!!

        val result = Dijkstra.findShortestPath(currentGraph, start, end)
        return if (result != null) {
            AlgorithmResult.ShortestPath(
                path = result.path,
                distance = result.distance.coerceAtLeast(0.0) // Защита от NaN
            )
        } else {
            AlgorithmResult.MessageResult("Путь не найден")
        }
    }

    private fun handleKosaraju(graph: Graph<Edge>): AlgorithmResult {
        return AlgorithmResult.ConnectedComponents(
            Kosaraju.findStronglyConnectedComponents(graph)
        )
    }

    fun exportToJson(): String {
        return Json.encodeToString(graph.value?.toSerializable())
    }

    fun importFromJson(json: String) {
        try {
            val serialized = Json.decodeFromString<SerializableGraph>(json)
            _graph.value = Graph.fromSerializable(serialized)
            _graph.value?.positions?.forEach { (v, pos) ->
                _graph.value?.setPosition(v, pos.x, pos.y)
            }
            val maxId = _graph.value?.vertices?.mapNotNull {
                it.id.removePrefix("V").toIntOrNull()
            }?.maxOrNull() ?: 0

            vertexCounter = maxId
        } catch (e: Exception) {
            println("Ошибка десериализации: ${e.message}")
        }
    }
}
