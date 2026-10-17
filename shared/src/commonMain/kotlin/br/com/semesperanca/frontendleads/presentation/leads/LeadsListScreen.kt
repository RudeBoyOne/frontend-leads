package br.com.semesperanca.frontendleads.presentation.leads

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AddButton
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.FilterItem
import br.com.semesperanca.frontendleads.presentation.ui.FilterRow
import br.com.semesperanca.frontendleads.presentation.ui.FilterTitle
import br.com.semesperanca.frontendleads.presentation.ui.Formatters
import br.com.semesperanca.frontendleads.presentation.ui.LeadSkeletonList
import br.com.semesperanca.frontendleads.presentation.ui.Pill
import br.com.semesperanca.frontendleads.presentation.ui.SearchField
import br.com.semesperanca.frontendleads.presentation.ui.form.DynamicForm
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.archive
import br.com.semesperanca.frontendleads.resources.calendar_month
import br.com.semesperanca.frontendleads.resources.close
import br.com.semesperanca.frontendleads.resources.history
import br.com.semesperanca.frontendleads.resources.inbox
import br.com.semesperanca.frontendleads.resources.list
import br.com.semesperanca.frontendleads.resources.schedule
import br.com.semesperanca.frontendleads.resources.star
import br.com.semesperanca.frontendleads.resources.swap_horiz
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private val FILTER_OPTIONS = listOf(
    FilterItem("Todos", Res.drawable.list),
    FilterItem("Futuros", Res.drawable.schedule),
    FilterItem("Favoritos", Res.drawable.star),
    FilterItem("Arquivados", Res.drawable.archive),
)

private val FILTERS = LeadFilter.entries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsListScreen(
    onLeadClick: (LeadUi) -> Unit = {},
    viewModel: LeadsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    var actionSheetOpen by remember { mutableStateOf(false) }
    var selectedLead by remember { mutableStateOf<LeadUi?>(null) }
    val dismissedNewIds = remember { mutableStateListOf<String>() }
    fun dismissNew(id: String) {
        if (id !in dismissedNewIds) dismissedNewIds += id
    }

    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        val formValues = remember {
            mutableStateMapOf<String, String>().apply { putAll(leadFormDefaults()) }
        }
        LeadFormScreen(
            values = formValues,
            onValueChange = { key, value -> formValues[key] = value },
            onSubmit = { showForm = false },
            onDismiss = { showForm = false },
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        SearchField(
            query = state.searchQuery,
            onQueryChange = viewModel::onSearchChange,
            placeholder = "Buscar leads...",
        )

        FilterRow(
            items = FILTER_OPTIONS,
            selectedIndex = FILTERS.indexOf(state.activeFilter),
            onSelect = { viewModel.setActiveFilter(FILTERS[it]) },
            itemSize = 48.dp,
            bottomPadding = 12.dp,
        )

        FilterTitle(label = FILTER_OPTIONS[FILTERS.indexOf(state.activeFilter)].label)

        if (state.hasNewLeads && state.activeFilter == LeadFilter.TODOS) {
            NewLeadsBanner(
                count = state.newLeadsCount,
                onClick = viewModel::refresh,
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading && state.displayLeads.isEmpty() -> {
                    LeadSkeletonList()
                }
                state.error != null && state.displayLeads.isEmpty() -> {
                    ErrorView(
                        message = state.error,
                        onRetry = { viewModel.fetchLeadsAction() },
                    )
                }
                state.displayLeads.isEmpty() -> {
                    EmptyView(filter = state.activeFilter)
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = viewModel::refresh,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LeadList(
                            leads = state.displayLeads,
                            isLoadingMore = state.isLoadingMore,
                            dismissedNewIds = dismissedNewIds,
                            onNewDismissed = ::dismissNew,
                            onLeadPress = { lead ->
                                viewModel.markLeadAsSeen(lead.id)
                                dismissNew(lead.id)
                                onLeadClick(lead)
                            },
                            onLeadLongPress = { lead ->
                                selectedLead = lead
                                actionSheetOpen = true
                            },
                            onLoadMore = viewModel::loadMore,
                        )
                    }
                }
            }

            AddButton(
                onClick = { showForm = true },
                contentDescription = "Novo Lead",
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            )
        }
    }

    LeadActionSheet(
        isOpen = actionSheetOpen,
        onClose = { actionSheetOpen = false },
        lead = selectedLead,
        onToggleFavorite = viewModel::toggleFavorite,
        onArchive = viewModel::archiveLead,
        onUnarchive = viewModel::unarchiveLead,
    )
}

@Composable
private fun NewLeadsBanner(count: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Emerald50)
            .border(
                width = 1.dp,
                color = AppColors.Emerald200,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val plural = count != 1
            Text(
                text = "$count novo${if (plural) "s" else ""} lead${if (plural) "s" else ""} disponível${if (plural) "is" else "l"}",
                color = AppColors.Emerald700,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Puxe para atualizar ou toque aqui",
                color = AppColors.Emerald500,
                fontSize = 12.sp,
            )
        }
        Icon(
            painter = painterResource(Res.drawable.list),
            contentDescription = null,
            tint = AppColors.Emerald500,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LeadList(
    leads: List<LeadUi>,
    isLoadingMore: Boolean,
    dismissedNewIds: List<String>,
    onNewDismissed: (String) -> Unit,
    onLeadPress: (LeadUi) -> Unit,
    onLeadLongPress: (LeadUi) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .collect { lastVisible ->
                val total = listState.layoutInfo.totalItemsCount
                if (lastVisible >= total - 4) onLoadMore()
            }
    }

    LaunchedEffect(listState) {
        var previouslyVisible = emptySet<String>()
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.mapNotNull { it.key as? String }.toSet()
        }.collect { visible ->
            val newlyHidden = previouslyVisible - visible
            newlyHidden.forEach(onNewDismissed)
            previouslyVisible = visible
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(leads, key = { it.id }) { lead ->
            LeadCard(
                lead = lead,
                showNew = lead.isNew && lead.id !in dismissedNewIds,
                onNewDismissed = onNewDismissed,
                onPress = { onLeadPress(lead) },
                onLongPress = { onLeadLongPress(lead) },
            )
        }
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = AppColors.Emerald600,
                        strokeWidth = 3.dp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LeadCard(
    lead: LeadUi,
    showNew: Boolean,
    onNewDismissed: (String) -> Unit,
    onPress: () -> Unit,
    onLongPress: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .pointerInput(lead.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Exit) onNewDismissed(lead.id)
                    }
                }
            }
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.White)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPress,
                onLongClick = onLongPress,
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = lead.name,
                    color = AppColors.Gray900,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (showNew) {
                    Text(
                        text = "Novo",
                        color = AppColors.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AppColors.Emerald500)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lead.email,
                color = AppColors.Gray500,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Imóvel",
                    color = AppColors.Gray400,
                    fontSize = 12.sp,
                )
                Text(
                    text = lead.propertyName,
                    color = AppColors.Gray900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Valor",
                    color = AppColors.Gray400,
                    fontSize = 12.sp,
                )
                Text(
                    text = Formatters.formatCurrency(lead.propertyValue),
                    color = AppColors.Gray900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Pill(text = lead.nature, backgroundColor = Formatters.natureColor(lead.nature))
        }
    }
}

@Composable
private fun ErrorView(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message ?: "Erro",
            color = AppColors.Red500,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Emerald600,
                contentColor = AppColors.White,
            ),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Tentar novamente",
                color = AppColors.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmptyView(filter: LeadFilter) {
    val isFuture = filter == LeadFilter.FUTUROS
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(Res.drawable.inbox),
            contentDescription = null,
            tint = AppColors.Gray300,
            modifier = Modifier.padding(bottom = 16.dp).size(32.dp),
        )
        Text(
            text = if (isFuture) "Nenhuma atividade" else "Nenhum lead encontrado",
            color = AppColors.Gray500,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = if (isFuture) {
                "Nenhuma atividade agendada para este filtro."
            } else {
                "Tente ajustar os filtros ou busque por outro termo."
            },
            color = AppColors.Gray400,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LeadActionSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    lead: LeadUi?,
    onToggleFavorite: (String) -> Unit,
    onArchive: (String) -> Unit,
    onUnarchive: (String) -> Unit,
) {
    if (!isOpen || lead == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Black50)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClose() },
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(AppColors.White)
                .padding(bottom = 8.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(AppColors.Gray300),
            )
            ActionSheetItem(
                icon = Res.drawable.star,
                iconTint = if (lead.isFavorite) AppColors.Yellow500 else AppColors.Gray500,
                label = if (lead.isFavorite) "Remover dos Favoritos" else "Adicionar aos Favoritos",
            ) {
                onToggleFavorite(lead.id)
                onClose()
            }
            ActionSheetItem(
                icon = Res.drawable.calendar_month,
                iconTint = AppColors.Gray500,
                label = "Adicionar Atividade",
            ) {
                onClose()
            }
            ActionSheetItem(
                icon = Res.drawable.swap_horiz,
                iconTint = AppColors.Gray500,
                label = "Transferir Lead",
            ) {
                onClose()
            }
            ActionSheetItem(
                icon = Res.drawable.history,
                iconTint = if (lead.isArchived) AppColors.Emerald600 else AppColors.Gray500,
                label = if (lead.isArchived) "Desarquivar Lead" else "Arquivar Lead",
            ) {
                if (lead.isArchived) onUnarchive(lead.id) else onArchive(lead.id)
                onClose()
            }
        }
    }
}

@Composable
private fun ActionSheetItem(
    icon: DrawableResource,
    iconTint: androidx.compose.ui.graphics.Color,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = AppColors.Gray900,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun LeadFormScreen(
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
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
                Icon(
                    painter = painterResource(Res.drawable.close),
                    contentDescription = "Fechar",
                    tint = AppColors.Gray700,
                )
            }
            Text(
                text = "Novo Lead",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Gray900,
            )
        }
        DynamicForm(
            sections = leadFormSections,
            values = values,
            onValueChange = onValueChange,
            onSubmit = onSubmit,
            onClear = { leadFormFields.forEach { onValueChange(it.key, "") } },
            modifier = Modifier.weight(1f),
            submitLabel = "Gravar",
        )
    }
}