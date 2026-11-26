package br.com.semesperanca.frontendleads.presentation.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.sensors.SensorsScreen
import br.com.semesperanca.frontendleads.presentation.ui.AddButton
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.apartment
import org.jetbrains.compose.resources.painterResource

private enum class PropertiesTab(val label: String) {
    IMOVEIS("Imóveis"),
    SENSORES("Sensores"),
}

@Composable
fun PropertiesScreen(
    onCreateProperty: () -> Unit = {},
    onAssociateDevice: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(PropertiesTab.IMOVEIS) }

    Box(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            PropertiesTabRow(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it },
            )
            when (selectedTab) {
                PropertiesTab.IMOVEIS -> PropertiesEmptyState()
                PropertiesTab.SENSORES -> SensorsScreen()
            }
        }

        AddButton(
            onClick = when (selectedTab) {
                PropertiesTab.IMOVEIS -> onCreateProperty
                PropertiesTab.SENSORES -> onAssociateDevice
            },
            size = 56,
            contentDescription = when (selectedTab) {
                PropertiesTab.IMOVEIS -> "Novo imóvel"
                PropertiesTab.SENSORES -> "Associar dispositivo"
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        )
    }
}

@Composable
private fun PropertiesTabRow(
    selectedTab: PropertiesTab,
    onSelect: (PropertiesTab) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().background(AppColors.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            PropertiesTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                Text(
                    text = tab.label,
                    color = if (selected) AppColors.White else AppColors.Gray600,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) AppColors.Emerald600 else AppColors.Gray200)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSelect(tab) }
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = AppColors.Gray200)
    }
}

@Composable
private fun PropertiesEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.apartment),
            contentDescription = null,
            tint = AppColors.Gray300,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Nenhum imóvel cadastrado",
            color = AppColors.Gray400,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}
