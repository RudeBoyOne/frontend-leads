package br.com.semesperanca.frontendleads.presentation.properties

import br.com.semesperanca.frontendleads.data.dto.CreatePropertyRequestDto
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

val propertyFormSections: List<FormSection> = listOf(
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
    FormSection(
        title = "Vinculação",
        fields = listOf(
            FormField(key = "branchOfficeId", label = "Filial (ID)", required = true),
        ),
    ),
)

val propertyFormFields: List<FormField> = propertyFormSections.flatMap { it.fields }

fun propertyFormDefaults(): Map<String, String> = mapOf(
    "status" to PropertyStatus.AVAILABLE.value,
)

fun toCreatePropertyRequest(values: Map<String, String>): CreatePropertyRequestDto {
    fun text(key: String): String? = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return CreatePropertyRequestDto(
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

private fun parseDecimal(raw: String?): Double? {
    val cleaned = raw?.trim()?.replace("R$", "")?.replace(" ", "")?.takeIf { it.isNotEmpty() }
        ?: return null
    val normalized = if (cleaned.contains(',')) cleaned.replace(".", "").replace(",", ".") else cleaned
    return normalized.toDoubleOrNull()
}
