package dk.itu.moapd.x9.visv.core.time

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats this epoch millis timestamp as a readable date-time string.
 *
 * @param locale The locale to use for formatting.
 *
 * @return A readable date-time string.
 */
fun Long.toSimpleDateTimeString(locale: Locale = Locale.getDefault()): String {
    val dateFormat = SimpleDateFormat("E, MMM dd yyyy hh:mm:ss a", locale)
    return dateFormat.format(this)
}