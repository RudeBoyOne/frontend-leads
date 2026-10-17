package br.com.semesperanca.frontendleads.presentation.teams

import br.com.semesperanca.frontendleads.data.dto.CreateTeamRequestDto
import br.com.semesperanca.frontendleads.data.dto.TeamDto
import br.com.semesperanca.frontendleads.data.dto.UpdateTeamRequestDto
import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldType
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldWidth
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

val teamFormSections: List<FormSection> = listOf(
    FormSection(
        fields = listOf(
            FormField(key = "managerId", label = "ID do Gerente", required = true),
            FormField(key = "name", label = "Nome do Time", required = true, maxLength = 100),
            FormField(key = "description", label = "Descrição", type = FormFieldType.MULTILINE, maxLength = 255),
            FormField(
                key = "maxAgents",
                label = "Máx. Corretores",
                type = FormFieldType.INTEGER,
                maxLength = 3,
                width = FormFieldWidth.HALF,
            ),
            FormField(
                key = "goalAmount",
                label = "Meta (R$)",
                type = FormFieldType.CURRENCY,
                width = FormFieldWidth.HALF,
            ),
        ),
    ),
)

val teamFormFields: List<FormField> = teamFormSections.flatMap { it.fields }

fun teamFormDefaults(team: TeamDto? = null): Map<String, String> = mapOf(
    "managerId" to (team?.managerId ?: ""),
    "name" to (team?.name ?: ""),
    "description" to (team?.description ?: ""),
    "maxAgents" to (team?.maxAgents?.toString() ?: ""),
    "goalAmount" to (team?.goalAmount?.toString() ?: ""),
)

fun toCreateTeamRequest(values: Map<String, String>): CreateTeamRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return CreateTeamRequestDto(
        managerId = text("managerId") ?: "",
        name = text("name") ?: "",
        description = text("description"),
        maxAgents = text("maxAgents")?.toIntOrNull(),
        goalAmount = text("goalAmount")?.toDoubleOrNull(),
    )
}

fun toUpdateTeamRequest(values: Map<String, String>): UpdateTeamRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return UpdateTeamRequestDto(
        managerId = text("managerId"),
        name = text("name") ?: "",
        description = text("description"),
        maxAgents = text("maxAgents")?.toIntOrNull(),
        goalAmount = text("goalAmount")?.toDoubleOrNull(),
    )
}
