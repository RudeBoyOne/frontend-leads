package br.com.semesperanca.frontendleads.presentation.ui.form

enum class FormFieldType {
    TEXT,
    MULTILINE,
    INTEGER,
    DECIMAL,
    CURRENCY,
    DROPDOWN,
    DATE_TIME,
}

enum class FormFieldWidth(val span: Int) {
    FULL(12),
    HALF(6),
    THIRD(4),
}

data class FormOption(
    val value: String,
    val label: String,
)

data class FormField(
    val key: String,
    val label: String,
    val type: FormFieldType = FormFieldType.TEXT,
    val required: Boolean = false,
    val placeholder: String = "",
    val options: List<FormOption> = emptyList(),
    val maxLength: Int? = null,
    val width: FormFieldWidth = FormFieldWidth.FULL,
)

data class FormSection(
    val title: String? = null,
    val fields: List<FormField>,
)
