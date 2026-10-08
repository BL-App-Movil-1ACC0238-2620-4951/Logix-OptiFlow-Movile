package com.logix.optiflow.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.domain.model.NotificationType
import com.logix.optiflow.ui.ViewModelFactories
import com.logix.optiflow.ui.theme.OptiFlowChipBackground
import com.logix.optiflow.ui.theme.OptiFlowGradientBottom
import com.logix.optiflow.ui.theme.OptiFlowMuted
import com.logix.optiflow.ui.theme.OptiFlowNavy

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    viewModel: NotificationsViewModel = viewModel(factory = ViewModelFactories.notifications),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(OptiFlowGradientBottom),
    ) {
        NotificationsTopBar(onBack = onBack)

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = viewModel::markAllRead) {
                Text(
                    text = stringResource(R.string.notifications_mark_read),
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OptiFlowNavy)
                }
            }
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(text = uiState.errorMessage.orEmpty(), color = Color(0xFFC62828))
                }
            }
            else -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.sections.forEach { section ->
                        Text(
                            text = section.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = OptiFlowNavy,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                        )
                        section.items.forEach { row ->
                            NotificationRow(
                                row = row,
                                onClick = {
                                    viewModel.onNotificationClick(row.item.id)
                                    if (row.item.type == NotificationType.PROMO) {
                                        onOpenSearch()
                                    }
                                },
                            )
                            HorizontalDivider(color = Color(0xFFE0E8EF))
                        }
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(onBack: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(OptiFlowNavy)
                .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Box(
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.notifications_back),
                        tint = Color.White,
                    )
                }
            }
            Text(
                text = stringResource(R.string.notifications_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NotificationRow(
    row: NotificationRowUi,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OptiFlowChipBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = iconFor(row.item.type),
                contentDescription = null,
                tint = OptiFlowNavy,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = row.item.title, fontWeight = FontWeight.Bold, color = OptiFlowNavy, fontSize = 16.sp)
            Text(text = row.item.message, color = OptiFlowMuted, fontSize = 14.sp, lineHeight = 20.sp)
            Text(text = row.timeLabel, color = OptiFlowMuted.copy(alpha = 0.8f), fontSize = 12.sp)
        }
        when {
            row.item.navigable ->
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = OptiFlowMuted,
                )
            !row.isRead ->
                Box(
                    modifier =
                        Modifier
                            .padding(top = 6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1565C0)),
                )
        }
    }
}

private fun iconFor(type: NotificationType): ImageVector =
    when (type) {
        NotificationType.APPOINTMENT -> Icons.Filled.CalendarMonth
        NotificationType.ORDER -> Icons.Filled.Inventory2
        NotificationType.HEALTH -> Icons.Filled.Visibility
        NotificationType.PROMO -> Icons.Filled.Campaign
    }
