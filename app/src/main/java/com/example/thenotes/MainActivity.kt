package com.example.thenotes


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.thenotes.ui.theme.TheNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                TheNotesApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun TheNotesApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.NOTE) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = ""
                        )
                    },
                   // label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            if(currentDestination.label.contentEquals("Note")) {
                Greeting(
                    name = "Notes",
                    modifier = Modifier.padding(innerPadding)
                )
            }else if(currentDestination.label.contentEquals("Favorites")){
                Greeting(
                    name = "Favourite",
                    modifier = Modifier.padding(innerPadding)
                )
            }else if(currentDestination.label.contentEquals("Settings")){
                Greeting(
                    name = "Settings",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon:  ImageVector,
) {
    NOTE("Note", Icons.AutoMirrored.Filled.Note),
    FAVORITES("Favorites", Icons.Default.Favorite),
    SETTINGS("Settings", Icons.Default.Settings),
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "${name}!",
        modifier = modifier
    )
}
