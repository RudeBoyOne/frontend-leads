package br.com.semesperanca.frontendleads.presentation.activities

import br.com.semesperanca.frontendleads.data.dto.ActivityDto
import br.com.semesperanca.frontendleads.data.dto.CreateActivityRequestDto
import br.com.semesperanca.frontendleads.data.dto.UpdateActivityRequestDto
import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldType
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldWidth
import br.com.semesperanca.frontendleads.presentation.ui.form.FormOption
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

enum class ActivityCompleted(val value: String, val label: String) {
    PENDING("false", "Pendente"),
    DONE("true", "Concluída"),
}

val activityFormSections: List<FormSection> = listOf(
    FormSection(
        fields = listOf(
            FormField(key = "leadId", label = "ID do Lead", required = true),
            FormField(key = "propertyId", label = "ID do Imóvel (opcional)"),
            FormField(key = "title", label = "Título", required = true, maxLength = 150),
            FormField(key = "description", label = "Descrição", required = true, type = FormFieldType.MULTILINE),
            FormField(
                key = "activityDate",
                label = "Data e Hora (yyyy-MM-ddTHH:mm)",
                required = true,
                placeholder = "2026-10-15T14:30",
            ),
            FormField(
                key = "completed",
                label = "Status",
                type = FormFieldType.DROPDOWN,
                width = FormFieldWidth.HALF,
                options = ActivityCompleted.entries.map { FormOption(value = it.value, label = it.label) },
            ),
        ),
    ),
)

val activityFormFields: List<FormField> = activityFormSections.flatMap { it.fields }

fun activityFormDefaults(activity: ActivityDto? = null): Map<String, String> = mapOf(
    "leadId" to (activity?.leadId ?: ""),
    "propertyId" to (activity?.propertyId ?: ""),
    "title" to (activity?.title ?: ""),
    "description" to (activity?.description ?: ""),
    "activityDate" to (activity?.activityDate?.take(16) ?: ""),
    "completed" to (activity?.completed?.toString() ?: "false"),
)

fun toCreateActivityRequest(values: Map<String, String>): CreateActivityRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return CreateActivityRequestDto(
        leadId = text("leadId") ?: "",
        propertyId = text("propertyId"),
        title = text("title") ?: "",
        description = text("description") ?: "",
        activityDate = text("activityDate") ?: "",
    )
}

fun toUpdateActivityRequest(values: Map<String, String>): UpdateActivityRequestDto {
    fun text(key: String) = values[key]?.trim()?.takeIf { it.isNotEmpty() }
    return UpdateActivityRequestDto(
        propertyId = text("propertyId"),
        title = text("title") ?: "",
        description = text("description") ?: "",
        activityDate = text("activityDate") ?: "",
        completed = values["completed"]?.toBooleanStrictOrNull() ?: false,
    )
}
