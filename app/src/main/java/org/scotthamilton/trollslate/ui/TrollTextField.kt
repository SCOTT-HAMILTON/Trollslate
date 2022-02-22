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
import org.scotthamilton.trollslate.data.FontData
import java.util.*

data class TrollTextFieldData(
    var text: MutableState<String>,
    val showError: MutableState<Boolean>
)

fun defaultTrollTextFieldData(): TrollTextFieldData =
    TrollTextFieldData(
        text = mutableStateOf(""),
        showError = mutableStateOf(true)
    )
@Composable
fun TrollTextField(data: TrollTextFieldData) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    TextField(
        modifier = Modifier.width(300.dp).height(80.dp),
        value = text,
        onValueChange = {
            text = it.copy(text = it.text.uppercase(Locale.getDefault()))
            data.showError.value =
                !text.text.all { c -> c in FontData.lettersCodonTable.keys } || text.text.isEmpty()
            data.text.value = text.text
        },
        isError = data.showError.value,
        label = { Text(text = "Le texte troll", fontSize = 20.sp) },
        placeholder = { Text(text = "Votre texte", fontSize = 17.sp) },
        colors =
            TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)
    )
}
