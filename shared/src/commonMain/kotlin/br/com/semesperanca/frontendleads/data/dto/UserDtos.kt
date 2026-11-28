package br.com.semesperanca.frontendleads.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RealEstateAgentDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val creci: String? = null,
    val branchOfficeId: String? = null,
    val role: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
)

@Serializable
data class ManagerDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val branchOfficeId: String? = null,
    val role: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
)

@Serializable
data class BranchOwnerDto(
    val id: String? = null,
    val realEstateAgencyId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
)

@Serializable
data class CreateAgentRequest(
    val name: String,
    val email: String,
    val password: String,
    val creci: String,
    val branchOfficeId: String,
)

@Serializable
data class CreateManagerRequest(
    val name: String,
    val email: String,
    val password: String,
    val branchOfficeId: String,
)

@Serializable
data class CreateBranchOwnerRequest(
    val realEstateAgencyId: String,
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
)
