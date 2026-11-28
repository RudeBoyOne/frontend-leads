package br.com.semesperanca.frontendleads.presentation.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.SubHeader
import br.com.semesperanca.frontendleads.presentation.ui.form.DynamicForm
import br.com.semesperanca.frontendleads.presentation.ui.form.FormSection

@Composable
fun PropertyFormScreen(
    isEditing: Boolean,
    sections: List<FormSection>,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    isLoading: Boolean,
    error: String?,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        SubHeader(title = if (isEditing) "Editar Imóvel" else "Novo Imóvel", onBack = onBack)
        DynamicForm(
            sections = sections,
            values = values,
            onValueChange = onValueChange,
            onSubmit = onSubmit,
            onClear = onClear,
            modifier = Modifier.weight(1f),
            isLoading = isLoading,
            errorMessage = error,
        )
    }
}
