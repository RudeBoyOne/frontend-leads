package br.com.semesperanca.frontendleads.presentation.branches

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.data.dto.BranchOfficeDto
import br.com.semesperanca.frontendleads.presentation.ui.AddButton
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.Pill
import br.com.semesperanca.frontendleads.presentation.ui.SearchField
import br.com.semesperanca.frontendleads.presentation.ui.form.DynamicForm
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.close
import br.com.semesperanca.frontendleads.resources.delete
import br.com.semesperanca.frontendleads.resources.edit
import br.com.semesperanca.frontendleads.resources.storefront
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BranchOfficesScreen() {
    val viewModel: BranchOfficeViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose { viewModel.dismissForm() }
    }

    if (state.showForm) {
        val editing = state.editingOffice
        val formValues =
            remember(editing) {
                mutableStateMapOf<String, String>().apply { putAll(branchOfficeFormDefaults(editing)) }
            }
        BranchOfficeFormScreen(
            isEditing = editing != null,
            values = formValues,
            onValueChange = { k, v -> formValues[k] = v },
            isLoading = state.isLoading,
            error = state.error,
            onSubmit = {
                if (editing != null) {
                    viewModel.update(
                        id = editing.id!!,
                        request = toUpdateBranchOfficeRequest(formValues),
                        active = branchOfficeStatusOf(formValues),
                        wasActive = editing.active,
                    )
                } else {
                    viewModel.create(toCreateBranchOfficeRequest(formValues))
                }
            },
            onDismiss = { viewModel.dismissForm() },
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))
            SearchField(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (it.isBlank()) viewModel.loadOffices() else viewModel.search(it)
                },
                placeholder = "Buscar por nome...",
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.Emerald600)
                }
            } else if (state.offices.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.storefront),
                        contentDescription = null,
                        tint = AppColors.Gray300,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhuma filial cadastrada",
                        color = AppColors.Gray400,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.offices) { office ->
                        BranchOfficeCard(
                            office = office,
                            onEdit = { viewModel.showEditForm(office) },
                            onToggleActive = { office.id?.let { viewModel.toggleActive(it) } },
                            onDelete = { office.id?.let { viewModel.delete(it) } },
                        )
                    }
                }
            }
        }

        AddButton(
            onClick = { viewModel.showCreateForm() },
            size = 56,
            contentDescription = "Nova Filial",
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        )
    }
}

@Composable
private fun BranchOfficeCard(
    office: BranchOfficeDto,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = office.name ?: "Sem nome",
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Gray900,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Pill(
                    text = if (office.active) "Ativa" else "Inativa",
                    backgroundColor = if (office.active) AppColors.Emerald600 else AppColors.Gray400,
                    modifier =
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleActive,
                        ),
                )
            }
            office.address?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, color = AppColors.Gray500, fontSize = 13.sp)
            }
            office.phone?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text("Telefone: $it", color = AppColors.Gray400, fontSize = 12.sp)
            }
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(painterResource(Res.drawable.edit), contentDescription = "Editar", tint = AppColors.Emerald600)
            }
            IconButton(onClick = onDelete) {
                Icon(painterResource(Res.drawable.delete), contentDescription = "Excluir", tint = AppColors.Red400)
            }
        }
    }
}

@Composable
private fun BranchOfficeFormScreen(
    isEditing: Boolean,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    isLoading: Boolean,
    error: String?,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppColors.Gray50),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(AppColors.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(painterResource(Res.drawable.close), contentDescription = "Fechar", tint = AppColors.Gray700)
            }
            Text(
                text = if (isEditing) "Editar Filial" else "Nova Filial",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Gray900,
            )
        }
        DynamicForm(
            sections = branchOfficeFormSections,
            values = values,
            onValueChange = onValueChange,
            onSubmit = onSubmit,
            onClear = { branchOfficeFormFields.forEach { onValueChange(it.key, "") } },
            modifier = Modifier.weight(1f),
            submitLabel = "Gravar",
            isLoading = isLoading,
            errorMessage = error,
        )
    }
}
