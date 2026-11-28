package br.com.semesperanca.frontendleads.data.remote

import br.com.semesperanca.frontendleads.data.dto.CreatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.dto.PropertyDto
import br.com.semesperanca.frontendleads.data.dto.SpringPageDto
import br.com.semesperanca.frontendleads.data.dto.UpdatePropertyRequestDto
import io.ktor.client.request.parameter

class PropertyApi(private val client: ApiClient) {

    suspend fun findAll(page: Int = 0, size: Int = 20): ApiResult<SpringPageDto<PropertyDto>> =
        client.get("/properties") {
            parameter("page", page)
            parameter("size", size)
        }

    suspend fun findById(id: String): ApiResult<PropertyDto> =
        client.get("/properties/$id")

    suspend fun search(title: String): ApiResult<List<PropertyDto>> =
        client.get("/properties/search") {
            parameter("title", title)
        }

    suspend fun create(request: CreatePropertyRequestDto): ApiResult<PropertyDto> =
        client.post("/properties", request)

    suspend fun update(id: String, request: UpdatePropertyRequestDto): ApiResult<PropertyDto> =
        client.put("/properties/$id", request)

    suspend fun delete(id: String): ApiResult<Unit> =
        client.delete("/properties/$id")
}
