package org.scotthamilton.trollslate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import org.scotthamilton.trollslate.ui.*
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme
import org.scotthamilton.trollslate.ui.theme.trollslateColorScheme
import org.scotthamilton.trollslate.utils.decodeUrlBase64
import org.scotthamilton.trollslate.utils.toUrlBase64

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val navbackable = AtomicBoolean(false)
    private var navController: NavHostController? = null
    enum class NavPage {
        Main,
        TrollContent
    }
    private val currentPage: AtomicInteger = AtomicInteger(NavPage.Main.ordinal)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrollslateTheme {
                val trollColorScheme = trollslateColorScheme()
                navController = rememberNavController()
                navController?.let { navController ->
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            currentPage.set(NavPage.Main.ordinal)
                            MainActivityScaffold(
                                navController,
                                this@MainActivity,
                                trollColorScheme,
                            )
                        }
                        composable(
                            route = "troll?text={text}&angle={angle}&navbackable={navbackable}",
                            arguments =
                                listOf(
                                    navArgument("text") { defaultValue = "INVALID".toUrlBase64() },
                                    navArgument("angle") { defaultValue = 45f },
                                    navArgument("navbackable") { defaultValue = false },
                                )
                        ) {
                            currentPage.set(NavPage.TrollContent.ordinal)
                            navbackable.set(it.arguments?.getBoolean("navbackable") ?: false)
                            val text = it.arguments?.getString("text")?.decodeUrlBase64()
                            TrollContent(
                                text ?: "INVALID",
                                it.arguments?.getFloat("angle") ?: 45f,
                                this@MainActivity,
                                colorScheme = trollColorScheme
                            )
                        }
                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        if (currentPage.get() == NavPage.TrollContent.ordinal && navbackable.get()) {
            navController?.navigate("main")
        }
    }
}
