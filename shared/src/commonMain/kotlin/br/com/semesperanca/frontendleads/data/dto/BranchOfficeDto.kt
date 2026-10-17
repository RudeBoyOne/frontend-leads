package br.com.semesperanca.frontendleads.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BranchOfficeDto(
    val id: String? = null,
    val realEstateAgencyId: String? = null,
    val branchOwnerId: String? = null,
    val name: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
)

@Serializable
data class CreateBranchOfficeRequestDto(
    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val branchOwnerId: String,
)

@Serializable
data class UpdateBranchOfficeRequestDto(
    val name: String,
    val address: String? = null,
    val phone: String? = null,
)
