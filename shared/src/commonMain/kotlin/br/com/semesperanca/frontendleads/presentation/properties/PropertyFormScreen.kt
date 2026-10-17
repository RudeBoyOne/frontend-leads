package br.com.semesperanca.frontendleads.presentation.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.SubHeader
import br.com.semesperanca.frontendleads.presentation.ui.form.DynamicForm
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PropertyFormScreen(
    title: String,
    onBack: () -> Unit,
    viewModel: PropertyFormViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        SubHeader(title = title, onBack = onBack)
        DynamicForm(
            sections = propertyFormSections,
            values = state.values,
            onValueChange = viewModel::onFieldChange,
            onSubmit = viewModel::onSave,
            onClear = viewModel::clearFields,
            modifier = Modifier.weight(1f),
        )
    }

    if (state.showUnavailableNotice) {
        AlertDialog(
            onDismissRequest = viewModel::dismissNotice,
            confirmButton = {
                TextButton(onClick = viewModel::dismissNotice) {
                    Text(text = "Entendi", color = AppColors.Emerald600, fontWeight = FontWeight.SemiBold)
                }
            },
            title = { Text(text = "Ação indisponível", color = AppColors.Gray900, fontSize = 18.sp) },
            text = {
                Text(
                    text = "O cadastro de imóveis estará disponível em breve.",
                    color = AppColors.Gray600,
                    fontSize = 14.sp,
                )
            },
            containerColor = AppColors.White,
        )
    }
}
