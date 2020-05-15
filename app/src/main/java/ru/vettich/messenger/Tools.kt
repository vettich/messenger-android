package ru.vettich.messenger

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Tools {
    companion object {
        fun dateTimeFormat(s : String) : String {
            val time = LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
            val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy")
            return time.format(formatter)
        }
    }
}