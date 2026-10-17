package br.com.semesperanca.frontendleads.presentation.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.RoundedTextField
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.arrow_drop_down
import org.jetbrains.compose.resources.painterResource

private const val GRID_COLUMNS = 12

@Composable
fun ReusableForm(
    sections: List<FormSection>,
    values: Map<String, String>,
    onValueChange: (key: String, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        sections.forEach { section ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                section.title?.let {
                    Text(
                        text = it,
                        color = AppColors.Gray500,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                packRows(section.fields).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        row.forEach { field ->
                            Box(modifier = Modifier.weight(field.width.span.toFloat())) {
                                FormFieldItem(
                                    field = field,
                                    value = values[field.key].orEmpty(),
                                    onValueChange = onValueChange,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun packRows(fields: List<FormField>): List<List<FormField>> {
    val rows = mutableListOf<List<FormField>>()
    var current = mutableListOf<FormField>()
    var used = 0
    fields.forEach { field ->
        val span = field.width.span
        if (used + span > GRID_COLUMNS && current.isNotEmpty()) {
            rows += current
            current = mutableListOf()
            used = 0
        }
        current += field
        used += span
    }
    if (current.isNotEmpty()) rows += current
    return rows
}

@Composable
private fun FormFieldItem(
    field: FormField,
    value: String,
    onValueChange: (key: String, value: String) -> Unit,
) {
    val hint = field.placeholder.ifBlank { field.label }
    val placeholder = if (field.required) "$hint *" else hint

    when (field.type) {
        FormFieldType.DROPDOWN -> DropdownField(
            value = value,
            placeholder = placeholder,
            options = field.options,
            onSelect = { onValueChange(field.key, it) },
        )

        else -> RoundedTextField(
            value = value,
            onValueChange = { input ->
                val limited = field.maxLength?.let { input.take(it) } ?: input
                onValueChange(field.key, limited)
            },
            placeholder = placeholder,
            multiline = field.type == FormFieldType.MULTILINE,
            minHeight = if (field.type == FormFieldType.MULTILINE) 96 else 0,
            keyboardType = when (field.type) {
                FormFieldType.INTEGER -> KeyboardType.Number
                FormFieldType.DECIMAL, FormFieldType.CURRENCY -> KeyboardType.Decimal
                else -> KeyboardType.Text
            },
        )
    }
}

@Composable
private fun DropdownField(
    value: String,
    placeholder: String,
    options: List<FormOption>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = options.firstOrNull { it.value == value }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.Gray100)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = true }
                .padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected?.label ?: placeholder,
                color = if (selected != null) AppColors.Gray900 else AppColors.Gray400,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(Res.drawable.arrow_drop_down),
                contentDescription = null,
                tint = AppColors.Gray400,
                modifier = Modifier.size(22.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.label, color = AppColors.Gray900, fontSize = 15.sp) },
                    onClick = {
                        onSelect(option.value)
                        expanded = false
                    },
                )
            }
        }
    }
}
