package br.com.semesperanca.frontendleads.presentation.ui.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AppColors

@Composable
fun DynamicForm(
    sections: List<FormSection>,
    values: Map<String, String>,
    onValueChange: (key: String, value: String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    submitLabel: String = "Gravar",
    isLoading: Boolean = false,
    errorMessage: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            ReusableForm(
                sections = sections,
                values = values,
                onValueChange = onValueChange,
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = AppColors.Red500,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        FormActions(
            onSave = onSubmit,
            onClear = onClear,
            saveLabel = submitLabel,
            isLoading = isLoading,
        )
    }
}
