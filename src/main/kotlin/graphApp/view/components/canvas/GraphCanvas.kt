package graphApp.view.components.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.rememberTextMeasurer
import graphApp.model.graph.Edge
import graphApp.model.graph.Position
import graphApp.model.graph.Vertex
import graphApp.viewmodel.AlgorithmResult
import kotlinx.coroutines.delay

@Composable
fun GraphCanvas(
    vertices: Map<Vertex, Position>,
    edges: List<Edge>,
    algorithmResult: AlgorithmResult? = null,
    selectedVertex: Vertex? = null,
    firstVertexForMerge: Vertex? = null,
    dijkstraStart: Vertex? = null,
    dijkstraEnd: Vertex? = null,
    hoveredVertex: Vertex? = null,
    scale: Float = 1f,
    offset: Offset = Offset.Zero,
    modifier: Modifier = Modifier
) {
    var time by remember { mutableStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(Unit) {
        while (true) {
            delay(1)
            time += 1f
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        withTransform({
            scale(scale, scale)
            translate(offset.x, offset.y)
        }) {
            // Отрисовка рёбер
            edges.forEach { edge ->
                val fromPos = vertices[edge.from]
                val toPos = vertices[edge.to]
                if (fromPos != null && toPos != null) {
                    drawEdge(
                        textMeasurer = textMeasurer,
                        start = Offset(fromPos.x, fromPos.y),
                        end = Offset(toPos.x, toPos.y),
                        time = time,
                        isDirected = edge.isDirected(),
                        weight = edge.weight
                    )
                }
            }

            if (algorithmResult is AlgorithmResult.ConnectedComponents) {
                algorithmResult.components.forEachIndexed { index, component ->
                    val color = getVibrantColorForComponent(index)
                    component.forEach { vertex ->
                        vertices[vertex]?.let { pos ->
                            // Яркий фон компоненты
                            drawCircle(
                                color = color.copy(alpha = 0.2f),
                                radius = 45f,
                                center = Offset(pos.x, pos.y)
                            )

                            // Яркая обводка вершины
                            drawCircle(
                                color = color,
                                radius = 30f,
                                center = Offset(pos.x, pos.y),
                                style = Stroke(width = 4f)
                            )
                        }
                    }
                }
            }

            // Отрисовка вершин
            vertices.forEach { (vertex, pos) ->
                drawVertex(
                    vertex = vertex,
                    position = pos,
                    textMeasurer = textMeasurer,
                    color = getVertexColor(
                        vertex,
                        selectedVertex,
                        hoveredVertex,
                        firstVertexForMerge,
                        algorithmResult,
                        dijkstraStart,
                        dijkstraEnd
                    ),
                    isHovered = vertex == hoveredVertex
                )
            }

            if (algorithmResult is AlgorithmResult.ShortestPath) {
                drawPathHighlight(algorithmResult.path, vertices)
            }
        }
    }
}

