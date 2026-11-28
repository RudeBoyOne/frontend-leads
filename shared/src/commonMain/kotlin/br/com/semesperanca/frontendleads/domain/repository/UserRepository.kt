package br.com.semesperanca.frontendleads.domain.repository

import br.com.semesperanca.frontendleads.data.dto.CreateAgentRequest
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOwnerRequest
import br.com.semesperanca.frontendleads.data.dto.CreateManagerRequest
import br.com.semesperanca.frontendleads.data.remote.ApiResult

enum class UserType {
    AGENT,
    MANAGER,
    BRANCH_OWNER,
}

data class UserSummary(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val type: UserType,
    val creci: String? = null,
    val phone: String? = null,
    val branchOfficeId: String? = null,
    val realEstateAgencyId: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
)

interface UserRepository {
    suspend fun listUsers(): ApiResult<List<UserSummary>>

    suspend fun createAgent(body: CreateAgentRequest): ApiResult<UserSummary>

    suspend fun createManager(body: CreateManagerRequest): ApiResult<UserSummary>

    suspend fun createBranchOwner(body: CreateBranchOwnerRequest): ApiResult<UserSummary>
}
