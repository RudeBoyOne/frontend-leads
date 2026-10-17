package br.com.semesperanca.frontendleads.data.remote

import br.com.semesperanca.frontendleads.data.dto.CreateRealEstateAgencyRequestDto
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgencyDto
import br.com.semesperanca.frontendleads.data.dto.UpdateRealEstateAgencyRequestDto
import io.ktor.client.request.parameter

class RealEstateAgencyApi(
    private val client: ApiClient,
) {
    suspend fun findAll(): ApiResult<List<RealEstateAgencyDto>> = client.get("/real-estate-agencies")

    suspend fun findById(id: String): ApiResult<RealEstateAgencyDto> = client.get("/real-estate-agencies/$id")

    suspend fun search(name: String): ApiResult<List<RealEstateAgencyDto>> =
        client.get("/real-estate-agencies/search") {
            parameter("name", name)
        }

    suspend fun create(request: CreateRealEstateAgencyRequestDto): ApiResult<RealEstateAgencyDto> =
        client.post("/real-estate-agencies", request)

    suspend fun update(
        id: String,
        request: UpdateRealEstateAgencyRequestDto,
    ): ApiResult<RealEstateAgencyDto> = client.put("/real-estate-agencies/$id", request)

    suspend fun delete(id: String): ApiResult<Unit> = client.delete("/real-estate-agencies/$id")
}
