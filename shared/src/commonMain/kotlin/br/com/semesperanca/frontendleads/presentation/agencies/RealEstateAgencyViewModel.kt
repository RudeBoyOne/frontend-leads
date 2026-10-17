package br.com.semesperanca.frontendleads.presentation.agencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.CreateRealEstateAgencyRequestDto
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgencyDto
import br.com.semesperanca.frontendleads.data.dto.UpdateRealEstateAgencyRequestDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.RealEstateAgencyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RealEstateAgenciesUiState(
    val agencies: List<RealEstateAgencyDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showForm: Boolean = false,
    val editingAgency: RealEstateAgencyDto? = null,
)

class RealEstateAgencyViewModel(
    private val api: RealEstateAgencyApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RealEstateAgenciesUiState())
    val uiState: StateFlow<RealEstateAgenciesUiState> = _uiState.asStateFlow()

    init {
        loadAgencies()
    }

    fun loadAgencies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = api.findAll()) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            agencies = result.data,
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
                            agencies = result.data,
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
        _uiState.value = _uiState.value.copy(showForm = true, editingAgency = null)
    }

    fun showEditForm(agency: RealEstateAgencyDto) {
        _uiState.value = _uiState.value.copy(showForm = true, editingAgency = agency)
    }

    fun dismissForm() {
        _uiState.value = _uiState.value.copy(showForm = false, editingAgency = null)
    }

    fun create(request: CreateRealEstateAgencyRequestDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.create(request)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            showForm = false,
                            successMessage = "Agência cadastrada com sucesso!",
                        )
                    loadAgencies()
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
        request: UpdateRealEstateAgencyRequestDto,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = api.update(id, request)) {
                is ApiResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            showForm = false,
                            successMessage = "Agência atualizada com sucesso!",
                        )
                    loadAgencies()
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
                            successMessage = "Agência removida.",
                        )
                    loadAgencies()
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
