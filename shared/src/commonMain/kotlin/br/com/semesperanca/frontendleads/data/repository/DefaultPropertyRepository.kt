package br.com.semesperanca.frontendleads.data.repository

import br.com.semesperanca.frontendleads.data.dto.CreatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.dto.PropertyDto
import br.com.semesperanca.frontendleads.data.dto.UpdatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.remote.ApiResult
import br.com.semesperanca.frontendleads.data.remote.PropertyApi
import br.com.semesperanca.frontendleads.domain.repository.PropertyPage
import br.com.semesperanca.frontendleads.domain.repository.PropertyRepository

class DefaultPropertyRepository(private val propertyApi: PropertyApi) : PropertyRepository {
    override suspend fun findAll(page: Int, size: Int): ApiResult<PropertyPage> =
        when (val result = propertyApi.findAll(page, size)) {
            is ApiResult.Success -> ApiResult.Success(
                PropertyPage(result.data.content, result.data.totalElements),
            )
            is ApiResult.Error -> result
        }

    override suspend fun findById(id: String): ApiResult<PropertyDto> = propertyApi.findById(id)

    override suspend fun search(title: String): ApiResult<List<PropertyDto>> = propertyApi.search(title)

    override suspend fun create(request: CreatePropertyRequestDto): ApiResult<PropertyDto> =
        propertyApi.create(request)

    override suspend fun update(id: String, request: UpdatePropertyRequestDto): ApiResult<PropertyDto> =
        propertyApi.update(id, request)

    override suspend fun delete(id: String): ApiResult<Unit> = propertyApi.delete(id)
}
