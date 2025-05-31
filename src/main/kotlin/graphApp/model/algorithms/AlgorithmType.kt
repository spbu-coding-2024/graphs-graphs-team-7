package graphApp.model.algorithms

enum class AlgorithmType(val displayName: String) {
    DIJKSTRA("Dijkstra's algorithm"),
    FORCEATLAS2("ForceAtlas2"),
    KOSARAJU("searching for components of strong connectivity")
}