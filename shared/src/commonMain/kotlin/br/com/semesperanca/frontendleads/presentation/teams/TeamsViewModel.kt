package br.com.semesperanca.frontendleads.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.CreateTeamRequestDto
import br.com.semesperanca.frontendleads.data.dto.TeamDto
import br.com.semesperanca.frontendleads.data.dto.UpdateTeamRequestDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.TeamApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TeamsUiState(
    val teams: List<TeamDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

class TeamsViewModel(private val api: TeamApi) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamsUiState())
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    init { loadTeams() }

    fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = api.findAll()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    teams = result.data,
                    isLoading = false,
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message,
                )
            }
        }
    }

    fun search(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.search(name)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    teams = result.data, isLoading = false,
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, error = result.message,
                )
            }
        }
    }

    fun create(request: CreateTeamRequestDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.create(request)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Time criado com sucesso!",
                    )
                    loadTeams()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, error = result.message,
                )
            }
        }
    }

    fun update(id: String, request: UpdateTeamRequestDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.update(id, request)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Time atualizado com sucesso!",
                    )
                    loadTeams()
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
                        isLoading = false, successMessage = "Time removido.",
                    )
                    loadTeams()
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
