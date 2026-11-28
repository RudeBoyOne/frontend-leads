package br.com.semesperanca.frontendleads.domain.repository

import br.com.semesperanca.frontendleads.data.dto.CreatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.dto.PropertyDto
import br.com.semesperanca.frontendleads.data.dto.UpdatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult

data class PropertyPage(
    val properties: List<PropertyDto>,
    val total: Long,
)

interface PropertyRepository {
    suspend fun findAll(page: Int = 0, size: Int = 20): ApiResult<PropertyPage>
    suspend fun findById(id: String): ApiResult<PropertyDto>
    suspend fun search(title: String): ApiResult<List<PropertyDto>>
    suspend fun create(request: CreatePropertyRequestDto): ApiResult<PropertyDto>
    suspend fun update(id: String, request: UpdatePropertyRequestDto): ApiResult<PropertyDto>
    suspend fun delete(id: String): ApiResult<Unit>
}
