package br.com.semesperanca.frontendleads.data.remote

import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOfficeRequestDto
import br.com.semesperanca.frontendleads.data.dto.UpdateBranchOfficeRequestDto
import io.ktor.client.request.parameter

class BranchOfficeApi(
    private val client: ApiClient,
) {
    suspend fun findAll(): ApiResult<List<BranchOfficeDto>> = client.get("/branch-offices")

    suspend fun findById(id: String): ApiResult<BranchOfficeDto> = client.get("/branch-offices/$id")

    suspend fun search(name: String): ApiResult<List<BranchOfficeDto>> =
        client.get("/branch-offices/search") {
            parameter("name", name)
        }

    suspend fun create(request: CreateBranchOfficeRequestDto): ApiResult<BranchOfficeDto> = client.post("/branch-offices", request)

    suspend fun update(
        id: String,
        request: UpdateBranchOfficeRequestDto,
    ): ApiResult<BranchOfficeDto> = client.put("/branch-offices/$id", request)

    suspend fun toggleActive(id: String): ApiResult<Unit> = client.patch("/branch-offices/$id/toggle-active")

    suspend fun delete(id: String): ApiResult<Unit> = client.delete("/branch-offices/$id")
}
