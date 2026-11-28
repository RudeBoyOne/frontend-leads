package br.com.semesperanca.frontendleads.data.repository

import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.AuthApi
import br.com.semesperanca.frontendleads.data.storage.SessionStorage
import br.com.semesperanca.frontendleads.domain.repository.AuthRepository

class DefaultAuthRepository(
    private val authApi: AuthApi,
    private val session: SessionStorage,
) : AuthRepository {
    override suspend fun login(email: String, password: String): ApiResult<Unit> =
        when (val result = authApi.login(email, password)) {
            is ApiResult.Success -> {
                session.token = result.data.token
                ApiResult.Success(Unit)
            }
            is ApiResult.Error -> result
        }

    override fun isLoggedIn(): Boolean = session.isLoggedIn

    override fun logout() = session.clear()
}
