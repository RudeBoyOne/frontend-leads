package br.com.semesperanca.frontendleads.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RealEstateAgencyDto(
    val id: String? = null,
    val name: String? = null,
    val cnpj: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
)

@Serializable
data class CreateRealEstateAgencyRequestDto(
    val name: String,
    val cnpj: String,
    val email: String,
    val phone: String? = null,
)

@Serializable
data class UpdateRealEstateAgencyRequestDto(
    val name: String,
    val cnpj: String,
    val email: String,
    val phone: String? = null,
    val active: Boolean = true,
)
