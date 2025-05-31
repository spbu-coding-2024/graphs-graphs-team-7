import graphApp.viewmodel.UiState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UiStateTest {

    @Test
    fun `Idle state should be a singleton`() {
        val idle1 = UiState.Idle
        val idle2 = UiState.Idle

        assertSame(idle1, idle2)
    }

    @Test
    fun `Loading state should be a singleton`() {
        val loading1 = UiState.Loading
        val loading2 = UiState.Loading

        assertSame(loading1, loading2)
    }

    @Test
    fun `Success state should store message and optional data`() {
        val success = UiState.Success("Operation completed", 42)

        assertEquals("Operation completed", success.message)
        assertEquals(42, success.data)
    }

    @Test
    fun `Success states with the same message and data should be equal`() {
        val success1 = UiState.Success("Operation completed", 42)
        val success2 = UiState.Success("Operation completed", 42)

        assertEquals(success1, success2)
        assertEquals(success1.hashCode(), success2.hashCode())
    }

    @Test
    fun `Success states with different messages or data should not be equal`() {
        val success1 = UiState.Success("Operation completed", 42)
        val success2 = UiState.Success("Operation failed", 42)
        val success3 = UiState.Success("Operation completed", null)

        assertNotEquals(success1, success2)
        assertNotEquals(success1, success3)
    }

    @Test
    fun `Error state should store error message`() {
        val error = UiState.Error("An error occurred")

        assertEquals("An error occurred", error.message)
    }

    @Test
    fun `Error states with the same message should be equal`() {
        val error1 = UiState.Error("An error occurred")
        val error2 = UiState.Error("An error occurred")

        assertEquals(error1, error2)
        assertEquals(error1.hashCode(), error2.hashCode())
    }

    @Test
    fun `Error states with different messages should not be equal`() {
        val error1 = UiState.Error("An error occurred")
        val error2 = UiState.Error("Another error occurred")

        assertNotEquals(error1, error2)
    }

    @Test
    fun `Different UiState types should not be equal`() {
        val idle = UiState.Idle
        val loading = UiState.Loading
        val success = UiState.Success("Operation completed")
        val error = UiState.Error("An error occurred")

        assertNotEquals(idle, loading)
        assertNotEquals(idle, success)
        assertNotEquals(idle, error)
        assertNotEquals(loading, success)
        assertNotEquals(loading, error)
        assertNotEquals(success, error)
    }
}
