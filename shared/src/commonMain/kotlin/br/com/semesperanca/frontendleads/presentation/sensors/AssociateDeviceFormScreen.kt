package br.com.semesperanca.frontendleads.presentation.sensors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.SubHeader
import br.com.semesperanca.frontendleads.presentation.ui.form.DynamicForm

@Composable
fun AssociateDeviceFormScreen(onBack: () -> Unit) {
    val values = remember { mutableStateMapOf<String, String>() }
    var showConfirmation by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        SubHeader(title = "Associar Imóvel a Dispositivo", onBack = onBack)
        DynamicForm(
            sections = sensorFormSections,
            values = values,
            onValueChange = { key, value -> values[key] = value },
            onSubmit = { showConfirmation = true },
            onClear = { values.clear() },
            modifier = Modifier.weight(1f),
            submitLabel = "Associar",
        )
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmation = false
                    onBack()
                }) {
                    Text(text = "Concluir", color = AppColors.Emerald600, fontWeight = FontWeight.SemiBold)
                }
            },
            title = { Text(text = "Associação registrada", color = AppColors.Gray900, fontSize = 18.sp) },
            text = {
                Text(
                    text = "Dispositivo ${values["deviceId"].orEmpty()} " +
                        "associado ao imóvel ${values["propertyId"].orEmpty()}.",
                    color = AppColors.Gray600,
                    fontSize = 14.sp,
                )
            },
            containerColor = AppColors.White,
        )
    }
}
