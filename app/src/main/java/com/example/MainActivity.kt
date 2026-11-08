package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AegisTab
import com.example.ui.AegisViewModel
import com.example.ui.components.AegisTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: AegisViewModel = viewModel()
                val currentTab by viewModel.currentTab.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val analytics by viewModel.analytics.collectAsState()
                val userMessage by viewModel.userMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Display reactive snackbars for action feedback
                LaunchedEffect(userMessage) {
                    userMessage?.let {
                        snackbarHostState.showSnackbar(
                            message = it,
                            duration = SnackbarDuration.Short
                        )
                        viewModel.clearUserMessage()
                    }
                }

                // Handle back button: return to dashboard if on other tab
                BackHandler(enabled = currentTab != AegisTab.DASHBOARD) {
                    viewModel.selectTab(AegisTab.DASHBOARD)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TacticalObsidian),
                    topBar = {
                        AegisTopBar(
                            userProfile = currentUser,
                            threatScore = analytics.threatMatrixScore,
                            onRoleSelect = { newRole -> viewModel.switchRole(newRole) }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("aegis_bottom_navigation"),
                            containerColor = TacticalObsidian,
                            tonalElevation = 6.dp,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            AegisTab.entries.forEach { tab ->
                                val selected = currentTab == tab
                                val icon = when (tab) {
                                    AegisTab.DASHBOARD -> Icons.Default.Dashboard
                                    AegisTab.TACTICAL_MAP -> Icons.Default.Map
                                    AegisTab.SUSPECTS -> Icons.Default.Badge
                                    AegisTab.SENSORS -> Icons.Default.Sensors
                                    AegisTab.API_GATEWAY -> Icons.Default.Api
                                }

                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(tab) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = tab.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyanGlow,
                                        selectedTextColor = CyanGlow,
                                        indicatorColor = CyanSubtle.copy(alpha = 0.5f),
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                                )
                            }
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = TacticalSurfaceVariant,
                                contentColor = CyanGlow,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                        }
                    },
                    containerColor = TacticalObsidian
                ) { innerPadding ->
                    when (currentTab) {
                        AegisTab.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AegisTab.TACTICAL_MAP -> TacticalMapScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AegisTab.SUSPECTS -> CriminalDatabaseScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AegisTab.SENSORS -> SensorsMonitoringScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AegisTab.API_GATEWAY -> ApiGatewayScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
