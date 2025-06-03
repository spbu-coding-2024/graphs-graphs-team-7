package graphApp.view

// Для алгоритма Дейкстры

sealed class SelectionMode {
    object NONE : SelectionMode()
    object DIJKSTRA_START : SelectionMode()
    object DIJKSTRA_END : SelectionMode()
}
