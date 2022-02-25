package org.scotthamilton.trollslate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.scotthamilton.trollslate.data.decodeUrlBase64
import org.scotthamilton.trollslate.data.toUrlBase64
import org.scotthamilton.trollslate.ui.*
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrollslateTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainActivityScaffold(navController, this@MainActivity) }
                    composable(
                        route = "troll?text={text}&angle={angle}",
                        arguments =
                            listOf(
                                navArgument("text") {
                                    defaultValue = "INVALID".toUrlBase64()
                                },
                                navArgument("angle") { defaultValue = 45f }
                            )
                    ) {
                        val text = it.arguments?.getString("text")?.decodeUrlBase64()
                        TrollContent(
                            text ?: "INVALID",
                            it.arguments?.getFloat("angle") ?: 45f
                        )
                    }
                }
            }
        }
    }
    override fun onBackPressed() {}
}
