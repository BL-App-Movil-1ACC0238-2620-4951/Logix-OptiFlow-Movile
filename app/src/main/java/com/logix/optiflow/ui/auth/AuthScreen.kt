package com.logix.optiflow.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.ui.ViewModelFactories
import com.logix.optiflow.ui.theme.OptiFlowChipBackground
import com.logix.optiflow.ui.theme.OptiFlowFieldBorder
import com.logix.optiflow.ui.theme.OptiFlowGradientBottom
import com.logix.optiflow.ui.theme.OptiFlowGradientTop
import com.logix.optiflow.ui.theme.OptiFlowMuted
import com.logix.optiflow.ui.theme.OptiFlowNavy

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactories.auth),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSucceeded) {
        if (uiState.loginSucceeded) {
            onAuthenticated()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(OptiFlowGradientTop, OptiFlowGradientBottom),
                    ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (onBack != null) {
                Text(
                    text = "← Volver",
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onBack),
                    color = OptiFlowNavy,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Image(
                painter = painterResource(R.drawable.optiflow_logo),
                contentDescription = null,
                modifier = Modifier.size(width = 120.dp, height = 140.dp),
            )
            Text(
                text = stringResource(R.string.welcome_brand_name),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = OptiFlowNavy,
            )
            Text(
                text = stringResource(R.string.welcome_tagline),
                color = OptiFlowMuted,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = stringResource(R.string.welcome_card_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OptiFlowNavy,
                    )
                    Text(
                        text = stringResource(R.string.welcome_card_subtitle),
                        color = OptiFlowMuted,
                        fontSize = 13.sp,
                    )

                    if (uiState.session?.token?.isNotBlank() == true) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::continueWithSavedSession,
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = OptiFlowNavy,
                                    contentColor = Color.White,
                                ),
                        ) {
                            Text(stringResource(R.string.welcome_continue_session))
                        }
                    }

                    AuthModeToggle(
                        isRegister = uiState.isRegisterMode,
                        onLogin = { viewModel.setRegisterMode(false) },
                        onRegister = { viewModel.setRegisterMode(true) },
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        RoleCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.welcome_role_patient),
                            subtitle = stringResource(R.string.welcome_role_patient_desc),
                            initial = "P",
                            selected = uiState.role == UserRole.PATIENT,
                            onClick = { viewModel.setRole(UserRole.PATIENT) },
                        )
                        RoleCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.welcome_role_clinical),
                            subtitle = stringResource(R.string.welcome_role_clinical_desc),
                            initial = "C",
                            selected = uiState.role == UserRole.CLINICAL,
                            onClick = { viewModel.setRole(UserRole.CLINICAL) },
                        )
                    }

                    if (uiState.isRegisterMode) {
                        AuthField(
                            label = stringResource(R.string.auth_name_hint),
                            value = uiState.name,
                            onValueChange = viewModel::onNameChange,
                        )
                        AuthField(
                            label = stringResource(R.string.auth_phone_hint),
                            value = uiState.phone,
                            onValueChange = viewModel::onPhoneChange,
                        )
                    }

                    AuthField(
                        label = stringResource(R.string.auth_email_hint),
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.auth_password_hint),
                            fontWeight = FontWeight.SemiBold,
                            color = OptiFlowNavy,
                        )
                        if (!uiState.isRegisterMode) {
                            Text(
                                text = stringResource(R.string.welcome_forgot_password),
                                color = OptiFlowMuted,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = { Text(stringResource(R.string.welcome_password_placeholder)) },
                        visualTransformation =
                            if (uiState.passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        trailingIcon = {
                            IconButton(onClick = viewModel::togglePasswordVisibility) {
                                Icon(
                                    imageVector =
                                        if (uiState.passwordVisible) {
                                            Icons.Filled.VisibilityOff
                                        } else {
                                            Icons.Filled.Visibility
                                        },
                                    contentDescription = null,
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors(),
                        singleLine = true,
                    )

                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        onClick = viewModel::submit,
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = OptiFlowNavy,
                                contentColor = Color.White,
                            ),
                    ) {
                        Text(
                            text =
                                if (uiState.isRegisterMode) {
                                    stringResource(R.string.welcome_register_cta)
                                } else {
                                    stringResource(R.string.welcome_login_cta)
                                },
                            fontWeight = FontWeight.SemiBold,
                        )
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }

                    uiState.errorMessage?.let { message ->
                        Text(text = message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }
                    uiState.successMessage?.let { message ->
                        Text(text = message, color = OptiFlowNavy, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthModeToggle(
    isRegister: Boolean,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(OptiFlowChipBackground)
                .padding(4.dp),
    ) {
        ToggleChip(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.welcome_tab_login),
            selected = !isRegister,
            onClick = onLogin,
        )
        ToggleChip(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.welcome_tab_register),
            selected = isRegister,
            onClick = onRegister,
        )
    }
}

@Composable
private fun ToggleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) Color.White else Color.Transparent)
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = OptiFlowNavy,
        )
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    initial: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) OptiFlowNavy else OptiFlowFieldBorder,
                    shape = RoundedCornerShape(16.dp),
                )
                .background(Color.White)
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(OptiFlowChipBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = initial, fontWeight = FontWeight.Bold, color = OptiFlowNavy)
        }
        Text(text = title, fontWeight = FontWeight.SemiBold, color = OptiFlowNavy, fontSize = 14.sp)
        Text(text = subtitle, color = OptiFlowMuted, fontSize = 11.sp, lineHeight = 14.sp)
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Text(text = label, fontWeight = FontWeight.SemiBold, color = OptiFlowNavy)
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        singleLine = true,
    )
}

@Composable
private fun fieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = OptiFlowNavy,
        unfocusedBorderColor = OptiFlowFieldBorder,
        focusedContainerColor = Color(0xFFF8FBFE),
        unfocusedContainerColor = Color(0xFFF8FBFE),
    )
