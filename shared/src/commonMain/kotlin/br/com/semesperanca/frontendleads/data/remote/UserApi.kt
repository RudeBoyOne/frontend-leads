package br.com.semesperanca.frontendleads.data.remote

import br.com.semesperanca.frontendleads.data.dto.BranchOwnerDto
import br.com.semesperanca.frontendleads.data.dto.CreateAgentRequest
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOwnerRequest
import br.com.semesperanca.frontendleads.data.dto.CreateManagerRequest
import br.com.semesperanca.frontendleads.data.dto.ManagerDto
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgentDto
import br.com.semesperanca.frontendleads.data.dto.SpringPageDto
import io.ktor.client.request.parameter

class UserApi(
    private val client: ApiClient,
) {
    suspend fun listAgents(): ApiResult<SpringPageDto<RealEstateAgentDto>> =
        client.get("/real-estate-agents") {
            parameter("page", 0)
            parameter("size", 500)
        }

    suspend fun listManagers(): ApiResult<SpringPageDto<ManagerDto>> =
        client.get("/managers") {
            parameter("page", 0)
            parameter("size", 500)
        }

    suspend fun listBranchOwners(): ApiResult<SpringPageDto<BranchOwnerDto>> =
        client.get("/branch-owners") {
            parameter("page", 0)
            parameter("size", 500)
        }

    suspend fun createAgent(body: CreateAgentRequest): ApiResult<RealEstateAgentDto> =
        client.post("/real-estate-agents", body)

    suspend fun createManager(body: CreateManagerRequest): ApiResult<ManagerDto> =
        client.post("/managers", body)

    suspend fun createBranchOwner(body: CreateBranchOwnerRequest): ApiResult<BranchOwnerDto> =
        client.post("/branch-owners", body)
}
