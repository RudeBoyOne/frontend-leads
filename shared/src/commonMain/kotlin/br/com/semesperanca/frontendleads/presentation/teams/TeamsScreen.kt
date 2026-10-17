package br.com.semesperanca.frontendleads.presentation.teams

import androidx.compose.foundation.background
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
import br.com.semesperanca.frontendleads.data.dto.TeamDto
import br.com.semesperanca.frontendleads.presentation.ui.AddButton
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.SearchField
import br.com.semesperanca.frontendleads.presentation.ui.form.DynamicForm
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.close
import br.com.semesperanca.frontendleads.resources.delete
import br.com.semesperanca.frontendleads.resources.edit
import br.com.semesperanca.frontendleads.resources.groups
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeamsScreen() {
    val viewModel: TeamsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose { viewModel.dismissForm() }
    }

    if (state.showForm) {
        val editing = state.editingTeam
        val formValues = remember(editing) {
            mutableStateMapOf<String, String>().apply { putAll(teamFormDefaults(editing)) }
        }
        TeamFormScreen(
            isEditing = editing != null,
            values = formValues,
            onValueChange = { k, v -> formValues[k] = v },
            isLoading = state.isLoading,
            error = state.error,
            onSubmit = {
                if (editing != null) {
                    viewModel.update(editing.id!!, toUpdateTeamRequest(formValues))
                } else {
                    viewModel.create(toCreateTeamRequest(formValues))
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
                    if (it.isBlank()) viewModel.loadTeams() else viewModel.search(it)
                },
                placeholder = "Buscar por nome...",
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.Emerald600)
                }
            } else if (state.teams.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.groups),
                        contentDescription = null,
                        tint = AppColors.Gray300,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum time cadastrado",
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
                    items(state.teams) { team ->
                        TeamCard(
                            team = team,
                            onEdit = { viewModel.showEditForm(team) },
                            onDelete = { team.id?.let { viewModel.delete(it) } },
                        )
                    }
                }
            }
        }

        AddButton(
            onClick = { viewModel.showCreateForm() },
            size = 56,
            contentDescription = "Novo Time",
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        )
    }
}

@Composable
private fun TeamCard(team: TeamDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.White, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team.name ?: "Sem nome",
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Gray900,
                fontSize = 15.sp,
            )
            team.description?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, color = AppColors.Gray500, fontSize = 13.sp)
            }
            team.maxAgents?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Máx. $it corretores", color = AppColors.Gray400, fontSize = 12.sp)
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
private fun TeamFormScreen(
    isEditing: Boolean,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    isLoading: Boolean,
    error: String?,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Gray50),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(painterResource(Res.drawable.close), contentDescription = "Fechar", tint = AppColors.Gray700)
            }
            Text(
                text = if (isEditing) "Editar Time" else "Novo Time",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Gray900,
            )
        }
        DynamicForm(
            sections = teamFormSections,
            values = values,
            onValueChange = onValueChange,
            onSubmit = onSubmit,
            onClear = { teamFormFields.forEach { onValueChange(it.key, "") } },
            modifier = Modifier.weight(1f),
            submitLabel = if (isEditing) "Gravar" else "Gravar",
            isLoading = isLoading,
            errorMessage = error,
        )
    }
}
