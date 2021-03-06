package org.scotthamilton.trollslate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.scotthamilton.trollslate.data.FontData

data class TrollTextFieldData(var text: MutableState<String>, val showError: MutableState<Boolean>)

fun defaultTrollTextFieldData(): TrollTextFieldData =
    TrollTextFieldData(text = mutableStateOf(""), showError = mutableStateOf(true))

private fun visualTransformAnnotate(colorScheme: ColorScheme, text: String): AnnotatedString {
    val utext = text.uppercase()
    val badRanges =
        listOf(-1 to -1) +
            utext
                .mapIndexed { index, c -> index to c }
                .filter { it.second !in FontData.lettersCodonTable.keys }
                .fold(listOf()) { a, c ->
                    if (a.isEmpty()) {
                        listOf(c.first to c.first)
                    } else {
                        val last = a.last()
                        if (c.first - 1 == last.second) {
                            a.dropLast(1) + listOf(last.copy(second = c.first))
                        } else {
                            a + listOf(c.first to c.first)
                        }
                    }
                } +
            listOf(utext.length to utext.length)
    val goodRanges =
        badRanges
            .foldRight((0 to 0) to mutableListOf<Pair<Int, Int>>()) { t, r ->
                val prev = r.first
                if (prev != 0 to 0) {
                    val newstart = t.second + 1
                    val newend = r.first.first - 1
                    if (newend >= newstart) {
                        r.second.add(newstart to newend)
                    }
                }
                t to r.second
            }
            .second
    val ranges = (goodRanges + badRanges.dropLast(1).drop(1)).sortedBy { it.first }

    return AnnotatedString.Builder("")
        .apply {
            //        append(utext)
            var isBad = badRanges.size >= 2 && badRanges[1].first == 0
            ranges.forEach {
                pushStyle(
                    SpanStyle(color = if (isBad) colorScheme.onError else colorScheme.onSurface)
                )
                append(utext.slice(IntRange(it.first, it.second)))
                pop()
                isBad = !isBad
            }
        }
        .toAnnotatedString()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TrollTextField(
    colorScheme: ColorScheme,
    data: TrollTextFieldData,
    onValueChanged: suspend (CoroutineScope) -> Unit
) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val isAllowedChar = { c: Char -> c.isWhitespace() || c in FontData.lettersCodonTable.keys }
    OutlinedTextField(
        modifier = Modifier.width(300.dp).height(160.dp).testTag("trollTextField"),
        value = text,
        onValueChange = {
            text = it
            data.text.value = text.text.uppercase()
            data.showError.value = !data.text.value.all(isAllowedChar) || data.text.value.isEmpty()
            scope.launch { withContext(Dispatchers.IO) { onValueChanged(this) } }
        },
        visualTransformation = { annotatedString ->
            TransformedText(
                visualTransformAnnotate(colorScheme, annotatedString.text),
                OffsetMapping.Identity
            )
        },
        isError = data.showError.value,
        label = {
            Text(
                text = stringResource(id = org.scotthamilton.trollslate.R.string.troll_text),
                fontSize = 20.sp
            )
        },
        placeholder = {
            Text(
                text = stringResource(id = org.scotthamilton.trollslate.R.string.your_text),
                fontSize = 17.sp
            )
        },
        colors =
            TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = colorScheme.onSurface,
                errorIndicatorColor = colorScheme.onSurface,
                focusedLabelColor = colorScheme.outline,
                unfocusedLabelColor = colorScheme.outline,
                disabledLabelColor = colorScheme.outline,
                errorLabelColor = colorScheme.onError,
                cursorColor = colorScheme.outline,
                errorCursorColor = colorScheme.onError,
                placeholderColor = colorScheme.outline,
                disabledPlaceholderColor = colorScheme.outline,
            ),
        shape = RoundedCornerShape(20),
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Go,
            ),
        keyboardActions =
            KeyboardActions(
                onGo = {
                    val selection = TextRange(text.text.length + 1)
                    text =
                        text.copy(
                            text = text.text.insert(text.selection.start, '\n'),
                            selection = selection
                        )
                    data.text.value = text.text.uppercase()
                }
            ),
        textStyle =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = colorScheme.onSurface
            ),
    )
}

private fun String.insert(index: Int, c: Char): String =
    StringBuilder(this).apply { insert(index, c) }.toString()
