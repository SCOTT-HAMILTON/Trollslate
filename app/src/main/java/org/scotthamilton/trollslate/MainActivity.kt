package org.scotthamilton.trollslate

import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scotthamilton.trollslate.ui.theme.TrollslateTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrollslateTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun LettersSlide() {
    val letters = 'A'..'Z'
    Box (modifier =
    Modifier
        .height(100.dp)
        .width(300.dp)
        .background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(20)
        ),
        contentAlignment = Alignment.Center)
    {
        LazyRow(modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize(0.8f),
//            contentPadding = PaddingValues(30.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            items(letters.count()) { index ->
                Letter(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillParentMaxHeight()
                        .width(50.dp)
//                        .padding(10.dp)
                            ,
                    letter = letters.elementAt(index),
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer)
//                Divider(thickness = 10.dp)
            }
        }
    }
}

@Composable
fun MainTextField() {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var showError by remember { mutableStateOf(false) }
    TextField(
        modifier = Modifier
            .width(300.dp)
            .height(80.dp),
        value = text,
        onValueChange = {
            text = it
            showError = !text.text.all { it in 'A'..'Z' }
        },
        isError = showError,
        label = { Text(text = "Le texte troll", fontSize = 20.sp)},
        placeholder = { Text(text = "Votre texte", fontSize = 17.sp) },
        colors = TextFieldDefaults.textFieldColors(
//            textColor = MaterialTheme.colorScheme.onPrimary,
            backgroundColor = MaterialTheme.colorScheme.primary
        ),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    )
}

fun ilerp(from: IntRange, to: IntRange, value: Int) =
    (value - from.first).toFloat()*to.count().toFloat()/from.count().toFloat()+to.first.toFloat()

@RequiresApi(value = 26)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AngleSlide() {
    var scrollOffset by remember { mutableStateOf(0f) }
    val scrollRange = IntRange(-1000, 1000)
    val angleRange = IntRange(10, 80)
    Box (modifier =
    Modifier
        .height(200.dp)
        .width(300.dp)
        .background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(20)
        )
        .scrollable(
            orientation = Orientation.Vertical,
            state = rememberScrollableState { delta ->
                val new_offset = scrollOffset - delta
                if (scrollRange.first < new_offset && new_offset < scrollRange.last) {
                    scrollOffset = new_offset
                }
                delta
            }
        ),
        contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas3DPhone(
                modifier = Modifier
                    .size(150.dp, 75.dp)
                    .padding(start = 10.dp),
                backgroundColor =
                    MaterialTheme.colorScheme.secondaryContainer,
                angle = ilerp(
                    scrollRange,
                    angleRange,
                    scrollOffset.toInt()
                )
            )
        }
    }

}

@RequiresApi(value = 26)
@Preview(showBackground = true)
@Composable
fun Main() {
    TrollslateTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                LettersSlide()
                MainTextField()
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp))
                AngleSlide()
            }
        }
    }
}