package graphApp.viewmodel

import graphApp.model.graph.Vertex

sealed class AlgorithmResult {
    data class ShortestPath(val path: List<Vertex>, val distance: Double) : AlgorithmResult()
    data class ConnectedComponents(val components: List<List<Vertex>>) : AlgorithmResult()
    data class MessageResult(val message: String) : AlgorithmResult()
}

