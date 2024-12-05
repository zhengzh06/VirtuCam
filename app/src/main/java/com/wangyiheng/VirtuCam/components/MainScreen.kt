package com.wangyiheng.VirtuCam.components

import HomeScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wangyiheng.VirtuCam.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home_screen") {
                HomeScreen()
            }
            composable("user_profile") {
                UserProfileScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home_screen", "user_profile")
    val icons = listOf(R.drawable.home_icon, R.drawable.profile_icon)
    val labels = listOf("Home", "Profile")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color(0xffEFEEF6), // Background color
        contentColor = Color(0x1AFFFFFF) // Icon and text color
    ) {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },

                label = {
                    Text(
                        labels[index],
                        color = if (currentDestination?.route == screen) Color(0xFF000000) else Color(0x80000000)
                    )
                },
                selected = currentDestination?.route == screen,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF25D47F),
                    unselectedIconColor = Color(0xFF6D7991),
                    selectedTextColor = Color(0xFF000000),
                    unselectedTextColor = Color(0x80000000),
                    indicatorColor = Color(0xffB6E7D5)
                )
            )
        }
    }
}

