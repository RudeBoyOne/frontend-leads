package br.com.semesperanca.frontendleads.presentation.branches

import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.data.dto.CreateBranchOfficeRequestDto
import br.com.semesperanca.frontendleads.data.dto.UpdateBranchOfficeRequestDto
import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldType
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldWidth
import br.com.semesperanca.frontendleads.presentation.ui.form.FormOption
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

enum class BranchOfficeStatus(
    val value: String,
    val label: String,
) {
    ACTIVE("true", "Ativa"),
    INACTIVE("false", "Inativa"),
}

val branchOfficeFormSections: List<FormSection> =
    listOf(
        FormSection(
            title = "Dados da Filial",
            fields =
                listOf(
                    FormField(key = "name", label = "Nome da Filial", required = true, maxLength = 150),
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
                        options = BranchOfficeStatus.entries.map { FormOption(value = it.value, label = it.label) },
                    ),
                    FormField(
                        key = "address",
                        label = "Endereço",
                        maxLength = 255,
                        placeholder = "Rua, número - bairro, cidade/UF",
                    ),
                ),
        ),
        FormSection(
            title = "Vínculo",
            fields =
                listOf(
                    FormField(
                        key = "branchOwnerId",
                        label = "ID do Responsável (Branch Owner)",
                        required = true,
                        placeholder = "UUID do responsável pela filial",
                    ),
                ),
        ),
    )

val branchOfficeFormFields: List<FormField> = branchOfficeFormSections.flatMap { it.fields }

fun branchOfficeFormDefaults(office: BranchOfficeDto? = null): Map<String, String> =
    mapOf(
        "name" to (office?.name ?: ""),
        "phone" to (office?.phone ?: ""),
        "active" to (office?.active?.toString() ?: "true"),
        "address" to (office?.address ?: ""),
        "branchOwnerId" to (office?.branchOwnerId ?: ""),
    )

fun toCreateBranchOfficeRequest(values: Map<String, String>): CreateBranchOfficeRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return CreateBranchOfficeRequestDto(
        name = text("name") ?: "",
        address = text("address"),
        phone = text("phone"),
        branchOwnerId = text("branchOwnerId") ?: "",
    )
}

fun toUpdateBranchOfficeRequest(values: Map<String, String>): UpdateBranchOfficeRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return UpdateBranchOfficeRequestDto(
        name = text("name") ?: "",
        address = text("address"),
        phone = text("phone"),
    )
}

fun branchOfficeStatusOf(values: Map<String, String>): Boolean = values["active"]?.toBooleanStrictOrNull() ?: true
