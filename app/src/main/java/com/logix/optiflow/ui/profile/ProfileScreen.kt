package com.logix.optiflow.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.ui.ViewModelFactories
import com.logix.optiflow.ui.auth.AuthViewModel
import com.logix.optiflow.ui.components.OptiFlowScaffold
import com.logix.optiflow.ui.theme.OptiFlowMuted
import com.logix.optiflow.ui.theme.OptiFlowNavy

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactories.auth),
) {
    val uiState by viewModel.uiState.collectAsState()
    val session = uiState.session

    OptiFlowScaffold(title = stringResource(R.string.nav_profile)) { modifier ->
        Column(
            modifier = modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = session?.name ?: stringResource(R.string.profile_guest),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OptiFlowNavy,
            )
            Text(
                text = session?.email ?: stringResource(R.string.profile_no_session),
                color = OptiFlowMuted,
            )
            session?.patientId?.let { id ->
                Text(text = stringResource(R.string.profile_patient_id, id), color = OptiFlowMuted)
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.refreshSession() },
            ) {
                Text(stringResource(R.string.profile_refresh))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OptiFlowNavy, contentColor = Color.White),
            ) {
                Text(stringResource(R.string.flow_auth_logout))
            }
        }
    }
}
