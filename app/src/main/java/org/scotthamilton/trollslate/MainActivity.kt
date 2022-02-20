package org.scotthamilton.trollslate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.scotthamilton.trollslate.ui.LettersSlide
import org.scotthamilton.trollslate.ui.PhoneAngleSelector
import org.scotthamilton.trollslate.ui.TrollTextField
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrollslateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { Main() }
            }
        }
    }
}

@RequiresApi(value = 26)
@Preview(showBackground = true)
@Composable
fun Main() {
    TrollslateTheme {
        Surface(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                LettersSlide()
                TrollTextField()
                PhoneAngleSelector()
            }
        }
    }
}
