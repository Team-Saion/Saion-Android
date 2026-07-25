package com.saion.feature.schedule.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun formatScheduleDateText(startDate: String, endDate: String): String {
    val start = runCatching { LocalDate.parse(startDate) }.getOrNull() ?: return startDate
    val end = runCatching { LocalDate.parse(endDate) }.getOrNull() ?: return if (startDate == endDate) startDate else "$startDate ~ $endDate"
    val locale = Locale.KOREAN
    if (start == end) {
        return stringResource(
            R.string.schedule_detail_date,
            start.year,
            start.monthValue,
            start.dayOfMonth,
            start.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
        )
    }
    val startDayOfWeek = start.dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
    val endDayOfWeek = end.dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
    return if (start.year == end.year) {
        stringResource(
            R.string.schedule_detail_date_range_same_year,
            start.year,
            start.monthValue,
            start.dayOfMonth,
            startDayOfWeek,
            end.monthValue,
            end.dayOfMonth,
            endDayOfWeek,
        )
    } else {
        stringResource(
            R.string.schedule_detail_date_range_different_year,
            start.year,
            start.monthValue,
            start.dayOfMonth,
            startDayOfWeek,
            end.year,
            end.monthValue,
            end.dayOfMonth,
            endDayOfWeek,
        )
    }
}

internal fun formatScheduleTimeRange(
    isAllDay: Boolean,
    startTime: String?,
    endTime: String?,
): String {
    if (isAllDay) return "하루 종일"
    val start = startTime?.toMeridiemText()
    val end = endTime?.toMeridiemText()
    return when {
        start != null && end != null -> "$start ~ $end"
        start != null -> start
        end != null -> end
        else -> ""
    }
}

internal fun formatDateTimeRow(
    date: LocalDate,
    time: LocalTime,
): String {
    val meridiem = if (time.hour < 12) "오전" else "오후"
    val hour = when (val normalized = time.hour % 12) {
        0 -> 12
        else -> normalized
    }
    return "%d. %d. %d  %s %d:%02d".format(
        Locale.KOREAN,
        date.year,
        date.monthValue,
        date.dayOfMonth,
        meridiem,
        hour,
        time.minute,
    )
}

internal fun String.toMeridiemText(): String {
    val parsed = runCatching { LocalTime.parse(this) }.getOrNull() ?: return this
    val meridiem = if (parsed.hour < 12) "오전" else "오후"
    val hour = when (val normalized = parsed.hour % 12) {
        0 -> 12
        else -> normalized
    }
    return "%s %d:%02d".format(Locale.KOREAN, meridiem, hour, parsed.minute)
}

internal fun initialScheduleDateTime(now: LocalDateTime = LocalDateTime.now()): Pair<LocalDateTime, LocalDateTime> {
    val roundedHour = if (now.minute == 0 && now.second == 0 && now.nano == 0) {
        now.withMinute(0).withSecond(0).withNano(0)
    } else {
        now.plusHours(1).withMinute(0).withSecond(0).withNano(0)
    }
    return roundedHour to roundedHour.plusHours(1)
}

internal fun LocalDate.toScheduleApiText(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)

internal fun LocalTime.toScheduleApiText(): String = format(DateTimeFormatter.ofPattern("HH:mm"))
