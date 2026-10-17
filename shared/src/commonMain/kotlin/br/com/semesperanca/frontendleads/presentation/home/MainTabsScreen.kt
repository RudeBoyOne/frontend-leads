package br.com.semesperanca.frontendleads.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.activities.ActivitiesScreen
import br.com.semesperanca.frontendleads.presentation.agencies.AgenciesTabScreen
import br.com.semesperanca.frontendleads.presentation.auth.LoginViewModel
import br.com.semesperanca.frontendleads.presentation.leads.LeadDetailScreen
import br.com.semesperanca.frontendleads.presentation.leads.LeadUi
import br.com.semesperanca.frontendleads.presentation.leads.LeadsListScreen
import br.com.semesperanca.frontendleads.presentation.leads.LeadsViewModel
import br.com.semesperanca.frontendleads.presentation.properties.PropertiesScreen
import br.com.semesperanca.frontendleads.presentation.teams.TeamsScreen
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.users.UsersScreen
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.apartment
import br.com.semesperanca.frontendleads.resources.calendar_month
import br.com.semesperanca.frontendleads.resources.domain
import br.com.semesperanca.frontendleads.resources.groups
import br.com.semesperanca.frontendleads.resources.list
import br.com.semesperanca.frontendleads.resources.logout
import br.com.semesperanca.frontendleads.resources.manage_accounts
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private enum class MainTab(
    val label: String,
    val icon: DrawableResource,
) {
    LEADS("Leads", Res.drawable.list),
    EQUIPES("Equipes", Res.drawable.groups),
    ATIVIDADES("Atividades", Res.drawable.calendar_month),
    IMOVEIS("Imóveis", Res.drawable.apartment),
    AGENCIAS("Agências", Res.drawable.domain),
    USUARIOS("Usuários", Res.drawable.manage_accounts),
}

@Composable
fun MainTabsScreen() {
    var selectedTab by remember { mutableStateOf(MainTab.LEADS) }
    var leadDetail by remember { mutableStateOf<LeadUi?>(null) }
    val leadsViewModel: LeadsViewModel = koinViewModel()
    val loginViewModel: LoginViewModel = koinViewModel()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (leadDetail != null) {
        LeadDetailScreen(
            lead = leadDetail!!,
            onBack = { leadDetail = null },
            onArchive = {
                leadsViewModel.archiveLead(it)
                leadDetail = null
            },
            onUnarchive = {
                leadsViewModel.unarchiveLead(it)
                leadDetail = null
            },
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        Header(onLogoutClick = { showLogoutDialog = true })
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                MainTab.LEADS -> {
                    LeadsListScreen(
                        onLeadClick = { leadDetail = it },
                    )
                }

                MainTab.EQUIPES -> {
                    TeamsScreen()
                }

                MainTab.ATIVIDADES -> {
                    ActivitiesScreen()
                }

                MainTab.IMOVEIS -> {
                    PropertiesScreen()
                }

                MainTab.AGENCIAS -> {
                    AgenciesTabScreen()
                }

                MainTab.USUARIOS -> {
                    UsersScreen()
                }
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(AppColors.White)
                    .navigationBarsPadding(),
        ) {
            HorizontalDivider(thickness = 1.dp, color = AppColors.Gray200)
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MainTab.entries.forEach { tab ->
                    TabBarItem(
                        title = tab.label,
                        icon = tab.icon,
                        selected = selectedTab == tab,
                        modifier = Modifier.weight(1f),
                    ) {
                        selectedTab = tab
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Sair", color = AppColors.Gray900, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(text = "Tem certeza que deseja sair?", color = AppColors.Gray700)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        loginViewModel.logout()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = AppColors.Red600,
                            contentColor = AppColors.White,
                        ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = AppColors.Gray700)
                }
            },
        )
    }
}

@Composable
private fun Header(onLogoutClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "100 Esperança",
            color = AppColors.Gray900,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onLogoutClick) {
            Icon(
                painter = painterResource(Res.drawable.logout),
                contentDescription = "Sair",
                tint = AppColors.Gray700,
            )
        }
    }
}

@Composable
private fun TabBarItem(
    title: String,
    icon: DrawableResource,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onClick() }
                .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            tint = if (selected) AppColors.Emerald600 else AppColors.Gray500,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = if (selected) AppColors.Emerald600 else AppColors.Gray500,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}
