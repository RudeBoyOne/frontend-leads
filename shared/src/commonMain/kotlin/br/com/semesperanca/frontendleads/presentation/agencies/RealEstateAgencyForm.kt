package br.com.semesperanca.frontendleads.presentation.agencies

import br.com.semesperanca.frontendleads.data.dto.CreateRealEstateAgencyRequestDto
import br.com.semesperanca.frontendleads.data.dto.RealEstateAgencyDto
import br.com.semesperanca.frontendleads.data.dto.UpdateRealEstateAgencyRequestDto
import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldType
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldWidth
import br.com.semesperanca.frontendleads.presentation.ui.form.FormOption
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

enum class RealEstateAgencyStatus(
    val value: String,
    val label: String,
) {
    ACTIVE("true", "Ativa"),
    INACTIVE("false", "Inativa"),
}

val realEstateAgencyFormSections: List<FormSection> =
    listOf(
        FormSection(
            title = "Dados da Agência",
            fields =
                listOf(
                    FormField(key = "name", label = "Nome", required = true, maxLength = 150),
                    FormField(
                        key = "cnpj",
                        label = "CNPJ",
                        required = true,
                        maxLength = 18,
                        placeholder = "00.000.000/0000-00",
                    ),
                    FormField(
                        key = "email",
                        label = "E-mail",
                        required = true,
                        maxLength = 150,
                        placeholder = "contato@agencia.com.br",
                    ),
                    FormField(
                        key = "phone",
                        label = "Telefone",
                        maxLength = 20,
                        placeholder = "(11) 99999-9999",
                        width = FormFieldWidth.HALF,
                    ),
                    FormField(
                        key = "active",
                        label = "Situação",
                        type = FormFieldType.DROPDOWN,
                        width = FormFieldWidth.HALF,
                        options = RealEstateAgencyStatus.entries.map { FormOption(value = it.value, label = it.label) },
                    ),
                ),
        ),
    )

val realEstateAgencyFormFields: List<FormField> = realEstateAgencyFormSections.flatMap { it.fields }

fun realEstateAgencyFormDefaults(agency: RealEstateAgencyDto? = null): Map<String, String> =
    mapOf(
        "name" to (agency?.name ?: ""),
        "cnpj" to (agency?.cnpj ?: ""),
        "email" to (agency?.email ?: ""),
        "phone" to (agency?.phone ?: ""),
        "active" to (agency?.active?.toString() ?: "true"),
    )

fun toCreateRealEstateAgencyRequest(values: Map<String, String>): CreateRealEstateAgencyRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return CreateRealEstateAgencyRequestDto(
        name = text("name") ?: "",
        cnpj = text("cnpj") ?: "",
        email = text("email") ?: "",
        phone = text("phone"),
    )
}

fun toUpdateRealEstateAgencyRequest(values: Map<String, String>): UpdateRealEstateAgencyRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return UpdateRealEstateAgencyRequestDto(
        name = text("name") ?: "",
        cnpj = text("cnpj") ?: "",
        email = text("email") ?: "",
        phone = text("phone"),
        active = values["active"]?.toBooleanStrictOrNull() ?: true,
    )
}
