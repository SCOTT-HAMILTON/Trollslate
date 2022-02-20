package org.scotthamilton.trollslate.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrollTextField() {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var showError by remember { mutableStateOf(false) }
    TextField(
        modifier = Modifier.width(300.dp).height(80.dp),
        value = text,
        onValueChange = {
            text = it
            showError = !text.text.all { it in 'A'..'Z' }
        },
        isError = showError,
        label = { Text(text = "Le texte troll", fontSize = 20.sp) },
        placeholder = { Text(text = "Votre texte", fontSize = 17.sp) },
        colors =
            TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)
    )
}
