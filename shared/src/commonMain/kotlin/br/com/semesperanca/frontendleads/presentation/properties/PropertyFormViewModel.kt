package br.com.semesperanca.frontendleads.presentation.properties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.CreatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.dto.UpdatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.BranchOfficeApi
import br.com.semesperanca.frontendleads.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PropertyFormUiState(
    val branchOffices: List<BranchOfficeDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class PropertyFormViewModel(
    private val repository: PropertyRepository,
    private val branchOfficeApi: BranchOfficeApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyFormUiState())
    val uiState: StateFlow<PropertyFormUiState> = _uiState.asStateFlow()

    init {
        loadBranchOffices()
    }

    private fun loadBranchOffices() {
        viewModelScope.launch {
            when (val result = branchOfficeApi.findAll()) {
                is ApiResult.Success -> _uiState.update { it.copy(branchOffices = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            }
        }
    }

    fun create(request: CreatePropertyRequestDto, onSuccess: () -> Unit) {
        val validationError = validate(request.title, request.branchOfficeId)
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.create(request)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun update(id: String, request: UpdatePropertyRequestDto, onSuccess: () -> Unit) {
        val validationError = validate(request.title, branchOfficeId = null)
        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.update(id, request)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    private fun validate(title: String, branchOfficeId: String?): String? =
        when {
            title.isBlank() -> "Informe o título do imóvel."
            branchOfficeId != null && branchOfficeId.isBlank() -> "Selecione uma filial antes de gravar."
            else -> null
        }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
