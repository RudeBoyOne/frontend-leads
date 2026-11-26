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
import br.com.semesperanca.frontendleads.presentation.navigation.AppNavKey
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
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

private enum class MainTab(
    val label: String,
    val icon: DrawableResource,
    val key: AppNavKey,
) {
    LEADS("Leads", Res.drawable.list, AppNavKey.Leads),
    EQUIPES("Equipes", Res.drawable.groups, AppNavKey.Teams),
    ATIVIDADES("Atividades", Res.drawable.calendar_month, AppNavKey.Activities),
    IMOVEIS("Imóveis", Res.drawable.apartment, AppNavKey.Properties),
    AGENCIAS("Agências", Res.drawable.domain, AppNavKey.Agencies),
    USUARIOS("Usuários", Res.drawable.manage_accounts, AppNavKey.Users),
}

@Composable
fun MainTabsScreen(
    currentTopLevel: AppNavKey,
    onTabClick: (AppNavKey) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        Header(onLogoutClick = { showLogoutDialog = true })
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            content()
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
                        selected = currentTopLevel == tab.key,
                        modifier = Modifier.weight(1f),
                    ) { onTabClick(tab.key) }
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
                        onLogout()
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
