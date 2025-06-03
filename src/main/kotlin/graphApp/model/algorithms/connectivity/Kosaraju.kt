package graphApp.model.algorithms.connectivity

import graphApp.model.graph.DirectedEdge
import graphApp.model.graph.Edge
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex

object Kosaraju {
    fun <T : Edge> findStronglyConnectedComponents(graph: Graph<T>): List<List<Vertex>> {
        if (graph.vertices.isEmpty()) return emptyList()

        val directedGraph = createDirectedGraph(graph)

        val visited = mutableSetOf<Vertex>()
        val order = mutableListOf<Vertex>()

        directedGraph.vertices.forEach { vertex ->
            if (vertex !in visited) {
                dfsForward(vertex, directedGraph, visited, order)
            }
        }

        val transposedGraph = transposeGraph(directedGraph)
        visited.clear()

        val components = mutableListOf<List<Vertex>>()
        order.asReversed().forEach { vertex ->
            if (vertex !in visited) {
                val component = mutableListOf<Vertex>()
                dfsBackward(vertex, transposedGraph, visited, component)
                components.add(component)
            }
        }

        return components
    }

    private fun <T : Edge> createDirectedGraph(graph: Graph<T>): Graph<DirectedEdge> {
        val directedGraph = Graph<DirectedEdge>()

        graph.vertices.forEach { directedGraph.addVertex(it) }

        graph.edges.forEach { edge ->
            if (!edge.isDirected()) {
                directedGraph.addEdge(DirectedEdge(edge.from, edge.to))
                directedGraph.addEdge(DirectedEdge(edge.to, edge.from))
            } else {
                directedGraph.addEdge(DirectedEdge(edge.from, edge.to))
            }
        }

        return directedGraph
    }

    private fun transposeGraph(graph: Graph<DirectedEdge>): Graph<DirectedEdge> {
        val transposed = Graph<DirectedEdge>()

        graph.vertices.forEach { transposed.addVertex(it) }

        graph.edges.forEach { edge ->
            transposed.addEdge(DirectedEdge(edge.to, edge.from))
        }

        return transposed
    }

    private fun dfsForward(
        vertex: Vertex,
        graph: Graph<DirectedEdge>,
        visited: MutableSet<Vertex>,
        order: MutableList<Vertex>
    ) {
        visited.add(vertex)
        graph.edges
            .filter { it.from == vertex }
            .forEach { edge ->
                if (edge.to !in visited) {
                    dfsForward(edge.to, graph, visited, order)
                }
            }
        order.add(vertex)
    }

    private fun dfsBackward(
        vertex: Vertex,
        graph: Graph<DirectedEdge>,
        visited: MutableSet<Vertex>,
        component: MutableList<Vertex>
    ) {
        visited.add(vertex)
        component.add(vertex)
        graph.edges
            .filter { it.from == vertex }
            .forEach { edge ->
                if (edge.to !in visited) {
                    dfsBackward(edge.to, graph, visited, component)
                }
            }
    }
}