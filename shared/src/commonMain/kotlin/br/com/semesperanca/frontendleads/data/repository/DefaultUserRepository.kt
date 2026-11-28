package br.com.semesperanca.frontendleads.data.repository

import br.com.semesperanca.frontendleads.data.dto.BranchOwnerDto
import br.com.semesperanca.frontendleads.data.dto.CreateAgentRequest
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOwnerRequest
import br.com.semesperanca.frontendleads.data.dto.CreateManagerRequest
import br.com.semesperanca.frontendleads.data.dto.ManagerDto
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgentDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.UserApi
import br.com.semesperanca.frontendleads.domain.repository.UserRepository
import br.com.semesperanca.frontendleads.domain.repository.UserSummary
import br.com.semesperanca.frontendleads.domain.repository.UserType

class DefaultUserRepository(
    private val api: UserApi,
) : UserRepository {

    override suspend fun listUsers(): ApiResult<List<UserSummary>> {
        val agents = api.listAgents()
        val managers = api.listManagers()
        val owners = api.listBranchOwners()

        if (agents is ApiResult.Error) return agents
        if (managers is ApiResult.Error) return managers
        if (owners is ApiResult.Error) return owners

        val merged = mutableListOf<UserSummary>()

        (agents as ApiResult.Success).data.content.forEach { d ->
            merged += UserSummary(
                id = d.id.orEmpty(),
                name = d.name.orEmpty(),
                email = d.email.orEmpty(),
                role = d.role.orEmpty().ifBlank { "REAL_ESTATE_AGENT" },
                type = UserType.AGENT,
                creci = d.creci,
                branchOfficeId = d.branchOfficeId,
                active = d.active,
                createdAt = d.createdAt,
            )
        }

        (managers as ApiResult.Success).data.content.forEach { d ->
            merged += UserSummary(
                id = d.id.orEmpty(),
                name = d.name.orEmpty(),
                email = d.email.orEmpty(),
                role = d.role.orEmpty().ifBlank { "MANAGER" },
                type = UserType.MANAGER,
                branchOfficeId = d.branchOfficeId,
                active = d.active,
                createdAt = d.createdAt,
            )
        }

        (owners as ApiResult.Success).data.content.forEach { d ->
            merged += UserSummary(
                id = d.id.orEmpty(),
                name = d.name.orEmpty(),
                email = d.email.orEmpty(),
                role = d.role.orEmpty().ifBlank { "ADMIN" },
                type = UserType.BRANCH_OWNER,
                phone = d.phone,
                realEstateAgencyId = d.realEstateAgencyId,
                active = d.active,
                createdAt = d.createdAt,
            )
        }

        return ApiResult.Success(merged.sortedBy { it.name.lowercase() })
    }

    override suspend fun createAgent(body: CreateAgentRequest): ApiResult<UserSummary> =
        when (val r = api.createAgent(body)) {
            is ApiResult.Success -> ApiResult.Success(
                UserSummary(
                    id = r.data.id.orEmpty(),
                    name = r.data.name.orEmpty(),
                    email = r.data.email.orEmpty(),
                    role = r.data.role.orEmpty().ifBlank { "REAL_ESTATE_AGENT" },
                    type = UserType.AGENT,
                    creci = r.data.creci,
                    branchOfficeId = r.data.branchOfficeId,
                    active = r.data.active,
                    createdAt = r.data.createdAt,
                )
            )
            is ApiResult.Error -> r
        }

    override suspend fun createManager(body: CreateManagerRequest): ApiResult<UserSummary> =
        when (val r = api.createManager(body)) {
            is ApiResult.Success -> ApiResult.Success(
                UserSummary(
                    id = r.data.id.orEmpty(),
                    name = r.data.name.orEmpty(),
                    email = r.data.email.orEmpty(),
                    role = r.data.role.orEmpty().ifBlank { "MANAGER" },
                    type = UserType.MANAGER,
                    branchOfficeId = r.data.branchOfficeId,
                    active = r.data.active,
                    createdAt = r.data.createdAt,
                )
            )
            is ApiResult.Error -> r
        }

    override suspend fun createBranchOwner(body: CreateBranchOwnerRequest): ApiResult<UserSummary> =
        when (val r = api.createBranchOwner(body)) {
            is ApiResult.Success -> ApiResult.Success(
                UserSummary(
                    id = r.data.id.orEmpty(),
                    name = r.data.name.orEmpty(),
                    email = r.data.email.orEmpty(),
                    role = r.data.role.orEmpty().ifBlank { "ADMIN" },
                    type = UserType.BRANCH_OWNER,
                    phone = r.data.phone,
                    realEstateAgencyId = r.data.realEstateAgencyId,
                    active = r.data.active,
                    createdAt = r.data.createdAt,
                )
            )
            is ApiResult.Error -> r
        }
}
