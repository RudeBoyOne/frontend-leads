package br.com.semesperanca.frontendleads.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.semesperanca.frontendleads.presentation.ui.AppColors
import br.com.semesperanca.frontendleads.presentation.ui.ModalSheet
import br.com.semesperanca.frontendleads.presentation.ui.RoundedTextField
import br.com.semesperanca.frontendleads.presentation.ui.form.FormActions
import br.com.semesperanca.frontendleads.resources.Res
import br.com.semesperanca.frontendleads.resources.visibility
import br.com.semesperanca.frontendleads.resources.visibility_off
import org.jetbrains.compose.resources.painterResource

@Composable
fun UserFormSheet(
    onDismiss: () -> Unit,
    onSave: (UserUi) -> Unit,
) {
    var role by remember { mutableStateOf(UserRole.REAL_ESTATE_AGENT) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var creci by remember { mutableStateOf("") }
    var branchOfficeId by remember { mutableStateOf("") }
    var realEstateAgencyId by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    ModalSheet(onDismiss = onDismiss) {
        Text(
            text = "Novo Usuário",
            color = AppColors.Gray900,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tipo de Usuário", color = AppColors.Gray400, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserRole.entries.forEach { r ->
                val selected = role == r
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) AppColors.Emerald600 else AppColors.Gray200)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { role = r }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = r.label,
                        color = if (selected) AppColors.White else AppColors.Gray600,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Common fields
        FieldLabel("Nome")
        Spacer(modifier = Modifier.height(6.dp))
        RoundedTextField(value = name, onValueChange = { name = it }, placeholder = "Digite o nome")
        Spacer(modifier = Modifier.height(14.dp))

        FieldLabel("E-mail")
        Spacer(modifier = Modifier.height(6.dp))
        RoundedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Digite o e-mail",
            keyboardType = KeyboardType.Email,
        )
        Spacer(modifier = Modifier.height(14.dp))

        FieldLabel("Senha")
        Spacer(modifier = Modifier.height(6.dp))
        RoundedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Digite a senha",
            keyboardType = KeyboardType.Password,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(if (isPasswordVisible) Res.drawable.visibility_off else Res.drawable.visibility),
                        contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha",
                        tint = AppColors.Gray500,
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Conditional fields
        when (role) {
            UserRole.REAL_ESTATE_AGENT -> {
                FieldLabel("CRECI")
                Spacer(modifier = Modifier.height(6.dp))
                RoundedTextField(value = creci, onValueChange = { creci = it }, placeholder = "Digite o CRECI")
                Spacer(modifier = Modifier.height(14.dp))

                FieldLabel("Filial (ID)")
                Spacer(modifier = Modifier.height(6.dp))
                RoundedTextField(value = branchOfficeId, onValueChange = { branchOfficeId = it }, placeholder = "ID da filial")
                Spacer(modifier = Modifier.height(14.dp))
            }

            UserRole.MANAGER -> {
                FieldLabel("Filial (ID)")
                Spacer(modifier = Modifier.height(6.dp))
                RoundedTextField(value = branchOfficeId, onValueChange = { branchOfficeId = it }, placeholder = "ID da filial")
                Spacer(modifier = Modifier.height(14.dp))
            }

            UserRole.BRANCH_OWNER -> {
                FieldLabel("Agência (ID)")
                Spacer(modifier = Modifier.height(6.dp))
                RoundedTextField(
                    value = realEstateAgencyId,
                    onValueChange = { realEstateAgencyId = it },
                    placeholder = "ID da agência",
                )
                Spacer(modifier = Modifier.height(14.dp))

                FieldLabel("Telefone (opcional)")
                Spacer(modifier = Modifier.height(6.dp))
                RoundedTextField(value = phone, onValueChange = { phone = it }, placeholder = "Digite o telefone")
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        if (!error.isNullOrBlank()) {
            Text(text = error!!, color = AppColors.Red500, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        FormActions(
            onSave = {
                val validationError = when {
                    name.isBlank() || email.isBlank() || password.isBlank() ->
                        "Preencha nome, e-mail e senha"

                    role == UserRole.REAL_ESTATE_AGENT && (creci.isBlank() || branchOfficeId.isBlank()) ->
                        "Preencha CRECI e Filial (ID)"

                    role == UserRole.MANAGER && branchOfficeId.isBlank() ->
                        "Preencha Filial (ID)"

                    role == UserRole.BRANCH_OWNER && realEstateAgencyId.isBlank() ->
                        "Preencha Agência (ID)"

                    else -> null
                }
                error = validationError
                if (validationError == null) {
                    onSave(
                        UserUi(
                            name = name.trim(),
                            email = email.trim(),
                            role = role,
                            creci = creci.trim().takeIf { it.isNotEmpty() },
                            phone = phone.trim().takeIf { it.isNotEmpty() },
                            branchOfficeId = branchOfficeId.trim().takeIf { it.isNotEmpty() },
                            realEstateAgencyId = realEstateAgencyId.trim().takeIf { it.isNotEmpty() },
                        ),
                    )
                }
            },
            onClear = {
                name = ""
                email = ""
                password = ""
                creci = ""
                branchOfficeId = ""
                realEstateAgencyId = ""
                phone = ""
                error = null
            },
            saveLabel = "Gravar",
            clearLabel = "Limpar Campos",
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = AppColors.Gray400,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}
