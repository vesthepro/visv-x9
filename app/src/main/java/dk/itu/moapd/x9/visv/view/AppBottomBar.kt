package dk.itu.moapd.x9.visv.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AppBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard")},
            selected = currentRoute == "dashboard",
            onClick = {
                navController.navigate("dashboard") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true}
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "Report") },
            label = { Text("Report")},
            selected = currentRoute == "report",
            onClick = {
                navController.navigate("report") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true}
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
            label = { Text("Map") },
            selected = currentRoute == "map",
            onClick = {
                navController.navigate("map") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Report") },
            label = { Text("Settings")},
            selected = currentRoute == "settings",
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true}
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}