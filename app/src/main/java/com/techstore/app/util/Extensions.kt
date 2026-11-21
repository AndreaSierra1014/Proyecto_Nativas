package com.techstore.app.util

import java.text.NumberFormat
import java.util.Locale

fun Double.toCOP(): String =
    NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(this)
