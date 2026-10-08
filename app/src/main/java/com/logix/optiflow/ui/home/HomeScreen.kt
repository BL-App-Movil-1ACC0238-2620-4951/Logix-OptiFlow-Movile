package com.logix.optiflow.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.ui.ViewModelFactories
import com.logix.optiflow.ui.theme.OptiFlowChipBackground
import com.logix.optiflow.ui.theme.OptiFlowGradientBottom
import com.logix.optiflow.ui.theme.OptiFlowMuted
import com.logix.optiflow.ui.theme.OptiFlowNavy

@Composable
fun HomeScreen(
    onViewAllAppointments: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenNotifications: () -> Unit,
    onTryVirtual: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactories.home),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(OptiFlowGradientBottom)
                .verticalScroll(rememberScrollState()),
    ) {
        HomeTopBar(
            greetingName = uiState.greetingName,
            onOpenNotifications = onOpenNotifications,
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            VirtualTryOnBanner(onTryVirtual = onTryVirtual)

            NextAppointmentSection(
                appointment = uiState.nextAppointment,
                onViewAll = onViewAllAppointments,
            )

            QuickAccessSection(
                activePrescriptions = uiState.activePrescriptions,
                ordersInTransit = uiState.ordersInTransit,
                onPrescriptions = { onShowSnackbar("Mis recetas — próximamente en OptiFlow.") },
                onOrders = onOpenOrders,
                onHistory = { onShowSnackbar("Historial clínico — próximamente.") },
            )

            FeaturedFramesSection(
                frames = uiState.featuredFrames,
                onViewAll = onOpenSearch,
            )

            EyeRestTipCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeTopBar(
    greetingName: String,
    onOpenNotifications: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(OptiFlowNavy)
                .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_greeting, greetingName),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = stringResource(R.string.notifications_title),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun VirtualTryOnBanner(onTryVirtual: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onTryVirtual),
    ) {
        Image(
            painter = painterResource(R.drawable.home_banner_ar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xCC0D1B2A), Color(0x660D1B2A)),
                        ),
                    ),
        )
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                color = Color(0xFF2EC4B6),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_virtual_badge),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = stringResource(R.string.home_virtual_title),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp,
            )
            Text(
                text = stringResource(R.string.home_virtual_subtitle),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                modifier = Modifier.clickable(onClick = onTryVirtual),
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.home_virtual_cta),
                        color = OptiFlowNavy,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = OptiFlowNavy,
                    )
                }
            }
        }
    }
}

@Composable
private fun NextAppointmentSection(
    appointment: HomeAppointmentUi?,
    onViewAll: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_next_appointment),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = OptiFlowNavy,
            )
            TextButton(onClick = onViewAll) {
                Text(stringResource(R.string.home_view_all))
            }
        }
        if (appointment != null) {
            AppointmentCard(appointment)
        }
    }
}

@Composable
private fun AppointmentCard(appointment: HomeAppointmentUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(OptiFlowChipBackground)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = appointment.dayOfMonth,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = OptiFlowNavy,
                )
                Text(
                    text = appointment.monthLabel,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = OptiFlowNavy,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.statusLabel,
                        color = Color(0xFF1565C0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Text(
                    text = appointment.title,
                    fontWeight = FontWeight.Bold,
                    color = OptiFlowNavy,
                    fontSize = 16.sp,
                )
                Text(
                    text = "${appointment.storeName} • ${appointment.timeLabel}",
                    color = OptiFlowMuted,
                    fontSize = 13.sp,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = OptiFlowMuted,
            )
        }
    }
}

@Composable
private fun QuickAccessSection(
    activePrescriptions: Int,
    ordersInTransit: Int,
    onPrescriptions: () -> Unit,
    onOrders: () -> Unit,
    onHistory: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.home_quick_access),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = OptiFlowNavy,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Outlined.Description, null, tint = OptiFlowNavy) },
                title = stringResource(R.string.home_recipes),
                subtitle = stringResource(R.string.home_recipes_active, activePrescriptions),
                onClick = onPrescriptions,
            )
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Filled.Inventory2, null, tint = OptiFlowNavy) },
                title = stringResource(R.string.home_orders),
                subtitle = stringResource(R.string.home_orders_transit, ordersInTransit),
                onClick = onOrders,
            )
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Filled.FavoriteBorder, null, tint = OptiFlowNavy) },
                title = stringResource(R.string.home_history),
                subtitle = stringResource(R.string.home_history_sub),
                onClick = onHistory,
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(OptiFlowChipBackground),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Text(text = title, fontWeight = FontWeight.SemiBold, color = OptiFlowNavy, fontSize = 13.sp)
            Text(text = subtitle, color = OptiFlowMuted, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun FeaturedFramesSection(
    frames: List<FeaturedFrameUi>,
    onViewAll: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_featured_frames),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = OptiFlowNavy,
            )
            TextButton(onClick = onViewAll) {
                Text(stringResource(R.string.home_view_all_short))
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(frames) { frame ->
                FeaturedFrameCard(frame)
            }
        }
    }
}

@Composable
private fun FeaturedFrameCard(frame: FeaturedFrameUi) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Image(
                painter = painterResource(frame.imageRes),
                contentDescription = frame.name,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            )
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = frame.category, color = OptiFlowMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = frame.name, color = OptiFlowNavy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = frame.price, color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun EyeRestTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBBDEFB)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Visibility, contentDescription = null, tint = OptiFlowNavy)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.home_eye_tip_title),
                    fontWeight = FontWeight.Bold,
                    color = OptiFlowNavy,
                )
                Text(
                    text = stringResource(R.string.home_eye_tip_body),
                    color = OptiFlowMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}
