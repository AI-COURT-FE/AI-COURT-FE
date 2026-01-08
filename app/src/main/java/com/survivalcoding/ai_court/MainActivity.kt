package com.survivalcoding.ai_court

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.survivalcoding.ai_court.presentation.navigation.CourtNavGraph
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            AI_COURTTheme {
                val navController = rememberNavController()
                CourtNavGraph(navController = navController)
            }
        }
    }
}
