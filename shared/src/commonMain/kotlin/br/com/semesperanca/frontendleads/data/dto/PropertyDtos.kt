package br.com.semesperanca.frontendleads.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PropertyDto(
    val id: String,
    val realEstateAgencyId: String,
    val title: String,
    val description: String? = null,
    val price: Double? = null,
    val propertyType: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val areaSqm: Double? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val status: String? = null,
    val active: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class CreatePropertyRequestDto(
    val title: String,
    val description: String? = null,
    val price: Double? = null,
    val propertyType: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val areaSqm: Double? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val branchOfficeId: String,
    val status: String? = "available",
)

@Serializable
data class UpdatePropertyRequestDto(
    val title: String,
    val description: String? = null,
    val price: Double? = null,
    val propertyType: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val areaSqm: Double? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val status: String? = "available",
)
