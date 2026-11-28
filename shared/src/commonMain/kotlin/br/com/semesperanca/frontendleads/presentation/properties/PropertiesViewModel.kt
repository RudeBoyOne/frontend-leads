package br.com.semesperanca.frontendleads.presentation.properties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.PropertyDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PropertiesUiState(
    val properties: List<PropertyDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
) {
    val displayProperties: List<PropertyDto>
        get() {
            val query = searchQuery.trim().lowercase()
            if (query.isEmpty()) return properties
            return properties.filter { property ->
                property.title.lowercase().contains(query) ||
                    property.city.orEmpty().lowercase().contains(query) ||
                    property.propertyType.orEmpty().lowercase().contains(query)
            }
        }
}

class PropertiesViewModel(
    private val repository: PropertyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertiesUiState())
    val uiState: StateFlow<PropertiesUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.findAll(size = 100)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(properties = result.data.properties, isLoading = false)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun search(title: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.search(title)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(properties = result.data, isLoading = false)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.delete(id)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(successMessage = "Imóvel removido.") }
                    loadProperties()
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
