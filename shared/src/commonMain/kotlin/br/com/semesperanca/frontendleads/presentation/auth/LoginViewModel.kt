package br.com.semesperanca.frontendleads.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = repository.isLoggedIn()),
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun togglePassword() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val current = _uiState.value
        if (current.isLoading) return

        if (current.email.isBlank() || current.password.isBlank()) {
            _uiState.update { it.copy(error = "Preencha e-mail e senha para continuar") }
            return
        }

        val email = current.email.trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.login(email, current.password)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        email = email,
                        password = "",
                        isAuthenticated = true,
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update { AuthUiState() }
    }
}
