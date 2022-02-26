package org.scotthamilton.trollslate.utils

import android.util.Base64
import java.nio.charset.StandardCharsets

fun String.toUrlBase64(): String =
    Base64.encode(toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_PADDING)
        .decodeToString()
        .trim()
        .replace("\n", "")

fun String.decodeUrlBase64(): String =
    Base64.decode(this, Base64.URL_SAFE or Base64.NO_PADDING).decodeToString()
