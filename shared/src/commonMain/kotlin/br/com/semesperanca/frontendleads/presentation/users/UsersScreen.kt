package br.com.semesperanca.frontendleads.presentation.users

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AddButton
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.manage_accounts
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsersScreen(
    onCreate: () -> Unit = {},
    viewModel: UsersViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(AppColors.Gray50)) {
        if (!uiState.error.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Red50)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = uiState.error.orEmpty(),
                    color = AppColors.Red600,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = viewModel::clearError) {
                    Text("Fechar", color = AppColors.Red600)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.users.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColors.Emerald600,
                    )
                }
                !uiState.error.isNullOrBlank() && uiState.users.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = uiState.error.orEmpty(), color = AppColors.Red500, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.load() }) { Text("Tentar novamente") }
                    }
                }
                uiState.displayUsers.isEmpty() -> {
                    UsersEmptyState()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.displayUsers) { user ->
                            UserCard(user = user)
                        }
                    }
                }
            }

            AddButton(
                onClick = onCreate,
                size = 56,
                contentDescription = "Novo usuário",
                modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            )
        }
    }
}

@Composable
private fun UsersEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.manage_accounts),
            contentDescription = null,
            tint = AppColors.Gray300,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Nenhum usuário cadastrado",
            color = AppColors.Gray400,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun UserCard(user: UserUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = user.name, color = AppColors.Gray900, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.email, color = AppColors.Gray500, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tipo: ${user.role.label}", color = AppColors.Gray600, fontSize = 13.sp)
            user.creci?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "CRECI: $it", color = AppColors.Gray600, fontSize = 13.sp)
            }
            user.phone?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Telefone: $it", color = AppColors.Gray600, fontSize = 13.sp)
            }
            user.branchOfficeId?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Filial (ID): $it", color = AppColors.Gray600, fontSize = 13.sp)
            }
            user.realEstateAgencyId?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Agência (ID): $it", color = AppColors.Gray600, fontSize = 13.sp)
            }
        }
    }
}
