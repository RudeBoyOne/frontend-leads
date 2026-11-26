package br.com.semesperanca.frontendleads.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.ActivityDto
import br.com.semesperanca.frontendleads.data.dto.CreateActivityRequestDto
import br.com.semesperanca.frontendleads.data.dto.UpdateActivityRequestDto
import br.com.semesperanca.frontendleads.data.remote.ActivityApi
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActivitiesUiState(
    val activities: List<ActivityDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

class ActivitiesViewModel(private val api: ActivityApi) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivitiesUiState())
    val uiState: StateFlow<ActivitiesUiState> = _uiState.asStateFlow()

    init { loadActivities() }

    fun loadActivities(search: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = api.findAll(search = search)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    activities = result.data.content,
                    isLoading = false,
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, error = result.message,
                )
            }
        }
    }

    fun create(request: CreateActivityRequestDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.create(request)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Atividade criada com sucesso!",
                    )
                    loadActivities()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, error = result.message,
                )
            }
        }
    }

    fun update(id: String, request: UpdateActivityRequestDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.update(id, request)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Atividade atualizada com sucesso!",
                    )
                    loadActivities()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, error = result.message,
                )
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.delete(id)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, successMessage = "Atividade removida.",
                    )
                    loadActivities()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, error = result.message,
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
