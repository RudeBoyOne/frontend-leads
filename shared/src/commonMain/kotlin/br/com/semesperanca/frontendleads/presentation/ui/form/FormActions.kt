package br.com.semesperanca.frontendleads.presentation.ui.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.semesperanca.frontendleads.presentation.ui.AppColors

@Composable
fun FormActions(
    onSave: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    saveLabel: String = "Gravar",
    clearLabel: String = "Limpar Campos",
    isLoading: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onSave,
            enabled = !isLoading,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Emerald600,
                contentColor = AppColors.White,
            ),
            modifier = Modifier.weight(1f).height(46.dp),
        ) {
            Text(
                text = if (isLoading) "Gravando..." else saveLabel,
                color = AppColors.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Button(
            onClick = onClear,
            enabled = !isLoading,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = AppColors.Gray700,
            ),
            border = BorderStroke(1.dp, AppColors.Gray300),
            modifier = Modifier.weight(1f).height(46.dp),
        ) {
            Text(text = clearLabel, color = AppColors.Gray700, fontWeight = FontWeight.SemiBold)
        }
    }
}
