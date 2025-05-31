package graphApp.viewmodel

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Success(
        val message: String,
        val data: Any? = null
    ) : UiState()
    data class Error(val message: String) : UiState()
}
