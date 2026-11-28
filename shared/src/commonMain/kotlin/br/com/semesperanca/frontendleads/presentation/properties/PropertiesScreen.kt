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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import br.com.semesperanca.frontendleads.data.dto.PropertyDto
import br.com.semesperanca.frontendleads.presentation.sensors.SensorsScreen
import br.com.semesperanca.frontendleads.presentation.ui.AddButton
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.Formatters
import br.com.semesperanca.frontendleads.presentation.ui.Pill
import br.com.semesperanca.frontendleads.presentation.ui.SearchField
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.apartment
import br.com.semesperanca.frontendleads.resources.delete
import br.com.semesperanca.frontendleads.resources.edit
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private enum class PropertiesTab(val label: String) {
    IMOVEIS("Imóveis"),
    SENSORES("Sensores"),
}

@Composable
fun PropertiesScreen(
    onCreateProperty: () -> Unit = {},
    onEditProperty: (PropertyDto) -> Unit = {},
    onAssociateDevice: () -> Unit = {},
) {
    val viewModel: PropertiesViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(PropertiesTab.IMOVEIS) }
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            PropertiesTabRow(
                selectedTab = selectedTab,
                onSelect = { selectedTab = it },
            )
            when (selectedTab) {
                PropertiesTab.SENSORES -> SensorsScreen()
                PropertiesTab.IMOVEIS -> {
                    SearchField(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.onSearchChange(it)
                            if (it.isBlank()) viewModel.loadProperties() else viewModel.search(it.trim())
                        },
                        placeholder = "Buscar por título...",
                    )

                    state.error?.let { error ->
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(text = error, color = AppColors.Red500, fontSize = 13.sp)
                            TextButton(onClick = viewModel::clearMessages) {
                                Text(text = "Fechar", color = AppColors.Emerald600, fontSize = 13.sp)
                            }
                        }
                    }

                    when {
                        state.isLoading && state.properties.isEmpty() ->
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = AppColors.Emerald600)
                            }

                        state.displayProperties.isEmpty() -> PropertiesEmptyState()

                        else ->
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(state.displayProperties) { property ->
                                    PropertyCard(
                                        property = property,
                                        onEdit = { onEditProperty(property) },
                                        onDelete = { viewModel.delete(property.id) },
                                    )
                                }
                            }
                    }
                }
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
private fun PropertyCard(
    property: PropertyDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(AppColors.White, RoundedCornerShape(12.dp))
                .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Gray900,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.size(8.dp))
                val statusLabel = PropertyStatus.entries.firstOrNull { it.value == property.status }?.label
                Pill(
                    text = statusLabel ?: if (property.active) "Ativo" else "Inativo",
                    backgroundColor =
                        if (property.active && property.status != PropertyStatus.INACTIVE.value) {
                            AppColors.Emerald600
                        } else {
                            AppColors.Gray400
                        },
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            property.price?.let {
                Text(text = Formatters.formatCurrency(it), color = AppColors.Emerald700, fontSize = 14.sp)
            }
            val details = listOfNotNull(property.propertyType, property.city, property.state).joinToString(" · ")
            if (details.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = details, color = AppColors.Gray500, fontSize = 13.sp)
            }
            val rooms = listOfNotNull(
                property.bedrooms?.let { "$it quartos" },
                property.bathrooms?.let { "$it banheiros" },
            ).joinToString(" · ")
            if (rooms.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = rooms, color = AppColors.Gray400, fontSize = 12.sp)
            }
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(Res.drawable.edit),
                    contentDescription = "Editar imóvel",
                    tint = AppColors.Emerald600,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(Res.drawable.delete),
                    contentDescription = "Excluir imóvel",
                    tint = AppColors.Red400,
                )
            }
        }
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
