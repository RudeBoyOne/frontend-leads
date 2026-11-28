package br.com.semesperanca.frontendleads.presentation.properties

import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.CreatePropertyRequestDto
import br.com.semesperanca.frontendleads.data.dto.PropertyDto
import br.com.semesperanca.frontendleads.data.dto.UpdatePropertyRequestDto
import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldType
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldWidth
import br.com.semesperanca.frontendleads.presentation.ui.form.FormOption
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

enum class PropertyStatus(val value: String, val label: String) {
    AVAILABLE("available", "Disponível"),
    RESERVED("reserved", "Reservado"),
    SOLD("sold", "Vendido"),
    RENTED("rented", "Alugado"),
    INACTIVE("inactive", "Inativo"),
}

enum class PropertyType(val label: String) {
    HOUSE("Casa"),
    APARTMENT("Apartamento"),
    LAND("Terreno"),
    COMMERCIAL("Comercial"),
    RURAL("Rural"),
}

fun propertyFormSections(
    branchOffices: List<BranchOfficeDto> = emptyList(),
    isEditing: Boolean = false,
): List<FormSection> {
    val sections =
        mutableListOf(
            FormSection(
                title = "Informações Básicas",
                fields = listOf(
                    FormField(key = "title", label = "Título", required = true, maxLength = 150),
                    FormField(key = "description", label = "Descrição", type = FormFieldType.MULTILINE),
                ),
            ),
            FormSection(
                title = "Características",
                fields = listOf(
                    FormField(key = "price", label = "Preço", type = FormFieldType.CURRENCY, width = FormFieldWidth.HALF),
                    FormField(
                        key = "propertyType",
                        label = "Tipo",
                        type = FormFieldType.DROPDOWN,
                        width = FormFieldWidth.HALF,
                        options = PropertyType.entries.map { FormOption(value = it.label, label = it.label) },
                    ),
                    FormField(
                        key = "bedrooms",
                        label = "Quartos",
                        type = FormFieldType.INTEGER,
                        maxLength = 2,
                        width = FormFieldWidth.THIRD,
                    ),
                    FormField(
                        key = "bathrooms",
                        label = "Banheiros",
                        type = FormFieldType.INTEGER,
                        maxLength = 2,
                        width = FormFieldWidth.THIRD,
                    ),
                    FormField(
                        key = "areaSqm",
                        label = "Área (m²)",
                        type = FormFieldType.DECIMAL,
                        width = FormFieldWidth.THIRD,
                    ),
                    FormField(
                        key = "status",
                        label = "Situação",
                        type = FormFieldType.DROPDOWN,
                        width = FormFieldWidth.HALF,
                        options = PropertyStatus.entries.map { FormOption(value = it.value, label = it.label) },
                    ),
                ),
            ),
            FormSection(
                title = "Endereço",
                fields = listOf(
                    FormField(key = "address", label = "Endereço"),
                    FormField(key = "city", label = "Cidade"),
                    FormField(key = "state", label = "UF", maxLength = 2, width = FormFieldWidth.THIRD),
                    FormField(key = "zipCode", label = "CEP", maxLength = 10, width = FormFieldWidth.THIRD),
                ),
            ),
        )

    if (!isEditing) {
        sections +=
            FormSection(
                title = "Vinculação",
                fields = listOf(
                    FormField(
                        key = "branchOfficeId",
                        label = "Filial",
                        type = FormFieldType.DROPDOWN,
                        required = true,
                        options =
                            branchOffices.mapNotNull { office ->
                                val id = office.id?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                                val name = office.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                                FormOption(value = id, label = name)
                            },
                    ),
                ),
            )
    }

    return sections
}

fun propertyFormDefaults(property: PropertyDto? = null): Map<String, String> {
    if (property == null) {
        return mapOf("status" to PropertyStatus.AVAILABLE.value)
    }
    return mapOf(
        "title" to property.title,
        "description" to property.description.orEmpty(),
        "price" to property.price?.toString().orEmpty(),
        "propertyType" to property.propertyType.orEmpty(),
        "bedrooms" to property.bedrooms?.toString().orEmpty(),
        "bathrooms" to property.bathrooms?.toString().orEmpty(),
        "areaSqm" to property.areaSqm?.toString().orEmpty(),
        "address" to property.address.orEmpty(),
        "city" to property.city.orEmpty(),
        "state" to property.state.orEmpty(),
        "zipCode" to property.zipCode.orEmpty(),
        "status" to (property.status ?: PropertyStatus.AVAILABLE.value),
    )
}

private data class PropertyFormValues(
    val title: String,
    val description: String?,
    val price: Double?,
    val propertyType: String?,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val areaSqm: Double?,
    val address: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val branchOfficeId: String,
    val status: String,
)

private fun readPropertyFormValues(values: Map<String, String>): PropertyFormValues {
    fun text(key: String): String? = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return PropertyFormValues(
        title = text("title") ?: "",
        description = text("description"),
        price = parseDecimal(values["price"]),
        propertyType = text("propertyType"),
        bedrooms = text("bedrooms")?.toIntOrNull(),
        bathrooms = text("bathrooms")?.toIntOrNull(),
        areaSqm = parseDecimal(values["areaSqm"]),
        address = text("address"),
        city = text("city"),
        state = text("state"),
        zipCode = text("zipCode"),
        branchOfficeId = text("branchOfficeId") ?: "",
        status = text("status") ?: PropertyStatus.AVAILABLE.value,
    )
}

fun toCreatePropertyRequest(values: Map<String, String>): CreatePropertyRequestDto {
    val form = readPropertyFormValues(values)
    return CreatePropertyRequestDto(
        title = form.title,
        description = form.description,
        price = form.price,
        propertyType = form.propertyType,
        bedrooms = form.bedrooms,
        bathrooms = form.bathrooms,
        areaSqm = form.areaSqm,
        address = form.address,
        city = form.city,
        state = form.state,
        zipCode = form.zipCode,
        branchOfficeId = form.branchOfficeId,
        status = form.status,
    )
}

fun toUpdatePropertyRequest(values: Map<String, String>): UpdatePropertyRequestDto {
    val form = readPropertyFormValues(values)
    return UpdatePropertyRequestDto(
        title = form.title,
        description = form.description,
        price = form.price,
        propertyType = form.propertyType,
        bedrooms = form.bedrooms,
        bathrooms = form.bathrooms,
        areaSqm = form.areaSqm,
        address = form.address,
        city = form.city,
        state = form.state,
        zipCode = form.zipCode,
        status = form.status,
    )
}

private fun parseDecimal(raw: String?): Double? {
    val cleaned = raw?.trim()?.replace("R$", "")?.replace(" ", "")?.takeIf { it.isNotEmpty() }
        ?: return null
    val normalized = if (cleaned.contains(',')) cleaned.replace(".", "").replace(",", ".") else cleaned
    return normalized.toDoubleOrNull()
}
