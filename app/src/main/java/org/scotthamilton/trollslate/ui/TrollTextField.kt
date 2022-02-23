package org.scotthamilton.trollslate.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.scotthamilton.trollslate.data.FontData

data class TrollTextFieldData(var text: MutableState<String>, val showError: MutableState<Boolean>)

fun defaultTrollTextFieldData(): TrollTextFieldData =
    TrollTextFieldData(text = mutableStateOf(""), showError = mutableStateOf(true))

@Composable
fun TrollTextField(data: TrollTextFieldData, onValueChanged: suspend (CoroutineScope)->Unit) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue("")) }
    TextField(
        modifier = Modifier
            .width(300.dp)
            .height(80.dp),
        value = text,
        onValueChange = {
            text = it
            data.text.value = text.text.uppercase()
            data.showError.value =
                !data.text.value.all { c -> c in FontData.lettersCodonTable.keys } ||
                    data.text.value.isEmpty()
            scope.launch {
                withContext(Dispatchers.IO) {
                    onValueChanged(this)
                }
            }
        },
        visualTransformation = { text ->
            TransformedText(AnnotatedString(text.text.uppercase()), OffsetMapping.Identity)
        },
        isError = data.showError.value,
        label = { Text(text = "Le texte troll", fontSize = 20.sp) },
        placeholder = { Text(text = "Votre texte", fontSize = 17.sp) },
        colors =
            TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)
    )
}
