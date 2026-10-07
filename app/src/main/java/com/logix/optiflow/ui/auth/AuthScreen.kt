package com.logix.optiflow.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.ui.ViewModelFactories
import com.logix.optiflow.ui.components.OptiFlowScaffold

@Composable
fun AuthScreen(
    onBack: () -> Unit,
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactories.auth),
) {
    val uiState by viewModel.uiState.collectAsState()

    OptiFlowScaffold(title = stringResource(R.string.flow_auth_title), onBack = onBack) {
        Column(
            modifier =
                it
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.session?.let { session ->
                Text(
                    text = stringResource(R.string.flow_auth_session, session.email, session.patientId),
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onAuthenticated) {
                    Text(stringResource(R.string.flow_auth_continue))
                }
                TextButton(onClick = viewModel::logout) {
                    Text(stringResource(R.string.flow_auth_logout))
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.auth_name_hint)) },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(stringResource(R.string.auth_email_hint)) },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text(stringResource(R.string.auth_phone_hint)) },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                visualTransformation = PasswordVisualTransformation(),
                label = { Text(stringResource(R.string.auth_password_hint)) },
            )

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::register,
                enabled = !uiState.isLoading,
            ) {
                Text(stringResource(R.string.auth_register))
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.login()
                },
                enabled = !uiState.isLoading,
            ) {
                Text(stringResource(R.string.auth_login))
            }

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
            uiState.successMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
