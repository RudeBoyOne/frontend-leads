package br.com.semesperanca.frontendleads.presentation.branches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOfficeRequestDto
import br.com.semesperanca.frontendleads.data.dto.UpdateBranchOfficeRequestDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.BranchOfficeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BranchOfficesUiState(
    val offices: List<BranchOfficeDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showForm: Boolean = false,
    val editingOffice: BranchOfficeDto? = null,
)

class BranchOfficeViewModel(
    private val api: BranchOfficeApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BranchOfficesUiState())
    val uiState: StateFlow<BranchOfficesUiState> = _uiState.asStateFlow()

    init {
        loadOffices()
    }

    fun loadOffices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = api.findAll()) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            offices = result.data,
                            isLoading = false,
                        )
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                }
            }
        }
    }

    fun search(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.search(name)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            offices = result.data,
                            isLoading = false,
                        )
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                }
            }
        }
    }

    fun showCreateForm() {
        _uiState.value = _uiState.value.copy(showForm = true, editingOffice = null)
    }

    fun showEditForm(office: BranchOfficeDto) {
        _uiState.value = _uiState.value.copy(showForm = true, editingOffice = office)
    }

    fun dismissForm() {
        _uiState.value = _uiState.value.copy(showForm = false, editingOffice = null)
    }

    fun create(request: CreateBranchOfficeRequestDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.create(request)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            showForm = false,
                            successMessage = "Filial cadastrada com sucesso!",
                        )
                    loadOffices()
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                }
            }
        }
    }

    fun update(
        id: String,
        request: UpdateBranchOfficeRequestDto,
        active: Boolean,
        wasActive: Boolean,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.update(id, request)) {
                is ApiResult.Success -> {
                    if (active != wasActive) api.toggleActive(id)
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            showForm = false,
                            successMessage = "Filial atualizada com sucesso!",
                        )
                    loadOffices()
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                }
            }
        }
    }

    fun toggleActive(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.toggleActive(id)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Situação da filial alterada.",
                        )
                    loadOffices()
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                }
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.delete(id)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Filial removida.",
                        )
                    loadOffices()
                }

                is ApiResult.Error -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
