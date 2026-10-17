package br.com.semesperanca.frontendleads.presentation.leads

import br.com.semesperanca.frontendleads.presentation.ui.form.FormField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldType
import br.com.semesperanca.frontendleads.presentation.ui.form.FormFieldWidth
import br.com.semesperanca.frontendleads.presentation.ui.form.FormOption
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

private enum class TransactionType(val value: String, val label: String) {
    VENDA("VENDA", "Venda"),
    ALUGUEL("ALUGUEL", "Aluguel"),
}

private enum class LeadTemperature(val value: String, val label: String) {
    QUENTE("QUENTE", "Quente"),
    MORNA("MORNA", "Morna"),
    FRIA("FRIA", "Fria"),
}

val leadFormSections: List<FormSection> = listOf(
    FormSection(
        title = "Dados do Lead",
        fields = listOf(
            FormField(key = "name", label = "Nome", required = true, maxLength = 120),
            FormField(key = "email", label = "E-mail", required = true),
            FormField(
                key = "phone",
                label = "Telefone",
                type = FormFieldType.INTEGER,
                maxLength = 11,
                width = FormFieldWidth.HALF,
            ),
            FormField(
                key = "transactionType",
                label = "Tipo de Transação",
                type = FormFieldType.DROPDOWN,
                width = FormFieldWidth.HALF,
                options = TransactionType.entries.map { FormOption(value = it.value, label = it.label) },
            ),
            FormField(
                key = "temperature",
                label = "Temperatura",
                type = FormFieldType.DROPDOWN,
                width = FormFieldWidth.HALF,
                options = LeadTemperature.entries.map { FormOption(value = it.value, label = it.label) },
            ),
            FormField(key = "leadOrigin", label = "Origem", width = FormFieldWidth.HALF),
        ),
    ),
    FormSection(
        title = "Mensagem",
        fields = listOf(
            FormField(key = "message", label = "Mensagem", type = FormFieldType.MULTILINE),
        ),
    ),
)

val leadFormFields: List<FormField> = leadFormSections.flatMap { it.fields }

fun leadFormDefaults(): Map<String, String> = leadFormFields.associate { it.key to "" }
