package br.com.semesperanca.frontendleads.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.CreateAgentRequest
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOwnerRequest
import br.com.semesperanca.frontendleads.data.dto.CreateManagerRequest
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgencyDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.BranchOfficeApi
import br.com.semesperanca.frontendleads.data.remote.RealEstateAgencyApi
import br.com.semesperanca.frontendleads.domain.repository.UserRepository
import br.com.semesperanca.frontendleads.domain.repository.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<UserUi> = emptyList(),
    val branchOffices: List<BranchOfficeDto> = emptyList(),
    val agencies: List<RealEstateAgencyDto> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
) {
    val displayUsers: List<UserUi>
        get() {
            val q = searchQuery.trim().lowercase()
            return users.filter { u ->
                q.isEmpty() || u.name.lowercase().contains(q) || u.email.lowercase().contains(q)
            }
        }
}

class UsersViewModel(
    private val repository: UserRepository,
    private val branchOfficeApi: BranchOfficeApi,
    private val agencyApi: RealEstateAgencyApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        load(refresh = false)
    }

    fun load(refresh: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isRefreshing = refresh, error = null) }
            when (val result = repository.listUsers()) {
                is ApiResult.Success -> {
                    val offices = branchOfficeApi.findAll()
                    val officeList = (offices as? ApiResult.Success)?.data ?: emptyList()
                    val agencies = agencyApi.findAll()
                    val agencyList = (agencies as? ApiResult.Success)?.data ?: emptyList()
                    _uiState.update {
                        it.copy(
                            users = result.data.map { summary ->
                                UserUi(
                                    name = summary.name,
                                    email = summary.email,
                                    role = when (summary.type) {
                                        UserType.AGENT -> UserRole.REAL_ESTATE_AGENT
                                        UserType.MANAGER -> UserRole.MANAGER
                                        UserType.BRANCH_OWNER -> UserRole.BRANCH_OWNER
                                    },
                                    creci = summary.creci,
                                    phone = summary.phone,
                                    branchOfficeId = summary.branchOfficeId,
                                    realEstateAgencyId = summary.realEstateAgencyId,
                                )
                            },
                            branchOffices = officeList,
                            agencies = agencyList,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(
                        error = result.message,
                        isLoading = false,
                        isRefreshing = false,
                    )
                }
            }
        }
    }

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun addUser(user: UserUi, password: String) {
        viewModelScope.launch {
            val result = when (user.role) {
                UserRole.REAL_ESTATE_AGENT -> {
                    val branchOfficeId = user.branchOfficeId
                    if (branchOfficeId.isNullOrBlank() || user.creci.isNullOrBlank()) {
                        _uiState.update { it.copy(error = "Preencha CRECI e Filial") }
                        return@launch
                    }
                    repository.createAgent(
                        CreateAgentRequest(
                            name = user.name,
                            email = user.email,
                            password = password,
                            creci = user.creci,
                            branchOfficeId = branchOfficeId,
                        )
                    )
                }
                UserRole.MANAGER -> {
                    val branchOfficeId = user.branchOfficeId
                    if (branchOfficeId.isNullOrBlank()) {
                        _uiState.update { it.copy(error = "Preencha Filial") }
                        return@launch
                    }
                    repository.createManager(
                        CreateManagerRequest(
                            name = user.name,
                            email = user.email,
                            password = password,
                            branchOfficeId = branchOfficeId,
                        )
                    )
                }
                UserRole.BRANCH_OWNER -> {
                    val agencyId = user.realEstateAgencyId
                    if (agencyId.isNullOrBlank()) {
                        _uiState.update { it.copy(error = "Preencha Agência") }
                        return@launch
                    }
                    repository.createBranchOwner(
                        CreateBranchOwnerRequest(
                            realEstateAgencyId = agencyId,
                            name = user.name,
                            email = user.email,
                            password = password,
                            phone = user.phone,
                        )
                    )
                }
            }

            when (result) {
                is ApiResult.Success -> load(refresh = true)
                is ApiResult.Error -> _uiState.update { it.copy(error = result.message) }
            }
        }
    }
}
